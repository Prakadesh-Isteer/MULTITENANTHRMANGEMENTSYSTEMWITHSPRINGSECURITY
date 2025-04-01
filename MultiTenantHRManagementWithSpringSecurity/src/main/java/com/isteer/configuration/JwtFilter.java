package com.isteer.configuration;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isteer.dto.ErrorMessageDto;
import com.isteer.enums.HrManagementEnum;
import com.isteer.service.CustomerUserDetailsService;
import com.isteer.util.JwtUtil;
import com.isteer.util.RedisService;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter  extends OncePerRequestFilter{
	

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomerUserDetailsService userDetailsService;
    
    @Autowired
    private RedisService redisService;

  

	@Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        
        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        try {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            username = jwtUtil.extractUserName(jwt);

            // Check if the token is still valid in Redis
            String latestToken = redisService.getLatestToken(username);
            if (latestToken == null || !latestToken.equals(jwt)) {
                // Token is logged out, reject authentication
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");

                ErrorMessageDto error = new ErrorMessageDto(
                    HrManagementEnum.logout_invaild_token.getStatusCode(),
                    HrManagementEnum.logout_invaild_token.getStatusMessage()
                );

                ObjectMapper objectMapper = new ObjectMapper();
                String jsonResponse = objectMapper.writeValueAsString(error);

                response.getWriter().write(jsonResponse);
                response.getWriter().flush();
                return;
            }
        }
        }catch(ExpiredJwtException e){
        	
        	  response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
              response.setContentType("application/json");

              ErrorMessageDto error = new ErrorMessageDto(
                  HrManagementEnum.TOKEN_EXPIRATION.getStatusCode(),
                  HrManagementEnum.TOKEN_EXPIRATION.getStatusMessage()
              );

              ObjectMapper objectMapper = new ObjectMapper();
              String jsonResponse = objectMapper.writeValueAsString(error);

              response.getWriter().write(jsonResponse);
              response.getWriter().flush();
              return;
        }
    	System.out.println("authentication not set1..................................................");
    	
    	

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            System.out.println(userDetails);
System.out.println("set-----------------------------------------------------------------------------");
            if (jwtUtil.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        chain.doFilter(request, response);
    }

}

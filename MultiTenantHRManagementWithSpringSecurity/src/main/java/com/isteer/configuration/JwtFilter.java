package com.isteer.configuration;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isteer.dto.ErrorMessageDto;
import com.isteer.enums.HrManagementEnum;
import com.isteer.service.CustomerUserDetailsService;
import com.isteer.service.RedisService;
import com.isteer.util.JwtUtil;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {
	public static Logger logging = LogManager.getLogger(JwtFilter.class);

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
				logging.info("JWT token received. Extracted username: {}", username);

				logging.info("Check if the token is still valid in Redis");
				String latestToken = redisService.getLatestToken(username);
				if (latestToken == null || !latestToken.equals(jwt)) {
					
					logging.warn("Token is invalid or logged out. Rejecting authentication for username: {}", username);
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					
					response.setContentType("application/json");

					ErrorMessageDto error = new ErrorMessageDto(HrManagementEnum.logout_invaild_token.getStatusCode(),
							HrManagementEnum.logout_invaild_token.getStatusMessage());

					ObjectMapper objectMapper = new ObjectMapper();
					String jsonResponse = objectMapper.writeValueAsString(error);

					response.getWriter().write(jsonResponse);
					response.getWriter().flush();
					return;
				}
			}
		} catch (ExpiredJwtException e) {
			
            logging.error("JWT token expired for username: {}. Error: {}", username, e.getMessage());

			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json");

			ErrorMessageDto error = new ErrorMessageDto(HrManagementEnum.TOKEN_EXPIRATION.getStatusCode(),
					HrManagementEnum.TOKEN_EXPIRATION.getStatusMessage());

			ObjectMapper objectMapper = new ObjectMapper();
			String jsonResponse = objectMapper.writeValueAsString(error);

			response.getWriter().write(jsonResponse);
			response.getWriter().flush();
			return;
		}catch(MalformedJwtException e) {
			
			 logging.error("JWT token Malformed for username: {}. Error: {}", username, e.getMessage());

			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json");

			ErrorMessageDto error = new ErrorMessageDto(HrManagementEnum.Token_malform_Exception.getStatusCode(),
					HrManagementEnum.Token_malform_Exception.getStatusMessage());

			ObjectMapper objectMapper = new ObjectMapper();
			String jsonResponse = objectMapper.writeValueAsString(error);

			response.getWriter().write(jsonResponse);
			response.getWriter().flush();
		}catch(SignatureException e){
			 logging.error("JWT token Signature Exception for username: {}. Error: {}", username, e.getMessage());

			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json");

			ErrorMessageDto error = new ErrorMessageDto(HrManagementEnum.Token_signature_exception.getStatusCode(),
					HrManagementEnum.Token_signature_exception.getStatusMessage());

			ObjectMapper objectMapper = new ObjectMapper();
			String jsonResponse = objectMapper.writeValueAsString(error);

			response.getWriter().write(jsonResponse);
			response.getWriter().flush();	
		}

		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			 logging.info("Validating token for user: {}", username);
			UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
			if (jwtUtil.validateToken(jwt, userDetails)) {
				logging.info("Authentication successful for user: {}", username);
				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
						null, userDetails.getAuthorities());
				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authToken);
			}
		}
		chain.doFilter(request, response);
	}

}

package com.isteer.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.isteer.dto.HttpMethodRoleRights;
import com.isteer.dto.RequestPermisionDto;
import com.isteer.dto.UserDetailsDto;
import com.isteer.entity.Employee;
import com.isteer.enums.HrManagementEnum;
import com.isteer.repository.AuthRepoImpl;
import com.isteer.repository.EmployeeRepoDaoImpl;
import com.isteer.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
@Service
public class AuthService {
	
	@Autowired
	AuthRepoImpl repo;
	
	@Autowired
	HttpServletRequest request;
	
	@Autowired
	EmployeeRepoDaoImpl employeeRepoDaoImpl;
	
	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	JwtUtil jwtUtil;
	
    private static final Logger logger = LogManager.getLogger(AuthService.class);

	
	public boolean hasPermission() {
	    logger.info("Checking permissions for the current request");
	    
	    String userName = request.getUserPrincipal().getName();
	    String url = request.getRequestURI();
	    String httpMethod = request.getMethod();
	    
	    // Fetch user details based on username
	    Employee user = employeeRepoDaoImpl.findByUserName(userName);
	    logger.debug("User role UUID: {}", user.getRoleUuid());
	    logger.debug("Request URL: {}", url);
	    System.out.println(user.getRoleUuid());
	    System.out.println(url);
	    
	    // Fetch URL permissions and HTTP method permissions
	    List<RequestPermisionDto>  permission = repo.findUrl(url);
	    List<HttpMethodRoleRights> methodPermission = repo.findHttpMethod(httpMethod);
	    logger.debug("Fetched URL permissions: {}", permission);
	    logger.debug("Fetched method permissions for HTTP method {}: {}", httpMethod, methodPermission);
	    System.out.println(methodPermission);
	    System.out.println(httpMethod);
	    
	    // Check if the user has permission based on roleId for URL permissions
	    
	    boolean urlPermission = permission.stream().anyMatch(r -> r.getRoleUuid().equals(user.getRoleUuid()));
	    

	    boolean methodPermissionStatus = methodPermission.stream().anyMatch(m-> m.getRoleUuid().equals(user.getRoleUuid()));
	    
	    logger.debug("URL permission: {}", urlPermission);
	    logger.debug("Method permission: {}", methodPermissionStatus);
	    
	    System.out.println(urlPermission);
	    System.out.println(methodPermissionStatus);
	    
	    // If both URL and HTTP method permissions are granted, return true
	    if (urlPermission && methodPermissionStatus) {
	        return true;
	    }
	    logger.warn("User does not have permission to access the endpoint");

	    return false;
	}
	
    public Employee getuserByLogged(String userId){
    	logger.info("Fetching the logged-in user details for userId: {}", userId);
		String userName = SecurityContextHolder.getContext().getAuthentication().getName();
		Employee loggedInEmployee = employeeRepoDaoImpl.findByUserName(userName);

		return loggedInEmployee;
	
	}

	
	public String userLogin(UserDetailsDto loginData) {
		logger.info("Attempting login for user: {}", loginData.getUserName());
		Employee wrkCredentials = employeeRepoDaoImpl.findByUserName(loginData.getUserName());
		logger.debug("Retrieved user credentials: {}", wrkCredentials != null ? wrkCredentials.getUserName() : "Not found");
		System.out.println(loginData.getPassword());
		System.out.println(loginData.getUserName());
		try {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginData.getUserName(), loginData.getPassword()));
	     	
		if (authentication.isAuthenticated()) {
			String token = jwtUtil.generateToken(wrkCredentials.getUserName());
            logger.info("User {} successfully logged in, token generated", loginData.getUserName());
			return token;
		}
		}catch(BadCredentialsException e){
	        logger.error("Authentication failed for user: {}", loginData.getUserName());
			throw new com.isteer.exception.BadCredentialsException(HrManagementEnum.Bad_credentials_exception);
		}
	    logger.warn("Login failed for user: {}", loginData.getUserName());

		return null;
		}
	
	
	// Method to add an endpoint with its role mapping
	 // Service method to add a new endpoint with role mapping using role_uuid
    public int addEndpointWithRoleMapping(String endpointUrl, String roleUuid) {
        logger.info("Successfully added endpoint with role mapping for URL: {}", endpointUrl);
        logger.info("Adding endpoint with URL: {} and role UUID: {}", endpointUrl, roleUuid);
        return repo.addEndpointWithRoleMapping(endpointUrl, roleUuid);
    }
    
    // Service method to add HTTP method mapping using role UUID and HTTP method name
    public int addHttpMethodMapping(String httpMethod,String roleUuid) {
        // Get the HTTP method UUID based on the HTTP method name
        logger.info("Adding HTTP method mapping for HTTP method: {} and role UUID: {}", httpMethod, roleUuid);
        logger.info("Successfully added HTTP method mapping for method: {}", httpMethod);

        // Add the HTTP method mapping between the HTTP method and the role
        return repo.addHttpMethodMapping(httpMethod, roleUuid);
    }

}

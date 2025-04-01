package com.isteer.service;

import java.util.List;

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
import com.isteer.exception.TenantIdNullException;
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
	
	
	
	public boolean hasPermission() {
	    System.out.println("....................");
	    
	    String userName = request.getUserPrincipal().getName();
	    String url = request.getRequestURI();
	    String httpMethod = request.getMethod();
	    
	    // Fetch user details based on username
	    Employee user = employeeRepoDaoImpl.findByUserName(userName);
	    System.out.println(user.getRoleUuid());
	    System.out.println(url);
	    
	    // Fetch URL permissions and HTTP method permissions
	    List<RequestPermisionDto>  permission = repo.findUrl(url);
	    List<HttpMethodRoleRights> methodPermission = repo.findHttpMethod(httpMethod);
	    
	    System.out.println(methodPermission);
	    System.out.println(httpMethod);
	    
	    // Check if the user has permission based on roleId for URL permissions
	    
	    boolean urlPermission = permission.stream().anyMatch(r -> r.getRoleUuid().equals(user.getRoleUuid()));
	    
//	    boolean urlPermission = false;
//	    if (permission != null && permission.getRoleId().equals(user.getRoleId())) {
//	        urlPermission = true;
//	    }
	    boolean methodPermissionStatus = methodPermission.stream().anyMatch(m-> m.getRoleUuid().equals(user.getRoleUuid()));
	    
	    // Check if the user has permission based on roleId for HTTP method permissions
//	    boolean methodPermissionStatus = false;
//	    if (methodPermission != null && methodPermission.getRoleId().equals(user.getRoleId())) {
//	        methodPermissionStatus = true;
//	    }
	    
	    System.out.println(urlPermission);
	    System.out.println(methodPermissionStatus);
	    
	    // If both URL and HTTP method permissions are granted, return true
	    if (urlPermission && methodPermissionStatus) {
	        return true;
	    }

	    return false;
	}
	
    public Employee getuserByLogged(String userId){
		
		String userName = SecurityContextHolder.getContext().getAuthentication().getName();
		Employee loggedInEmployee = employeeRepoDaoImpl.findByUserName(userName);

		return loggedInEmployee;
	
	}

	
	public String userLogin(UserDetailsDto loginData) {
		Employee wrkCredentials = employeeRepoDaoImpl.findByUserName(loginData.getUserName());
		System.out.println(loginData.getPassword());
		System.out.println(loginData.getUserName());
		try {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginData.getUserName(), loginData.getPassword()));
	     	
		if (authentication.isAuthenticated()) {
			String token = jwtUtil.generateToken(wrkCredentials.getUserName());
			return token;
		}
		}catch(BadCredentialsException e){
			throw new com.isteer.exception.BadCredentialsException(HrManagementEnum.Bad_credentials_exception);
		}
		return null;
		}
	
	
	// Method to add an endpoint with its role mapping
	 // Service method to add a new endpoint with role mapping using role_uuid
    public int addEndpointWithRoleMapping(String endpointUrl, String roleUuid) {
        // Call repository to add the endpoint with the role mapping
        return repo.addEndpointWithRoleMapping(endpointUrl, roleUuid);
    }
    
    // Service method to add HTTP method mapping using role UUID and HTTP method name
    public int addHttpMethodMapping(String httpMethod,String roleUuid) {
        // Get the HTTP method UUID based on the HTTP method name
      
        // Add the HTTP method mapping between the HTTP method and the role
        return repo.addHttpMethodMapping(httpMethod, roleUuid);
    }

}

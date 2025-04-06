package com.isteer.repository.dao;

import java.util.List;

import com.isteer.dto.HttpMethodRoleRights;
import com.isteer.dto.RequestPermisionDto;

public interface AuthRepoDao {
	
	public List<RequestPermisionDto> findUrl(String url);
	public List<HttpMethodRoleRights> findHttpMethod(String httpMethod);
    public int addEndpointWithRoleMapping(String endpointUrl, String roleUuid); 
    public int addHttpMethodMapping(String httpMethod, String roleUuid);
}

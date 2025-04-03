package com.isteer.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class RedisService {
    
    @Cacheable(value = "token", key = "#userName")
    public String getLatestToken(String userName) {
        return null;
    }
    
    @CachePut(value = "token", key = "#userName")
    public String putUpdatedToken(String userName, String updatedToken) {
        return updatedToken;
    }
    
    @CacheEvict(value = "token", key = "#userName") 
    public void removeToken(String userName) {
        // Removes the user's token from the cache
    }
    
    
}

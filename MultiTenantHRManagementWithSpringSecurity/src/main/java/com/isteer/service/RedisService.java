package com.isteer.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class RedisService {
	
	private static final Logger logger = LogManager.getLogger(RedisService.class);

    
    @Cacheable(value = "token", key = "#userName")
    public String getLatestToken(String userName) {
        logger.info("Fetching token for user: {}", userName);

        return null;
    }
    
    @CachePut(value = "token", key = "#userName")
    public String putUpdatedToken(String userName, String updatedToken) {
        logger.info("Updating token for user: {}", userName);

        return updatedToken;
    }
    
    @CacheEvict(value = "token", key = "#userName") 
    public void removeToken(String userName) {
        logger.info("Removing token for user: {}", userName);

        // Removes the user's token from the cache
    }
    
    
}

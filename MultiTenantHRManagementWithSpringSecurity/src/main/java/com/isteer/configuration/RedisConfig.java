package com.isteer.configuration;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Configuration
@EnableCaching
public class RedisConfig {
	
	private static final Logger logging = LogManager.getLogger(RedisConfig.class);
	
	@Bean
	CacheManager cacheManager(RedisConnectionFactory redisConnectionfactory ) {
		 logging.info("Configuring Redis Cache Manager...");
		
		RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig().serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericToStringSerializer<>(Object.class)));
		 RedisCacheManager cacheManager = RedisCacheManager.builder(redisConnectionfactory)
	                .cacheDefaults(cacheConfiguration)
	                .build();
		 logging.info("Redis Cache Manager configured successfully.");
	        
	        return cacheManager;
	}
	
	
	@Bean
	RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory){
		
		logging.info("Configuring Redis Template...");
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new GenericToStringSerializer<>(Object.class));
		return template;
		
	}
	
	
	

}

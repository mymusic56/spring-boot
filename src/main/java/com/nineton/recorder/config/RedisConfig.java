package com.nineton.recorder.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import redis.clients.jedis.Jedis;

@Configuration
@PropertySource("application.properties")
public class RedisConfig {
	
	@Value("${spring.data.redis.host}")
	private String host;
	
	@Value("${spring.data.redis.port}")
	private int port;
	
	@Value("${spring.data.redis.password}")
	private String password;
	
	@Bean
	public Jedis redis() {
		Jedis redis= new Jedis(host, port);
		redis.auth(password);
		redis.select(3);
		
		return redis;
	}
}

package com.nineton.recorder.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nineton.recorder.dao.RecordListMongoDao;
import com.nineton.recorder.entity.RecordLists;

import redis.clients.jedis.Jedis;

@RestController
@RequestMapping(path = "/hello")
public class HelloController {
	
	@Autowired
	RecordListMongoDao recordListMongoDao;
	
	@Autowired
	Jedis redis;
	
	@RequestMapping("/index")
	public String helloWorld() {
		return "hello world";
	}
	@RequestMapping("/get")
	public RecordLists get() {
		RecordLists record = recordListMongoDao.getRecord(0);
		return record;
	}
	
	@RequestMapping("/getRedis")
	public String getByRedisKey() {
		String value = redis.get("a");
		return value;
	}
	
}

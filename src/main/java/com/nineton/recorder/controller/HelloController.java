package com.nineton.recorder.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nineton.recorder.dao.RecordListMongoDao;
import com.nineton.recorder.dao.VoiceToWordsMongoDao;
import com.nineton.recorder.entity.RecordLists;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@RestController
@RequestMapping(path = "/hello")
public class HelloController {
	
	@Autowired
	RecordListMongoDao recordListMongoDao;
	
	@Autowired
	VoiceToWordsMongoDao voiceDao;
	
	Jedis redis;
	
	@Autowired
	JedisPool jedisPool;
	
	@RequestMapping("/index")
	public String helloWorld() {
		return "hello world";
	}
	@RequestMapping("/get")
	public RecordLists get() {
		RecordLists record = recordListMongoDao.getRecord(0);
		return record;
	}
	
	@RequestMapping("/getList")
	public void getList() {
		voiceDao.getListByStatus(1, 1, 20);
	}
	
	@RequestMapping("/getRedis")
	public String getByRedisKey() {
		System.out.println(new Date(System.currentTimeMillis()+28800000));
		redis = jedisPool.getResource();
		String value = redis.get("a");
		System.out.println(value);
		redis.close();
		return value;
	}
	
}

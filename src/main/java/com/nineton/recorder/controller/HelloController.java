package com.nineton.recorder.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nineton.recorder.dao.RecordListMongoDao;
import com.nineton.recorder.entity.RecordLists;

@RestController
@RequestMapping(path = "/hello")
public class HelloController {
	
	@Autowired
	RecordListMongoDao recordListMongoDao;
	
	@RequestMapping("/index")
	public String helloWorld() {
		return "hello world";
	}
	@RequestMapping("/get")
	public RecordLists get() {
		RecordLists record = recordListMongoDao.getRecord(0);
		return record;
	}
	
	
}

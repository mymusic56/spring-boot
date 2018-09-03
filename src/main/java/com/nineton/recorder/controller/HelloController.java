package com.nineton.recorder.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.nineton.recorder.dao.RecordListMongoDao;
import com.nineton.recorder.dao.VoiceToWordsMongoDao;
import com.nineton.recorder.entity.RecordLists;
import com.nineton.recorder.entity.VoiceToWordsEntity;
import com.nineton.recorder.service.DownloadService;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@RestController
@RequestMapping(path = "/hello")
public class HelloController {
	
	@Autowired
	RecordListMongoDao recordListMongoDao;
	
	@Autowired
	VoiceToWordsMongoDao voiceDao;
	
	@Autowired
	DownloadService download;
	
	Jedis redis;
	
	@Autowired
	JedisPool jedisPool;
	
	private Logger logger = null;
	
	public HelloController() {
		logger = Logger.getLogger(HelloController.class);
	}
	
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
		redis = jedisPool.getResource();
		String value = redis.get("a");
		redis.close();
		return value;
	}
	
	@RequestMapping("/waitQuery")
	public ModelAndView getWaitQuery(Map<String, Object> map) {
		List<VoiceToWordsEntity> voiceList = voiceDao.getWaitQueryList(3, 1, 40);
		map.put("date", new Date(System.currentTimeMillis()));
		
		ModelAndView view = new ModelAndView();
		view.addObject("date", new Date());
		view.addObject("voiceList", voiceList);
		return view;
	}
	
	@RequestMapping("/waitQuery2")
	public Model waitQuery2(Model model) {
		
		model.addAttribute("date", "Date: "+new Date(System.currentTimeMillis()+28800000));
		return model;
	}
	
	@RequestMapping("/getUrl")
	public String getUrl(long fileId, int userId) {
		String url = download.getSingUrl(fileId, userId);
		System.out.println("url: "+url);
		return "url:"+url;
	}
}

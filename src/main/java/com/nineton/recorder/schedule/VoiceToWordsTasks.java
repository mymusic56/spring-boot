package com.nineton.recorder.schedule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.nineton.recorder.dao.VoiceToWordsMongoDao;
import com.nineton.recorder.service.DownloadService;
import com.nineton.recorder.service.ResultService;
import com.nineton.recorder.service.UploadService;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Component
public class VoiceToWordsTasks {
	private static final Log logger = LogFactory.getLog(VoiceToWordsTasks.class);
	
	@Autowired
	DownloadService downService;
	
	@Autowired
	UploadService uploadService;
	
	@Autowired
	ResultService resultService;
	
	@Scheduled(initialDelay = 1000, fixedDelay = 5000)
	@Async
	public void fileDownload() {
		downService.download();
//		logger.info(" .+++++++++++++. download ++++++++++");
	}

	@Scheduled(initialDelay = 2000, fixedDelay = 5000)
	@Async
	public void fileUpload() {
		uploadService.upload();
//		logger.info(" ............... upload ...............");
	}

	@Scheduled(cron = "0 0/5 * * * *")
	public void fileResult() {
//		logger.info(" ----------- result -----------");
		resultService.getResult();
	}
}

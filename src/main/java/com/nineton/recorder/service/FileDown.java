package com.nineton.recorder.service;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;

import com.google.gson.Gson;
import com.nineton.recorder.dao.VoiceToWordsMongoDao;
import com.nineton.recorder.entity.VoiceToWordsEntity;
import com.nineton.recorder.util.FileDownload;

import redis.clients.jedis.Jedis;

@PropertySource("application.properties")
public class FileDown{

	private static Logger logger = null;
	
	private String basePath = "http://__BUCKET__.oss-cn-hangzhou.aliyuncs.com/";

	private String tempDir = "tmp/download";
	
	private String downloadQueue = "REORDER:voice_to_words_down_queue";
	
	@Autowired
	Jedis redis;
	
	public FileDown(){
		logger = Logger.getLogger(FileDown.class);
	}
	
	
	/**
	 * 完成下载工作后，把本地路径写入数据库
	 * @return
	 */
	public void download(){
		//获取待下载的数据
		String log = redis.rpop(this.downloadQueue);
		Gson gson = new Gson();
		VoiceToWordsEntity voiceEntity = gson.fromJson(log, VoiceToWordsEntity.class);
		if(voiceEntity != null){
			VoiceToWordsMongoDao voiceDao = new VoiceToWordsMongoDao();
			System.out.println(voiceEntity.getC_id());
			//路径为空
			if("".equals(voiceEntity.getUrl())){
				//标记为异常
				voiceEntity.setRemark("文件路径为空");
				voiceEntity.setStatus(4);
				voiceDao.updateStatusById(voiceEntity.getId(), voiceEntity);
			}
			
			String filename = voiceEntity.getUser_id()+"-"+String.valueOf(new Date().getTime())+"-"+voiceEntity.getUrl().substring(voiceEntity.getUrl().lastIndexOf("/")+1);
			
			String bucket = "nineton";
			if (voiceEntity.getUrl().startsWith("file")) {
				bucket = "record-tool";
			}
			basePath = basePath.replace("__BUCKET__", bucket);
			
//			System.out.println("start download file: "+filename);
//			logger.info("start download file: "+filename);
			String filePath = FileDownload.saveUrlAs(basePath+voiceEntity.getUrl(), tempDir, filename, "GET");
			logger.info("end download file:"+filePath);
			if("".equals(filePath)){
				voiceEntity.setRemark("下载失败");
				voiceEntity.setStatus(4);
				voiceDao.updateStatusById(voiceEntity.getId(), voiceEntity);
			}
			
			//标记下载完成
			voiceEntity.setRemark("下载完成");
			voiceEntity.setStatus(2);
			voiceDao.updateStatusById(voiceEntity.getId(), voiceEntity);
		}
	}
}

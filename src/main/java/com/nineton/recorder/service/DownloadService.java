package com.nineton.recorder.service;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.nineton.recorder.dao.VoiceToWordsMongoDao;
import com.nineton.recorder.entity.VoiceToWordsEntity;
import com.nineton.recorder.util.FileDownload;

import redis.clients.jedis.Jedis;

@PropertySource("application.properties")
@Service
public class DownloadService{

	private Logger logger = null;
	
	private String basePath = "http://__BUCKET__.oss-cn-hangzhou.aliyuncs.com/";

	private String tempDir = "tmp/download";
	
	private String downloadQueue = "REORDER:voice_to_words_down_queue";
	private String uploadQueue = "REORDER:voice_to_words_upload_queue";
	
	@Autowired
	Jedis redis;
	
	@Autowired
	VoiceToWordsMongoDao voiceDao;
	
	public DownloadService(){
		logger = Logger.getLogger(DownloadService.class);
	}
	
	
	/**
	 * 完成下载工作后，把本地路径写入数据库
	 * @return
	 */
	public Boolean download(){
		logger.info(".........start download.........");
		//获取待下载的数据
		String log = redis.rpop(this.downloadQueue);
		if (log == null) {
			return false;
		}
		
		VoiceToWordsEntity voiceEntity = JSON.parseObject(log, VoiceToWordsEntity.class);
		if(voiceEntity == null){
			logger.error("download - parse error:"+log);
			return false;
		}
		
		//路径为空
		if("".equals(voiceEntity.getUrl())){
			logger.error("文件路径为空:"+log);
			//标记为异常
			voiceEntity.setRemark("文件路径为空");
			voiceEntity.setStatus(4);
			voiceDao.updateStatusById(voiceEntity.getId(), voiceEntity);
			return false;
		}
		
		String filename = voiceEntity.getUser_id()+"-"+String.valueOf(new Date().getTime())+"-"+voiceEntity.getUrl().substring(voiceEntity.getUrl().lastIndexOf("/")+1);
		
		String bucket = "nineton";
		if (voiceEntity.getUrl().startsWith("file")) {
			bucket = "record-tool";
		}
		String basePathTmp = basePath.replace("__BUCKET__", bucket);
		
//			System.out.println("start download file: "+filename);
//			logger.info("start download file: "+filename);
		String filePath = FileDownload.saveUrlAs(basePathTmp+voiceEntity.getUrl(), tempDir, filename, "GET");
//			logger.info("end download file:"+filePath);
		if("".equals(filePath)){
			logger.error("下载失败:"+log);
			voiceEntity.setStatus(4);
			voiceEntity.setRemark("下载失败");
			voiceDao.updateStatusById(voiceEntity.getId(), voiceEntity);
			return false;
		}
		
		
		//标记下载完成
		voiceEntity.setStatus(2);
		voiceEntity.setRemark("下载完成");
		voiceEntity.setLocal_path(filePath);
		long flag = voiceDao.updateLocalPath(voiceEntity.getId(), voiceEntity);
		redis.lpush(uploadQueue, JSON.toJSONString(voiceEntity));
		return true;
	}
}

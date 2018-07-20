package com.nineton.recorder.service;

import java.io.File;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.iflytek.msp.cpdb.lfasr.client.LfasrClientImp;
import com.iflytek.msp.cpdb.lfasr.exception.LfasrException;
import com.iflytek.msp.cpdb.lfasr.model.LfasrType;
import com.iflytek.msp.cpdb.lfasr.model.Message;
import com.nineton.recorder.dao.VoiceToWordsMongoDao;
import com.nineton.recorder.entity.VoiceToWordsEntity;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
@PropertySource("application.properties")
public class UploadService {
	private LfasrClientImp lc = null; 
	
	// 首次默认查询间隔时间
	private int firstIntervalTime = 30;

	private LfasrType type = LfasrType.LFASR_STANDARD_RECORDED_AUDIO;
	
	private HashMap<String, String> params = null;
	
	private Logger logger = null;
	
	private String uploadQueue = "REORDER:voice_to_words_upload_queue";
	
	@Autowired
	JedisPool jedisPoll;
	
	@Autowired
	VoiceToWordsMongoDao voiceDao;
	
	/**
	 * 根据录音时长获取初始查询时间
	 * @param duration  录音时长
	 * @return 单位：秒
	 */
	
	public int getNextQueryTime(int duration) {
		//5分钟
		int queryTimeInterval = 300;
		if (duration <= 300) {
			queryTimeInterval = 300;//5分钟
		} else if (duration <= 1800) {
			//半小时
			queryTimeInterval = 600;//10分钟
		} else if (duration < 3600) {
			//1小时
			queryTimeInterval = 1200;//20分钟
		} else if (duration < 7200) {
			//2小时
			queryTimeInterval = 1800;//30分钟
		} else {
			//3小时
			queryTimeInterval = 1800;//40分钟
		}
		
		return queryTimeInterval;
	}
	
	public Boolean upload(){
		
		params = new HashMap<String, String>();
		params.put("has_participle", "false");
		
		logger = Logger.getLogger(UploadService.class);
		logger.info("upload start [1]");
		try {
			lc = LfasrClientImp.initLfasrClient();
		} catch (LfasrException e) {
			// 初始化异常，解析异常描述信息
			Message initMsg = JSON.parseObject(e.getMessage(), Message.class);
			logger.error("ecode=" + initMsg.getErr_no()+",failed=" + initMsg.getFailed());
			return false;
		}
		
		//获取已下载的文档
		String log = null;
		Jedis redis = jedisPoll.getResource();
		try {
			log = redis.rpop(uploadQueue);
		} catch (Exception e) {
			redis.close();
			logger.error("redis error: [rpop from uploadQueue]"+e.getMessage());
			return false;
		}
		
		VoiceToWordsEntity voiceEntity = null;
		if (log == null) {
			redis.close();
			return false;
		}
		logger.info("upload start [2]");
		voiceEntity = JSON.parseObject(log, VoiceToWordsEntity.class);
		if(voiceEntity == null){
			redis.close();
			logger.error("upload - json parse error:"+log);
			return false;
		}
		
		// 获取上传任务ID
		String local_file = voiceEntity.getLocal_path();
		String task_id = "";
		
		File file = new File(local_file);
		if (file.exists() == false) {
			logger.error("upload - local file error:"+log);
			voiceEntity.setStatus(4);
			voiceEntity.setRemark("文件下载至本地出错");
			voiceDao.updateStatusById(voiceEntity.getId(), voiceEntity);
			redis.close();
			return false;
		}
		
		try {
			//正在上傳
			voiceEntity.setStatus(2);
			voiceEntity.setRemark("准备上传");
			voiceDao.updateStatusById(voiceEntity.getId(), voiceEntity);
			// 上传音频文件
			Message uploadMsg = lc.lfasrUpload(local_file, type, params);
			
			// 判断返回值
			int ok = uploadMsg.getOk();
			if (ok == 0) {
				// 创建任务成功
				task_id = uploadMsg.getData();
//				logger.info("task_id=" + task_id);
				
				int next_query_time = getNextQueryTime(voiceEntity.getDuration());
				voiceEntity.setStatus(3);
				voiceEntity.setRemark("上传成功");
				voiceEntity.setNext_query_time(new Date(System.currentTimeMillis() + next_query_time*1000));
				voiceEntity.setTask_id(task_id);
				voiceDao.updateTaskId(voiceEntity.getId(), voiceEntity);
				
				
				//删除文件
				if (local_file != "" && local_file != null) {
					if(file != null && file.exists()){
						file.delete();
					}
				}
			} else {
				
				// 创建任务失败-服务端异常
				logger.error("upload - failed: ecode=" + uploadMsg.getErr_no() + ",failed=" + uploadMsg.getFailed());
				voiceEntity.setStatus(4);
				voiceEntity.setRemark("上传失败"+",ecode=" + uploadMsg.getErr_no()+",failed=" + uploadMsg.getFailed());
				voiceDao.updateStatusById(voiceEntity.getId(), voiceEntity);
				return false;
			}
		} catch (LfasrException e) {
			// 上传异常，解析异常描述信息
			Message uploadMsg = JSON.parseObject(e.getMessage(), Message.class);
			logger.error("upload - exception: ecode=" + uploadMsg.getErr_no() + ",failed=" + uploadMsg.getFailed());
			
			voiceEntity.setStatus(4);
			voiceEntity.setRemark("上传异常"+",ecode=" + uploadMsg.getErr_no()+",failed=" + uploadMsg.getFailed());
			voiceDao.updateStatusById(voiceEntity.getId(), voiceEntity);
			return false;
		} finally {
			redis.close();
		}
		lc = null;
		return true;
	}
}

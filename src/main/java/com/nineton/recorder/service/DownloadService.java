package com.nineton.recorder.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.nineton.recorder.bean.RequestRootBean;
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
		String oldname = voiceEntity.getUrl().substring(voiceEntity.getUrl().lastIndexOf("/")+1);
		if (oldname.endsWith(".aac")) {//aac文件可以通过将后缀修改为m4a后进行上传
			oldname = oldname.replace(".aac", ".m4a");
		}
		
		String filename = voiceEntity.getUser_id()+"-"+String.valueOf(new Date().getTime())+"-"+oldname;
		
		String bucket = "nineton";
		if (voiceEntity.getUrl().startsWith("file")) {
			bucket = "record-tool";
		}
		String basePathTmp = basePath.replace("__BUCKET__", bucket);
		
//			System.out.println("start download file: "+filename);
//			logger.info("start download file: "+filename);
		String remoteUrl = basePathTmp+voiceEntity.getUrl();
		if (voiceEntity.getUrl().startsWith("file")) {
			remoteUrl = getSingUrl(voiceEntity.getC_id());
			if (remoteUrl == null || "".equals(remoteUrl)) {
				logger.error("下载失败，签名链接获取失败:"+log);
				voiceEntity.setStatus(4);
				voiceEntity.setRemark("下载失败，签名链接获取失败");
				voiceDao.updateStatusById(voiceEntity.getId(), voiceEntity);
				return false;
			}
		}
		
		String filePath = FileDownload.saveUrlAs(remoteUrl, tempDir, filename, "GET");
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
	
	public String getSingUrl(long fileId) {
		CloseableHttpResponse httpResponse = null;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost("http://recorder.nineton.cn/v3.record/getSignUrl");
		String result = "";
		ArrayList param = new ArrayList();
		param.add(new BasicNameValuePair("c_id", String.valueOf(fileId)));
		param.add(new BasicNameValuePair("app_id", "10000"));
		param.add(new BasicNameValuePair("platform", "android"));
		param.add(new BasicNameValuePair("version", "1000.1000.1000"));
		param.add(new BasicNameValuePair("channel", "test"));
		param.add(new BasicNameValuePair("imei", "00000000000"));
		param.add(new BasicNameValuePair("timestamp", String.valueOf(System.currentTimeMillis())));
		param.add(new BasicNameValuePair("postmantest", "3A362DA87FA3E89A95BE27A4624D6279"));
		param.add(new BasicNameValuePair("uuid", "3A362DA87FA3E89A95BE27A4624D6279"));
		param.add(new BasicNameValuePair("token", "c8e768959c34f6a9048b5b9e0674127fc7a37379"));
		
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(param, "utf-8"));
			httpResponse = httpClient.execute(httpPost);
			result = EntityUtils.toString(httpResponse.getEntity());
			
			RequestRootBean result2 = JSON.parseObject(result, RequestRootBean.class);
			result = result2.getResult().getShare_url();
			
		} catch (Exception e) {
			
		} finally {
			try {
				if (httpResponse != null) {
					httpResponse.close();
				}
			} catch (IOException e2) {
				
			}

			try {
				if (httpClient != null) {
					httpClient.close();
				}
			} catch (IOException e3) {
				
			}

		}

		return result;
	}
}

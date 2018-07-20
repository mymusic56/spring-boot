package com.nineton.recorder.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.iflytek.msp.cpdb.lfasr.client.LfasrClientImp;
import com.iflytek.msp.cpdb.lfasr.exception.LfasrException;
import com.iflytek.msp.cpdb.lfasr.model.Message;
import com.iflytek.msp.cpdb.lfasr.model.ProgressStatus;
import com.nineton.recorder.dao.VoiceToWordsMongoDao;
import com.nineton.recorder.entity.Content;
import com.nineton.recorder.entity.VoiceToWordsEntity;
import com.nineton.recorder.entity.WordsResultList;

@Service
public class ResultService {
	private LfasrClientImp lc = null;

	private Logger logger = null;

	private int limitQueryTime = 9;

	private HashMap<Integer, Integer> intervalTimes = null;

	@Autowired
	VoiceToWordsMongoDao voiceDao;
	
	public ResultService() {
		// 查询时间间隔
		intervalTimes = new HashMap<Integer, Integer>();
		intervalTimes.put(0, 60 * 10);
		intervalTimes.put(1, 60 * 10);
		intervalTimes.put(2, 60 * 30);
		intervalTimes.put(3, 60 * 60);
		intervalTimes.put(4, 60 * 2 *60);// 2小时
		intervalTimes.put(5, 60 * 6 * 60);// 6小时
		intervalTimes.put(6, 60 * 12 * 60);// 12小时
		intervalTimes.put(7, 60 * 24 * 60);// 12小时
		intervalTimes.put(8, 60 * 24 * 60);// 24小时

		logger = Logger.getLogger(ResultService.class);
	}

	public int getDateTime() {
		return (int) (new Date().getTime() / 1000);
	}

	public void getResult() {
		// 初始化LFASR实例
		try {
			lc = LfasrClientImp.initLfasrClient();
		} catch (LfasrException e) {
			// 初始化异常，解析异常描述信息
			Message initMsg = JSON.parseObject(e.getMessage(), Message.class);
			logger.error("result - ecode=" + initMsg.getErr_no() + ",failed=" + initMsg.getFailed());
			System.exit(0);
		}
		//查询下次查询时间小于当前时间的记录
		List<VoiceToWordsEntity> voiceList = voiceDao.getWaitQueryList(3, 1, 20);

		logger.info(Thread.currentThread().getName()+" 总数量"+voiceList.size());
		if (voiceList != null) {
			for(VoiceToWordsEntity item : voiceList) {
				getResult(item);
				//每次执行完睡一秒钟
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		voiceList = null;
		lc = null;
	}

	public Boolean getResult(VoiceToWordsEntity voiceEntity) {
		int times = getDateTime();
		String task_id = voiceEntity.getTask_id();
		logger.info("查询处理状态:"+voiceEntity.getId()+", 任务ID：" + voiceEntity.getTask_id());
		// 处理结果状态
		ProgressStatus progressStatus = null;
		// JSON数据解析结果
		HashMap<String, String> jsonParseRes = null;
		// 音频上传结果
		Message uploadMsg = null;
		// 进度查询
		Message progressMsg = null;
		// 处理结果
		Message resultMsg = null;
		// 是否转换成文字，0：未处理，1：已完成，
		// 2：正在上传第三方服务器，3：等待异步处理,
		// -1:上传第三方服务器失败， 4：异常（长时间未处理）,
		// 5:已下载至本地，6：已处理完成，等待查询结果
		try {
			// 获取处理进度
			progressMsg = lc.lfasrGetProgress(task_id);

			// 如果返回状态不等于0，则任务失败
			if (progressMsg.getOk() != 0) {
				logger.error("task was fail. task_id:" + task_id + "ecode=" + progressMsg.getErr_no() + "failed="
						+ progressMsg.getFailed());
				// 服务端处理异常-服务端内部有重试机制（不排查极端无法恢复的任务）
				// 客户端可根据实际情况选择：
				// 1. 客户端循环重试获取进度
				// 2. 退出程序，反馈问题

				// 标记失败
				voiceEntity.setStatus(4);
				voiceEntity.setRemark("Status Query: ecode=" + progressMsg.getErr_no() + "failed=" + progressMsg.getFailed());
				voiceDao.updateStatusById(voiceEntity.getId(), voiceEntity);
				
				return false;
			} else {
				progressStatus = JSON.parseObject(progressMsg.getData(), ProgressStatus.class);
				if (progressStatus.getStatus() == 9) {
					// 处理完成
//					logger.error("task was completed. task_id:" + task_id);
					// 准备获取结果

					// recordDao.updateUploadStatus(record.getMonId(), 6, "Status Query:
					// 数据已处理完成，等待写入数据库");
				} else {
					// 未处理完成
//					logger.info("task was incomplete. task_id:" + task_id + ", status:" + progressStatus.getDesc());

					// 更新下次处理时间
					// 超过查询次数， 或者没有配置查询时间间隔， 标记异常
					if (voiceEntity.getQuery_times() + 1 > limitQueryTime) {
						voiceEntity.setStatus(4);
						voiceEntity.setRemark("超过查询次数");
						voiceDao.updateNextQueryTime(voiceEntity.getId(), voiceEntity);
					} else if (!intervalTimes.containsKey(voiceEntity.getQuery_times() + 1)) {
						voiceEntity.setStatus(4);
						voiceEntity.setRemark("查询条件配置错误");
						voiceDao.updateNextQueryTime(voiceEntity.getId(), voiceEntity);
					} else {
						// 更新下次处理时间
						int tmpIntervalTime = intervalTimes.get(voiceEntity.getQuery_times() + 1);

						voiceEntity.setQuery_times(voiceEntity.getQuery_times() + 1);
						voiceEntity.setNext_query_time(new Date(System.currentTimeMillis() + 28800000 + tmpIntervalTime*1000));
						voiceEntity.setStatus(3);
						voiceDao.updateNextQueryTime(voiceEntity.getId(), voiceEntity);
					}
					
					progressMsg = null;
					progressStatus = null;
					return true;
				}
			}
		} catch (LfasrException e) {
			// 获取进度异常处理，根据返回信息排查问题后，再次进行获取
			progressMsg = JSON.parseObject(e.getMessage(), Message.class);
			logger.error("ecode=" + progressMsg.getErr_no() + ",failed=" + progressMsg.getFailed());
			
			voiceEntity.setStatus(4);
			voiceEntity.setRemark("Status Query: ecode=" + progressMsg.getErr_no() + ",failed=" + progressMsg.getFailed());
			voiceDao.updateStatusById(voiceEntity.getId(), voiceEntity);

			progressMsg = null;
			progressStatus = null;
			voiceEntity = null;
			return false;
		}

		/************************************************/

		/*
		 * 操作三：获取任务结果
		 */
		try {
//			logger.info("获取处理结果， 任务ID：" + task_id);
			resultMsg = lc.lfasrGetResult(task_id);
			// logger.info(resultMsg.getData());
			// 如果返回状态等于0，则任务处理成功
			if (resultMsg.getOk() == 0) {
				// 打印转写结果
				// 获取解析后的数据
				// jsonParseRes = parseJson(resultMsg.getData());

				// 把JSON数据转化为对象
				List<Content> list = new ArrayList<Content>();
				JSONArray array = JSON.parseArray(resultMsg.getData());

//				logger.info(resultMsg.getData());
				
				StringBuffer segementStrBuffer = new StringBuffer();
				StringBuffer originalStrBuffer = new StringBuffer();
				List<WordsResultList> wordsResultList = null;
				Content content = null;
				for (int i = 0; i < array.size(); i++) {
//					logger.info(array.get(i));
					content = JSON.parseObject(array.get(i).toString(), Content.class);
					wordsResultList = content.getWordsResultList();
					// 获取原始字符串
					originalStrBuffer.append(content.getOnebest());

					// 有分词，就獲取分詞后的字符串
					if (wordsResultList != null) {
						for (int j = 0; j < wordsResultList.size(); j++) {
							segementStrBuffer.append(wordsResultList.get(j).getWordsName());
							segementStrBuffer.append(" ");
						}
					}
				}
				// 保存解析后的数据
				voiceEntity.setStatus(1);
				voiceEntity.setRemark("转写成功");
				voiceEntity.setContent(originalStrBuffer.toString());
				voiceDao.updateContentWithFinished(voiceEntity.getId(), voiceEntity);
				
//				logger.info(voiceEntity.getContent());
				
				list = null;
				segementStrBuffer = null;
				originalStrBuffer = null;
				resultMsg = null;
				logger.info("结果处理完成， 任务ID：" + task_id);
				// System.out.println(resultMsg.getData());
			} else {
				// 转写失败，根据失败信息进行处理
				logger.error("ecode=" + resultMsg.getErr_no() + ",failed=" + resultMsg.getFailed());
				// 保存解析后的数据
				voiceEntity.setStatus(4);
				voiceEntity.setRemark("转写失败: "+"Result Query: ecode=" + resultMsg.getErr_no() + ",failed=" + resultMsg.getFailed());
			}
		} catch (LfasrException e) {
			// 获取结果异常处理，解析异常描述信息
			resultMsg = JSON.parseObject(e.getMessage(), Message.class);
			voiceEntity.setStatus(4);
			voiceEntity.setRemark("转写失败: "+"Result Query: ecode=" + resultMsg.getErr_no() + ",failed=" + resultMsg.getFailed());
			logger.error("ecode=" + resultMsg.getErr_no() + ",failed=" + resultMsg.getFailed());
		}
		resultMsg = null;
		jsonParseRes = null;
		voiceEntity = null;
		return true;
	}
}

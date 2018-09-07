package com.nineton.recorder.dao;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;
import com.nineton.recorder.entity.RecordLists;

import static com.mongodb.client.model.Updates.set;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;

@Service
public class RecordListMongoDao {
	
	@Autowired
	MongoDatabase db;
	
	private String collectionName = "rec_record_lists";

	/**
	 * 保存转换后的文字信息
	 * @return
	 */
	public boolean updateRecordTextInfo(String mId, int convertStatus, String originalStr, String segementStr){
		long count = 0;
		MongoCollection<Document> collection = db.getCollection(collectionName);
		
		count = collection.updateOne(
				eq("_id", new ObjectId(mId)), 
				combine(
						set("has_convert", convertStatus),
						set("content", originalStr),
						set("content_words", segementStr)
						)
				).getMatchedCount();
		System.out.println(count);
		
		if(count > 0){
			return true;
		}
		return false;
	}
	
	/**
	 * 保存下载路径
	 * @return
	 */
	public boolean updateRecordDownInfo(String mId, int convertStatus, String msg, String tmpPath){
		long count = 0;
		MongoCollection<Document> collection = db.getCollection(collectionName);
		
		count = collection.updateOne(
				eq("_id", new ObjectId(mId)), 
				combine(
						set("has_convert", convertStatus),
						set("content", msg),
						set("temp_path", tmpPath)
						)
				).getMatchedCount();
		
		System.out.println(count);
		
		if(count > 0){
			return true;
		}
		return false;
	}
	
	/**
	 * 更具上传第三方服务器状态来获取录音记录
	 * 
	 * @param has_convert
	 * @return RecordList rList
	 */
	public RecordLists getRecord(int has_convert){
		RecordLists rList = null;
		MongoCollection<Document> collection = db.getCollection(collectionName);
		Document doc = collection.find(
				new Document("enabled",1)
				.append("up_status", 1)
				.append("has_convert", has_convert)
				).sort(Sorts.descending("c_date")).first();
		if(doc != null){
			rList = new RecordLists();
			rList.setId(doc.getObjectId("_id").toString());
			rList.setFile_path(doc.getString("file_path"));
			rList.setTemp_path(doc.getString("temp_path"));
			rList.setUser_id(doc.getInteger("user_id"));
			rList.setHas_convert(doc.getInteger("has_convert"));
			rList.setFile_size(doc.getInteger("file_size"));
		}

		return rList;
	}

	/**
	 * 
	 * 更具上传第三方服务器状态来获取录音记录[查询录音文件的处理状态时使用]
	 * 
	 * 1、必须是当前时间大于待处理时间 2、查询次数和时间间隔限制
	 * 
	 * @param has_convert
	 * @return RecordList rList
	 */
	public RecordLists getRecord(int has_convert, int times) {
		RecordLists rList = null;
		MongoCollection<Document> collection = db.getCollection("rec_record_lists");
		Document doc = collection.find(
				new Document("enabled",1)
				.append("up_status", 1)
				.append("has_convert", has_convert)
				.append("next_query_time", new Document("$lte",times))
				).sort(Sorts.ascending("created")).first();
		
		if(doc != null){
			rList = new RecordLists();
			rList.setId(doc.getObjectId("_id").toString());
			rList.setFile_path(doc.getString("file_path"));
			rList.setTask_id(doc.getString("task_id"));
			rList.setHas_convert(doc.getInteger("has_convert"));
			rList.setFile_size(doc.getInteger("file_size"));
			rList.setQuery_times(doc.getInteger("query_times"));;
		}
		

		return rList;
	}

	public boolean updateContent(int userId, long c_id, int status, String content) {
		long count = 0;
		MongoCollection<Document> collection = db.getCollection(this.collectionName);
		
		count = collection.updateOne(
				new Document("user_id", userId).append("c_id", c_id), 
				combine(
						set("has_convert", status),
						set("content", content),
						set("words_num", content.length())
						)
				).getMatchedCount();
		
		if (count > 0) {
			return true;
		}
		return false;
	}
	
	public boolean updateUploadStatus(String mId, int status, String msg) {
		long count = 0;
		MongoCollection<Document> collection = db.getCollection("rec_record_lists");
		
		count = collection.updateOne(
				eq("_id", new ObjectId(mId)), 
				combine(
						set("has_convert", status),
						set("content", msg)
						)
				).getMatchedCount();
		
		if (count > 0) {
			return true;
		}
		return false;
	}

	/**
	 * 更新更新task_id
	 * 
	 * @param mId 	db _id
	 * @param status
	 * @param task_id
	 * @return
	 */
	public boolean updateUploadStatus(String mId, int status, String task_id,
			int next_query_time, String msg) {
		long count = 0;
		MongoCollection<Document> collection = db.getCollection("rec_record_lists");
		
		count = collection.updateOne(
				eq("_id", new ObjectId(mId)), 
				combine(
						set("has_convert", status), 
						set("task_id", task_id),
						set("next_query_time", next_query_time),
						set("content", msg)
						)
				).getMatchedCount();
		System.out.println(count);
		
		if (count > 0) {
			return true;
		}
		return false;
	}

	/**
	 * 更新录音转文字，下一次处理的时间
	 * 
	 * @param id
	 * @param times
	 * @return
	 */
	public boolean updateNextQueryTime(String mId, int times, int queryTimes) {
		long count = 0;
		MongoCollection<Document> collection = db.getCollection("rec_record_lists");
		
		count = collection.updateOne(
				eq("_id", new ObjectId(mId)), 
				combine(
						set("query_times", queryTimes+1),//查询次数加1
						set("next_query_time", times)
						)
				).getMatchedCount();
		System.out.println(count);
		
		if (count > 0) {
			return true;
		}
		return false;
	}

}

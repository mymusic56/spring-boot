package com.nineton.recorder.dao;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import com.nineton.recorder.entity.VoiceToWordsEntity;

import static com.mongodb.client.model.Updates.set;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;

@Service
public class VoiceToWordsMongoDao {

	@Autowired
	MongoDatabase db;

	private String collectionName = "rec_voice_to_words";

	/**
	 * 更新状态
	 * @param id
	 * @param voiceEntity
	 * @return
	 */
	public long updateStatusById(String id, VoiceToWordsEntity voiceEntity) {
		MongoCollection<Document> collection = db.getCollection(this.collectionName);
		UpdateResult result = collection.updateOne(
				eq("_id", new ObjectId(id)),
				combine(
						set("status", voiceEntity.getStatus()),
						set("remark", voiceEntity.getRemark()),
						set("modified", new Date(System.currentTimeMillis()+28800000))
			    )
		);
		return result.getModifiedCount();
	}
	
	/**
	 * 更新本地路径
	 * @param id
	 * @param voiceEntity
	 * @return
	 */
	public long updateLocalPath(String id, VoiceToWordsEntity voiceEntity) {
		MongoCollection<Document> collection = db.getCollection(this.collectionName);
		UpdateResult result = collection.updateOne(
				eq("_id", new ObjectId(id)),
				combine(
						set("status", voiceEntity.getStatus()),
						set("local_path", voiceEntity.getLocal_path()),
						set("remark", voiceEntity.getRemark()),
						set("modified", new Date(System.currentTimeMillis()+28800000))
			    )
		);
		return result.getModifiedCount();
	}
	
	/**
	 * 更新上传成功后的task_id
	 * @param id
	 * @param voiceEntity
	 * @return
	 */
	public long updateTaskId(String id, VoiceToWordsEntity voiceEntity) {
		MongoCollection<Document> collection = db.getCollection(this.collectionName);
		UpdateResult result = collection.updateOne(
				eq("_id", new ObjectId(id)),
				combine(
						set("status", voiceEntity.getStatus()),
						set("task_id", voiceEntity.getTask_id()),
						set("remark", voiceEntity.getRemark()),
						set("modified", new Date(System.currentTimeMillis()+28800000))
			    )
		);
		return result.getModifiedCount();
	}
	
	/**
	 * 根据状态列表获取录音转文字记录
	 * @param status
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public List<VoiceToWordsEntity> getListByStatus(int status, int page, int pageSize){
		MongoCollection<Document> collection = db.getCollection(this.collectionName);
		List list = new ArrayList<VoiceToWordsEntity>();
		
		FindIterable<Document> result = collection.find(eq("status", status)).skip((page-1)*pageSize).limit(pageSize);
		
		for (Document doc : result) {
			VoiceToWordsEntity entity = new VoiceToWordsEntity();
			entity.setC_id(doc.getLong("c_id"));
			entity.setId(doc.get("_id").toString());
			entity.setCreated(doc.getInteger("created"));
			entity.setUser_id(doc.getInteger("user_id"));
			entity.setTask_id(doc.getString("task_id"));
			entity.setRemark(doc.getString("remark"));
			entity.setContent(doc.getString("content"));
			entity.setStatus(doc.getInteger("status"));
			entity.setUrl(doc.getString("url"));
			entity.setLocal_path(doc.getString("local_path"));
			
			list.add(entity);
			System.out.println(doc.get("created"));
			System.out.println(doc.get("_id")+"--"+doc.get("content"));
		}
		
		return list;
	}
}

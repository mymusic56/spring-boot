package com.nineton.recorder.entity;

import java.util.Date;

public class VoiceToWordsEntity {

	private String id;
	private int user_id;
	private String task_id;
	private long c_id;
	private int duration;
	private String url;
	private String local_path;
	private String remark;
	private long created;
	private String content;
	private int status;
	
	private int query_times;
	
	private Date next_query_time;

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setTask_id(String task_id) {
		this.task_id = task_id;
	}

	public String getTask_id() {
		return task_id;
	}

	public void setC_id(long c_id) {
		this.c_id = c_id;
	}

	public long getC_id() {
		return c_id;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getDuration() {
		return duration;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getRemark() {
		return remark;
	}

	public void setCreated(long created) {
		this.created = created;
	}

	public long getCreated() {
		return created;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getStatus() {
		return status;
	}

	public String getLocal_path() {
		return local_path;
	}

	public void setLocal_path(String local_path) {
		this.local_path = local_path;
	}

	public int getQuery_times() {
		return query_times;
	}

	public void setQuery_times(int query_times) {
		this.query_times = query_times;
	}

	public Date getNext_query_time() {
		return next_query_time;
	}

	public void setNext_query_time(Date next_query_time) {
		this.next_query_time = next_query_time;
	}

}

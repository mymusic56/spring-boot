package com.nineton.recorder.entity;

import javax.persistence.Id;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "rec_record_lists")
public class RecordLists {
	
	@Id
	private String id;

	private long c_id;

	private int user_id;

	private String file_sign;

	private String file_name;

	private int file_size;

	private String file_path;
	
	private String temp_path;

	private int file_duration;

//	private List<String> tag_ids;

	private int voice_change;

	private String voice_change_url;

	private long c_modified;

	private String modified;

	private int action_type;

	private int item_status;

	private int file_upload;

	private int show_status;

	private String real_name;

	private String created;

	private long c_created;

	private int c_date;

	private int enabled;

	private int is_minus_volume;

	private int has_convert;

	private String task_id;

	private int next_query_time;

	private int query_times;

	private String content;

	private String content_words;

	private int up_status;

	private int has_backup;


	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public int getUser_id() {
		return this.user_id;
	}

	public void setFile_sign(String file_sign) {
		this.file_sign = file_sign;
	}

	public String getFile_sign() {
		return this.file_sign;
	}

	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}

	public String getFile_name() {
		return this.file_name;
	}

	public void setFile_size(int file_size) {
		this.file_size = file_size;
	}

	public int getFile_size() {
		return this.file_size;
	}

	public void setFile_path(String file_path) {
		this.file_path = file_path;
	}

	public String getFile_path() {
		return this.file_path;
	}

	public void setFile_duration(int file_duration) {
		this.file_duration = file_duration;
	}

	public int getFile_duration() {
		return this.file_duration;
	}

//	public void setString(List<String> tag_ids) {
//		this.tag_ids = tag_ids;
//	}
//
//	public List<String> getString() {
//		return this.tag_ids;
//	}

	public void setVoice_change(int voice_change) {
		this.voice_change = voice_change;
	}

	public int getVoice_change() {
		return this.voice_change;
	}

	public void setVoice_change_url(String voice_change_url) {
		this.voice_change_url = voice_change_url;
	}

	public String getVoice_change_url() {
		return this.voice_change_url;
	}

	public long getC_id() {
		return c_id;
	}

	public void setC_id(long c_id) {
		this.c_id = c_id;
	}

	public long getC_modified() {
		return c_modified;
	}

	public void setC_modified(long c_modified) {
		this.c_modified = c_modified;
	}

	public long getC_created() {
		return c_created;
	}

	public void setC_created(long c_created) {
		this.c_created = c_created;
	}

	public void setModified(String modified) {
		this.modified = modified;
	}

	public String getModified() {
		return this.modified;
	}

	public void setAction_type(int action_type) {
		this.action_type = action_type;
	}

	public int getAction_type() {
		return this.action_type;
	}

	public void setItem_status(int item_status) {
		this.item_status = item_status;
	}

	public int getItem_status() {
		return this.item_status;
	}

	public void setFile_upload(int file_upload) {
		this.file_upload = file_upload;
	}

	public int getFile_upload() {
		return this.file_upload;
	}

	public void setShow_status(int show_status) {
		this.show_status = show_status;
	}

	public int getShow_status() {
		return this.show_status;
	}

	public void setReal_name(String real_name) {
		this.real_name = real_name;
	}

	public String getReal_name() {
		return this.real_name;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public String getCreated() {
		return this.created;
	}

	public void setC_date(int c_date) {
		this.c_date = c_date;
	}

	public int getC_date() {
		return this.c_date;
	}

	public void setEnabled(int enabled) {
		this.enabled = enabled;
	}

	public int getEnabled() {
		return this.enabled;
	}

	public void setIs_minus_volume(int is_minus_volume) {
		this.is_minus_volume = is_minus_volume;
	}

	public int getIs_minus_volume() {
		return this.is_minus_volume;
	}

	public void setHas_convert(int has_convert) {
		this.has_convert = has_convert;
	}

	public int getHas_convert() {
		return this.has_convert;
	}

	public void setTask_id(String task_id) {
		this.task_id = task_id;
	}

	public String getTask_id() {
		return this.task_id;
	}

	public void setNext_query_time(int next_query_time) {
		this.next_query_time = next_query_time;
	}

	public int getNext_query_time() {
		return this.next_query_time;
	}

	public void setQuery_times(int query_times) {
		this.query_times = query_times;
	}

	public int getQuery_times() {
		return this.query_times;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent_words(String content_words) {
		this.content_words = content_words;
	}

	public String getContent_words() {
		return this.content_words;
	}

	public void setUp_status(int up_status) {
		this.up_status = up_status;
	}

	public int getUp_status() {
		return this.up_status;
	}

	public void setHas_backup(int has_backup) {
		this.has_backup = has_backup;
	}

	public int getHas_backup() {
		return this.has_backup;
	}

	public String getTemp_path() {
		return temp_path;
	}

	public void setTemp_path(String temp_path) {
		this.temp_path = temp_path;
	}
	
}

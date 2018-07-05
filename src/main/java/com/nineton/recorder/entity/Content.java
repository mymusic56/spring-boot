package com.nineton.recorder.entity;

import java.util.List;

public class Content {
	private String bg;
	private String ed;
	private String nc;
	private String onebest;
	private String si;
	private String speaker;
	private List<WordsResultList> wordsResultList;
	public String getBg() {
		return bg;
	}
	public void setBg(String bg) {
		this.bg = bg;
	}
	public String getEd() {
		return ed;
	}
	public void setEd(String ed) {
		this.ed = ed;
	}
	public String getNc() {
		return nc;
	}
	public void setNc(String nc) {
		this.nc = nc;
	}
	public String getOnebest() {
		return onebest;
	}
	public void setOnebest(String onebest) {
		this.onebest = onebest;
	}
	public String getSi() {
		return si;
	}
	public void setSi(String si) {
		this.si = si;
	}
	public String getSpeaker() {
		return speaker;
	}
	public void setSpeaker(String speaker) {
		this.speaker = speaker;
	}
	public List<WordsResultList> getWordsResultList() {
		return wordsResultList;
	}
	public void setWordsResultList(List<WordsResultList> worsResultList) {
		this.wordsResultList = worsResultList;
	}
	
}

package com.company.demo.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "yt_videos")
public class Video {

	@Id
	private String videoId;
	private String title;
	private String description;
	private String contentType;
	private String filePath;
	
//	@ManyToOne
//	private Course course;
	
	public String getVideoId() {
		return videoId;
	}
	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	@Override
	public String toString() {
		return "Video [videoId=" + videoId + ", title=" + title + ", description=" + description + ", contentType="
				+ contentType + ", filePath=" + filePath + "]";
	}
	
	
	
}

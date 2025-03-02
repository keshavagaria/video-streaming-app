package com.company.demo.service;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import com.company.demo.entity.Video;

public interface VideoService {

	
	Video saveVideo(Video video,MultipartFile file);
	Video getVideo(String videoId);
	Video getByTitle(String title);
	List<Video>getAllVideos();
	void processVideo(String videoId);
	void deleteVideo(String videoId);
}

package com.company.demo.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.company.demo.entity.Video;
import com.company.demo.payload.CustomMessage;
import com.company.demo.service.VideoService;

@RestController
@RequestMapping("api/v1/videos")
public class VideoController {

	@Autowired
	private VideoService videoService;
	
	@PostMapping
	public ResponseEntity<?> create(
				@RequestParam("file") MultipartFile file,
				@RequestParam("title") String title,
				@RequestParam("description") String description
			){
		
		Video video=new Video();
		video.setTitle(title);
		video.setDescription(description);
		video.setVideoId(UUID.randomUUID().toString());
		Video savedVideo = videoService.saveVideo(video, file);
		System.out.println(savedVideo);
		System.out.println("File name:"+file.getOriginalFilename());
		
		if(savedVideo!=null && !file.getOriginalFilename().isBlank()) {
			return ResponseEntity
					.status(HttpStatus.OK)
					.body(video);
		}else{
			return ResponseEntity
						.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body("VIdeo nOt uploaded");
			
		}
		                 
	}
}

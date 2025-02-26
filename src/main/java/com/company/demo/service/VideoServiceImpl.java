package com.company.demo.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import com.company.demo.entity.Video;
import com.company.demo.repository.VideoRepository;



@Service
public class VideoServiceImpl implements VideoService{

	@Value("${files.video}")
	String DIR;
	
	@Autowired
	private VideoRepository videoRepository;
	
	@PostConstruct
	public void init() {
		
		File file=new File(DIR);
		
		if(!file.exists()) {
			file.mkdir();
			System.out.println("Folder Created");
		}else {
			System.out.println("Folder already created");
		}
	}
	@Override
	public Video saveVideo(Video video, MultipartFile file) {
		// TODO Auto-generated method stub

		try {
			String filename = file.getOriginalFilename();
			String contentType = file.getContentType();

			InputStream inputStream = file.getInputStream();

			String cleanFileName = 	StringUtils.cleanPath(filename);
			String cleanFolder = 	StringUtils.cleanPath(DIR);
			
			Path path= Paths.get(cleanFolder,cleanFileName);
			System.out.println(path);
			System.out.println(DIR);
			System.out.println("The File name is:"+filename);
			
			Files.copy(inputStream, path,StandardCopyOption.REPLACE_EXISTING);
			
			video.setContentType(contentType);
			video.setFilePath(path.toString());
			
			
			
		} catch (IOException e) {}
		return videoRepository.save(video);
		
	}

	@Override
	public Video getVideo(String videoId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Video getByTitle(String title) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Video> getAllVideos() {
		// TODO Auto-generated method stub
		return null;
	}

}

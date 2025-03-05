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
	
	@Value("${files.video.hsl}")
	String HSL_DIR;
	
	@Autowired
	private VideoRepository videoRepository;
	
	@PostConstruct
	public void init() {
		
		File file=new File(DIR);
		try {
			Files.createDirectories(Paths.get(HSL_DIR));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
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
		Video savedVideo=null;
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
			
			savedVideo = videoRepository.save(video);
			processVideo(savedVideo.getVideoId());
			
		} catch (IOException e) {}
		return savedVideo;
		
	}
	
	@Override
	public void processVideo(String videoId){
		
		Video video = this.getVideo(videoId);
		System.out.println("The Video File Path :"+video.toString());
		String filePath =video.getFilePath();
		System.out.println(filePath);
		Path videoPath = Paths.get(filePath);
		
//		String output_360p=HSL_DIR+videoId+"/360p/";
//		String output_720p=HSL_DIR+videoId+"/720p/";
//		String output_1080p=HSL_DIR+videoId+"/1080p/";
		
		
		
		try {
//			Files.createDirectories(Paths.get(output_360p));
//			Files.createDirectories(Paths.get(output_720p));
//			Files.createDirectories(Paths.get(output_1080p));
			
			
			 Path outputPath = Paths.get(HSL_DIR, videoId);

	            Files.createDirectories(outputPath);
	            
	            System.out.println(videoPath+" "+outputPath);
	            System.out.println("Working Directory = " + System.getProperty("user.dir")+"\\"+outputPath); 
	            
	            String ffmpegCmd = String.format("chmod 755 ffmpeg -i \"%s\" -c:v h264 -flags +cgop -g 30 -hls_time 1 \"%s/out.m3u8\" ",
	            		videoPath,outputPath);
	            //videoPath,outputPath);
//			StringBuilder ffmpegCmd = new StringBuilder();
//			ffmpegCmd.append("ffmpeg -i")
//						.append(videoPath.toString())
//						.append(" -c:v libx264 -c:a aac")
//	                    .append(" ")
//	                    .append("-map 0:v -map 0:a -s:v:0 640x360 -b:v:0 800k ")
//	                    .append("-map 0:v -map 0:a -s:v:1 1280x720 -b:v:1 2800k ")
//	                    .append("-map 0:v -map 0:a -s:v:2 1920x1080 -b:v:2 5000k ")
//	                    .append("-var_stream_map \"v:0,a:0 v:1,a:0 v:2,a:0\" ")
//	                    .append("-master_pl_name ").append(HSL_DIR).append(videoId).append("/master.m3u8 ")
//	                    .append("-f hls -hls_time 10 -hls_list_size 0 ")
//	                    .append("-hls_segment_filename \"").append(HSL_DIR).append(videoId).append("/v%v/fileSequence%d.ts\" ")
//	                    .append("\"").append(HSL_DIR).append(videoId).append("/v%v/prog_index.m3u8\"");
//			
	            System.out.println("ffmpeg command :"+ffmpegCmd);
	            //file this command
//	            ProcessBuilder processBuilder = new ProcessBuilder("C:\\Users\\visha\\	",
//	            		"-c", ffmpegCmd);
	           
	            ProcessBuilder processBuilder = new ProcessBuilder();
	            
	            //processBuilder.command("cmd.exe","/c",ffmpegCmd);
	            processBuilder.command("sh","-c",ffmpegCmd);
	            
	            processBuilder.inheritIO();
	            Process process = processBuilder.start();
	            
	            int exit = process.waitFor();
	            if (exit != 0) {
	                throw new RuntimeException("video processing failed!!");
	            }

	            //return videoId;
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Video Processing Failed!!!");
		}catch (InterruptedException e) {
            throw new RuntimeException(e);
		}
		
	}

	@Override
	public Video getVideo(String videoId) {
		
		return videoRepository.getById(videoId);
	}

	@Override
	public Video getByTitle(String title) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Video> getAllVideos() {
		
		return videoRepository.findAll();
	}
	@Override
	public void deleteVideo(String videoId) {
		// TODO Auto-generated method stub
		videoRepository.deleteById(videoId);
		
	}

}

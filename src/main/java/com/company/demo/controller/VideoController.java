package com.company.demo.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.company.demo.entity.Video;
import com.company.demo.service.AppConstants;
import com.company.demo.service.VideoService;

@RestController
@RequestMapping("api/v1/videos")
@CrossOrigin("*")
public class VideoController {

	
	@Value("${files.video}")
	String DIR;
	
	@Value("${files.video.hsl}")
	String HSL_DIR;
	
	
	@Autowired
	private VideoService videoService;
	
	@PostMapping("")
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
	
	@RequestMapping(value="/stream/{videoId}",method = RequestMethod.GET)
	public ResponseEntity<String> stream(@PathVariable String videoId){
		Video video = videoService.getVideo(videoId);
		
		
		String contentType = video.getContentType();
		String filePath = video.getFilePath();
		Resource resource=new FileSystemResource(filePath);
		System.out.println(contentType+" "+filePath+" "+resource);
		
		if(contentType==null) {
			contentType = "application/octet-stream";
		}
		return ResponseEntity
				.ok()
				.contentType(MediaType.parseMediaType(contentType))
				.body(filePath);
	}
	
	@RequestMapping(value = "/stream/range/{videoId}",method = RequestMethod.GET)
	public ResponseEntity<Resource> streamVideoRange(@PathVariable String videoId,
													 @RequestHeader(value = "range",required = false)String range) throws IOException{
		Video video = videoService.getVideo(videoId);
		
		System.out.println("Range :"+range);
		String contentType = video.getContentType();
		Path path = Paths.get(video.getFilePath());
		
		Resource resource=new FileSystemResource(path);
		
		if(contentType==null) {
			contentType = "application/octet-stream";
		}
		
		//file length
		long fileLength = path.toFile().length();
		
		if(range==null) {
			
			return ResponseEntity
				.ok()
				.contentType(MediaType.parseMediaType(contentType))
				.body(resource);
		}
		
		long rangeStart;
		long rangeEnd;
		
		String[] ranges = range.replace("bytes=", "").split("-");
		rangeStart = Long.parseLong(ranges[0]);
		
		rangeEnd = rangeStart+AppConstants.CHUNK_SIZE-1;
		
		if(rangeEnd >= fileLength) {
			rangeEnd = fileLength-1;
		}
		
		
		System.out.println("StartRange : "+rangeStart);
		System.out.println("End Range : "+rangeEnd);
		InputStream inputStream;
		
		try {
			inputStream =   Files.newInputStream(path);
			inputStream.skip(rangeStart);
			
		} catch (IOException e) {
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.build();
		}
		long contentLength = rangeEnd - rangeStart + 1;
		
		byte[] data=new byte[(int)contentLength];
		int read = inputStream.read(data,0,data.length);
		System.out.println("Read(Number of Bytes :"+read);
		
		HttpHeaders headers=new HttpHeaders();
		headers.add("Content-Range", "bytes "+rangeStart+"-"+rangeEnd+"/"+fileLength);
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Pragma", "no-cache");
		headers.add("Expires", "0");
		headers.add("X-Content-Type-Options", "nosniff");
		headers.setContentLength(contentLength);
		
		return ResponseEntity
				.status(HttpStatus.PARTIAL_CONTENT)
				.headers(headers)
				.contentType(MediaType.parseMediaType(contentType))
				.body(new ByteArrayResource(data));
				
			
	}
	
//	@GetMapping("/process/{videoId}")
//	public String processVideo(@PathVariable("videoId")String videoId){
//		return videoService.processVideo(videoId);
//		
//	}
	

	@GetMapping("/{videoId}/master.m3u8")
	public ResponseEntity<Resource> serveMasterFile(@PathVariable String  videoId){
		
	Path path = 	Paths.get(HSL_DIR,videoId,"master.m3u8");
	System.out.println(path);
		
	if(!Files.exists(path)) {
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
	
	Resource resource=new FileSystemResource(path);
		return ResponseEntity
				.ok()
				.header(HttpHeaders.CONTENT_TYPE,"application/vnd.apple.mpegurl")
				.body(resource);
	}
	
	
	@GetMapping("/{videoId}/{segment}.ts")
	public ResponseEntity<Resource> serveSegments(
					@PathVariable String  videoId,
					@PathVariable String segment){
		
	     Path path = Paths.get(HSL_DIR,videoId,segment+".ts");
	     if(!Files.exists(path)) {
	    	 
	    	 return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	     }
	     
	     Resource resource=new FileSystemResource(path);
			return ResponseEntity
					.ok()
					.header(HttpHeaders.CONTENT_TYPE,"video/mp2t")
					.body(resource);
		
	}
	
	@GetMapping("")
	public List<Video> getAllVideos(){
		return videoService.getAllVideos();
		
	}
	
	
	@DeleteMapping("/{videoId}")
	public void deleteVideo(@PathVariable String videoId){
		 videoService.deleteVideo(videoId);
		
	}
	
	
}

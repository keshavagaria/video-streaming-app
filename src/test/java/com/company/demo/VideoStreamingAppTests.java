package com.company.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.company.demo.service.VideoService;

@SpringBootTest
class VideoStreamingAppTests {

	@Autowired
	VideoService videoService;
	
	@Test
	void contextLoads() {
		
		
		videoService.processVideo("596527a9-1404-4a9d-a480-3b2836de2027");
	}

}

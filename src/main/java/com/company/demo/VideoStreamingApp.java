package com.company.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VideoStreamingApp	
{
    public static void main( String[] args )
    {
    	SpringApplication.run(VideoStreamingApp.class, args);
    }
    
//    @Override
//	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
//		// TODO Auto-generated method stub
//		return builder.sources(VideoStreamingApp.class);
//	}
}

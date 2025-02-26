package com.company.demo.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.company.demo.entity.Video;

@Repository
public interface VideoRepository extends JpaRepository<Video, String>{
	
	Optional<Video> findByTitle(String title);

}

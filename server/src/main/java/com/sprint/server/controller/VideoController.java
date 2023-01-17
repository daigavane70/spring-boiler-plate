package com.sprint.server.controller;

import com.sprint.common.response.HttpApiResponse;
import com.sprint.common.response.HttpErrorResponse;
import com.sprint.repository.entity.Video;
import com.sprint.repository.repositories.VideoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/video")
public class VideoController {
    @Autowired
    VideoRepository videoRepository;

    @GetMapping
    HttpApiResponse getAllVideos() {
        List<Video> videos = videoRepository.findAll();
        return new HttpApiResponse(videos);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    HttpApiResponse createVideo(@RequestBody Video video) {
        try {
            log.info("[videoController : createVideo] creating video for payload: {}", video);
            Video newVideo = videoRepository.save(video);
            return new HttpApiResponse(newVideo);
        } catch (Exception e) {
            return new HttpApiResponse(false, null, new HttpErrorResponse(1001, e.getMessage()));
        }
    }
}

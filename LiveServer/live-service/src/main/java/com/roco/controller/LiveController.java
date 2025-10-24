package com.roco.controller;

import com.roco.entity.LiveRequest;
import com.roco.entity.LiveStream;
import com.roco.service.LiveStreamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/live")
@CrossOrigin
public class LiveController {

    @Autowired
    private LiveStreamService liveStreamService;

    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> createLiveStream(
            @RequestBody LiveRequest liveRequest,
            HttpServletRequest request) {


        Integer userId = (Integer) request.getSession().getAttribute("userId");
        if (userId == null) {
            userId = 1;
        }

        try {
            LiveStream liveStream = liveStreamService.createLiveStream(userId, liveRequest.getTitle());

            Map<String, Object> response = new HashMap<>();
            response.put("code", 0);

            Map<String, Object> data = new HashMap<>();
            data.put("stream_key", liveStream.getStreamKey());
            data.put("webrtc_url", liveStreamService.generateWebRTCUrl(liveStream.getStreamKey()));
            data.put("rtmp_url", liveStreamService.generateRTMPUrl(liveStream.getStreamKey()));

            response.put("data", data);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", -1);
            errorResponse.put("msg", "Failed to start live stream");
            return ResponseEntity.ok(errorResponse);
        }
    }

    @PostMapping("/stop/{streamKey}")
    public ResponseEntity<Map<String, Object>> stopLiveStream(@PathVariable String streamKey) {
        boolean success = liveStreamService.stopStream(streamKey);

        Map<String, Object> response = new HashMap<>();
        if (success) {
            response.put("code", 0);
            response.put("msg", "Live stream stopped successfully");
        } else {
            response.put("code", -1);
            response.put("msg", "Failed to stop live stream, stream not found");
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getActiveStreams() {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("data", liveStreamService.getActiveStreams());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/info/{streamKey}")
    public ResponseEntity<Map<String, Object>> getStreamInfo(
            @PathVariable String streamKey) {

        Optional<LiveStream> optional = liveStreamService.findByStreamKey(streamKey);

        Map<String, Object> response = new HashMap<>();
        if (optional.isPresent()) {
            response.put("code", 0);
            response.put("data", optional.get());
        } else {
            response.put("code", -1);
            response.put("msg", "Live stream not found");
        }

        return ResponseEntity.ok(response);
    }
}
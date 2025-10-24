package com.roco.controller;


import com.roco.entity.LiveStreamStatus;
import com.roco.entity.SrsCallbackRequest;
import com.roco.service.LiveStreamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/srs-callback")
@CrossOrigin
public class SrsCallbackController {

    @Autowired
    private LiveStreamService liveStreamService;

    @PostMapping("/on-publish")
    public ResponseEntity<Map<String, Object>> onPublish(@RequestBody SrsCallbackRequest callbackRequest) {
        String streamKey = callbackRequest.getStream();

        Optional<com.roco.entity.LiveStream> optional = liveStreamService.findByStreamKey(streamKey);

        Map<String, Object> response = new HashMap<>();

        if (optional.isPresent()) {
            com.roco.entity.LiveStream liveStream = optional.get();

            liveStreamService.updateStreamStatus(streamKey, LiveStreamStatus.LIVE.getCode());

            response.put("code", 0);
            response.put("msg", "Success");
            return ResponseEntity.ok(response);
        } else {
            response.put("code", 1);
            response.put("msg", "Stream key invalid");
            return ResponseEntity.status(403).body(response);
        }
    }

    @PostMapping("/on-unpublish")
    public ResponseEntity<Map<String, Object>> onUnpublish(@RequestBody SrsCallbackRequest callbackRequest) {
        String streamKey = callbackRequest.getStream();

        liveStreamService.updateStreamStatus(streamKey, LiveStreamStatus.STOPPED.getCode());

        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("msg", "Success");

        return ResponseEntity.ok(response);
    }
}
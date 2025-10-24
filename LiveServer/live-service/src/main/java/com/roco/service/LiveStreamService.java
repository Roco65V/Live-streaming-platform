package com.roco.service;


import com.roco.entity.LiveStream;

import java.util.List;
import java.util.Optional;

public interface LiveStreamService {
    LiveStream createLiveStream(Integer userId, String title);
    Optional<LiveStream> findByStreamKey(String streamKey);
    LiveStream updateStreamStatus(String streamKey, Integer status);
    List<LiveStream> getActiveStreams();
    List<LiveStream> getUserStreams(Integer userId);
    boolean stopStream(String streamKey);
    
    String generateWebRTCUrl(String streamKey);
    String generateRTMPUrl(String streamKey);
}
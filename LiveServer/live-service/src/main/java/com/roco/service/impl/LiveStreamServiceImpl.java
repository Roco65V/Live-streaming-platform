package com.roco.service.impl;


import com.roco.entity.LiveStream;
import com.roco.entity.LiveStreamRepository;
import com.roco.entity.LiveStreamStatus;
import com.roco.service.LiveStreamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LiveStreamServiceImpl implements LiveStreamService {

    @Autowired
    private LiveStreamRepository liveStreamRepository;

    @Value("${srs.server.ip:your-srs-server-ip}")
    private String srsServerIp;

    @Value("${srs.app.name:live}")
    private String appName;

    @Override
    public LiveStream createLiveStream(Integer userId, String title) {
        String streamKey = generateStreamKey();

        LiveStream liveStream = new LiveStream();
        liveStream.setUserId(userId);
        liveStream.setTitle(title);
        liveStream.setStreamKey(streamKey);
        liveStream.setStatus(LiveStreamStatus.CREATED.getCode());
        liveStream.setCreatedAt(new Date());

        return liveStreamRepository.save(liveStream);
    }

    @Override
    public Optional<LiveStream> findByStreamKey(String streamKey) {
        return liveStreamRepository.findByStreamKey(streamKey);
    }

    @Override
    public LiveStream updateStreamStatus(String streamKey, Integer status) {
        Optional<LiveStream> optional = liveStreamRepository.findByStreamKey(streamKey);
        if (optional.isPresent()) {
            LiveStream stream = optional.get();
            stream.setStatus(status);

            if (status.equals(LiveStreamStatus.LIVE.getCode())) {
                stream.setStartTime(new Date());
            } else if (status.equals(LiveStreamStatus.STOPPED.getCode())) {
                stream.setEndTime(new Date());
            }

            return liveStreamRepository.save(stream);
        }
        return null;
    }

    @Override
    public List<LiveStream> getActiveStreams() {
        return liveStreamRepository.findByStatus(LiveStreamStatus.LIVE.getCode());
    }

    @Override
    public List<LiveStream> getUserStreams(Integer userId) {
        return liveStreamRepository.findByUserId(userId);
    }

    @Override
    public boolean stopStream(String streamKey) {
        Optional<LiveStream> optional = liveStreamRepository.findByStreamKey(streamKey);
        if (optional.isPresent()) {
            LiveStream stream = optional.get();
            stream.setStatus(LiveStreamStatus.STOPPED.getCode());
            stream.setEndTime(new Date());
            liveStreamRepository.save(stream);
            return true;
        }
        return false;
    }

    @Override
    public String generateWebRTCUrl(String streamKey) {
        return String.format("webrtc://%s/%s/%s", srsServerIp, appName, streamKey);
    }

    @Override
    public String generateRTMPUrl(String streamKey) {
        return String.format("rtmp://%s/%s/%s", srsServerIp, appName, streamKey);
    }

    private String generateStreamKey() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}
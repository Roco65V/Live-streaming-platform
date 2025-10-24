# Live Streaming Platform Setup Guide

## Overview

This guide will help you set up a live streaming platform using LiveServer backend and SRS streaming server.

## Prerequisites

- Docker and Docker Compose installed
- Basic knowledge of terminal/command line
- Access to a server or domain for SRS streaming

## Step-by-Step Setup

### 1. Database Setup

```bash
# Import the database schema
mysql -u your_username -p your_database < live_streams.sql
```



### 2. LiveServer Backend Configuration

#### Update Application Configuration

Edit `/src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://your-database-host:3306/your_database_name
    username: your_database_username
    password: your_database_password

srs:
  server: your-srs-server-domain.com
```



#### Update Service Implementation

Edit `/src/main/java/com/roco/service/impl/LiveStreamServiceImpl.java`:

```java
@Value("${srs.server.ip:your-actual-srs-server-ip}")
private String srsServerIp;
```



### 3. SRS Server Configuration

#### Update SRS Configuration

Edit `srs.conf` on your SRS server:

```json
http_hooks {
    enabled on;
    on_publish http://your-liveserver-domain.com/api/stream/on-publish;
    on_unpublish http://your-liveserver-domain.com/api/stream/on-unpublish;
}
```



**Note:** If running locally, use a tunneling service like ngrok for external access.

### 4. Deployment

#### Start Services with Docker

```bash
# Navigate to project directory and start services
docker-compose up -d
```

### 5. Frontend Configuration

#### For UNI-APP 

1. Open `/pages/live` and `/pages/streamer`
2. Replace all instances of `http://localhost:9001/api/` with your LiveServer backend domain and port
3. In `/pages/live`, update the video source:

#### For Vue Web

1. Import the provided pages into your Vue project
2. Replace all API endpoints with your LiveServer backend domain and port
3. Run the project

```vue
<video :src="'http://your-srs-domain.com/live/' + stream.streamKey + '.m3u8'"></video>
```



## Testing the Setup

### OBS Streaming Configuration

1. Open OBS Studio
2. Go to Settings → Stream
3. Set Service to "Custom"
4. Server: `rtmp://localhost/live/stream_key`
5. Stream Key: `your-stream-key`
6. Click "OK" and start streaming

## Verification Steps

1. **Backend Check**: Ensure LiveServer is running and accessible
2. **Database Connection**: Verify database connectivity
3. **SRS Server**: Confirm SRS server is operational
4. **Frontend**: Test both streamer and viewer interfaces
5. **End-to-End**: Start a stream with OBS and verify it appears in the viewer

## Troubleshooting

### Common Issues

- **Database Connection Failed**: Check credentials and network connectivity
- **SRS Hooks Not Working**: Verify endpoint URLs and firewall settings
- **Stream Not Playing**: Confirm SRS domain and stream key are correct
- **CORS Errors**: Ensure proper CORS configuration in backend

### SRS-SERVER

```json
    ports:
      - "1935:1935"       # RTMP
      - "1985:1985"       # HTTP API
      - "9000:9000/udp"   # RTC
      - "9090:9090"       # HTTP Server (播放页面)
      
```



## Support

For additional assistance, please refer to:

- Docker documentation
- SRS server documentation
- Project-specific README files
- https://ossrs.io/lts/zh-cn/
- https://github.com/ossrs/srs

------


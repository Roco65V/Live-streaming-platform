# Live Streaming System Architecture

A modern, scalable live streaming platform built with Spring Boot, SRS media server, and uni-app frontend.

## Architecture Overview



## System Components

### 1. Media Ingestion & Distribution
- **SRS (Simple RTMP Server)** - Core media processing engine
- **Protocols**: RTMP (ingestion), WEBRTC, HLS/HTTP-FLV (playback on srs-dashboard)
- **Key Functions**:
  - RTMP stream ingestion
  - Real-time transcoding and transmuxing
  - Automatic stream detection and pulling
  - Stream status continuously monitored and updated in MySQL

### 2. Backend Services
- **Spring Boot** - Business logic and API layer
- **Key Responsibilities**:
  - Stream management and key generation
  - User authentication & authorization
  - RESTful API endpoints
  - SRS integration via HTTP API
  - Stream status monitoring

### 3. Frontend Application
- **uni-app** - Cross-platform client
- **Supported Platforms**: Web, iOS, Android, Mini-Programs
- **Key Features**:
  - Live stream discovery and browsing
  - Video playback (HLS/HTTP-FLV)
  - Real-time interaction (chat, likes)
  - Direct media streaming from SRS

## Technology Stack

| Layer | Technology | Purpose |
|-------|------------|---------|
| Media Server | SRS | High-performance streaming engine |
| Backend | Spring Boot | Business logic & APIs |
| Frontend | uni-app | Cross-platform client |
| Ingestion | RTMP | Stable stream pushing |
| Playback | RTMP/WEBRTC | Universal & low-latency playback |

## Key Features

- **Separation of Concerns**: Media and control planes are decoupled
- **High Performance**: Optimized media handling via SRS
- **Cross-Platform**: Single codebase for multiple platforms
- **Scalable**: Independent scaling of media and business layers

## Data Flow

1. Fetch stream link
1. Streamer pushes stream to SRS
2. SRS processes and converts stream
3. uni-app fetches stream list from Spring Boot backend
4. Viewer plays stream directly from SRS server

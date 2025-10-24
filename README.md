# Live Streaming System Architecture

A modern, scalable live streaming platform built with Spring Boot, SRS media server, and uni-app frontend.

## Overview
Push

<img width="567" height="1175" alt="QQ20251024-130058" src="https://github.com/user-attachments/assets/8b3a20cf-e5e2-42da-828d-3da2eb65eee8" />
<img width="1876" height="1146" alt="QQ20251024-130617" src="https://github.com/user-attachments/assets/d2679b44-7f7e-4cf6-a13e-44a1d4a8141a" />
<img width="589" height="1184" alt="QQ20251024-130854" src="https://github.com/user-attachments/assets/0b70f62a-6d54-444f-a95a-df27c385c556" />
<img width="2008" height="1091" alt="QQ20251024-130844" src="https://github.com/user-attachments/assets/5a634bd6-0a9d-4d54-9df1-0e059d7eb6bb" />

Pull
<img width="358" height="585" alt="QQ20251024-131229" src="https://github.com/user-attachments/assets/afafae74-0420-4c38-8beb-42a1dd21bf74" />
<img width="369" height="595" alt="QQ20251024-131238" src="https://github.com/user-attachments/assets/8546dd45-4e15-462d-b54b-a36e7033ab45" />
<img width="452" height="600" alt="QQ20251024-131245" src="https://github.com/user-attachments/assets/b92445bc-5fda-4376-a859-fcee93f7b6cd" />


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

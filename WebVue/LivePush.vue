<template>
  <div class="live-push-container">
    <div class="header">
      <h1 class="title">Stream Manage</h1>
      <button class="refresh-btn" @click="refreshStreams">
        <i class="el-icon-refresh" style="margin-right: 6px;"></i>
        Refresh List
      </button>
    </div>
 
    <div class="content-wrapper">
      <div class="push-info-section card">
        <div class="section-header">
          <span class="section-title">Streamer Message</span>
        </div>
        
        <div class="instruction card">
          <div class="tip-icon">
            <i class="el-icon-info" style="font-size: 20px; color: #409eff;"></i>
          </div>
          <span class="tip-text">Use OBS or other streaming software to stream using the following information</span>
        </div>
 
        <div class="stream-info">
          <div class="info-item">
            <label class="label">Title</label>
            <input class="input title-input" v-model="title" placeholder="Please enter the live broadcast title" />
            <button class="generate-btn primary-btn" @click="generateStreamInfo">
              <i class="el-icon-paperplane" style="margin-right: 6px;"></i>
              GenerateStreamInfo
            </button>
          </div>
 
          <template v-if="webrtcUrl">
            <div class="info-group">
              <div class="info-item url-item">
                <label class="label">WebRTC URL:</label>
                <div class="input-group">
                  <input class="input url-input" v-model="webrtcUrl" readonly />
                  <button class="copy-btn" @click="copyText(webrtcUrl)">
                    <i class="el-icon-copy-document" style="margin-right: 6px;"></i>
                    <span class="copy-text">Copy</span>
                  </button>
                </div>
              </div>
              
              <div class="info-item url-item">
                <label class="label">RTMP URL:</label>
                <div class="input-group">
                  <input class="input url-input" v-model="rtmpUrl" readonly />
                  <button class="copy-btn" @click="copyText(rtmpUrl)">
                    <i class="el-icon-copy-document" style="margin-right: 6px;"></i>
                    <span class="copy-text">Copy</span>
                  </button>
                </div>
              </div>
              
              <div class="info-item url-item">
                <label class="label">Stream Key:</label>
                <div class="input-group">
                  <input class="input url-input" v-model="streamKey" readonly />
                  <button class="copy-btn" @click="copyText(streamKey)">
                    <i class="el-icon-copy-document" style="margin-right: 6px;"></i>
                    <span class="copy-text">Copy</span>
                  </button>
                </div>
              </div>
            </div>
            
            <div class="info-item">
              <button class="stop-btn danger-btn" @click="stopStream">
                <i class="el-icon-close" style="margin-right: 6px;"></i>
                Stop streaming
              </button>
            </div>
          </template>
        </div>
      </div>
      
      <div class="active-streams-section card">
        <div class="section-header">
          <span class="section-title">Current live stream</span>
          <span class="stream-count">{{ activeStreams.length }} live streams are available</span>
        </div>
        
        <div class="streams-list">
          <div class="stream-item" v-for="(stream, index) in activeStreams" :key="index">
            <div class="stream-header">
              <div class="stream-status live-indicator"></div>
              <span class="stream-title">{{ stream.title }}</span>
            </div>
            <div class="stream-details">
              <div class="detail-item">
                <i class="el-icon-view" style="font-size: 14px; color: #999;"></i>
                <span class="detail-text">Viewer count: {{ stream.viewers || 0 }} 人</span>
              </div>
              <div class="detail-item" v-if="stream.created_at">
                <i class="el-icon-time" style="font-size: 14px; color: #999;"></i>
                <span class="detail-text">Start time: {{ formatTime(stream.created_at) }}</span>
              </div>
            </div>
          </div>
          
          <div class="no-streams" v-if="activeStreams.length === 0">
            <i class="el-icon-video-camera" style="font-size: 40px; color: #c0c4cc;"></i>
            <span class="no-streams-text">Currently, there is no live broadcast scheduled for the start time</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
 
<script>
export default {
  data() {
    return {
      title: '',
      webrtcUrl: '',
      rtmpUrl: '',
      streamKey: '',
      activeStreams: []
    }
  },
  methods: {
    async generateStreamInfo() {
      if (!this.title) {
        this.$message({
          message: 'Please enter the live broadcast title',
          type: 'warning',
          duration: 2000
        });
        return;
      }
 
      try {
        const response = await fetch('http://localhost:9001/api/live/start', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({ title: this.title })
        });
 
        const data = await response.json();
        if (data.code === 0) {
          this.webrtcUrl = data.data.webrtc_url;
          this.rtmpUrl = data.data.rtmp_url;
          this.streamKey = data.data.stream_key;
          
          this.$message({
            message: 'Live Streaming Information has been generated',
            type: 'success',
            duration: 2000
          });
        } else {
          throw new Error(data.message || 'Generation failed');
        }
      } catch (error) {
        console.error('Failed to fetch streaming information:', error);
        this.$message({
          message: 'Failed to fetch streaming information: ' + error.message,
          type: 'error',
          duration: 3000
        });
      }
    },
 
    async stopStream() {
      if (!this.streamKey) {
        this.$message({
          message: 'No streaming being broadcast live',
          type: 'warning',
          duration: 2000
        });
        return;
      }
 
      try {
        const response = await fetch('http://localhost:9001/api/live/stop', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({ stream_key: this.streamKey })
        });
 
        const data = await response.json();
        if (data.code === 0) {
          this.webrtcUrl = '';
          this.rtmpUrl = '';
          this.streamKey = '';
          this.title = '';
          this.$message({
            message: 'Live stream has ended.',
            type: 'success',
            duration: 2000
          });
        } else {
          throw new Error(data.message || 'Failed to stop');
        }
      } catch (error) {
        console.error('Failed to stop:', error);
        this.$message({
          message: 'Failed to stop: ' + error.message,
          type: 'error',
          duration: 3000
        });
      }
    },
 
    async refreshStreams() {
      try {
        const response = await fetch('http://localhost:9001/api/live/active');
        const data = await response.json();
        this.activeStreams = data.data || [];
        
        this.$message({
          message: 'Refresh success',
          type: 'success',
          duration: 1000
        });
      } catch (error) {
        console.error('Failed to fetch streaming information:', error);
        this.$message({
          message: 'Refresh failed',
          type: 'error',
          duration: 2000
        });
      }
    },
    
    copyText(text) {
      navigator.clipboard.writeText(text).then(() => {
        this.$message({
          message: 'Copied to clipboard',
          type: 'success',
          duration: 1500
        });
      }).catch(err => {
        this.$message({
          message: 'Copied to clipboard Failed',
          type: 'error',
          duration: 1500
        });
        console.error('Copied to clipboard Failed:', err);
      });
    },
    
    formatTime(timestamp) {
      const date = new Date(timestamp);
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      const hours = String(date.getHours()).padStart(2, '0');
      const minutes = String(date.getMinutes()).padStart(2, '0');
      return `${year}-${month}-${day} ${hours}:${minutes}`;
    }
  },
  mounted() {
    this.refreshStreams();
    this.refreshInterval = setInterval(this.refreshStreams, 10000);
  },
  beforeDestroy() {
    clearInterval(this.refreshInterval);
  }
}
</script>

<style scoped>
.live-push-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
  padding: 24px 32px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.5);
}

.title {
  font-size: 32px;
  font-weight: 700;
  color: #2d3748;
  background: linear-gradient(135deg, #4361ee 0%, #3a0ca3 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  letter-spacing: -0.5px;
}

.refresh-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  background: linear-gradient(135deg, #4361ee 0%, #3a0ca3 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
  box-shadow: 0 4px 15px rgba(67, 97, 238, 0.3);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
}

.refresh-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(67, 97, 238, 0.4);
}

.refresh-btn:active {
  transform: translateY(0);
  box-shadow: 0 2px 10px rgba(67, 97, 238, 0.4);
}

.content-wrapper {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  padding: 32px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.5);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 2px solid #f1f5f9;
}

.section-title {
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
  letter-spacing: -0.5px;
}

.stream-count {
  font-size: 14px;
  font-weight: 600;
  color: #64748b;
  background-color: #f1f5f9;
  padding: 6px 12px;
  border-radius: 20px;
}

.instruction {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
  padding: 20px;
  background: linear-gradient(135deg, #e0f2fe 0%, #bae6fd 100%);
  border-radius: 16px;
  border: 1px solid #bae6fd;
}

.tip-icon {
  display: flex;
  align-items: center;
}

.tip-text {
  font-size: 15px;
  color: #0369a1;
  font-weight: 500;
  line-height: 1.5;
}

.info-item {
  margin-bottom: 24px;
}

.label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  font-size: 14px;
  color: #475569;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.input {
  width: 100%;
  padding: 16px 20px;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  font-size: 16px;
  box-sizing: border-box;
  background-color: #ffffff;
  transition: all 0.3s ease;
  color: #1e293b;
  font-weight: 500;
}

.title-input {
  font-size: 18px;
  padding: 18px 20px;
  border-width: 2px;
}

.url-input {
  font-size: 14px;
  font-family: 'Fira Code', 'Monaco', 'Consolas', monospace;
  background: #f8fafc;
}

.input:focus {
  border-color: #4361ee;
  outline: none;
  box-shadow: 0 0 0 3px rgba(67, 97, 238, 0.1);
}

.input-group {
  display: flex;
  gap: 12px;
}

.copy-btn {
  min-width: 100px;
  background: linear-gradient(135deg, #f1f5f9 0%, #e2e8f0 100%);
  border: 2px solid #e2e8f0;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  transition: all 0.3s ease;
  padding: 0 16px;
  cursor: pointer;
  font-weight: 600;
  font-size: 14px;
  color: #475569;
}

.copy-btn:hover {
  background: linear-gradient(135deg, #e2e8f0 0%, #cbd5e1 100%);
  transform: translateY(-1px);
}

.copy-btn:active {
  transform: translateY(0);
}

.generate-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  width: 100%;
  padding: 16px;
  background: linear-gradient(135deg, #4361ee 0%, #3a0ca3 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  box-shadow: 0 4px 15px rgba(67, 97, 238, 0.3);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  margin-top: 8px;
}

.generate-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(67, 97, 238, 0.4);
}

.generate-btn:active {
  transform: translateY(0);
  box-shadow: 0 2px 10px rgba(67, 97, 238, 0.4);
}

.stop-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  width: 100%;
  padding: 16px;
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  box-shadow: 0 4px 15px rgba(239, 68, 68, 0.3);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
}

.stop-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(239, 68, 68, 0.4);
}

.stop-btn:active {
  transform: translateY(0);
  box-shadow: 0 2px 10px rgba(239, 68, 68, 0.4);
}

.info-group {
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-radius: 16px;
  padding: 24px;
  margin: 16px 0 24px;
  border: 2px solid #f1f5f9;
}

.url-item {
  margin-bottom: 16px;
}

.url-item:last-child {
  margin-bottom: 0;
}

.streams-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.stream-item {
  padding: 20px;
  border-radius: 16px;
  background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  border: 2px solid #f1f5f9;
  transition: all 0.3s ease;
}

.stream-item:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
  border-color: #e2e8f0;
}

.stream-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.live-indicator {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  animation: pulse 2s infinite;
  flex-shrink: 0;
}

@keyframes pulse {
  0% {
    box-shadow: 0 0 0 0 rgba(239, 68, 68, 0.7);
    transform: scale(1);
  }
  70% {
    box-shadow: 0 0 0 10px rgba(239, 68, 68, 0);
    transform: scale(1.1);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(239, 68, 68, 0);
    transform: scale(1);
  }
}

.stream-title {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
  line-height: 1.4;
}

.stream-details {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding-left: 24px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.detail-text {
  font-size: 14px;
  color: #64748b;
  font-weight: 500;
}

.no-streams {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 0;
  gap: 16px;
  color: #94a3b8;
}

.no-streams-text {
  font-size: 16px;
  font-weight: 500;
  color: #94a3b8;
}

@media (max-width: 768px) {
  .live-push-container {
    padding: 16px;
  }
  
  .header {
    padding: 20px;
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }
  
  .title {
    font-size: 28px;
  }
  
  .card {
    padding: 24px 20px;
  }
  
  .input-group {
    flex-direction: column;
  }
  
  .copy-btn {
    width: 100%;
    padding: 12px;
  }
  
  .section-header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
}
</style>
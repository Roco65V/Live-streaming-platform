<template>
  <view class="live-push-container">
    <view class="header">
      <text class="title">Stream Manage</text>
      <button class="refresh-btn" @click="refreshStreams">
        <uni-icons type="refresh" size="16" color="#fff"></uni-icons>
        Refresh List
      </button>
    </view>
 
    <view class="content-wrapper">
      <view class="push-info-section card">
        <view class="section-header">
          <text class="section-title">Streamer Message</text>
        </view>
        
        <view class="instruction card">
          <view class="tip-icon">
            <uni-icons type="info" size="20" color="#409eff"></uni-icons>
          </view>
          <text class="tip-text">Use OBS or other streaming software to stream using the following information
		  </text>
        </view>
 
        <view class="stream-info">
          <view class="info-item">
            <text class="label">Title</text>
            <input class="input title-input" v-model="title" placeholder="Please enter the live broadcast title" />
            <button class="generate-btn primary-btn" @click="generateStreamInfo">
              <uni-icons type="paperplane" size="16" color="#fff"></uni-icons>
              GenerateStreamInfo
            </button>
          </view>
 
          <template v-if="webrtcUrl">
            <view class="info-group">
              <view class="info-item url-item">
                <text class="label">WebRTC URL:</text>
                <view class="input-group">
                  <input class="input url-input" v-model="webrtcUrl" readonly />
                  <button class="copy-btn" @click="copyText(webrtcUrl)">
                    <uni-icons type="copy" size="16" color="#666"></uni-icons>
                    <text class="copy-text">Copy</text>
                  </button>
                </view>
              </view>
              
              <view class="info-item url-item">
                <text class="label">RTMP URL:</text>
                <view class="input-group">
                  <input class="input url-input" v-model="rtmpUrl" readonly />
                  <button class="copy-btn" @click="copyText(rtmpUrl)">
                    <uni-icons type="copy" size="16" color="#666"></uni-icons>
                    <text class="copy-text">Copy</text>
                  </button>
                </view>
              </view>
              
              <view class="info-item url-item">
                <text class="label">Stream Key:</text>
                <view class="input-group">
                  <input class="input url-input" v-model="streamKey" readonly />
                  <button class="copy-btn" @click="copyText(streamKey)">
                    <uni-icons type="copy" size="16" color="#666"></uni-icons>
                    <text class="copy-text">Copy</text>
                  </button>
                </view>
              </view>
            </view>
            
            <view class="info-item">
              <button class="stop-btn danger-btn" @click="stopStream">
                <uni-icons type="closeempty" size="16" color="#fff"></uni-icons>
                Stop streaming
              </button>
            </view>
          </template>
        </view>
      </view>
      
      <view class="active-streams-section card">
        <view class="section-header">
          <text class="section-title">Current live stream</text>
          <text class="stream-count">{{ activeStreams.length }} live streams are available</text>
        </view>
        
        <view class="streams-list">
          <view class="stream-item" v-for="(stream, index) in activeStreams" :key="index">
            <view class="stream-header">
              <view class="stream-status live-indicator"></view>
              <text class="stream-title">{{ stream.title }}</text>
            </view>
            <view class="stream-details">
              <view class="detail-item">
                <uni-icons type="eye" size="14" color="#999"></uni-icons>
                <text class="detail-text">Viewer count: {{ stream.viewers || 0 }} 人</text>
              </view>
              <view class="detail-item" v-if="stream.created_at">
                <uni-icons type="clock" size="14" color="#999"></uni-icons>
                <text class="detail-text">Start time: {{ formatTime(stream.created_at) }}</text>
              </view>
            </view>
          </view>
          
          <view class="no-streams" v-if="activeStreams.length === 0">
            <uni-icons type="videocam" size="40" color="#c0c4cc"></uni-icons>
            <text class="no-streams-text">Currently, there is no live broadcast scheduled for the start time</text>
          </view>
        </view>
      </view>
    </view>
  </view>
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
        uni.showToast({
          title: 'Please enter the live broadcast title',
          icon: 'none',
          duration: 2000
        });
        return;
      }
 
      uni.showLoading({
        title: 'Generating...'
      });
 
      try {
        const response = await uni.request({
          url: 'http://localhost:9001/api/live/start',
          method: 'POST',
          header: {
            'Content-Type': 'application/json',
          },
          data: { title: this.title }
        });
 
        uni.hideLoading();
 
        const data = response.data;
        if (data.code === 0) {
          this.webrtcUrl = data.data.webrtc_url;
          this.rtmpUrl = data.data.rtmp_url;
          this.streamKey = data.data.stream_key;
          
          uni.showToast({
            title: 'Live Streaming Information has been generated',
            icon: 'success',
            duration: 2000
          });
        } else {
          throw new Error(data.message || 'Generation failed');
        }
      } catch (error) {
        uni.hideLoading();
        console.error('Failed to fetch streaming information:', error);
        uni.showToast({
          title: 'Failed to fetch streaming information: ' + (error.errMsg || error.message),
          icon: 'none',
          duration: 3000
        });
      }
    },
 
    async stopStream() {
      if (!this.streamKey) {
        uni.showToast({
          title: 'No streaming being broadcast live',
          icon: 'none',
          duration: 2000
        });
        return;
      }
 
      uni.showLoading({
        title: 'Stop streaming...'
      });
 
      try {
        const response = await uni.request({
          url: 'http://localhost:9001/api/live/stop',
          method: 'POST',
          header: {
            'Content-Type': 'application/json',
          },
          data: { stream_key: this.streamKey }
        });
 
        uni.hideLoading();
 
        const data = response.data;
        if (data.code === 0) {
          this.webrtcUrl = '';
          this.rtmpUrl = '';
          this.streamKey = '';
          this.title = '';
          uni.showToast({
            title: 'Live stream has ended.',
            icon: 'success',
            duration: 2000
          });
        } else {
          throw new Error(data.message || 'Failed to stop');
        }
      } catch (error) {
        uni.hideLoading();
        console.error('Failed to stop:', error);
        uni.showToast({
          title: 'Failed to stop: ' + (error.errMsg || error.message),
          icon: 'none',
          duration: 3000
        });
      }
    },
 
    async refreshStreams() {
      try {
        const response = await uni.request({
          url: 'http://localhost:9001/api/live/active'
        });
        const data = response.data;
        this.activeStreams = data.data || [];
        
        uni.showToast({
          title: 'Refresh success',
          icon: 'success',
          duration: 1000
        });
      } catch (error) {
        console.error('Failed to fetch streaming information:', error);
        uni.showToast({
          title: 'Refresh failed',
          icon: 'none',
          duration: 2000
        });
      }
    },
    
    copyText(text) {
      uni.setClipboardData({
        data: text,
        success: () => {
          uni.showToast({
            title: 'Copied to clipboard',
            icon: 'success',
            duration: 1500
          });
        }
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
  onLoad() {
    this.refreshStreams();
  },
  onUnload() {
  }
}
</script>

<style scoped>
.live-push-container {
  padding: 20rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  min-height: 100vh;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30rpx;
  padding: 30rpx;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 20rpx;
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10rpx);
}

.title {
  font-size: 38rpx;
  font-weight: bold;
  color: #303133;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.refresh-btn {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 16rpx 24rpx;
  background: linear-gradient(135deg, #409eff 0%, #64b5f6 100%);
  color: white;
  border: none;
  border-radius: 50rpx;
  font-size: 26rpx;
  box-shadow: 0 4rpx 16rpx rgba(64, 158, 255, 0.3);
  transition: all 0.3s ease;
}

.refresh-btn:active {
  transform: scale(0.95);
  box-shadow: 0 2rpx 8rpx rgba(64, 158, 255, 0.5);
}

.content-wrapper {
  display: flex;
  flex-direction: column;
  gap: 30rpx;
}

.card {
  background: rgba(255, 255, 255, 0.92);
  border-radius: 20rpx;
  padding: 30rpx;
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10rpx);
  border: 1rpx solid rgba(255, 255, 255, 0.2);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30rpx;
  padding-bottom: 20rpx;
  border-bottom: 2rpx solid #f0f2f5;
}

.section-title {
  font-size: 34rpx;
  font-weight: bold;
  color: #303133;
}

.stream-count {
  font-size: 24rpx;
  color: #909399;
  background-color: #ecf5ff;
  padding: 6rpx 16rpx;
  border-radius: 20rpx;
}

.instruction {
  display: flex;
  align-items: flex-start;
  gap: 16rpx;
  margin-bottom: 30rpx;
  padding: 24rpx;
  background: linear-gradient(135deg, #e8f4ff 0%, #f0f8ff 100%);
  border-radius: 16rpx;
  border: 1rpx solid #d0e6ff;
}

.tip-icon {
  margin-top: 4rpx;
}

.tip-text {
  flex: 1;
  font-size: 26rpx;
  color: #409eff;
  line-height: 1.5;
}

.info-item {
  margin-bottom: 30rpx;
}

.label {
  display: block;
  margin-bottom: 16rpx;
  font-weight: 500;
  font-size: 28rpx;
  color: #606266;
}

.input {
  width: 100%;
  padding: 24rpx;
  margin-bottom: 10rpx;
  border: 3rpx solid #dcdfe6;
  border-radius: 12rpx;
  font-size: 30rpx;
  box-sizing: border-box;
  background-color: #fff;
  transition: all 0.3s ease;
  min-height: 80rpx;
}

.title-input {
  font-size: 32rpx;
  padding: 24rpx;
  border-width: 3rpx;
  min-height: 90rpx;
  font-weight: 500;
}

.url-input {
  font-size: 28rpx;
  padding: 20rpx;
  min-height: 70rpx;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
}

.input:focus {
  border-color: #409eff;
  outline: none;
  box-shadow: 0 0 0 4rpx rgba(64, 158, 255, 0.2);
}

.input-group {
  display: flex;
  gap: 16rpx;
}

.copy-btn {
  width: 120rpx;
  background-color: #f5f7fa;
  border: 2rpx solid #dcdfe6;
  border-radius: 12rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  flex-direction: column;
  padding: 10rpx 0;
}

.copy-btn:active {
  background-color: #e1e6f0;
  transform: scale(0.95);
}

.copy-text {
  font-size: 24rpx;
  color: #606266;
  margin-top: 4rpx;
}

.primary-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  width: 100%;
  padding: 24rpx;
  background: linear-gradient(135deg, #409eff 0%, #64b5f6 100%);
  color: white;
  border: none;
  border-radius: 12rpx;
  font-size: 28rpx;
  font-weight: 500;
  box-shadow: 0 4rpx 16rpx rgba(64, 158, 255, 0.3);
  transition: all 0.3s ease;
}

.primary-btn:active {
  transform: scale(0.98);
  box-shadow: 0 2rpx 8rpx rgba(64, 158, 255, 0.5);
}

.danger-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  width: 100%;
  padding: 24rpx;
  background: linear-gradient(135deg, #f56c6c 0%, #ff8e8e 100%);
  color: white;
  border: none;
  border-radius: 12rpx;
  font-size: 28rpx;
  font-weight: 500;
  box-shadow: 0 4rpx 16rpx rgba(245, 108, 108, 0.3);
  transition: all 0.3s ease;
}

.danger-btn:active {
  transform: scale(0.98);
  box-shadow: 0 2rpx 8rpx rgba(245, 108, 108, 0.5);
}

.info-group {
  background: linear-gradient(135deg, #f5f7fa 0%, #fafcff 100%);
  border-radius: 16rpx;
  padding: 24rpx;
  margin: 20rpx 0 30rpx;
  border: 1rpx solid #ebeef5;
}

.url-item {
  margin-bottom: 24rpx;
}

.url-item:last-child {
  margin-bottom: 0;
}

.active-streams-section {
  margin-top: 20rpx;
}

.streams-list {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.stream-item {
  padding: 24rpx;
  border-radius: 16rpx;
  background: linear-gradient(135deg, #ffffff 0%, #fafafa 100%);
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);
  border: 1rpx solid #f0f0f0;
  transition: all 0.3s ease;
}

.stream-item:hover {
  box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.1);
  transform: translateY(-4rpx);
}

.stream-header {
  display: flex;
  align-items: center;
  gap: 16rpx;
  margin-bottom: 16rpx;
}

.live-indicator {
  width: 16rpx;
  height: 16rpx;
  border-radius: 50%;
  background-color: #f56c6c;
  animation: pulse 1.5s infinite;
  box-shadow: 0 0 0 0 rgba(245, 108, 108, 0.7);
}

@keyframes pulse {
  0% {
    box-shadow: 0 0 0 0 rgba(245, 108, 108, 0.7);
  }
  70% {
    box-shadow: 0 0 0 10rpx rgba(245, 108, 108, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(245, 108, 108, 0);
  }
}

.stream-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #303133;
  flex: 1;
}

.stream-details {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
  padding-left: 32rpx;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.detail-text {
  font-size: 24rpx;
  color: #909399;
}

.no-streams {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60rpx 0;
  gap: 20rpx;
}

.no-streams-text {
  font-size: 28rpx;
  color: #c0c4cc;
}
</style>
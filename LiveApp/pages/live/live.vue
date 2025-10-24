<template>
	<view class="live-container">
		<view class="live-content">
			<swiper 
				class="video-swiper"
				vertical
				duration="300"
				circular
				@change="onSwiperChange">
				<swiper-item v-for="(stream, index) in activeStreams" :key="stream.id">
					<view class="stream-item">
						<video 
							:id="'liveVideo' + index" 
							:src="'http://your-domain/live/' + stream.streamKey + '.m3u8'"
							autoplay
							object-fit="cover"
							:show-fullscreen-btn="false"
							:show-play-btn="false"
							:show-center-play-btn="false"
							:enable-progress-gesture="false"
							:enable-play-gesture="false"
						></video>
						<view class="stream-info">
							<text class="stream-title">{{ stream.title }}</text>
						</view>
					</view>
				</swiper-item>
				
				<swiper-item v-if="activeStreams.length === 0">
					<view class="no-stream">
						<text class="no-stream-text">No live stream</text>
					</view>
				</swiper-item>
			</swiper>
		</view>
	</view>
</template>

<script lang="ts" setup>
	import { ref, onMounted, onUnmounted } from 'vue';

	const activeStreams = ref([]);
	const currentStreamIndex = ref(0);


	const fetchActiveStreams = () => {
		uni.request({
			url: 'http://localhost:9001/api/live/active',
			method: 'GET',
			success: (res) => {
				const result = res.data as any;
				if (result.code === 0) {
					activeStreams.value = result.data || [];
				}
			},
			fail: (err) => {
				console.error('Get live stream Fail:', err);
			}
		});
	};

	const onSwiperChange = (e) => {
		currentStreamIndex.value = e.detail.current;
	};

	onMounted(() => {
		fetchActiveStreams();
		
		const interval = setInterval(fetchActiveStreams, 10000);
		
		(onUnmounted as any).interval = interval;
	});

	onUnmounted(() => {
		if ((onUnmounted as any).interval) {
			clearInterval((onUnmounted as any).interval);
		}
	});
</script>

<style>
	page {
		height: 100%;
		overflow: hidden;
	}

	.live-container {
		height: 100%;
		display: flex;
		flex-direction: column;
		background-color: #000000;
	}

	.live-content {
		position: relative;
		flex: 1;
		overflow: hidden;
	}

	.video-swiper {
		width: 100%;
		height: 100%;
	}

	.stream-item {
		width: 100%;
		height: 100%;
		position: relative;
	}

	.stream-item video {
		width: 100%;
		height: 100%;
		object-fit: cover;
		display: block;
	}

	.stream-info {
		position: absolute;
		bottom: 200rpx;
		left: 20rpx;
		right: 20rpx;
		background: rgba(0, 0, 0, 0.6);
		padding: 20rpx;
		border-radius: 10rpx;
	}

	.stream-title {
		color: #ffffff;
		font-size: 32rpx;
		font-weight: bold;
	}

	.no-stream {
		display: flex;
		justify-content: center;
		align-items: center;
		height: 100%;
		background-color: #000000;
		width: 100%;
	}

	.no-stream-text {
		color: #ffffff;
		font-size: 36rpx;
	}
</style>
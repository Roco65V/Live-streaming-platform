"use strict";
const common_vendor = require("../../common/vendor.js");
const _sfc_main = {
  data() {
    return {
      title: "",
      webrtcUrl: "",
      rtmpUrl: "",
      streamKey: "",
      activeStreams: []
    };
  },
  methods: {
    async generateStreamInfo() {
      if (!this.title) {
        common_vendor.index.showToast({
          title: "Please enter the live broadcast title",
          icon: "none",
          duration: 2e3
        });
        return;
      }
      common_vendor.index.showLoading({
        title: "Generating..."
      });
      try {
        const response = await common_vendor.index.request({
          url: "http://localhost:9001/api/live/start",
          method: "POST",
          header: {
            "Content-Type": "application/json"
          },
          data: { title: this.title }
        });
        common_vendor.index.hideLoading();
        const data = response.data;
        if (data.code === 0) {
          this.webrtcUrl = data.data.webrtc_url;
          this.rtmpUrl = data.data.rtmp_url;
          this.streamKey = data.data.stream_key;
          common_vendor.index.showToast({
            title: "Live Streaming Information has been generated",
            icon: "success",
            duration: 2e3
          });
        } else {
          throw new Error(data.message || "Generation failed");
        }
      } catch (error) {
        common_vendor.index.hideLoading();
        common_vendor.index.__f__("error", "at pages/streamer/streamer.vue:171", "Failed to fetch streaming information:", error);
        common_vendor.index.showToast({
          title: "Failed to fetch streaming information: " + (error.errMsg || error.message),
          icon: "none",
          duration: 3e3
        });
      }
    },
    async stopStream() {
      if (!this.streamKey) {
        common_vendor.index.showToast({
          title: "No streaming being broadcast live",
          icon: "none",
          duration: 2e3
        });
        return;
      }
      common_vendor.index.showLoading({
        title: "Stop streaming..."
      });
      try {
        const response = await common_vendor.index.request({
          url: "http://localhost:9001/api/live/stop",
          method: "POST",
          header: {
            "Content-Type": "application/json"
          },
          data: { stream_key: this.streamKey }
        });
        common_vendor.index.hideLoading();
        const data = response.data;
        if (data.code === 0) {
          this.webrtcUrl = "";
          this.rtmpUrl = "";
          this.streamKey = "";
          this.title = "";
          common_vendor.index.showToast({
            title: "Live stream has ended.",
            icon: "success",
            duration: 2e3
          });
        } else {
          throw new Error(data.message || "Failed to stop");
        }
      } catch (error) {
        common_vendor.index.hideLoading();
        common_vendor.index.__f__("error", "at pages/streamer/streamer.vue:223", "Failed to stop:", error);
        common_vendor.index.showToast({
          title: "Failed to stop: " + (error.errMsg || error.message),
          icon: "none",
          duration: 3e3
        });
      }
    },
    async refreshStreams() {
      try {
        const response = await common_vendor.index.request({
          url: "http://localhost:9001/api/live/active"
        });
        const data = response.data;
        this.activeStreams = data.data || [];
        common_vendor.index.showToast({
          title: "Refresh success",
          icon: "success",
          duration: 1e3
        });
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/streamer/streamer.vue:246", "Failed to fetch streaming information:", error);
        common_vendor.index.showToast({
          title: "Refresh failed",
          icon: "none",
          duration: 2e3
        });
      }
    },
    copyText(text) {
      common_vendor.index.setClipboardData({
        data: text,
        success: () => {
          common_vendor.index.showToast({
            title: "Copied to clipboard",
            icon: "success",
            duration: 1500
          });
        }
      });
    },
    formatTime(timestamp) {
      const date = new Date(timestamp);
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, "0");
      const day = String(date.getDate()).padStart(2, "0");
      const hours = String(date.getHours()).padStart(2, "0");
      const minutes = String(date.getMinutes()).padStart(2, "0");
      return `${year}-${month}-${day} ${hours}:${minutes}`;
    }
  },
  onLoad() {
    this.refreshStreams();
  },
  onUnload() {
  }
};
if (!Array) {
  const _component_uni_icons = common_vendor.resolveComponent("uni-icons");
  _component_uni_icons();
}
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: common_vendor.p({
      type: "refresh",
      size: "16",
      color: "#fff"
    }),
    b: common_vendor.o((...args) => $options.refreshStreams && $options.refreshStreams(...args)),
    c: common_vendor.p({
      type: "info",
      size: "20",
      color: "#409eff"
    }),
    d: $data.title,
    e: common_vendor.o(($event) => $data.title = $event.detail.value),
    f: common_vendor.p({
      type: "paperplane",
      size: "16",
      color: "#fff"
    }),
    g: common_vendor.o((...args) => $options.generateStreamInfo && $options.generateStreamInfo(...args)),
    h: $data.webrtcUrl
  }, $data.webrtcUrl ? {
    i: $data.webrtcUrl,
    j: common_vendor.o(($event) => $data.webrtcUrl = $event.detail.value),
    k: common_vendor.p({
      type: "copy",
      size: "16",
      color: "#666"
    }),
    l: common_vendor.o(($event) => $options.copyText($data.webrtcUrl)),
    m: $data.rtmpUrl,
    n: common_vendor.o(($event) => $data.rtmpUrl = $event.detail.value),
    o: common_vendor.p({
      type: "copy",
      size: "16",
      color: "#666"
    }),
    p: common_vendor.o(($event) => $options.copyText($data.rtmpUrl)),
    q: $data.streamKey,
    r: common_vendor.o(($event) => $data.streamKey = $event.detail.value),
    s: common_vendor.p({
      type: "copy",
      size: "16",
      color: "#666"
    }),
    t: common_vendor.o(($event) => $options.copyText($data.streamKey)),
    v: common_vendor.p({
      type: "closeempty",
      size: "16",
      color: "#fff"
    }),
    w: common_vendor.o((...args) => $options.stopStream && $options.stopStream(...args))
  } : {}, {
    x: common_vendor.t($data.activeStreams.length),
    y: common_vendor.f($data.activeStreams, (stream, index, i0) => {
      return common_vendor.e({
        a: common_vendor.t(stream.title),
        b: "803df585-7-" + i0,
        c: common_vendor.t(stream.viewers || 0),
        d: stream.created_at
      }, stream.created_at ? {
        e: "803df585-8-" + i0,
        f: common_vendor.p({
          type: "clock",
          size: "14",
          color: "#999"
        }),
        g: common_vendor.t($options.formatTime(stream.created_at))
      } : {}, {
        h: index
      });
    }),
    z: common_vendor.p({
      type: "eye",
      size: "14",
      color: "#999"
    }),
    A: $data.activeStreams.length === 0
  }, $data.activeStreams.length === 0 ? {
    B: common_vendor.p({
      type: "videocam",
      size: "40",
      color: "#c0c4cc"
    })
  } : {});
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-803df585"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/streamer/streamer.js.map

"use strict";
const common_vendor = require("../../common/vendor.js");
const _sfc_main = /* @__PURE__ */ common_vendor.defineComponent({
  __name: "live",
  setup(__props) {
    const activeStreams = common_vendor.ref([]);
    const currentStreamIndex = common_vendor.ref(0);
    const fetchActiveStreams = () => {
      common_vendor.index.request({
        url: "http://localhost:9001/api/live/active",
        method: "GET",
        success: (res) => {
          const result = res.data;
          if (result.code === 0) {
            activeStreams.value = result.data || [];
          }
        },
        fail: (err) => {
          common_vendor.index.__f__("error", "at pages/live/live.vue:57", "Get live stream Fail:", err);
        }
      });
    };
    const onSwiperChange = (e) => {
      currentStreamIndex.value = e.detail.current;
    };
    common_vendor.onMounted(() => {
      fetchActiveStreams();
      const interval = setInterval(fetchActiveStreams, 1e4);
      common_vendor.onUnmounted.interval = interval;
    });
    common_vendor.onUnmounted(() => {
      if (common_vendor.onUnmounted.interval) {
        clearInterval(common_vendor.onUnmounted.interval);
      }
    });
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: common_vendor.f(activeStreams.value, (stream, index, i0) => {
          return {
            a: "liveVideo" + index,
            b: "http://your-domain/live/" + stream.streamKey + ".m3u8",
            c: common_vendor.t(stream.title),
            d: stream.id
          };
        }),
        b: activeStreams.value.length === 0
      }, activeStreams.value.length === 0 ? {} : {}, {
        c: common_vendor.o(onSwiperChange)
      });
    };
  }
});
wx.createPage(_sfc_main);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/live/live.js.map

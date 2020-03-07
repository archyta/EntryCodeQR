
<template>
  <div class="main">
    <div class="container">
      <mp-toptips :delay="5000" :msg="error" :show="errorShow" @hide="onErrorHidden" type="error"></mp-toptips>
      <image @tap="showShareModal" class="width" src="{{'./../static/image/example.png'}}" />
      <div class="bg_modal" v-if="showModalfirst || showModalSecond">
        <image class="image_text" src="{{'./../static/image/tip-font.png'}}" v-if="showModalfirst" />
        <div class="buttons" v-if="showModalfirst">
          <div class="add_desc">这个工具不错,物业使用肯定很赞!</div>
          <button @tap="changeShareModal" class="btn">
            <span class="text">我为抗疫出份力</span>
          </button>
        </div>
        <div class="share_view" v-if="showModalSecond">
          <div class="bg_img">
            <image class="image_text" src="{{'./../static/image/view_poster.png'}}" />
          </div>
          <div class="share_action">
            <button class="share_btn" open-type="share">
              <image class="icon" src="{{'./../static/icon/wechat.png'}}" />
              <span class="text">分享给好友</span>
            </button>
            <!-- <button class="share_btn">
              <image class="icon" src="{{'./../static/icon/firend_circle.png'}}"/>
              <span class="text">生成分享海报</span>
            </button>-->
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import wepy from '@wepy/core'
import eventHub from '../common/eventHub'
import store from '../store'
import getters from '@/store/getters'
import { getEstateInfo } from '@/api/login'
import { getBaseInfo, getOutInRecord } from '@/api/owner'
import { compareVersion } from '@/utils/common.js'
wepy.page({
  store,
  onShareAppMessage: function(res) {
    let agentId = wepy.wx.getStorageSync('agentId')
    if (agentId) {
      return {
        title: '出入福安 我为抗疫出份力！',
        path: `/pages/main?agentId=${agentId}`
      }
    } else {
      return {
        title: '出入福安 我为抗疫出份力！',
        path: '/pages/main'
      }
    }
  },
  hooks: {},
  data: {
    error: '',
    errorShow: false,
    showModalfirst: false,
    showModalSecond: false
  },
  computed: {
    ...getters(),
    isShowInfo: function() {
      return this.store_hasLogin
    }
  },
  onShow() {
    // 基础库2.9.5才支持
    if (compareVersion('2.9.5') > -1) {
      wepy.wx.hideHomeButton()
    }
  },
  methods: {
    showShareModal() {
      this.showModalfirst = true
    },
    changeShareModal() {
      this.showModalfirst = false
      this.showModalSecond = true
    },
    onErrorHidden() {
      this.error = ''
      this.errorShow = false
    }
  }
})
</script>
<config>
  {
    navigationBarTitleText: '出入福安',
    usingComponents: {
      'mp-icon':'module:weui-miniprogram/miniprogram_dist/icon/icon',
      'mp-toptips':'module:weui-miniprogram/miniprogram_dist/toptips/toptips'
    }
  }
</config>
<style lang="less" scoped>
page {
  height: 100%;
  background-color: #f3f3f3;
}
.main {
  display: flex;
  flex-direction: column;
  .container {
    display: flex;
    flex-direction: column;
    width: 100vw;
    min-height: 100vh;
    align-items: center;
    image {
      width: 750rpx;
      height: 1341rpx;
    }
    .bg_modal {
      padding-top: 64rpx;
      .share_view {
        border-radius: 8rpx;
        overflow: hidden;
        width: 650rpx;
        height: 1054rpx;
        box-shadow: 0rpx 4rpx 6rpx 0rpx rgba(57, 57, 57, 0.05);
        .bg_img {
          width: 100%;
          height: 954rpx;
          background: #3a6eff;
          position: relative;
          image {
            position: absolute;
            left: 20rpx;
            width: 610rpx;
            top: 20rpx;
            height: 914rpx;
          }
        }
        .share_action {
          background: #fff;
          height: 100rpx;
          display: flex;
          width: 100%;
          .share_btn {
            // flex: 0 0 50%;
            font-size: 30rpx;
            line-height: 100rpx;
            background: #fff;
            width: 100%;
            border: none;
            &:after {
              border: none;
            }
            &.first {
              border-right: 1rpx solid #eee;
            }
          }
          .icon {
            width: 50rpx;
            height: 50rpx;
            vertical-align: middle;
            margin-right: 8rpx;
          }
        }
      }
      content: ' ';
      position: fixed;
      left: 0;
      top: 0;
      width: 100vw;
      height: 100vh;
      background: rgba(234, 234, 264, 0.93);
      display: flex;
      align-items: center;
      flex-direction: column;
      .image_text {
        width: 338rpx;
        height: 348rpx;
        position: absolute;
        bottom: 316rpx;
      }
      .buttons {
        position: absolute;
        bottom: 0;
        display: flex;
        align-items: center;
        flex-direction: column;
        padding-top: 40rpx;
        width: 100%;
        height: 274rpx;
        background: #ffffff;
        box-shadow: 0rpx 0rpx 8rpx 0rpx rgba(3, 7, 23, 0.3);
        .add_desc {
          font-size: 32rpx;
          color: #222;
        }
        .btn {
          margin-top: 30rpx;
          height: 100rpx;
          line-height: 100rpx;
          background-color: #3a6eff;
          font-size: 32rpx;
          display: flex;
          justify-content: center;
          justify-items: center;
          box-shadow: 0rpx 4rpx 6rpx 0rpx rgba(57, 57, 57, 0.05);
          text-align: center;
          width: 640rpx;
          .text {
            // margin-left: 10rpx;
            // margin-top: 4rpx;
            color: #ffffff;
          }
        }
      }
    }
  }
}
</style>
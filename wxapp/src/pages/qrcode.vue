

<template>
  <div class="main">
    <div class="container">
      <panel class="panel img">
        <div class="text-center title">{{userName}}</div>
        <div class="address">{{showAddressFull}}</div>
        <!-- <image src="{{'./../static/image/qrcode.png'}}" mode="scaleToFill"
        lazy-load="true">
        </image>-->
        <canvas canvas-id="qrcode" class="image" />
        <button v-if="!isFobidden" @tap.stop="drawERCode" class="btn-refresh">
          <mp-icon :size="16" color="#3A6EFF" icon="refresh"></mp-icon>
          <span class="refresh-text">刷新</span>
        </button>
        <div v-else class="limit-warn">
          <span>您的出入次数已达小区限行上限</span>
        </div>
      </panel>
      <panel class="panel bor-t-1 out-padding">
        <image src="{{'./../static/image/passport.png'}}" />
        <!-- <span class="danger-wrap danger-main">
          <span>通</span>
          <span>行</span>
          <span>证</span>
        </span>
        <span class="danger-wrap danger-tips">
          <span>出</span>
          <span>门</span>
          <span>戴</span>
          <span>口</span>
          <span>罩</span>
          <span>，</span>
          <span>回</span>
          <span>家</span>
          <span>须</span>
          <span>消</span>
          <span>毒</span>
        </span>-->
      </panel>
    </div>
  </div>
</template>
      
<script>
import wepy from '@wepy/core'
import eventHub from '../common/eventHub'
import { mapState } from '@wepy/x'
import getters from '@/store/getters'
import store from '../store'
import { encodeObj } from '@/utils/encode.js'

import wxbarcode from 'wxbarcode'
import { compareVersion } from '@/utils/common.js'

wepy.page({
  store,
  config: {
    navigationBarTitleText: 'W11123'
  },
  hooks: {},
  data: {
    userName: '',
    isFobidden: false,
    qrdata: {},
    famlilyInfo: {
      estateName: '',
      building: '',
      unit: '',
      roomNumber: ''
    }
  },
  onLoad(option) {
    const { name, isFobidden, ...rest } = option
    this.isFobidden = isFobidden === 'true'
    this.userName = name
    this.qrdata = rest
  },
  onShow() {
    // 基础库2.9.5才支持
    if (compareVersion('2.9.5') > -1) {
      wepy.wx.hideHomeButton()
    }
    // 修复概率性 canves 不出现二维码问题
    setTimeout(() => {
      this.drawERCode()
    }, 200)
  },
  onReady() {
    // setTimeout()
    // 修复概率性 canves 不出现二维码问题
    this.drawERCode()
  },
  created() {
    this.famlilyInfo.estateName = this.store_housingName || ''
    this.famlilyInfo.building = this.store_building || ''
    this.famlilyInfo.unit = this.store_unit || ''
    this.famlilyInfo.roomNumber = this.store_roomNumber || ''
  },
  computed: {
    ...getters(),
    showAddressFull: function() {
      let str = ''
      if (this.famlilyInfo.estateName) {
        str += this.famlilyInfo.estateName
      }
      if (this.famlilyInfo.building) {
        str += this.famlilyInfo.building + '栋'
      }
      if (this.famlilyInfo.unit) {
        str += this.famlilyInfo.unit + '单元'
      }
      if (this.famlilyInfo.roomNumber) {
        str += this.famlilyInfo.roomNumber
      }
      return str
    }
  },
  methods: {
    commitBaseinfo() {
      this.$navigate({ url: '/pages/qrcode' })
    },
    drawERCode() {
      this.qrdata.expiredDate = new Date().getTime() + 300 * 1000
      // const dpr = wx.getSystemInfoSync().pixelRatio/2
      wxbarcode.qrcode('qrcode', JSON.stringify(encodeObj(this.qrdata)), 600, 600)
    }
  }
})
</script>
<config>
  {
    navigationBarTitleText: '出示通行证',
    usingComponents: {
      'mp-icon':'module:weui-miniprogram/miniprogram_dist/icon/icon'
    }
  }
</config>
      
<style lang="less" scoped>
page {
  height: 100%;
  background-color: #f0f0f0;
}
.main {
  display: flex;
  flex-direction: column;
}
.container {
  color: #222;
  padding: 20rpx;
  display: flex;
  flex-direction: column;
  margin-top: 20rpx;
  margin-bottom: 20rpx;
  .panel {
    display: flex;
    background-color: #fff;
    border-radius: 8rpx;
    &.img {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 20rpx;
      .title {
        text-align: center;
        font-size: 40rpx;
        line-height: 100rpx;
        border-bottom: 1rpx solid #eee;
        width: 100%;
      }
      .image {
        width: 600rpx;
        height: 600rpx;
        // margin-top: 30rpx;
      }
      .btn-refresh {
        font-size: 32rpx;
        color: #3a6eff;
        height: 64rpx;
        width: 152rpx;
        display: flex;
        flex-direction: row;
        padding-left: 5rpx;
        padding-right: 5rpx;
        justify-content: center;
        align-items: center;
        margin-bottom: 31rpx;
        .weui-icon {
          transform: translateY(-3rpx);
        }
        .refresh-text {
          margin-left: 10rpx;
        }
      }
      .limit-warn {
        background: #ff220d;
        width: 500rpx;
        color: #fff;
        box-shadow: 0px 2px 6px 0px rgba(237, 0, 0, 0.36);
        text-align: center;
        padding-top: 20rpx;
        padding-bottom: 20rpx;
        display: flex;
        justify-items: center;
        justify-content: center;
        border-radius: 8rpx;
      }
      .address {
        text-align: center;
        font-size: 32rpx;
        font-weight: bold;
        margin-top: 30rpx;
      }
    }
    &.out-padding {
      padding: 60rpx 0;
      position: relative;
      display: flex;
      border-top: 1rpx dashed #ccc;
      text-align: center;
      display: flex;
      justify-content: center;
      align-items: center;
      image {
        width: 476rpx;
        height: 182rpx;
      }
      &::after,
      &::before {
        content: ' ';
        position: absolute;
        top: 0;
        width: 20rpx;
        height: 20rpx;
        background: #f0f0f0;
        border-radius: 50%;
      }
      &::after {
        left: 0;
        transform: translate(-50%, -50%);
      }
      &::before {
        right: 0;
        transform: translate(50%, -50%);
      }
    }
  }
}
</style>
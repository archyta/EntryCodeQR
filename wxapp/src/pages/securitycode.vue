

<template>
  <div class="main">
    <div class="container">
      <panel class="panel img">
        <div class="text-center title">请住户扫此二维码登记信息</div>
        <div class="address">{{showAddressFull}}</div>
        <div class="image">
          <div :style="{'opacity':showQrImage}" class="absolute">
            <image :src="url" @error="onloadError" @load="onloadComplete" class="image-code" lazy-load="true" mode="scaleToFill" />
          </div>
          <div :style="{'display':showLoaddingImage}" class="absolute loading">
            <span v-if="!loadSuccess">
              <text>加载中</text>
              <image class="loading-image" src="{{'./../static/icon/loading.gif'}}" />
            </span>
            <span v-else>不好意思，加载失败咯！</span>
          </div>
        </div>

        <!-- <canvas canvas-id="qrcode" class="image" /> -->
      </panel>
      <panel class="panel out-padding">
        <image class="image" src="/static/image/warningtip.png" />
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

import wxbarcode from 'wxbarcode'
import { compareVersion } from '@/utils/common.js'

import { getERCode } from '@/api/guards'
wepy.page({
  store,
  config: {
    navigationBarTitleText: 'W11123'
  },
  hooks: {},
  data: {
    userName: '',
    famlilyInfo: {
      estateName: '',
      building: '',
      unit: '',
      roomNumber: ''
    },
    isLoaded: false,
    loadSuccess: false,
    url: null
  },
  onReady() {
    let estateId = this.store_housingEstateId
    let housingName = this.store_housingName
    // let paramstr = `?estateId=${estateId}&estateName=${housingName}`
    let accountInfo = wx.getAccountInfoSync()
    this.url = getERCode(accountInfo.miniProgram.appId, 'estate', this.store_housingEstateId)
    // estateId=1&estateName=测试小区
    // wxbarcode.qrcode('qrcode', 'https://anquan.myngo.cn/pages/index?estateId=1&estateName=测试小区', 552, 552);
  },
  created() {
    this.famlilyInfo.estateName = this.store_housingName
  },
  onShow() {
     // 基础库2.9.5才支持
    if (compareVersion('2.9.5') > -1) {
      wepy.wx.hideHomeButton()
    }
  },
  computed: {
    ...getters(),
    showAddressFull: function() {
      let str = ''
      if (this.store_streetOfficeName) {
        str += this.store_streetOfficeName
      }
      if (this.store_housingName) {
        str += this.store_housingName
      }
      return str
    },
    showLoaddingImage: function() {
      if (!this.isLoaded) {
        return ''
      } else {
        return 'none'
      }
    },
    showQrImage: function() {
      if (this.isLoaded) {
        return 1
      } else {
        return 0
      }
    }
  },
  methods: {
    onloadComplete(e) {
      this.isLoaded = true
      this.loadSuccess = true
    },
    onloadError(e) {
      this.isLoaded = false
      this.loadSuccess = true
    }
  }
})
</script>
<config>
  {
    navigationBarTitleText: '门卫防控',
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
          width: 552rpx;
          height: 552rpx;
          margin-top: 20rpx;
          position: relative;
          .absolute {
            width: 100%;
            height: 100%;
            position: absolute;
            left: 0;
            top: 0;
          }

          .image-code {
            width: 552rpx;
            height: 552rpx;
          }
        }
        .loading {
          display: flex;
          justify-content: center;
          align-items: center;
          .loading-image {
            width: 28rpx;
            height: 28rpx;
            margin-left: 20rpx;
          }
        }
        .address {
          text-align: center;
          font-size: 28rpx;
          height: 40rpx;
        }
      }
      &.out-padding {
        padding-top: 50rpx;
        padding-left: 100rpx;
        padding-right: 100rpx;
        padding-bottom: 100rpx;
        display: flex;
        flex-direction: column;
        align-items: center;
        color: #ff5309;
        text-align: center;
        image {
          height: 140rpx;
          width: 522rpx;
        }
      }
    }
  }
}
</style>
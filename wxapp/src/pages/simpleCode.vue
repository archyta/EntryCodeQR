

<template>
  <div class="main">
    <div class="container">
      <mp-toptips :delay="5000" :msg="error" :show="errorShow" @hide="onErrorHidden" type="error"></mp-toptips>
      <panel class="panel img" v-if="!successModal">
        <div class="wrap">
          <div class="text-center title">{{userName}}</div>
          <canvas canvas-id="qrcode" class="image" />
          <div class="address">{{showAddressFull}}</div>
          <div class="count-enterss">
            <div class="in-wrapper">
              <div class="count" :class="{'danger-text': outDanger}">{{parentData.outCount}}次</div>
              <div>{{parentData.rangeText}}出门</div>
            </div>
            <div class="out-wrapper">
              <div class="count" :class="{'danger-text': inDanger}">{{parentData.inCount}}次</div>
              <div>{{parentData.rangeText}}进门</div>
            </div>
          </div>
          <div class="community">{{store_housingName}}</div>
          <div class="temputare-wrap">
            <input placeholder="请输入您的体温" type="digit" v-model="temperature" />
            <span>℃</span>
          </div>
        </div>
        <div class="radio-wrap">
          <radio-group bindchange="radioChange" class="radio-group">
            <radio :checked="checked" value="1" class="radio" color="#3A6EFF" style="color:#3A6EFF;zoom:1.2" v-if="!outDanger">
              <text>出门</text>
            </radio>
            <radio :checked="checked" value="0" class="radio" color="#00AF51" style="color:#00AF51;zoom:1.2" v-if="!inDanger">
              <text>进门</text>
            </radio>
          </radio-group>
        </div>

        <!-- <button @tap.stop="drawERCode" class="btn-refresh" v-if="!isFobidden">
          <mp-icon :size="16" color="#3A6EFF" icon="refresh"></mp-icon>
          <span class="refresh-text">刷新</span>
        </button>
        <div class="limit-warn" v-else>
          <span>您的出入次数已达小区限行上限</span>
        </div>-->
        <button @tap="commitTogo" class="sure-btn" type="primary">
          <span class="text">登记进出记录</span>
        </button>
        <div class="time-addition">
          <span>{{timenow}}</span>
        </div>
      </panel>
      <!-- <panel class="panel bor-t-1 out-padding">
        <image src="{{'./../static/image/passport.png'}}" />
      </panel>-->
      <div class="modal-wrap" v-else>
        <div class="modal-content">
          <div class="icon-wrap">
            <icon :size="40" color="#00AF51" type="success" />
          </div>
          <div class="direction-desc">
            <span v-if="direction == '0'">进门</span>
            <span v-else style="color:#3A6EFF;">出门</span>
          </div>
          <div class="tem" v-if="temperature">
            {{temperature}}
            <span>℃</span>
          </div>
          <div class="time-back">{{timenow}}</div>
          <div class="status-desc">登记成功</div>
        </div>
        <button @tap="goBack" class="sure-btn" style="margin-top: 64rpx;" type="primary">
          <span class="text">返回首页</span>
        </button>
      </div>
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
import { simplePass } from '@/api/owner'

import wxbarcode from 'wxbarcode'
import { formate, compareVersion } from '@/utils/common.js'

wepy.page({
  store,
  hooks: {},
  data: {
    userName: '',
    error: '',
    errorShow: false,
    qrdata: {},
    userId: '',
    timenow: '',
    parentData: {},
    successModal: false,
    temperature: '',
    direction: '',
    famlilyInfo: {
      estateName: '',
      building: '',
      unit: '',
      roomNumber: ''
    }
  },
  onLoad(option) {
    const { name, outCount, inCount, limitCount, rangeText, ...rest } = option
    this.parentData = {
      outCount: +outCount,
      inCount: +inCount,
      limitCount: +limitCount,
      rangeText
    }
    this.userId = option.userId
    this.timenow = formate(new Date().getTime(), 'yyyy/MM/dd hh:mm')
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
    },
    outDanger() {
      return this.parentData.limitCount !== -1 && this.parentData.outCount >= this.parentData.limitCount
    },
    inDanger() {
      return this.parentData.limitCount !== -1 && this.parentData.inCount >= this.parentData.limitCount
    }
  },
  methods: {
    commitTogo() {
      let back = this.rangex()
      if (back) {
        this.openError(back)
        return
      }
      let params = null
      if (this.temperature === null || this.temperature == '') {
        params = {
          tripRecord: {
            status: this.direction,
            userId: this.userId,
            direction: this.direction,
            familyId: this.store_familyId,
            estateId: this.store_housingEstateId
          },
          healthStatus: {
            temperature: null
          }
        }
      } else {
        params = {
          tripRecord: {
            status: this.direction,
            userId: this.userId,
            direction: this.direction,
            familyId: this.store_familyId,
            estateId: this.store_housingEstateId
          },
          healthStatus: {
            temperature: +this.temperature
          }
        }
      }
      wx.showLoading({
        title: '加载中',
        mask: true
      })
      simplePass(params)
        .then(res => {
          wx.hideLoading()
          if (res.data.status === 200) {
            this.timenow = res.data.data.tripRecord.crtTime
            this.successModal = true
          } else {
            if (res.data.message) {
              this.openError(res.data.message)
            } else {
              this.openError('网络繁忙，请稍后再试')
            }
          }
        })
        .catch(err => {
          wx.hideLoading()
          if (err.data && err.data.message) {
            this.openError(err.data.message)
          } else {
            this.openError('网络繁忙，请稍后再试')
          }
        })
    },
    rangex() {
      if (this.temperature) {
        let value = +this.temperature
        if (value > 45 || value < 30) {
          return '温度范围异常，请输入30-45之前的温度值'
        }
        // const reg = /^\d+(\.\d+)?$/
        if (!value) {
          return '温度只能填入数字'
        }
      }
      if (!this.direction) {
        return '请选择出入方向'
      }
    },
    goBack() {
      wx.navigateBack()
    },
    drawERCode() {
      this.qrdata.expiredDate = new Date().getTime() + 300 * 1000
      // const dpr = wx.getSystemInfoSync().pixelRatio/2
      wxbarcode.qrcode('qrcode', JSON.stringify(encodeObj(this.qrdata)), 300, 300)
    },
    radioChange(e) {
      this.direction = e.$wx.detail.value
    },
    openError(msg) {
      this.error = msg
      this.errorShow = true
    },
    showError(msg) {
      this.error = msg
      this.errorShow = true
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
    navigationBarTitleText: '出示通行证',
    usingComponents: {
      'mp-icon':'module:weui-miniprogram/miniprogram_dist/icon/icon',
      'mp-toptips':'module:weui-miniprogram/miniprogram_dist/toptips/toptips'

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
  .sure-btn {
    height: 88rpx;
    background-color: #3a6eff;
    font-size: 32rpx;
    display: flex;
    justify-content: center;
    width: 100%;
    border-radius: 8rpx;
    justify-items: center;
    box-shadow: 0rpx 4rpx 6rpx 0rpx rgba(57, 57, 57, 0.05);
    .text {
      margin-left: 10rpx;
      margin-top: 4rpx;
    }
  }
  .panel {
    display: flex;
    background-color: #fff;
    border-radius: 8rpx;
    &.img {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 76rpx 56rpx 20rpx;
      .wrap {
        border: 2rpx solid #222;
        padding-top: 45rpx;
        position: relative;
        width: 100%;
        display: flex;
        flex-direction: column;
        align-items: center;
        &::after {
          content: ' ';
          position: absolute;
          height: 2rpx;
          width: 100%;
          border-bottom: 2rpx solid #222;
          left: 0;
          bottom: 100rpx;
        }
        .count-enterss {
          margin-top: 20rpx;
          display: flex;
          width: 400rpx;
          position: relative;
          line-height: 63rpx;
          &:after {
            position: absolute;
            content: ' ';
            width: 1rpx;
            height: 90%;
            border-right: 1rpx solid #222;
            top: 5%;
            left: 50%;
          }
        }
        .community {
          line-height: 110rpx;
        }
        .in-wrapper,
        .out-wrapper {
          flex: 1;
          display: inline-flex;
          flex-direction: column;
          align-items: center;
          text-align: center;
          font-size: 40rpx;
          .count {
            font-size: 56rpx;
            &.danger-text {
              color: #f00;
            }
          }
        }
        .temputare-wrap {
          display: flex;
          justify-content: flex-end;
          align-items: center;
          height: 100rpx;
          input {
            text-align: center;
          }
        }
      }
      .radio-wrap {
        font-size: 69rpx;
        margin: 40rpx 0;
        width: 100%;
        .radio-group {
          width: 100%;
          display: flex;
          justify-content: space-around;
          text {
            padding-left: 20rpx;
            display: inline-block;
          }
        }
      }

      .time-addition {
        margin-top: 20rpx;
        color: #222;
      }
      .title {
        position: absolute;
        font-weight: bold;
        left: 50%;
        transform: translate(-50%, -50%);
        text-align: center;
        font-size: 64rpx;
        line-height: 90rpx;
        background: #fff;
        padding: 0 60rpx;
        white-space: nowrap;
        top: 0;
      }
      .image {
        width: 300rpx;
        height: 300rpx;
        margin-top: 20rpx;
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
        box-shadow: 0rpx 2rpx 6rpx 0rpx rgba(237, 0, 0, 0.36);
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
        color: #262626;
        font-size: 44rpx;
        line-height: 60rpx;
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

  .modal-wrap {
    position: fixed;
    width: 100%;
    height: 100%;
    background: #fff;
    left: 0;
    top: 0;
    padding: 80rpx 56rpx;
    display: flex;
    justify-content: center;
    align-items: center;
    box-sizing: border-box;
    display: flex;
    flex-direction: column;
    justify-content: center;
    .modal-content {
      position: relative;
      width: 100%;
      border: 2rpx solid #222;
      height: 638rpx;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      .icon-wrap {
        position: absolute;
        left: 50%;
        top: 0;
        transform: translate(-50%, -50%);
        background: #fff;
        padding: 0 60rpx;
      }
      .direction-desc {
        font-size: 128rpx;
        color: #00af51;
      }
      .tem {
        font-size: 94rpx;
        color: #222;
      }
      .status-desc {
        color: #00af51;
        font-size: 68rpx;
      }
      .time-back {
        font-size: 36rpx;
        color: #222;
      }

      &.out {
        .direction-desc {
          color: #3a6eff;
        }
      }
    }
  }
}
</style>
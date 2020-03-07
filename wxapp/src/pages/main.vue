<template>
  <div class="main">
    <div class="container">
      <mp-toptips :delay="5000" :msg="error" :show="errorShow" @hide="onErrorHidden" type="error"></mp-toptips>
      <image class="width" src="{{'./../static/image/register.png'}}" />

      <div class="buttons">
        <div @tap="goManageIndex" class="btn">我是物业</div>
        <div @tap="goBase" class="btn">我是居民</div>
      </div>
      <div class="contact">
        <!-- v-if="contactPhone" -->
        <span @tap="showContact = true" class="calls" v-if="contactPhone">
          <!-- <image src="{{'./../static/icon/call.png'}}" /> -->
          联系我们
        </span>
        <span @tap="showModalSecond = true" class="calls">
          <!-- <image src="{{'./../static/icon/call.png'}}" /> -->
          我要推荐
        </span>
      </div>
    </div>

    <div @tap.stop="showContact = false" class="bg_modal" v-if="showContact&&contactPhone">
      <div class="contact_wrapper">
        <div class="contact-title">
          <span>“出入福安”与您共克时艰</span>
          <!-- <mp-icon @tap.stop="showContact = false" :size="32" color="#222" icon="close"></mp-icon> -->
        </div>
        <div class="body_desc">
          <span class="main_desc">{{contactName}} {{contactPhone}}</span>
          <span class="addition">服务支持或者问题解决</span>
        </div>
        <div @tap.stop="makeCall" class="button">
          <button>拨打电话</button>
        </div>
      </div>
    </div>
    <div @tap.stop="showModalSecond = false" class="bg_modal" v-if="showModalSecond">
      <div class="share_view">
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
</template>

<script>
import wepy from '@wepy/core'
import eventHub from '../common/eventHub'
import store from '../store'
import getters from '@/store/getters'
import { getEstateInfo } from '@/api/login'
import { getBaseInfo, getOutInRecord } from '@/api/owner'
import { getContactPhone } from '@/api/common/index.js'
import { compareVersion } from '@/utils/common.js'
wepy.page({
  store,
  hooks: {},
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
  data: {
    showContact: false,
    contactPhone: '',
    contactName: '',
    error: '',
    errorShow: false,
    showModalSecond: false,
    agentId: ''
  },
  computed: {
    ...getters(),
    isShowInfo: function() {
      return this.store_hasLogin
    }
  },
  onLoad(options) {
    // 如果存在代理信息
    if (options.agentId) {
      this.agentId = options.agentId
      wepy.wx.setStorage('agentId', options.agentId)
    }
    this.autoJump()
    this.loadPhoneData()
    eventHub.$on('login-success', (...args) => {
      this.autoJump()
    })
  },
  onShow() {
    // 基础库2.9.5才支持
    if (compareVersion('2.9.5') > -1) {
      wepy.wx.hideHomeButton()
    }
  },
  methods: {
    // 去物业界面
    goManageIndex() {
      this.$navigate('/pages/manageIndex')
    },
    goBase() {
      this.$navigate('/pages/examples')
    },
    makeCall() {
      let self = this
      wx.makePhoneCall({
        phoneNumber: this.contactPhone,
        complete() {
          self.showContact = false
        }
      })
    },
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
    },
    autoJump() {
      if (this.store_userId && this.store_role && Array.isArray(this.store_role)) {
        // 若存在管理权限更高的角色 则切换到更高的角色界面去
        if (this.store_role.indexOf('property') > -1) {
          wepy.wx.redirectTo('/pages/manageIndex')
        } else if (this.store_role.indexOf('guard') > -1) {
          wepy.wx.redirectTo('/pages/securityIndex')
        } else if (this.store_role.indexOf('owner') > -1) {
          wepy.wx.redirectTo('/pages/index')
        }
      }
    },
    loadPhoneData() {
      getContactPhone(this.store_housingEstateId, this.agentId)
        .then(res => {
          if (res.data.status === 200 && res.data.data) {
            this.contactPhone = res.data.data.mobilePhone
            this.contactName = res.data.data.name
          } else {
            if (res.data.message) {
              this.error = res.data.message
            } else {
              this.error = '网络繁忙，请稍后再试'
            }
            this.errorShow = true
            this.contactPhone = ''
            this.contactName = ''
          }
        })
        .catch(err => {
          if (err.data.message) {
            this.error = err.data.message
          } else {
            this.error = '网络繁忙，请稍后再试'
          }
          this.errorShow = true
        })
    }
  },
  created() {}
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
  background-color: #f0f0f0;
}
.main {
  display: flex;
  position: relative;
  flex-direction: column;

  .container {
    display: flex;
    flex-direction: column;
    width: 100vw;
    min-height: 100vh;
    background-color: #003eed;
    align-items: center;
    .contact {
      margin-top: 40rpx;
      padding: 10rpx;
      box-sizing: border-box;
      width: 560rpx;
      display: flex;
      justify-content: space-between;
    }
    .calls {
      font-size: 26rpx;
      // width: 250rpx;
      // height: 100rpx;
      // right: 20rpx;
      // top: 20rpx;
      // text-align: right;
      // position: absolute;
      color: #767eff;
      // image {
      //   width: 40rpx;
      //   height: 40rpx;
      //   vertical-align: middle;
      //   margin-right: 20rpx;
      // }
    }

    // background-image:  url('./../static/image/register.png');

    image {
      width: 640rpx;
      height: 658rpx;
      margin-top: 20rpx;
    }
    .buttons {
      display: flex;
      margin-top: 128rpx;
      width: 560rpx;
      color: #fff;
      justify-content: space-between;
      font-size: 38rpx;
      .btn {
        width: 250rpx;
        height: 250rpx;
        text-align: center;
        line-height: 250rpx;
        border-radius: 50%;
        background: #1a55ff;
        box-shadow: 0rpx 4rpx 8rpx 0rpx rgba(4, 6, 67, 0.13);
        &:active {
          background: #003eed;
        }
      }
    }
  }
  .bg_modal {
    position: fixed;
    width: 100vw;
    height: 100vh;
    left: 0;
    top: 0;
    background: rgba(234, 234, 264, 0.93);
    display: flex;
    justify-content: center;
    align-items: center;
    .contact_wrapper {
      width: 670rpx;
      height: 380rpx;
      background: #fff;
      border-radius: 8rpx;
      box-sizing: border-box;
      display: flex;
      flex-direction: column;
      .contact-title {
        margin-top: 40rpx;
        padding-left: 20rpx;
        padding-right: 20rpx;
        font-size: 32rpx;
        display: flex;

        align-items: center;
        // justify-content: space-between;
      }
      .body_desc {
        color: #222;
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        padding: 30rpx;
        .main_desc {
          font-weight: 600;
          font-size: 44rpx;
          color: #3a6eff;
        }
        .addition {
          font-size: 30rpx;
          margin-top: 20rpx;
        }
      }
      .button button {
        margin-top: 16rpx;
        background: #3a6eff;
        color: #fff;
        border-radius: 0;
      }
    }
  }
}
.bg_modal {
  // padding-top: 64rpx;
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
</style>

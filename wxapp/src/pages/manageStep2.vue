
<template>
  <div class="main">
    <mp-toptips :delay="5000" :msg="error" :show="errorShow" @hide="onErrorHidden" type="error"></mp-toptips>
    <div class="container">
      <!--  -->
      <div class="img-wrap">
        <image lazy-load="true" mode="scaleToFill" src="{{'./../static/image/poster.png'}}" />
      </div>
      <div class="desc-wrap">
        <div class="desc">
          <div class="row">
            <span class="bg-blue">1</span>
            <span>将此海报张贴于小区出、入口位置；</span>
          </div>
          <div class="row">
            <span class="bg-blue">2</span>
            <span>通知小区住户扫此码领取通行证；</span>
          </div>
          <div class="row">
            <span class="bg-blue">3</span>
            <span>住户进出时，扫此码出示通行证。</span>
          </div>
        </div>
      </div>
      <div class="button_wrap">
        <button @tap="onSave" class="panel deal-btn" type>
          <span class="text">保存到相册</span>
        </button>
        <button :class="{'disabled': !canNextStep}" @tap="nextStep" class="panel deal-btn" type>
          <span class="text">下一步（2/3）</span>
        </button>
      </div>
      <mp-dialog :show="dialogShow" @close="dialogShow=false">
        <div slot="title">
          <div class="main-title">提示</div>
          <div class="dialog__title">相册权限获取失败,无法保存,请授权！</div>
        </div>
        <div style="width: 700rpx;">
          <button @tap="dialogShow=false" class="open-settting" open-type="openSetting">打开设置</button>
        </div>
      </mp-dialog>
    </div>
  </div>
</template>

<script>
import wepy from '@wepy/core'
import eventHub from '../common/eventHub'
import { mapState, mapActions } from '@wepy/x'
import getters from '@/store/getters'
import store from '../store'
import { getImageUrl, downloadImage } from '@/api/common'
import { getUserPosterUrl, getPostUrl } from '@/api/manage'
import { compareVersion } from '@/utils/common.js'

wepy.page({
  store,
  hooks: {},
  data: {
    error: '',
    errorShow: false,
    canNextStep: false,
    dialogShow: false,
    id: ''
  },
  computed: {
    ...getters()
  },
  onLoad(option) {
    this.id = option.id
  },
  onShow() {
    // 基础库2.9.5才支持
    if (compareVersion('2.9.5') > -1) {
      wepy.wx.hideHomeButton()
    }
  },

  methods: {
    nextStep() {
      if (this.canNextStep) {
        this.$navigate(`/pages/manageStep3?id=${this.id}`)
      }
    },
    checkAuth() {
      let self = this
      wx.getSetting({
        success(res) {
          console.log(res)
          var authMap = res.authSetting
          var length = Object.keys(authMap).length
          console.log('长度:' + length)
          var isAuthAddress = 0 //是否授权通过，有三种情况，0:从未授权，1:授权成功，2:授权失败
          // if ('scope.address' in authMap) {
          if (authMap.hasOwnProperty('scope.writePhotosAlbum')) {
            if (authMap['scope.writePhotosAlbum']) {
              //已经授权成功
              isAuthAddress = 1
            } else {
              //授权拒绝
              isAuthAddress = 2
            }
          } else {
            //没有授权过
          }
          if (isAuthAddress !== 1) {
            self.dialogShow = true
          }
        }
      })
    },
    onSave() {
      this.canNextStep = true
      let accountInfo = wx.getAccountInfoSync()
      getUserPosterUrl(accountInfo.miniProgram.appId, this.id).then(res => {
        if (res.data.status === 200) {
          downloadImage(getPostUrl(res.data.data, 'owner')).catch(error => {
            this.checkAuth()
          })
        } else {
          this.openError('服务端异常，请稍后再试...')
        }
      })
    },
    downLoad() {},
    openError(msg) {
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
    navigationBarTitleText: '生成扫码海报',
    usingComponents: {
      'mp-form':'module:weui-miniprogram/miniprogram_dist/form/form',
      'mp-cell':'module:weui-miniprogram/miniprogram_dist/cell/cell',
      'mp-dialog':'module:weui-miniprogram/miniprogram_dist/dialog/dialog',
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
button:active {
  background: #eee;
}
.container {
  display: flex;
  flex-direction: column;
  margin-bottom: 110rpx;
  // margin: 20rpx;
  .img-wrap {
    margin-top: 70rpx;
    display: flex;
    justify-content: center;

    image {
      width: 572rpx;
      height: 732rpx;
    }
  }
  .desc-wrap {
    display: flex;
    justify-content: center;
    line-height: 60rpx;
    font-size: 32rpx;
    margin-top: 50rpx;
    color: #222222;
    .desc {
      .bg-blue {
        background: #3a6eff;
        color: #fff;
        border-radius: 50%;
        display: inline-block;
        height: 40rpx;
        width: 40rpx;
        vertical-align: middle;
        line-height: 40rpx;
        text-align: center;
        margin-right: 20rpx;
      }
    }
  }

  .button_wrap {
    display: flex;
    justify-content: stretch;
    align-items: center;
    line-height: 100rpx;
    // margin-top: 30rpx;
    position: fixed;
    width: 750rpx;
    left:0;
    bottom: 0;
    background: #fff;

    .deal-btn {
      height: 100rpx;
      border-radius: 0;
      background-color: #fff;
      width: 100%;
      font-size: 32rpx;
      display: flex;
      justify-content: center;
      justify-items: center;
      box-shadow: 0px 4px 6px 0px rgba(57, 57, 57, 0.05);
      &::after {
        border-radius: 0;
      }
      &.disabled {
        color: #ccc;
      }
      .text {
        margin-left: 10rpx;
        margin-top: 8rpx;
      }
    }
  }
}
.weui-dialog {
  border-radius: 8rpx !important;
  .main-title {
    font-size: 36rpx;
    font-weight: 600;
    padding-bottom: 8px;
    border-bottom: 0.5px solid #eee;
    transform: translateY(-26rpx);
    text-align: left;
  }
  .open-settting {
    font-size: 13px;
    margin-top: 55rpx;
    line-height: 100rpx;
    width: 100%;
    height: 100rpx;
    background: #3a6eff;
    color: #fff;
    border-radius: 0;
    &::after {
      border-radius: 0;
    }
  }
  .weui-dialog__ft {
    display: none !important;
  }
  .weui-dialog__hd {
    height: 100rpx;

    line-height: 100rpx;
    .dialog__title {
      font-weight: 400;
      font-size: 32rpx;
      text-align: left;
      color: #222222;
      line-height: 60rpx;
    }
  }
  .weui-dialog__bd {
    margin-bottom: 0 !important;
    display: flex;
    padding: 0 !important;
  }
}
</style>
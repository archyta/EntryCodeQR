
<template>
  <div class="main">
    <mp-toptips :delay="5000" :msg="error" :show="errorShow" @hide="onErrorHidden" type="error"></mp-toptips>
    <div class="container">
      <!--  -->
      <div class="img-wrap">
        <div class="cont_wrap">
          <div class="title">保安端注册</div>
          <div class="image">
            <div :style="{'opacity':showQrImage}" class="absolute">
              <image :src="url" @error="onloadError" @load="onloadComplete" class="img_self" lazy-load="true" mode="scaleToFill" />
            </div>
            <div :style="{'display':showLoaddingImage}" class="absolute loading">
              <span v-if="!loadSuccess">
                <text>加载中</text>
                <image class="loading-image" src="{{'./../static/icon/loading.gif'}}" />
              </span>
              <span v-else>不好意思，加载失败咯！</span>
            </div>
          </div>
          <div class="estateName">{{store_housingName}}</div>
          <div class="desc-wrap">
            <div class="desc" style="color: #fff;line-height: 50rpx;">
              <div class="row">
                <span class="bg-blue">1.</span>
                <span>保安扫此码注册保安端账号。</span>
              </div>
              <div class="row">
                <span class="bg-blue">2.</span>
                <span>
                  成功注册后，用此工具扫住户通行证，
                  并查验身份，确定是放行还是劝返。
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="button_wrap">
        <button @tap="onSave" class="panel deal-btn">
          <span class="text">保存到相册</span>
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
import { getGuardPosterUrl, getPostUrl } from '@/api/manage'
import { getERCode } from '@/api/guards'
import { compareVersion } from '@/utils/common.js'

wepy.page({
  store,
  hooks: {},
  data: {
    error: '',
    errorShow: false,
    isLoaded: false,
    canNextStep: true,
    loadSuccess: false,
    dialogShow: false,
    id: '',
    url: ''
  },
  computed: {
    ...getters(),
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
  onLoad(option) {
    this.id = option.id
  },
  onReady() {
    let accountInfo = wx.getAccountInfoSync()
    this.url = getERCode(accountInfo.miniProgram.appId, 'community', this.store_housingEstateId)
  },
  onShow() {
    // 基础库2.9.5才支持
    if (compareVersion('2.9.5') > -1) {
      wepy.wx.hideHomeButton()
    }
  },

  methods: {
    nextStep() {
      // this.$navigate('/pages/manageIndex')
      wx.navigateBack({
        delta: 3
      })
    },
    onSave() {
      this.canNextStep = true
      let accountInfo = wx.getAccountInfoSync()
      getGuardPosterUrl(accountInfo.miniProgram.appId, this.id).then(res => {
        if (res.data.status === 200) {
          downloadImage(getPostUrl(res.data.data, 'guard')).catch(error => {
            this.checkAuth()
          })
        } else {
          this.openError('服务端异常，请稍后再试...')
        }
      })
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
    onloadComplete(e) {
      this.isLoaded = true
      this.loadSuccess = true
    },
    onloadError(e) {
      this.isLoaded = false
      this.loadSuccess = true
    },
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
    navigationBarTitleText: '保安注册',
    usingComponents: {
      'mp-form':'module:weui-miniprogram/miniprogram_dist/form/form',
      'mp-cell':'module:weui-miniprogram/miniprogram_dist/cell/cell',
      'mp-toptips':'module:weui-miniprogram/miniprogram_dist/toptips/toptips',
      'mp-dialog':'module:weui-miniprogram/miniprogram_dist/dialog/dialog',
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
.mgt-10 {
  margin-top: 10rpx;
}
.text-center {
  text-align: center;
}
.nowrap {
  white-space: nowrap;
}
.required {
  color: #f00;
  vertical-align: middle;
  margin-left: 5rpx;
}
button:active {
  background: #eee;
}
.container {
  display: flex;
  flex-direction: column;
  // margin: 20rpx;
  .img-wrap {
    display: flex;
    justify-content: center;
    .image {
      width: 440rpx;
      height: 440rpx;
      border-radius: 50%;
      overflow: hidden;
      position: relative;
      background: #fff;
      display: flex;
      align-items: center;
      justify-content: center;
      .absolute {
        position: absolute;
        align-items: center;
        justify-content: center;
        display: flex;
        width: 380rpx;
        height: 380rpx;
        left: 30rpx;
        top: 30rpx;
        & > image {
          width: 380rpx;
          height: 380rpx;
        }
      }
    }
    .cont_wrap {
      width: 750rpx;
      height: calc(100vh - 100rpx);
      background: #3a6eff;
      display: flex;
      flex-direction: column;
      align-items: center;
      .title {
        font-size: 54rpx;
        color: #fff;
        margin-top: 76rpx;
      }
      .estateName {
        color: #fff;
        font-size: 62rpx;
        margin-top: 50rpx;
      }
      .loading {
        font-size: 32rpx;
        color: #666;
        text-align: center;
        image {
          width: 20rpx;
          height: 20rpx;
          margin-left: 20rpx;
        }
      }
    }
  }
  .desc-wrap {
    display: flex;
    justify-content: center;
    line-height: 80rpx;
    font-size: 32rpx;
    margin-top: 50rpx;
    color: #222222;
    .desc {
      .row {
        max-width: 500rpx;
        line-height: 70rpx;
        display: flex;
      }
      .bg-blue {
        background: #3a6eff;
        color: #fff;
        border-radius: 50%;
        display: inline-block;
        justify-content: flex-start;
        height: 40rpx;
        flex: 0 0 40rpx;
        vertical-align: middle;
        line-height: 40rpx;
        text-align: center;
        margin-right: 20rpx;
        margin-top: 10rpx;
      }
    }
  }

  .addition-img {
    height: 400rpx;
    border: 1rpx solid #eee;
    border-radius: 8rpx;
    background: #fff;
  }
  .addition {
    font-size: 26rpx;
    color: #222222;
  }
  .button_wrap {
    display: flex;
    justify-content: stretch;
    align-items: center;
    line-height: 100rpx;
    background: #fff;
    .deal-btn {
      height: 100rpx;
      background-color: #fff;
      width: 100%;
      font-size: 32rpx;
      display: flex;
      justify-content: center;
      justify-items: center;
      box-shadow: 0px 4px 6px 0px rgba(57, 57, 57, 0.05);
      .text {
        margin-left: 10rpx;
        margin-top: 8rpx;
      }
    }
  }

  .btn-wrap {
    display: flex;
    flex-flow: row wrap;
    justify-content: space-between;
    margin-top: 20rpx;
    .click-btn {
      box-sizing: border-box;
      background-color: white;
      text-align: center;
      flex: 0 0 90rpx;
      border-radius: 8rpx;
      margin: 20rpx 15rpx 0;
      height: 54rpx;
      line-height: 54rpx;
      border: 1rpx solid rgb(234, 234, 234);
      &:active {
        background: #eee;
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
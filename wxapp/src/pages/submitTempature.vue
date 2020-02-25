
<template>
  <div class="main">
    <div class="container">
      <mp-toptips :delay="5000" :msg="error" :show="errorShow" @hide="onErrorHidden" type="error"></mp-toptips>

      <mp-form id="form" models="{{form}}" ref="form" rules="{{rules}}">
        <div class="main-form mgt-10">
          <panel class="panel form-item" style="justify-content: center">
            <span style="font-weight: 600;">{{userName}}</span>
          </panel>
          <panel :class="{'danger': err === 'temperature'}" class="panel form-item">
            <div>
              <span>
                当前体温
                <span class="required">*</span>
              </span>
            </div>
            <input placeholder="请输入体温" type="digit" v-model="form.temperature" />
          </panel>
          <panel class="panel form-item last">
            <span>登记时间</span>
            <div class="right-text">
              <span>{{timenow}}</span>
            </div>
          </panel>
        </div>
      </mp-form>
      <button @tap="commitBaseinfo" class="panel submit-btn" type="primary">
        <span class="text">保存体温记录</span>
      </button>
    </div>
  </div>
</template>

<script>
import wepy from '@wepy/core'
import eventHub from '../common/eventHub'
import { mapState, mapActions } from '@wepy/x'
import getters from '@/store/getters'
import store from '../store'
import { submitTempature } from '@/api/owner'
import { formate, compareVersion } from '@/utils/common.js'

wepy.page({
  store,
  hooks: {},
  data: {
    userName: '',
    typeF: false,
    error: '',
    errorShow: false,
    err: '',
    showOneButtonDialog: false,
    buttons: [],
    timenow: '',
    clickBack: '',
    canSendCode: true,
    delayTime: 0,
    delayIntervel: null,
    form: {
      temperature: ''
    },
    rules: [
      {
        name: 'temperature',
        rules: [
          { required: true, message: '请填写温度' },
          {
            validator: function(rule, value, param, modeels) {
              if (value >= 50 || value < 25) {
                return '温度范围异常，请输入25-50之前的温度值'
              }
              if (value && !(/^\d+(\.\d+)?$/.test(value))) {
                return '温度只能填入数字'
              }
            }
          }
        ]
      }
    ]
  },
  computed: {
    ...getters()
  },
  onLoad(option) {
    this.userName = option.userName
    this.timenow = formate(new Date().getTime(), 'yyyy/MM/dd hh:mm')
    this.form.userId = option.userId
    this.form.familyId = this.store_familyId
    this.form.estateId = this.store_housingEstateId
  },
  onShow() {
    // 基础库2.9.5才支持
    if (compareVersion('2.9.5') > -1) {
      wepy.wx.hideHomeButton()
    }
  },

  methods: {
    commitBaseinfo() {
      // wx.navigateBack();
      this.$wx.selectComponent('#form').validate((valid, errors) => {
        if (!valid) {
          this.err = errors[0].name
          this.error = errors[0].message
          this.errorShow = true
        } else {
          this.err = ''
          wx.showLoading({
            title: '加载中',
            mask: true
          })
          submitTempature(this.form)
            .then(res => {
              wx.hideLoading()
              if (res.data.status === 200) {
                wx.navigateBack()
              } else {
                if (res.data && res.data.message) {
                  this.openError(res.data.message)
                } else {
                  this.openError('好像服务处理点问题哟,请稍后重试！')
                }
              }
            })
            .catch(err => {
              wx.hideLoading()
              if (err.data && err.data.message) {
                this.openError(err.data.message)
              } else {
                this.openError('好像服务处理点问题哟,请稍后重试！')
              }
            })
        }
      })
    },
    // onTemperatureInputChange(e) {
    //   let value = e.$wx.detail.value
    //   let m = this.convertNumber(value, 2)
    //   if (m) {
    //     this.form.temperature = m.toString()
    //   } else {
    //     this.form.temperature = null
    //   }
    // },
    convertNumber(value, decimalPlace) {
      if (value != null && value !== '') {
        value = Number(value)
        if (isNaN(value) || !isFinite(value)) {
          return null
        }
        if (decimalPlace === undefined) {
          decimalPlace = 0
        }
        value = value.toFixed(decimalPlace)
        return Number(value)
      } else {
        return null
      }
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
    navigationBarTitleText: '登记体温',
    usingComponents: {
      'mp-form':'module:weui-miniprogram/miniprogram_dist/form/form',
      'mp-cell':'module:weui-miniprogram/miniprogram_dist/cell/cell',
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
.width-image {
  width: 750rpx;
  height: 132rpx;
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
.container {
  display: flex;
  flex-direction: column;
  margin: 20rpx;
  .panel {
    display: flex;
    background-color: #fff;
    border-radius: 8rpx;
  }
  .main-form {
    padding: 0 30rpx;
    background: #fff;
    font-size: 32rpx;
    color: #222;
    input {
      font-size: 32rpx;
    }
  }
  .form-item {
    height: 110rpx;
    display: flex;
    align-items: center;
    justify-content: space-between;
    // box-shadow: 0px 4px 6px 0px rgba(57, 57, 57, 0.05);
    input {
      text-align: right;
      width: 350rpx;
    }
    .right-text {
      text-align: right;
      width: 400rpx;
      height: 40rpx;
    }
    .right-button {
      margin-left: 10rpx;
      text-align: right;
      width: 175rpx;
      // width: 200rpx;
      // height: 40rpx;
      button {
        font-size: 26rpx;
        padding-left: 5rpx;
        padding-right: 5rpx;
        background: #fff;
        color: #3a6eff;
        line-height: 60rpx;
        height: 60rpx;
      }
    }

    &:not(.last) {
      border-radius: 0;
      border-bottom: 1rpx solid #f0f0f0;
    }
    &.danger {
      border-bottom: 1rpx solid #f00;
    }
  }
  .submit-btn {
    margin-top: 30rpx;
    height: 100rpx;
    background-color: #3a6eff;
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
.weui-half-screen-dialog__hd__side {
  display: none;
  // content: ' 确定'
}
.weui-half-screen-dialog__ft {
  padding: 26rpx 24rpx !important;
  .weui-btn {
    background: #3a6eff;
  }
}
</style>
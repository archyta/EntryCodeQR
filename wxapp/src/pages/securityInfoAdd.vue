
<template>
  <div class="main">
    <image class="width-image" src="{{'./../static/image/tips.png'}}" />
    <div class="container">
      <mp-toptips :delay="5000" :msg="error" :show="errorShow" @hide="onErrorHidden" type="error"></mp-toptips>

      <mp-form id="form" models="{{form}}" ref="form" rules="{{rules}}">
        <div class="my-form">
          <div class="main-form">
            <panel class="panel form-item">
              <div style="font-weight: 600">须本人填写，代劳或非本人填写无效！</div>
            </panel>
            <panel :class="{'danger': err === 'name'}" class="panel form-item">
              <div>
                <span>
                  我的真实名字
                  <span class="required">*</span>
                </span>
              </div>
              <input placeholder="请输入姓名" type="text" v-model="form.name" />
            </panel>
            <panel :class="{'danger': err === 'mobilePhone'}" class="panel form-item">
              <div>
                <span>
                  我的手机号码
                  <span class="required">*</span>
                </span>
              </div>
              <input class="phone-number" placeholder="请输入手机号码" type="number" v-model="form.mobilePhone" />
              <div class="right-button">
                <button @tap.stop="sendCode">{{btnGetCodeText}}</button>
              </div>
            </panel>
            <panel :class="{'danger': err === 'verifyCode'}" class="panel form-item">
              <div>
                <span>
                  手机验证码（4位）
                  <span class="required">*</span>
                </span>
              </div>
              <input class="reg_code" placeholder="请输入验证码" type="number" v-model="form.verifyCode" />
            </panel>
            <!-- <panel :class="{'danger': err === 'idNumber'}" class="panel form-item">
              <div>
                <span>身份证号码</span>
              </div>
              <input placeholder="请输入身份证号码" type="idcard" v-model="form.idNumber" />
            </panel>-->
            <panel class="panel form-item">
              <span>我的防控区域</span>
              <div class="right-text">
                <span v-if="estateName">{{estateName}}</span>
              </div>
            </panel>
            <panel class="panel form-item last">
              <span>所在行政区域</span>
              <div class="right-text">
                <span v-if="streetOfficeName">{{streetOfficeName}}</span>
              </div>
            </panel>
          </div>
        </div>
      </mp-form>
      <div class="rules-wrap" v-if="isRegist">
        <checkbox-group @change="checkboxChange">
          <checkbox color="#3a6eff" id="cehckbox_1" value="rules">
            <label>已阅读并同意</label>
          </checkbox>
        </checkbox-group>

        <span @tap="goRules" class="href-page">法律声明及隐私条款</span>
      </div>
      <button @tap="commitBaseinfo" class="panel submit-btn" type="primary">
        <span class="text">提交信息</span>
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
import { isPhoneNumber } from '../utils/valid'
import { addSecurity, getSecurityInfo, updateSecurity } from '@/api/guards'
import { getCodeByPhone } from '@/api/common/index.js'
import { compareVersion } from '@/utils/common.js'

wepy.page({
  store,
  hooks: {},
  data: {
    outNum: 1,
    inNum: 2,
    userName: '',
    typeF: false,
    isRegist: false,
    error: '',
    errorShow: false,
    checkRules: '',
    err: '',
    estateName: '',
    streetOfficeName: '',
    showOneButtonDialog: false,
    buttons: [],
    clickBack: '',
    canSendCode: true,
    delayTime: 0,
    delayIntervel: null,
    form: {
      name: '',
      mobilePhone: '',
      housingEstateId: '',
      openId: '',
      verifyCode: ''
      // idNumber: ''
    },
    rules: [
      {
        name: 'name',
        rules: { required: true, message: '请填写名字' }
      },
      {
        name: 'mobilePhone',
        rules: {
          validator: function(rule, value, param, modeels) {
            if (!value) {
              return '请填写手机号'
            }
            if (value && !isPhoneNumber(value)) {
              return '手机号格式不正确'
            }
          }
        }
      },
      {
        name: 'verifyCode',
        rules: {
          validator: function(rule, value, param, modeels) {
            if (!value) {
              return '请输入验证码'
            }
            if (value && value.length !== 4) {
              return '验证码格式不正确'
            }
          }
        }
      }
      // {
      //   name: 'idNumber',
      //   rules: {
      //     validator: function(rule, value, param, modeels) {
      //       if (value && value.length !== 18 && value.length !== 15) {
      //         return '身份证号格式不正确'
      //       }
      //     }
      //   }
      // }
    ]
  },
  computed: {
    ...getters(),
    btnGetCodeText() {
      if (this.delayTime === 0) {
        return '获取验证码'
      } else {
        return this.delayTime + 'S'
      }
    }
  },
  onLoad(option) {
    if (option.edit) {
      this.isRegist = false
      this.getDetails()
    } else {
      this.isRegist = true
    }
    this.estateName = option.estateName || this.store_housingName
    this.streetOfficeName = option.streetOfficeName || this.store_streetOfficeName
    this.form.housingEstateId = option.estateId || this.store_housingEstateId
    this.form.openId = this.store_openId
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

          if (this.isRegist) {
            if (!this.checkRules) {
              this.error = '请同意隐私条款'
              this.errorShow = true
              return
            }
            wx.showLoading({
              title: '加载中',
              mask: true
            })
            addSecurity(this.form).then(res => {
              if (res.data.status === 200) {
                // this.$store.dispatch('setGuardInfo', res.data.data);
                // 重新登录 并切换到当前小区
                this.$store
                  .dispatch('login', this.form.housingEstateId)
                  .then(res => {
                    // this.$store.dispatch('switchEnstate', this.form.housingEstateId)
                    wx.hideLoading()
                    wx.navigateBack()
                  })
                  .catch(err => {
                    if (err.data && err.data.message) {
                      this.error = err.data.message
                    } else {
                      this.error = '网络繁忙，请稍后再试'
                    }
                    this.error = '网络繁忙，请稍后再试'
                    wx.hideLoading()
                  })
              } else {
                if (res.data && res.data.message) {
                  this.openError(res.data.message)
                } else {
                  this.openError('网络繁忙，请稍后再试')
                }
                wx.hideLoading()
              }
            })
          } else {
            this.form.id = this.store_userId
            wx.showLoading({
              title: '加载中',
              mask: true
            })
            updateSecurity(this.form)
              .then(res => {
                if (res.data.status === 200) {
                  wx.hideLoading()
                  wx.navigateBack()
                } else {
                  if (res.data && res.data.message) {
                    this.openError(res.data.message)
                  } else {
                    this.openError('好像服务处理点问题哟,请稍后再试！!')
                  }
                  wx.hideLoading()
                }
              })
              .catch(err => {
                if (err.data && err.data.message) {
                  this.openError(err.data.message)
                } else {
                  this.openError('好像服务处理点问题哟,请稍后再试！')
                }
                wx.hideLoading()
              })
          }
        }
      })
    },
    checkboxChange(e) {
      if (e.$wx && e.$wx.detail && e.$wx.detail.value && e.$wx.detail.value.length) {
        this.checkRules = e.$wx.detail.value[0]
      } else {
        this.checkRules = ''
      }
    },
    goRules() {
      this.$navigate('/pages/rules')
    },
    openError(msg) {
      this.error = msg
      this.errorShow = true
    },
    getDetails() {
      getSecurityInfo(this.store_userId, this.store_housingEstateId).then(res => {
        if (res && res.data && res.data.data && res.data.data.guardUser) {
          // this.form.idNumber = res.data.data.guardUser.idNumber
          this.form.mobilePhone = res.data.data.guardUser.mobilePhone
          this.form.name = res.data.data.guardUser.name
          this.form.id = res.data.data.guardUser.id
        }
      })
    },
    onErrorHidden() {
      this.error = ''
      this.errorShow = false
    },
    sendCode() {
      if (this.delayIntervel == null && !this.delayTime) {
        if (!this.form.mobilePhone) {
          this.err = 'mobilePhone'
          this.error = '请输入电话号码'
          this.errorShow = true
        } else if (!isPhoneNumber(this.form.mobilePhone)) {
          this.err = 'mobilePhone'
          this.error = '电话号码不正确'
          this.errorShow = true
        } else {
          this.err = ''
          this.errorShow = false
          this.delayTime = 60
          getCodeByPhone(this.store_openId, this.form.mobilePhone)
            .then(res => {
              if (res.data.status === 200) {
                // send vertifycode

                this.delayIntervel = setInterval(() => {
                  if (this.delayTime > 0) {
                    this.delayTime = this.delayTime - 1
                  } else {
                    this.delayTime = 0
                    clearInterval(this.delayIntervel)
                    this.delayIntervel = null
                  }
                }, 1000)
              } else {
                this.delayTime = 0
                if (res.data && res.data.message) {
                  this.error = res.data.message
                } else {
                  this.error = '网络繁忙，请稍后再试'
                }
                this.errorShow = true
              }
            })
            .catch(err => {
              this.delayTime = 0
              if (err.data && err.data.message) {
                this.error = err.data.message
              } else {
                this.error = '网络繁忙，请稍后再试'
              }
              this.errorShow = true
            })
        }
      }
    }
  }
})
</script>
<config>
  {
    navigationBarTitleText: '完善我的信息',
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
.mgt-20 {
  margin-top: 20rpx;
}
.my-form {
  border-radius: 8rpx;
  box-shadow: 0rpx 4rpx 6rpx 0rpx rgba(57, 57, 57, 0.05);
  overflow: hidden;
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
  .halfModel {
    width: 100%;
    box-sizing: border-box;
    padding: 15rpx 20rpx;
    border: 1rpx solid #eee;
    border-radius: 15rpx;
  }
  .panel {
    display: flex;
    background-color: #fff;
    border-radius: 8rpx;
    &.img {
      display: block;
      border-radius: 24rpx;
      height: 180rpx;

      image {
        width: 733rpx;
        height: 198rpx;
        margin-left: -11rpx;
        margin-top: -16rpx;
      }
    }
  }
  .main-form {
    padding: 0 30rpx;
    background: #fff;
    font-size: 32rpx;
    color: #222;
    input {
      font-size: 32rpx;
    }
    .cover-tip {
      color: #fff;
      line-height: 79rpx;

      width: 280rpx;
      height: 100rpx;
      background: #f00;
      position: absolute;
      left: 90rpx;
      top: -100rpx;
      background: #182343;
      border-radius: 20rpx;
      height: 160rpx;
    }
  }
  .form-item {
    height: 110rpx;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding-left: 10rpx;
    padding-right: 10rpx;
    // box-shadow: 0rpx 4rpx 6rpx 0rpx rgba(57, 57, 57, 0.05);
    input {
      text-align: right;
      width: 350rpx;
    }
    .reg_code {
      width: 250rpx;
    }
    .right-text {
      text-align: right;
      width: 400rpx;
      height: 40rpx;
    }
    .phone-number {
      width: 240rpx;
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
      border-bottom: 1rpx solid #ccc;
    }
    &.danger {
      border-bottom: 1rpx solid #f00;
    }
  }
  .inline-form-item {
    display: flex;
    align-items: center;
    justify-content: flex-start;
    height: 100%;
    &.danger {
      position: relative;
      &::after {
        content: ' ';
        position: absolute;
        bottom: 1rpx;
        width: 100%;
        border-bottom: 1rpx solid #f00;
      }
    }

    input {
      width: auto;
    }
    // justify-content: ;
    // justify-content: ;
  }
  .addition {
    font-size: 26rpx;
    color: #222222;
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
    box-shadow: 0rpx 4rpx 6rpx 0rpx rgba(57, 57, 57, 0.05);
    .text {
      margin-left: 10rpx;
      margin-top: 8rpx;
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
        background: #eeeeee;
      }
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
.rules-wrap {
  margin-top: 40rpx;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 20rpx;
  #cehckbox_1 {
    transform: scale(0.8);
    font-size: 40rpx;
  }
  .href-page {
    color: #3a6eff;
    font-size: 32rpx;
    margin-top: 4rpx;
  }
}
</style>
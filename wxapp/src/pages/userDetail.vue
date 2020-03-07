<template>
  <div class="main">
    <div class="container">
      <mp-toptips :delay="5000" :msg="error" :show="errorShow" @hide="onErrorHidden" type="error"></mp-toptips>
      <panel class="panel img">
        <image class="width" src="{{'./../static/image/base.png'}}" />
      </panel>

      <mp-form id="form" models="{{form}}" ref="form" rules="{{rules}}">
        <div class="my-form mgt-20">
          <div class="main-form">
            <panel :class="{ danger: err === 'name' }" class="panel form-item">
              <div>
                <span>
                  姓名
                  <span class="required">*</span>
                </span>
              </div>
              <input placeholder="请输入姓名" type="text" v-model="form.name" />
            </panel>
            <panel :class="{ danger: err === 'mobilePhone' }" class="panel form-item">
              <div>
                <span>
                  手机号码
                  <span class="required" v-if="form.isRegist">*</span>
                </span>
              </div>
              <input class="phoneNum" placeholder="请输入手机号码" type="number" v-model="form.mobilePhone" />
              <div class="right-button" v-if="form.isRegist">
                <button @tap.stop="sendCode">{{btnGetCodeText}}</button>
              </div>
            </panel>
            <panel :class="{'danger': err === 'verifyCode'}" class="panel form-item right-con" v-if="form.isRegist">
              <div>
                <span>
                  手机验证码（4位）
                  <span class="required">*</span>
                </span>
              </div>
              <input :disabled="disabled" class="phoneNum" placeholder="请输入验证码" type="number" v-model="form.verifyCode" />
            </panel>
            <!-- <panel :class="{ danger: err === 'idNumber' }" class="panel form-item">
              <div>
                <span>身份证号</span>
              </div>
              <input placeholder="请输入身份证号" type="idcard" v-model="form.idNumber" />
            </panel>-->
            <panel class="panel form-item">
              <div>
                <span>车牌号1</span>
              </div>
              <input placeholder="请输入车牌号1" type="text" v-model="form.numberPlate" />
            </panel>
            <panel class="panel form-item">
              <div>
                <span>车牌号2</span>
              </div>
              <input placeholder="请输入车牌号2" type="text" v-model="form.numberPlate1" />
            </panel>
            <panel :class="{ danger: err === 'housingEstateName' }" class="panel form-item">
              <span>所在小区</span>
              <input disabled placeholder="扫描二维码后自动生成" type="text" v-model="form.housingEstateName" />
            </panel>
            <panel :class="{ danger: err === 'building' }" @tap="tapBuilding" class="panel form-item">
              <div>
                <span>
                  楼栋
                  <span class="required">*</span>
                </span>
              </div>
              <!-- <input type="text" placeholder="请输入楼栋" readonly @click="showOneButtonDialog = true" v-model="form.building"/> -->
              <div style="text-align:right;width: 500rpx;height: 40rpx;">
                <span v-if="form.building">{{ form.building }}</span>
                <span style="color:#ccc" v-else>请输入楼栋</span>
              </div>
            </panel>
            <panel class="panel form-item last">
              <div :class="{ danger: err === 'unit' }" class="inline-form-item" style="position: relative">
                <!-- <span class="required">*</span> -->
                <span class="nowrap">单元</span>
                <input :disabled="disabled" placeholder="请输入" style="padding-right:10rpx;" type="text" v-model="form.unit" />
              </div>

              <div :class="{ danger: err === 'roomNumber' }" class="inline-form-item">
                <span class="nowrap">
                  房号
                  <span class="required">*</span>
                </span>
                <input :disabled="disabled" placeholder="请输入" type="text" v-model="form.roomNumber" />
              </div>
            </panel>
          </div>
        </div>
      </mp-form>
      <button @tap="commitBaseinfo" class="panel submit-btn" type="primary">
        <span class="text">提交信息</span>
      </button>
      <div class="addition text-center mgt-10">
        请诚实填写以上信息，保证真实有效。
        <br />一经提交将不可修改！
      </div>
      <!-- <mp-actionSheet @actiontap="btnClick" :show="showOneButtonDialog" :actions="groups" title="单元号输入" v-model="clickBack" /> -->
      <mp-halfScreenDialog :key="true" :show="typeF" @close="typeF = false" bindbuttontap="buttonTap">
        <div slot="title">请输入楼栋</div>
        <div slot="desc">
          <div class="halfModel">
            <input placeholder="请输入楼栋" type="text" v-model="form.building" />
          </div>

          <div class="btn-wrap">
            <div :key="item" @tap="enterData(item)" class="click-btn" v-for="item in btnData">{{ item }}</div>
          </div>
        </div>
        <div slot="footer">
          <button @tap="typeF = false" class="weui-btn" type="primary">确认</button>
        </div>
      </mp-halfScreenDialog>
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
import { getBuildings, addMainMember, addMember } from '@/api/owner/index.js'
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
    disabled: false,
    error: '',
    errorShow: false,
    err: '',
    showOneButtonDialog: false,
    buttons: [],
    canSendCode: true,
    delayTime: 0,
    delayIntervel: null,
    // buttonsss: [
    //   {
    //     type: 'default',
    //     className: '',
    //     text: '辅助操作',
    //     value: 0
    // },
    // {
    //     type: 'primary',
    //     className: '',
    //     text: '主操作',
    //     value: 1
    // }],
    btnData: [],
    clickBack: '',
    groups: [
      { text: '去输入楼栋号', value: 1 },
      { text: '本栋（幢）无单元号', value: 2 }
    ],
    form: {
      isRegist: false,
      name: '',
      mobilePhone: '',
      // idNumber: '',
      housingEstateId: '',
      housingEstateName: '',
      building: '',
      unit: '',
      roomNumber: '',
      openId: '',
      verifyCode: '',
      numberPlate1: '', // 车牌号
      numberPlate: '' // 车牌号
    },
    rules: [
      {
        name: 'name',
        rules: { required: true, message: '请输入姓名' }
      },
      {
        name: 'mobilePhone',
        rules: {
          validator: function(rule, value, param, modeels) {
            if (modeels.isRegist) {
              if (!value) {
                return '请输入电话号码'
              }
              if (!isPhoneNumber(value)) {
                return '手机号格式不正确'
              }
            } else {
              if (value && !isPhoneNumber(value)) {
                return '手机号格式不正确'
              }
            }
          }
        }
      },
      // {
      //   name: 'idNumber',
      //   rules: {
      //     validator: function(rule, value, param, modeels) {
      //       if (value && value.length !== 18 && value.length !== 15) {
      //         return '身份证号格式不正确'
      //       }
      //     }
      //   }
      // },
      {
        name: 'verifyCode',
        rules: {
          validator: function(rule, value, param, modeels) {
            if (modeels.isRegist) {
              if (!value) {
                return '请输入验证码'
              }
              if (value && value.length !== 4) {
                return '验证码格式不正确'
              }
            }
          }
        }
      },
      {
        name: 'building',
        rules: { required: true, message: '请输入楼栋' }
      },
      {
        name: 'unit',
        rules: [
          { maxlength: 2, message: '单元号不能超过三位' },
          {
            validator: function(rule, value, param, modeels) {
              if (value && !/^\d{1,2}$/.test(value)) {
                return '单元号只能填入数字'
              }
            }
          }
        ]
      },
      {
        name: 'roomNumber',
        rules: { required: true, message: '请输入房号' }
      }
    ],
    options: {}
  },
  onLoad(option) {
    this.options = option
    this.form.isRegist = option.isRegist === 'true'
    // this.form.openId = option.isRegist === 'false' ? '' : this.store_openId || ''
    this.disabled = option.isRegist !== 'true'
    this.form.housingEstateId = option.estateId || this.store_housingEstateId || ''
    this.form.housingEstateName = option.estateName || this.store_housingName || ''
    this.form.building = this.form.isRegist ? '' : this.store_building
    this.form.unit = this.form.isRegist ? '' : this.store_unit
    this.form.roomNumber = this.form.isRegist ? '' : this.store_roomNumber
    getBuildings(this.form.housingEstateId).then(res => {
      this.btnData = res.data.data
    })
  },
  onShow() {
    // 基础库2.9.5才支持
    if (compareVersion('2.9.5') > -1) {
      wepy.wx.hideHomeButton()
    }
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

  methods: {
    commitBaseinfo() {
      this.$wx.selectComponent('#form').validate((valid, errors) => {
        if (!valid) {
          this.err = errors[0].name
          this.error = errors[0].message
          this.errorShow = true
        } else {
          this.err = ''
          let openId = this.store_openId
          let { numberPlate1, numberPlate, ...rest } = this.form
          numberPlate = numberPlate ? numberPlate.toUpperCase() : ''
          numberPlate1 = numberPlate1 ? numberPlate1.toUpperCase() : ''
          if (numberPlate && numberPlate1) {
            numberPlate = `${numberPlate},${numberPlate1}`
          } else {
            numberPlate = numberPlate || numberPlate1
          }
          if (!rest.isRegist && this.store_userId) {
            rest.familyId = this.store_familyId
            wx.showLoading({
              title: '加载中',
              mask: true
            })
            addMember({ ...rest, openId, numberPlate })
              .then(res => {
                if (res.data.status === 200) {
                  wx.hideLoading()
                  wx.navigateBack('/pages/index')
                } else {
                  if (res.data && res.data.message) {
                    this.error = res.data.message
                  } else {
                    this.error = '网络繁忙，请稍后再试'
                  }
                  this.errorShow = true
                  wx.hideLoading()
                }
              })
              .catch(err => {
                if (err.data && err.data.message) {
                  this.error = err.data.message
                } else {
                  this.error = '网络繁忙，请稍后再试'
                }
                this.errorShow = true
                wx.hideLoading()
              })
          } else if (this.form.isRegist) {
            wx.showLoading({
              title: '加载中',
              mask: true
            })
            addMainMember({ ...rest, openId, numberPlate })
              .then(res => {
                if (res.data.status === 200) {
                  if (this.form.isRegist) {
                    // 重新登录 并切换到当前小区
                    this.$store
                      .dispatch('login', this.options.estateId)
                      .then(m => {
                        // this.$store.dispatch('switchEnstate', this.options.estateId)
                        wx.hideLoading()
                        wx.navigateBack('/pages/index')
                      })
                      .catch(m1 => {
                        this.error = '网络繁忙，请稍后再试'
                        this.errorShow = true
                        wx.hideLoading()
                      })
                  }
                } else {
                  if (res.data && res.data.message) {
                    this.error = res.data.message
                  } else {
                    this.error = '网络繁忙，请稍后再试'
                  }
                  this.errorShow = true
                  wx.hideLoading()
                }
              })
              .catch(err => {
                if (err.data && err.data.message) {
                  this.error = err.data.message
                } else {
                  this.error = '网络繁忙，请稍后再试'
                }
                this.errorShow = true
                wx.hideLoading()
              })
          }
        }
      })
    },
    tapBuilding() {
      if (this.disabled) {
        return
      }

      this.typeF = true
    },
    enterData(data) {
      this.form.building = data
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
                    clearInterval(this.delayIntervel)
                    this.delayTime = 0
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
    navigationBarTitleText: '基本信息录入',
    usingComponents: {
      'mp-form':'module:weui-miniprogram/miniprogram_dist/form/form',
      'mp-cell':'module:weui-miniprogram/miniprogram_dist/cell/cell',
      'mp-halfScreenDialog':'module:weui-miniprogram/miniprogram_dist/half-screen-dialog/half-screen-dialog',
      'mp-actionSheet':'module:weui-miniprogram/miniprogram_dist/actionSheet/actionSheet',
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
.mgt-10 {
  margin-top: 10rpx;
}
.mgt-20 {
  margin-top: 20rpx;
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
  .my-form {
    border-radius: 8rpx;
    box-shadow: 0rpx 4rpx 6rpx 0rpx rgba(57, 57, 57, 0.05);
    overflow: hidden;
  }
  .main-form {
    padding: 0 20rpx;
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
      width: 500rpx;
    }

    &:not(.last) {
      border-radius: 0;
      border-bottom: 1rpx solid #ccc;
    }
    &.danger {
      border-bottom: 1rpx solid #f00;
    }
    .phoneNum {
      width: 300rpx;
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
</style>

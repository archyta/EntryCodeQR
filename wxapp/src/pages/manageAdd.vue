
<template>
  <div class="main">
    <!-- <image class="width-image" src="{{'./../static/image/tips.png'}}" /> -->
    <div class="container">
      <mp-toptips :delay="5000" :msg="error" :show="errorShow" @hide="onErrorHidden" type="error"></mp-toptips>

      <mp-form id="form" models="{{form}}" ref="form" rules="{{rules}}">
        <div class="my-form mgt-20">
          <div class="main-form">
            <panel class="panel form-item">
              <div style="font-weight: 600">填写小区基本信息</div>
            </panel>
            <panel :class="{'danger': err === 'name'}" class="panel form-item">
              <div>
                <span>
                  小区名称
                  <span class="required">*</span>
                </span>
              </div>
              <input placeholder="请输入小区名称" type="text" v-model="form.name" />
            </panel>
            <panel :class="{'danger': err === 'address'}" class="panel form-item">
              <div>
                <span>
                  所在区域
                  <span class="required">*</span>
                </span>
              </div>
              <picker bindchange="bindRegionChange" custom-item="{{customItem}}" mode="region" value="{{address}}">
                <view class="picker">
                  {{getFullAddress}}
                  <image src="{{'./../static/icon/arror-right.png'}}" style="width: 30rpx;height: 30rpx;margin-left:2rpx;" />
                </view>
              </picker>
            </panel>
            <panel :class="{'danger': err === 'streetOfficeName'}" class="panel form-item">
              <div>
                <span>
                  所在街道
                  <span class="required">*</span>
                </span>
              </div>
              <input placeholder="请输入所在街道" type="text" v-model="form.streetOfficeName" />
            </panel>
            <panel :class="{'danger': err === 'responsible'}" class="panel form-item">
              <div>
                <span>
                  管理员姓名
                  <span class="required">*</span>
                </span>
              </div>
              <input placeholder="请输入管理员姓名" type="text" v-model="form.responsible" />
            </panel>
            <panel :class="{'danger': err === 'responsibleMobilePhone'}" class="panel form-item right-con">
              <div>
                <span>
                  手机号
                  <span class="required">*</span>
                </span>
              </div>
              <input placeholder="请输入手机号码" type="number" v-model="form.responsibleMobilePhone" />
              <div class="right-button">
                <button :class="{'disabled-btn': isResting}" @tap="getCode" style="width: 200rpx;">{{isResting? restSecond + 's':'获取验证码'}}</button>
              </div>
            </panel>
            <panel :class="{'danger': err === 'verifyCode'}" class="panel form-item right-con">
              <div>
                <span>手机验证码（4位）</span>
              </div>
              <input :disabled="disabled" placeholder="请输入验证码" type="number" v-model="form.verifyCode" />
            </panel>
            <panel class="panel form-item right-con">
              <div>
                <span>进出门限额</span>
              </div>
              <picker @change="bindMultiPickerChange" bindcolumnchange="bindMultiPickerColumnChange" mode="multiSelector" range="{{multiArray}}" range-key="label" value="{{multiIndex}}">
                <view class="picker">限{{multiArray[0][multiIndex[0]].label}} {{multiArray[1][multiIndex[1]].label}} {{multiArray[2][multiIndex[2]].label}}</view>
              </picker>
            </panel>
            <panel class="panel form-item right-con">
              <div>
                <span>门岗放行模式</span>
              </div>
              <picker :range="allGuardControlPatterns" @change="onAllGuardControlPatternsChange" mode="selector" range-key="name" v-model="guardControlPatternIndex">
                <view class="picker">{{allGuardControlPatterns[guardControlPatternIndex].name}}</view>
              </picker>
            </panel>
            <panel class="panel form-item">
              <div :class="{ danger: err === 'riskTemperature' }" class="inline-form-item" style="position: relative">
                <span class="nowrap">
                  警戒体温
                  <span class="required">*</span>
                </span>
                <input :disabled="disabled" placeholder="请输入" style="padding-right:10rpx;" type="digit" v-model="form.riskTemperature" />
              </div>

              <div :class="{ danger: err === 'abnormalTemperature' }" class="inline-form-item">
                <span class="nowrap">
                  异常体温
                  <span class="required">*</span>
                </span>
                <input :disabled="disabled" placeholder="请输入" type="digit" v-model="form.abnormalTemperature" />
              </div>
            </panel>
            <panel class="panel form-item last photo_panel">
              <span>小区门头照</span>
              <div class="right-button">
                <button @tap="uploadImage">上传照片</button>
              </div>
            </panel>
            <div class="addition-img">
              <div :style="{backgroundImage: 'url(' + getImageFullPath + ')'}" class="image" v-if="form.headPhotoPath"></div>
              <!-- <image :src="getImageFullPath" v-if="form.headPhotoPath" /> -->
            </div>
          </div>
        </div>
      </mp-form>
      <div class="rules-wrap">
        <checkbox-group @change="checkboxChange">
          <checkbox color="#3a6eff" id="cehckbox_1" value="rules">
            <label>已阅读并同意</label>
          </checkbox>
        </checkbox-group>

        <span @tap="goRules" class="href-page">法律声明及隐私条款</span>
      </div>
    </div>
    <button @tap="commitEstate" class="next-step" type>
      <span class="text">下一步（1/3）</span>
    </button>
  </div>
</template>

<script>
import wepy from '@wepy/core'
import eventHub from '../common/eventHub'
import { mapState, mapActions } from '@wepy/x'
import getters from '@/store/getters'
import store from '../store'
import { isPhoneNumber } from '../utils/valid'
import { saveImage, getCodeByPhone, getImageUrl, downloadImage } from '@/api/common'
import { addEstate, setEntressLimit } from '@/api/manage'
import { pickerData } from '@/constant/config'
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
    isResting: false,
    timer: null,
    restSecond: 0,
    err: '',
    estateName: '',
    checkRules: '',
    streetOfficeName: '',
    showOneButtonDialog: false,
    buttons: [],
    clickBack: '',
    customItem: '全部',
    allGuardControlPatterns: [
      { name: '保安扫码确认后放行', value: '0' },
      { name: '住户扫码登记后放行', value: '1' }
    ],

    guardControlPatternIndex: 0,
    form: {
      name: '',
      responsibleMobilePhone: '',
      abnormalTemperature: '37.3',
      riskTemperature: '37.1',
      openId: '',
      responsible: '',
      headPhotoPath: '',
      verifyCode: '',
      streetOfficeName: '',
      guardControlPattern: '0',
      agentId: ''
    },
    address: ['四川省', '成都市', '锦江区'],
    multiArray: pickerData,
    multiIndex: [0, 0, 0],
    rules: [
      {
        name: 'name',
        rules: { required: true, message: '请填写小区名称' }
      },
      {
        name: 'streetOfficeName',
        rules: { required: true, message: '请填写街道' }
      },
      {
        name: 'responsible',
        rules: { required: true, message: '请填写管理员姓名' }
      },
      {
        name: 'responsibleMobilePhone',
        rules: {
          validator: function(rule, value, param, modeels) {
            if (!value || !isPhoneNumber(value)) {
              return '手机号格式不正确'
            }
          }
        }
      },
      {
        name: 'verifyCode',
        rules: {
          validator: function(rule, value, param, modeels) {
            if (!value || (value && value.length !== 4)) {
              return '请输入4位数字的验证码'
            }
          }
        }
      },
      {
        name: 'riskTemperature',
        rules: [
          { required: true, message: '请输入体温' },
          {
            validator: function(rule, value, param, modeels) {
              if (value > 45 || value < 30) {
                return '温度范围异常，请输入30-45之前的温度值'
              }
              let m = /^\d+(\.\d+)?$/.test(value)
              if (value && !m) {
                return '温度只能填入数字'
              }
            }
          }
        ]
      },
      {
        name: 'abnormalTemperature',
        rules: [
          { required: true, message: '请输入体温' },
          {
            validator: function(rule, value, param, modeels) {
              if (value > 45 || value < 30) {
                return '温度范围异常，请输入30-45之前的温度值'
              }
              let m = /^\d+(\.\d+)?$/.test(value)
              if (value && !m) {
                return '温度只能填入数字'
              }
            }
          }
        ]
      }
    ]
  },
  computed: {
    ...getters(),
    getSubmit() {
      let accessType = this.multiArray[0][this.multiIndex[0]].value
      let accessDays = this.multiArray[1][this.multiIndex[1]].value
      let accessCount = this.multiArray[2][this.multiIndex[2]].value
      return {
        accessType,
        accessDays,
        accessCount
      }
    },
    getImageFullPath() {
      return getImageUrl(this.form.headPhotoPath)
    },
    getFullAddress() {
      return this.address
        .map(el => {
          if (el.length > 4) {
            return el.substring(0, 3) + '...'
          } else {
            return el
          }
        })
        .join(' ')
    }
  },
  onLoad(option) {
    this.form.openId = this.store_openId
    this.form.agentId = wepy.wx.getStorageSync('agentId')
  },
  onShow() {
    // 基础库2.9.5才支持
    if (compareVersion('2.9.5') > -1) {
      wepy.wx.hideHomeButton()
    }
  },
  methods: {
    commitEstate() {
      this.$wx.selectComponent('#form').validate((valid, errors) => {
        if (!valid) {
          this.err = errors[0].name
          this.error = errors[0].message
          this.errorShow = true
        } else {
          if (this.form.riskTemperature >= this.form.abnormalTemperature) {
            this.error = '警戒体温须小于异常体温'
            this.errorShow = true
            return
          }
          if (!this.checkRules) {
            this.error = '请同意隐私条款'
            this.errorShow = true
            return
          }
          this.err = ''
          let { address, ...rest } = this.form
          address = this.address.join(' ')
          wx.showLoading({
            title: '加载中',
            mask: true
          })
          addEstate({ address, ...rest }).then(res => {
            if (res.data.status === 200) {
              // this.$store.dispatch('setGuardInfo', res.data.data);
              let estateId = res.data.data.id
              setEntressLimit({ estateId, ...this.getSubmit })
                .then(response => {
                  if (response.data.status === 200) {
                    this.$store.dispatch('login', estateId).then(m => {
                      // this.$store.dispatch('switchEnstate', estateId)
                      wx.hideLoading()
                      this.$navigate(`/pages/manageStep2?id=${estateId}`)
                    })
                  } else {
                    this.openError('设置进出次数限制失败，可在详情页面中再次修改')
                    wx.hideLoading()
                  }
                })
                .catch(err => {
                  this.openError('设置进出次数限制失败，可在详情页面中再次修改')
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
        }
      })
      // this.$navigate('/pages/manageStep2')
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
    bindRegionChange: function(e) {
      this.address = e.$wx.detail.value
    },
    bindMultiPickerChange(data) {
      this.multiIndex = [...data.$wx.detail.value]
    },
    onAllGuardControlPatternsChange(e) {
      if (e.$wx.detail) {
        let m = e.$wx.detail.value
        this.form.guardControlPattern = this.allGuardControlPatterns[m].value
      }
    },
    uploadImage() {
      wx.chooseImage({
        count: 1,
        success: res => {
          const tempFilePaths = res.tempFilePaths
          let accountInfo = wx.getAccountInfoSync()
          saveImage(accountInfo.miniProgram.appId, tempFilePaths)
            .then(res => {
              if (res.data.status === 200) {
                this.form.headPhotoPath = res.data.data
              } else {
                this.openError('图片上传失败')
              }
            })
            .catch(err => {
              this.openError('图片上传失败')
            })
        }
      })
    },
    getCode() {
      if (this.isResting) {
        return
      }
      let value = this.form.responsibleMobilePhone
      if (!value || !isPhoneNumber(value)) {
        this.showError('请输入正确格式的手机号')
        return
      }
      this.isResting = true
      this.restSecond = 60
      getCodeByPhone(this.form.openId, value)
        .then(res => {
          if (res.data.status === 200) {
            this.startRestSecond()
            wx.showToast({
              title: '发送成功',
              icon: 'success',
              duration: 2000
            })
          } else if (res.data.status === 4001) {
            this.showError(res.data.message)
            this.isResting = false
          } else {
            this.showError('短信发送失败，请稍后重试')
            this.isResting = false
          }
        })
        .catch(el => {
          this.isResting = false
        })
    },
    startRestSecond() {
      this.restSecond = 60
      if (this.timer) {
        clearInterval(this.timer)
      }
      this.timer = setInterval(() => {
        this.restSecond--
        if (this.restSecond <= 0) {
          this.isResting = false
          clearInterval(this.timer)
          this.timer = null
        }
      }, 1000)
    },
    bindMultiPickerColumnChange(data) {
      console.log(data)
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
    navigationBarTitleText: '注册小区',
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
  margin-bottom: 10rpx;
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
  margin: 20rpx 20rpx 105rpx;
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
    padding: 0 30rpx 30rpx;
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
    // &.photo_panel{
    //   height: 300rpx;
    // }
    input {
      text-align: right;
      width: 400rpx;
    }
    &.right-con {
      height: 115rpx;
      input {
        width: 300rpx;
      }
    }
    .right-button {
      text-align: right;
      // width: 200rpx;
      // height: 40rpx;
      button {
        font-size: 26rpx;
        background: #fff;
        color: #3a6eff;
        line-height: 60rpx;
        height: 60rpx;
      }
      .disabled-btn {
        background: #eee;
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
  .addition-img {
    height: 400rpx;
    border: 1rpx solid #eee;
    border-radius: 8rpx;
    background: #fff;
    .image {
      background-position: center;
      background-size: cover;
      background-repeat: no-repeat;
      width: 100%;
      height: 100%;
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
        bottom: 0rpx;
        width: 100%;
        border-bottom: 2rpx solid #f00;
        height: 2rpx;
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
.next-step {
  // margin-top: 30rpx;
  height: 100rpx;
  // background-color: #3a6eff;
  width: 100%;
  width: 100%;
  border-radius: 0;
  font-size: 32rpx;
  position: fixed;
  bottom: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #fff;
  border-top: 1px solid #d8d8d8;
  z-index: 100;
  // box-shadow: 0rpx 4rpx 6rpx 0rpx rgba(57, 57, 57, 0.05);
}
.next-step::after {
  border: none;
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
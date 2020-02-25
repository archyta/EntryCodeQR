
<template>
  <div class="main">
    <div class="container">
      <mp-form id="form" models="{{form}}" ref="form" rules="{{rules}}">
        <div class="main-form mgt-10">
          <panel class="panel form-item">
            <div style="font-weight: 600">保安个人信息</div>
            <div @tap="goEditDetail" class="edit-wrap">
              <image style="width: 28rpx;height: 28rpx;" src="{{'./../static/icon/edit.png'}}" />
            </div>
          </panel>
          <panel :class="{'danger': err === 'name'}" class="panel form-item">
            <div>
              <span>
                我的真实名字
                <!-- <span class="required">*</span> -->
              </span>
            </div>
            <input placeholder="暂无" type="text" v-model="form.name" disabled />
          </panel>
          <panel :class="{'danger': err === 'mobilePhone'}" class="panel form-item">
            <div>
              <span>
                我的手机号码
                <!-- <span class="required">*</span> -->
              </span>
            </div>
            <input placeholder="暂无" type="text" v-model="form.mobilePhone" disabled />
          </panel>
          <panel :class="{'danger': err === 'idNumber'}" class="panel form-item">
            <div>
              <span>
                身份证号码
                <!-- <span class="required">*</span> -->
              </span>
            </div>
            <input placeholder="暂无" type="text" v-model="form.idNumber" disabled />
          </panel>
          <panel :class="{'danger': err === 'housingEstateName'}" class="panel form-item">
            <span>我的防控区域</span>
            <div class="right-text">
              <span v-if="estateName">{{estateName}}</span>
            </div>
          </panel>
          <panel :class="{'danger': err === 'building'}" class="panel form-item last">
            <span>所在行政区域</span>
            <div class="right-text">
              <span v-if="streetOfficeName">{{streetOfficeName}}</span>
            </div>
          </panel>
        </div>
      </mp-form>
      <!-- <button @tap="commitBaseinfo" class="panel submit-btn" type="primary">
        <span class="text">提交信息</span>
      </button>-->
    </div>
  </div>
</template>

<script>
import wepy from '@wepy/core'
import eventHub from '../common/eventHub'
import { mapState, mapActions } from '@wepy/x'
import getters from '@/store/getters'
import store from '../store'
import { isPhoneNumber, validateIDCard } from '../utils/valid'
import { getSecurityInfo } from '@/api/guards'
import { compareVersion } from '@/utils/common.js'

wepy.page({
  store,
  hooks: {},
  data: {
    error: '',
    errorShow: false,
    userId: '',
    load: true,
    estateName: '', // 防控区域
    streetOfficeName: '', // 行政区域
    form: {
      name: '',
      mobilePhone: '',
      idNumber: ''
    }
  },
  onLoad(option) {
    // const { housingName,streetOfficeName,userId } = this.store_getGuard
    this.estateName = this.store_housingName
    this.streetOfficeName = this.store_streetOfficeName
    this.getDetails(this.store_userId)
  },
  onShow() {
     // 基础库2.9.5才支持
    if (compareVersion('2.9.5') > -1) {
      wepy.wx.hideHomeButton()
    }
    if (!this.load) {
      this.getDetails()
    }
  },
  computed: {
    ...getters()
  },
  methods: {
    goEditDetail() {
      this.$navigate('/pages/securityInfoAdd?edit=1')
    },
    getDetails() {
      getSecurityInfo(this.store_userId,this.store_housingEstateId).then(res => {
        this.load = false
        if (res && res.data && res.data.data && res.data.data.guardUser) {
          this.form = res.data.data.guardUser || {}
        } else {
          this.form = {}
        }
      })
    }
  }
})
</script>
<config>
  {
    navigationBarTitleText: '我的信息',
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
.edit-wrap {
  padding: 0 10rpx;
  border: 1rpx solid #ccc;
  border-radius: 6rpx;
  &:active {
    background: #eee;
  }
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
    // box-shadow: 0px 4px 6px 0px rgba(57, 57, 57, 0.05);
    input {
      text-align: right;
      width: 400rpx;
    }
    .right-text {
      text-align: right;
      width: 400rpx;
      height: 40rpx;
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
    box-shadow: 0px 4px 6px 0px rgba(57, 57, 57, 0.05);
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
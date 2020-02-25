

<template>
  <div class="main">
    <div class="container">
      <panel class="panel">
        <div class="bg-header">{{(estateInfo.streetOfficeName || '') + store_housingName}}</div>
      </panel>
      <div class="panel row-item">
        <span>
          生成住户出入扫码海报
          <!-- <span class="required">*</span> -->
        </span>
        <div @tap="goUserPoster" class="right-con">
          <span>
            查看
            <image src="{{'./../static/icon/arror-right.png'}}" style="width: 30rpx;height: 30rpx;margin-left:2rpx;" />
          </span>
        </div>
      </div>
      <div class="panel row-item">
        <span>
          生成保安工具注册二维码
          <!-- <span class="required">*</span> -->
        </span>
        <div @tap="goGuardCode" class="right-con">
          <span>
            查看
            <image src="{{'./../static/icon/arror-right.png'}}" style="width: 30rpx;height: 30rpx;margin-left:2rpx;" />
          </span>
        </div>
      </div>
      <div class="panel row-item last">
        <span>人员名单及审批</span>
        <div @tap="goGuardList" class="right-con">
          <span>
            {{estateInfo.guardNum|| 0}}个
            <image src="{{'./../static/icon/arror-right.png'}}" style="width: 30rpx;height: 30rpx;margin-left:2rpx;" />
          </span>
        </div>
      </div>
      <div class="panel row-item mgt-20">
        <span>
          进出门限额
          <!-- <span class="required">*</span> -->
        </span>
        <div class="right-con" style="width: 400rpx;">
          <picker @change="bindMultiPickerChange" mode="multiSelector" range="{{multiArray}}" range-key="label" value="{{multiIndex}}">
            <span class="picker">限{{multiArray[0][multiIndex[0]].label}} {{multiArray[1][multiIndex[1]].label}} {{multiArray[2][multiIndex[2]].label}}</span>
            <image src="{{'./../static/icon/arror-right.png'}}" style="width: 30rpx;height: 30rpx;margin-left:2rpx;" />
          </picker>
        </div>
      </div>
      <panel class="panel row-item last">
        <div class="inline-form-item">
          <!-- <span class="required">*</span> -->
          <span class="nowrap">警戒体温</span>
          <div class="right-con">
            <span>{{estateInfo.riskTemperature}}℃</span>
          </div>
        </div>

        <div class="inline-form-item">
          <span class="nowrap">
            异常体温
            <!-- <span class="required">*</span> -->
          </span>
          <div class="right-con">
            <span>{{estateInfo.abnormalTemperature}}℃</span>
          </div>
        </div>
      </panel>
      <!-- <div class="panel row-item">
        <span>
          生成二级管理员注册码
        </span>
        <div class="right-con">
          <span>查看</span>
        </div>
      </div>-->

      <div class="panel row-item mgt-20">
        <span style="font-weight: 600">小区基本信息</span>
        <!-- <div class="right-con">
          <span>查看</span>
        </div>-->
      </div>
      <div class="panel row-item">
        <span>
          小区名称
          <!-- <span class="required">*</span> -->
        </span>
        <div class="right-con">
          <span>{{estateInfo.name}}</span>
        </div>
      </div>
      <div class="panel row-item">
        <span>
          管理员名称
          <!-- <span class="required">*</span> -->
        </span>
        <div class="right-con">
          <span>{{estateInfo.crtUser.name}}</span>
        </div>
      </div>
      <div class="panel row-item">
        <span>
          管理员手机号
          <!-- <span class="required">*</span> -->
        </span>
        <div class="right-con" style="width: 400rpx;">
          <span>{{estateInfo.crtUser.mobilePhone}}</span>
        </div>
      </div>
      <div class="panel row-item">
        <span>
          小区区域
          <!-- <span class="required">*</span> -->
        </span>
        <div class="right-con" style="width: 400rpx;">
          <span>{{getShowAddress}}</span>
        </div>
      </div>
      <div class="panel row-item last">
        <span>
          小区门头照
          <!-- <span class="required">*</span> -->
        </span>
        <!-- <div class="right-con">
          <span>查看</span>
        </div>-->
      </div>
      <div class="addition-img">
        <div :style="{backgroundImage: 'url(' + getImageFullPath + ')'}" class="image" v-if="estateInfo.headPhotoPath"></div>
        <!-- <image :src="getImageFullPath" v-if="estateInfo.headPhotoPath" /> -->
      </div>
    </div>
  </div>
</template>
      
<script>
import wepy from '@wepy/core'
import getters from '@/store/getters'
import store from '../store'
import { encodeObj } from '@/utils/encode.js'
import { getEstate, setEntressLimit, getEntressLimit } from '@/api/manage'
import { getImageUrl } from '@/api/common'
import { pickerData } from '@/constant/config'
import { compareVersion } from '@/utils/common.js'

wepy.page({
  store,
  hooks: {},
  data: {
    userName: '',
    qrdata: {},
    estateInfo: {},
    multiArray: pickerData,
    multiIndex: [0, 0, 0]
  },
  created() {
    // this.famlilyInfo.estateName = this.store_housingName || '';
    // this.famlilyInfo.building = this.store_building || '';
    // this.famlilyInfo.unit = this.store_unit || '';
    // this.famlilyInfo.roomNumber = this.store_roomNumber || '';
  },
  computed: {
    ...getters(),
    getImageFullPath() {
      return getImageUrl(this.estateInfo.headPhotoPath)
    },
    getStyle() {
      return {
        'background-image': `url('${this.getImageFullPath}')`
      }
    },
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
    getShowAddress() {
      if (!this.estateInfo.address) {
        return ''
      }
      return this.estateInfo.address
        .split(' ')
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
  onShow() {
    // 基础库2.9.5才支持
    if (compareVersion('2.9.5') > -1) {
      wepy.wx.hideHomeButton()
    }
    this.getDetails(this.store_housingEstateId)
  },
  methods: {
    goGuardCode() {
      this.$navigate({ url: `/pages/guardPoster?id=${this.estateInfo.id}` })
    },
    goUserPoster() {
      let city = this.estateInfo.address.split(' ')[1]
      city = city === '全部' ? ' ' : city
      this.$navigate({ url: `/pages/userPoster?id=${this.estateInfo.id}&city=${city}` })
    },
    goGuardList() {
      this.$navigate({ url: `/pages/guardList?id=${this.estateInfo.id}&name=${this.estateInfo.name}` })
    },
    bindMultiPickerChange(data) {
      this.multiIndex = [...data.$wx.detail.value]
      this.updateLimit()
    },
    findPickerIndex(data) {
      /* accessCount: "4"
accessDays: "3"
accessType: "family" */
      let index1 = this.multiArray[0].findIndex(el => el.value == data.accessType)
      let index2 = this.multiArray[1].findIndex(el => el.value == data.accessDays)
      let index3 = this.multiArray[2].findIndex(el => el.value == data.accessCount)
      index1 = index1 === -1 ? 0 : index1
      index2 = index2 === -1 ? 0 : index2
      index3 = index3 === -1 ? 0 : index3
      return [index1, index2, index3]
    },
    updateLimit() {
      let estateId = this.estateInfo.id
      setEntressLimit({ estateId, ...this.getSubmit })
        .then(response => {
          if (response.data.status !== 200) {
            this.openError('服务器异常，请稍后重试...')
          } else {
            wx.showToast({
              title: '更新成功',
              icon: 'success',
              duration: 2000
            })
          }
        })
        .catch(err => {
          this.openError('服务器异常，请稍后重试...')
        })
    },
    getDetails(id) {
      getEstate(id).then(res => {
        if (res.data.status === 200) {
          this.estateInfo = res.data.data
          this.getEntressLimit(res.data.data.id)
        } else {
          this.openError('服务器不知道去哪了,正在紧急查找中...')
        }
      })
    },
    getEntressLimit(id) {
      getEntressLimit(id).then(res => {
        this.multiIndex = this.findPickerIndex(res.data.data)
      })
    }
  }
})
</script>
<config>
  {
    navigationBarTitleText: '管理我的小区',
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
  height: 100%;
}
.required {
  color: #f00;
  vertical-align: middle;
  margin-left: 5rpx;
}
.mgt-20 {
  margin-top: 20rpx;
}
.addition-img {
  height: 400rpx;
  border: 1rpx solid #eee;
  border-radius: 8rpx;
  background-color: #fff;
  background-position: center;
  background-size: cover;
  .image {
    background-position: center;
    background-size: cover;
    background-repeat: no-repeat;
    width: 100%;
    height: 100%;
  }
}
.container {
  color: #222;
  font-size: 32rpx;
  padding: 20rpx;
  display: flex;
  flex-direction: column;
  margin-bottom: 20rpx;
  .panel {
    display: flex;
    background-color: #fff;
    flex-shrink: 0;
    overflow: hidden;
    .bg-header {
      background: #3a6eff;
      height: 112rpx;
      line-height: 112rpx;
      border-radius: 8rpx 8rpx 0 0;
      color: #fff;
      text-align: center;
      width: 100%;
    }
    &.row-item {
      height: 110rpx;
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 0 20rpx;
      box-shadow: 0px 4px 6px 0px rgba(57, 57, 57, 0.05);
      .right-con {
        text-align: right;
        width: 200rpx;
        // height: 40rpx;
      }
      input {
        text-align: right;
        width: 500rpx;
      }
      .inline-form-item {
        display: inline-flex;
        align-items: center;
        justify-content: flex-start;
        flex-grow: 1;
        // height: 100%;

      }

      &:not(.last) {
        border-radius: 0;
        border-bottom: 1rpx solid #ccc;
      }
      &.danger {
        border-bottom: 1rpx solid #f00;
      }
    }
  }
}
</style>
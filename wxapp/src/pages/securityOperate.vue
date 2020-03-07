
<template>
  <div class="main">
    <div class="container">
      <mp-toptips :msg="error" :delay="5000" @hide="onErrorHidden" type="error" :show="errorShow"></mp-toptips>
      <panel class="panel banner">
        <div class="banner-head">
          <span class="username">
            <span>{{mainUser.name}}</span>
            <span class="tip">{{accessType}}{{rangeText}}</span>
          </span>
          <span class="address">{{iscommon_com?showAddressFull:famlilyInfo.estateName }}</span>
          <span style="color:#ff2d2d" v-if="!iscommon_com">（非本小区人员）</span>
          <span class="limit" v-if="iscommon_com">{{limitText}}</span>
        </div>
        <!-- <span class="access-type">{{accessType}}近{{accessDays}}日</span> -->
        <div class="out-in-count">
          <div class="left-out-in-count">
            <span>进门</span>
            <span class="text-in-num" :class="{'text-warn':isForbiddenIn}">{{inNum}}</span>
            <span>次</span>
          </div>
          <div class="right-out-in-count">
            <span>出门</span>
            <span class="text-out-num" :class="{'text-warn':isForbiddenOut}">{{outNum}}</span>
            <span>次</span>
          </div>
        </div>
      </panel>

      <!--toast /-->
      <panel class="panel main-operate-info">
        <div class="operate-info-item">
          <span class="title">他/她要</span>
          <radio-group class="radio-group" v-model="tripRecord.direction">
            <radio class="radio" color="#3A6EFF" value="0" :class="{checked:radioCheckedStyle1}" :checked="true">
              <text>进门</text>
            </radio>
            <radio class="radio" color="#3A6EFF" value="1" :class="{checked:radioCheckedStyle2}">
              <text>出门</text>
            </radio>
          </radio-group>
        </div>
        <div class="operate-info-item">
          <span class="title">健康状态</span>
          <checkbox-group class="checkbox-group" @change="checkboxChange">
            <label class="checkbox">
              <checkbox color="#FF2D2D" id="cehckbox_1" value="fever">
                <label :class="{checked:healthStatus.fever}">发热</label>
              </checkbox>
            </label>
            <label class="checkbox">
              <checkbox color="#FF2D2D" id="cehckbox_2" value="fatigue">
                <label :class="{checked:healthStatus.fatigue}">乏力</label>
              </checkbox>
            </label>
            <label class="checkbox">
              <checkbox color="#FF2D2D" id="cehckbox_3" value="dryCough">
                <label :class="{checked:healthStatus.dryCough}">干咳</label>
              </checkbox>
            </label>
          </checkbox-group>
        </div>
        <div class="operate-info-item">
          <span class="title">体温</span>
          <div class="input-container">
            <input class="input" type="digit" @input="onTemperatureInput" @change="onTemperatureInputChange" v-model="healthStatus.temperature" placeholder-style="font-size:52rpx;font-family: '-apple-system,BlinkMacSystemFont,SF UI Text,Helvetica Neue,PingFang SC,Hiragino Sans GB,Microsoft YaHei UI,Microsoft YaHei,Arial,sans-serif';" placeholder="请门卫输入体温" />
            <span class="temperature">
              <span v-if="healthStatus.temperature">℃</span>
            </span>
          </div>
        </div>
        <cover-view v-if="isForbidden" class="reject-tip">
          <cover-view v-if="!iscommon_com">非本小区人员</cover-view>
          <cover-view v-else>该住户已用完进出配额，请立即劝返</cover-view>
        </cover-view>
      </panel>
      <div class="operate-panel">
        <button @tap.stop="submit(false)" class="btn btn-reject" :class="{'forbidden':!iscommon_com}" type="primary">
          <span class="text">劝 返</span>
        </button>
        <button @tap.stop="submit(true)" class="btn btn-success" :class="{'forbidden':isForbidden||!iscommon_com}" type="primary">
          <span class="text">放 行</span>
        </button>
      </div>
      <panel class="panel out-in-record">
        <span class="title">本户最近3天出入记录</span>
        <div class="record-content">
          <span class="record-null-content" v-if="!records||records.length===0">暂无记录</span>
          <div v-else>
            <div class="record-content-item" :key="index" v-for="(item,index) in records">
              <span class="date">{{item.crtTime}}</span>
              <span class="username">{{item.userName}}</span>
              <span class="status in" v-if="item.status==='0'">进门</span>
              <span class="status out" v-else-if="item.status==='1'">出门</span>
              <span class="status reject" v-else-if="item.status==='2'">进门/劝返</span>
              <span class="status reject" v-else-if="item.status==='3'">出门/劝返</span>
              <span class="status reject" v-else>未知</span>
            </div>
          </div>
        </div>
      </panel>
    </div>
  </div>
</template>

<script>
import wepy from '@wepy/core'
import eventHub from '../common/eventHub'
import store from '../store'
import getters from '@/store/getters'
import { getBaseInfo, getOutInRecord } from '@/api/owner'
import { setIsPass } from '@/api/guards'
import { compareVersion } from '@/utils/common.js'
wepy.page({
  store,
  hooks: {},
  data: {
    iscommon_com: true,
    outNum: 1,
    inNum: 2,
    error: '',
    errorShow: false,
    userId: null,
    familyId: null,
    accessType: '本户',
    accessDays: 2,
    accessCount: 3,
    famlilyInfo: {
      // 用户信息
      id: '',
      estateId: '',
      mainUserId: '',
      building: '',
      unit: '',
      roomNumber: '',
      estateName: ''
    },
    mainUser: {
      id: '',
      name: '',
      openId: '',
      mobilePhone: '',
      idNumber: '',
      familyId: ''
    },
    records: [
      // {
      //   id: '1',
      //   crtTime: '2020/12/12 19:30',
      //   userName: '张三',
      //   status: '0'
      // }
    ],
    tripRecord: {
      status: '0',
      userId: null,
      direction: '0',
      familyId: null,
      estateId: null
    },
    healthStatus: {
      fever: false,
      fatigue: false,
      dryCough: false,
      temperature: null
    },
    healthCheckData: ['fever', 'fatigue', 'dryCough']
  },
  computed: {
    ...getters(),
    showAddress: function() {
      let str = ''
      if (this.famlilyInfo) {
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
      }
    },
    showAddressFull: function() {
      let str = ''
      if (this.famlilyInfo) {
        // if (this.famlilyInfo.estateName) {
        //   str += this.famlilyInfo.estateName
        // }
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
      }
    },
    isForbidden: function() {
      if (this.iscommon_com) {
        if (this.tripRecord.direction === '0') {
          return this.isForbiddenIn
        } else {
          return this.isForbiddenOut
        }
      } else {
        return true
      }
    },
    isForbiddenOut: function() {
      return this.outNum >= this.accessCount && this.accessCount !== -1
    },
    isForbiddenIn: function() {
      return this.inNum >= this.accessCount && this.accessCount !== -1
    },
    radioCheckedStyle1: function() {
      if (this.tripRecord.direction === '0') {
        return true
      } else {
        return false
      }
    },
    radioCheckedStyle2: function() {
      if (this.tripRecord.direction === '1') {
        return true
      } else {
        return false
      }
    },
    limitText: function() {
      if (this.accessCount === -1) {
        return '不限制进出'
      } else if (this.accessCount === 0) {
        return '禁行'
      } else {
        return `限${this.accessType}每${this.accessDays}天进出${this.accessCount}次`
      }
    },
    rangeText: function() {
      if (this.accessDays === 1) {
        return '今日'
      } else {
        return `最近${this.accessDays}日`
      }
    }
  },
  onLoad(options) {
    this.userId = options.userId
    this.familyId = options.familyId
  },
  onShow() {
    // 基础库2.9.5才支持
    if (compareVersion('2.9.5') > -1) {
      wepy.wx.hideHomeButton()
    }
    this.loadPageData()
  },
  methods: {
    loadPageData() {
      if (this.userId && this.familyId) {
        // 加载用户记录
        getBaseInfo(this.userId, this.familyId)
          .then(res => {
            if (res.data.status === 200) {
              this.famlilyInfo = res.data.data
              if (this.famlilyInfo.estateId !== this.store_housingEstateId) {
                this.iscommon_com = false
              }
              //装载数据
              if (this.famlilyInfo.familyMembers && this.famlilyInfo.familyMembers.length > 0) {
                let currentuser = this.famlilyInfo.familyMembers.filter(el => el.id === this.userId)
                if (currentuser.length > 0) {
                  this.mainUser = currentuser[0]
                }

                this.familyMembers = this.famlilyInfo.familyMembers.filter(el => el.id !== this.userId)
              }
            } else {
              this.error = '网络繁忙，请稍后再试'
              this.errorShow = true
            }
          })
          .catch(err => {
            this.error = '网络繁忙，请稍后再试'
            this.errorShow = true
          })
        // 加载出入信息
        getOutInRecord(this.familyId, this.store_housingEstateId, this.userId)
          .then(res => {
            if (res.data.status === 200 && res.data.data) {
              this.inNum = res.data.data.inCount
              this.outNum = res.data.data.outCount
              this.records = res.data.data.tripRecords
              this.accessType = res.data.data.accessRule.accessType
              this.accessDays = Number(res.data.data.accessRule.accessDays)
              this.accessCount = Number(res.data.data.accessRule.accessCount)
            } else {
              this.error = '网络繁忙，请稍后再试'
              this.errorShow = true
            }
          })
          .catch(err => {
            this.error = '网络繁忙，请稍后再试'
            this.errorShow = true
          })
      }
    },
    serialize(obj) {
      if (obj) {
        let str = []
        for (let p in obj)
          if (obj.hasOwnProperty(p)) {
            str.push(p + '=' + obj[p])
          }
        let m = str.join('&')
        if (m) {
          return '?' + m
        }
      } else {
        return ''
      }
    },
    onErrorHidden() {
      this.error = ''
      this.errorShow = false
    },
    checkboxChange(e) {
      if (e.$wx && e.$wx.detail && e.$wx.detail.value) {
        let m = ['fever', 'fatigue', 'dryCough']
        m.forEach(el => {
          if (e.$wx.detail.value.indexOf(el) > -1) {
            this.$set(this.healthStatus, el, true)
          } else {
            this.$set(this.healthStatus, el, false)
          }
        })
      }
    },
    submit(isPass) {
      if (!this.iscommon_com) {
        return
      }
      if (this.familyId && this.userId && this.store_userId) {
        let status = '0'
        if (isPass) {
          if (this.isForbidden) {
            // 如果超过次数 禁止通行
            return
          }
          status = this.tripRecord.direction
        } else {
          if (this.tripRecord.direction === '0') {
            status = '2'
          } else {
            status = '3'
          }
        }
        let data = {
          tripRecord: {
            status: status,
            userId: this.userId,
            direction: this.tripRecord.direction,
            familyId: this.familyId,
            estateId: this.store_housingEstateId,
            guardId: this.store_userId
          },
          healthStatus: {
            fever: this.healthStatus.fever ? '1' : '0',
            fatigue: this.healthStatus.fatigue ? '1' : '0',
            dryCough: this.healthStatus.dryCough ? '1' : '0',
            temperature: this.healthStatus.temperature
          }
        }
        wx.showLoading({
          title: '加载中',
          mask: true
        })
        setIsPass(data)
          .then(res => {
            if (res.data.status === 200) {
              wx.showToast({
                title: '操作成功',
                icon: 'success',
                duration: 2000
              })
              wx.hideLoading()
              eventHub.$emit('operate-success')
              wx.navigateBack()
            } else {
              this.error = '网络繁忙，请稍后再试'
              this.errorShow = true
              wx.hideLoading()
            }
          })
          .catch(err => {
            this.error = '网络繁忙，请稍后再试'
            this.errorShow = true
            wx.hideLoading()
          })
      }
    },
    // 该校验函数只能校验   "xxx.xx" "xxx" 或者 "xx."  其中 "xx." 需要在change函数中单独处理
    onTemperatureInput(e) {
      let value = e.$wx.detail.value
      // this.$set(row, colGroup.prop + '.' + col.prop, value)
      if (value != null) {
        value = value.replace(/[^\d.]/g, '')
        let items = value.split('.')
        let first = items.shift()
        if (items.length > 0) {
          if (first === '') {
            first = '0'
          }
          // 处理输入大于100的情况,有小数点的情况
          let temp = this.convertNumber(first)
          if (temp != null) {
            if (temp > 99) {
              first = '99'
            }
          }
          value = first + '.' + items.join('')
        } else {
          // 处理输入小于100 无小数点的情况
          let temp = this.convertNumber(first)
          if (temp != null) {
            if (temp > 100) {
              first = '100'
            }
          }
          value = first
        }
        this.healthStatus.temperature = value
      }
    },
    onTemperatureInputChange(e) {
      let value = e.$wx.detail.value
      let m = this.convertNumber(value, 2)
      if (m) {
        this.healthStatus.temperature = m.toString()
      } else {
        this.healthStatus.temperature = null
      }
    },
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
    }
  },
  created() {}
})
</script>
<config>
  {
    navigationBarTitleText: '门卫防控',
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
  .container {
    display: flex;
    flex-direction: column;
    margin-top: 20rpx;
    margin-bottom: 20rpx;
    .panel {
      display: flex;
      margin-left: 20rpx;
      margin-right: 20rpx;
      background-color: #fff;
      border-radius: 8rpx;
      box-shadow: 0rpx 4rpx 6rpx 0rpx rgba(57, 57, 57, 0.05);
      // border: 1rpx solid red;
    }

    .banner {
      height: 214rpx;
      display: block;
      position: relative;
      .banner-head {
        height: 50%;
        width: 100%;
        border-bottom: 1rpx solid #d8d8d8;
        display: flex;
        justify-content: space-between;
        align-items: center;
        .username {
          color: #222222;
          margin-left: 30rpx;
          font-size: 40rpx;
          font-weight: bold;
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          .tip {
            font-size: 26rpx;
            font-weight: normal;
          }
        }
        .address {
          // margin-left: 30rpx;
          // flex-grow: 1;
          color: #222222;
          text-align: center;
          // display: block;
          font-size: 28rpx;
        }
        .limit {
          margin-right: 30rpx;
          color: #222222;
          text-align: center;
          // display: block;
          font-size: 28rpx;
          font-weight: bold;
        }
      }
      .access-type {
        font-size: 26rpx;
        position: absolute;
        top: 86rpx;
        left: 30rpx;
        height: 40rpx;
        line-height: 40rpx;
        border-radius: 20rpx;
        border: 1rpx solid #d8d8d8;
        background-color: #fff;
        padding-left: 10rpx;
        padding-right: 10rpx;
      }
      .out-in-count {
        height: 50%;
        width: 100%;
        display: flex;
        flex-direction: row;
        .left-out-in-count {
          width: 50%;
          font-size: 28rpx;
          color: #222222;
          border-right: 1rpx solid #d8d8d8;
          // border:1rpx solid red;
          display: flex;
          align-items: center;
          justify-content: center;
          .text-in-num {
            color: #2dc168;
            font-size: 72rpx;
            margin-left: 10rpx;
            margin-right: 10rpx;
            margin-bottom: 18rpx;
          }
          .text-warn {
            color: #ff220d;
          }
        }
        .right-out-in-count {
          width: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 28rpx;
          .text-out-num {
            color: #3a6eff;
            font-size: 72rpx;
            margin-left: 10rpx;
            margin-right: 10rpx;
            margin-bottom: 18rpx;
          }
          .text-warn {
            color: #ff220d;
          }
        }
      }
    }
    .main-operate-info {
      height: 440rpx;
      margin-top: 30rpx;
      display: flex;
      flex-direction: column;
      position: relative;
      .operate-info-item + .operate-info-item {
        border-top: 1rpx solid #d8d8d8;
      }
      .operate-info-item {
        padding-left: 32rpx;
        padding-right: 32rpx;
        display: flex;
        justify-content: space-between;
        align-items: center;
        flex-direction: row;
        height: 33%;

        .title {
          font-size: 32rpx;
          color: #222222;
          display: block;
          width: 200rpx;
        }

        .item-right {
          margin-left: 20rpx;
          flex-grow: 1;
        }

        .checkbox-group {
          flex-grow: 1;
          display: flex;
          justify-content: space-between;
          .checkbox {
            font-size: 32rpx;
            .checked {
              color: #ff2d2d;
            }
          }
        }

        .radio-group {
          flex-grow: 1;
          display: flex;
          justify-content: space-between;
          .radio {
            font-weight: bold;
            font-size: 54rpx;
            // display: block;
          }
          .checked {
            color: #3a6eff;
          }
        }
        .input-container {
          font-size: 52rpx;
          flex-grow: 1;
          height: 52rpx;
          display: flex;
          align-items: center;
          .input {
            text-align: right;
            height: 66rpx;
            font-size: 52rpx;
            flex-grow: 1;
            // font-family: '-apple-system,BlinkMacSystemFont,SF UI Text,Helvetica Neue,PingFang SC,Hiragino Sans GB,Microsoft YaHei UI,Microsoft YaHei,Arial,sans-serif';
          }
          .temperature {
            display: block;
            // height: 52rpx;
            width: 52rpx;
            font-size: 52rpx;
            // margin-bottom: 26rpx;
          }
        }
      }
      .reject-tip {
        position: absolute;
        width: 650rpx;
        color: #fff;
        // display: flex;
        background-color: #ff220d;
        box-shadow: 0rpx 2rpx 6rpx 0rpx rgba(237, 0, 0, 0.36);
        border-radius: 8rpx;
        padding-top: 20rpx;
        padding-bottom: 20rpx;
        bottom: 30rpx;
        left: 30rpx;
        text-align: center;
        z-index: 100;
      }
    }
    .operate-panel {
      margin-top: 30rpx;
      height: 150rpx;
      display: flex;
      justify-content: space-between;
      .btn {
        width: 340rpx;
        font-size: 70rpx;
        display: flex;
        justify-content: center;
        align-items: center;
      }
      .btn::after {
        border: none;
      }
      .btn-reject {
        background-color: #ff2d2d;
        box-shadow: 0rpx 4rpx 7rpx 0rpx rgba(255, 45, 45, 0.3);
      }
      .btn-success {
        background-color: #3a6eff;
        box-shadow: 0rpx 4rpx 7rpx 0rpx rgba(58, 110, 255, 0.3);
      }
      .forbidden {
        background-color: #bcbfca;
        box-shadow: 0rpx 4rpx 7rpx 0rpx rgba(188, 191, 202, 0.3);
        color: rgba(255, 255, 255, 0.33);
      }
    }

    .sub-user {
      margin-top: 20rpx;
      height: 120rpx;
      padding-left: 30rpx;
      padding-right: 30rpx;
      display: flex;
      flex-direction: row;
      justify-content: space-between;
      align-items: center;
      align-content: center;
      .username {
        color: #222222;
        font-size: 40rpx;
        flex-grow: 1;
        font-weight: bold;
      }
      .show-info-btn {
        background-color: #fff;
        // width: 200rpx;
        height: 80rpx;
        font-size: 32rpx;
        border-color: #a4a4a4;
      }
      .sub-user-no-tip {
        font-size: 40rpx;
      }
    }
    .out-in-record {
      margin-top: 20rpx;
      min-height: 280rpx;
      padding: 30rpx;
      display: flex;
      flex-direction: column;
      .title {
        display: block;
        font-size: 32rpx;
        color: #222222;
        margin-bottom: 30rpx;
      }
      .record-content {
        border-top: 1rpx solid #d8d8d8;
        text-align: center;
        .record-null-content {
          color: #222222;
          font-size: 26rpx;
          margin-top: 60rpx;
          display: block;
        }
        .record-content-item {
          height: 100rpx;
          display: flex;
          justify-content: space-between;
          align-items: center;
          font-size: 26rpx;
          color: #222222;
          .date {
            width: 226rpx;
            display: block;
          }
          .username {
            flex-grow: 1;
            display: block;
          }
          .status {
            width: 130rpx;
            display: block;
            text-align: right;
          }
          .out {
            color: #3a6eff;
          }
          .in {
            color: #00a909;
          }
          .reject {
            color: #ff220d;
          }
        }
        .record-content-item + .record-content-item {
          border-top: 1rpx solid #d8d8d8;
        }
      }
    }
  }
}
</style>
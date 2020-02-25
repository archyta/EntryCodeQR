

<template>
  <div class="main">
    <div class="container">
      <mp-toptips :delay="5000" :msg="error" :show="errorShow" @hide="onErrorHidden" type="error"></mp-toptips>
      <panel class="panel header">
        <div class="bg-left">
          <div class="communety">{{store_housingName}}</div>
          <div class="filter-time">
            <span v-if="activeFilter === 'filter'">{{showDate1}}-{{showDate2}}</span>
            <span v-if="activeFilter === 'today'">今日</span>
            <span v-if="activeFilter === 'yesterday'">昨日</span>
            <span v-if="filters.temperature">{{filters.temperature}}℃</span>
          </div>
        </div>
        <div class="bg-right">
          共计：
          <span class="highlight">{{total == -1? 0: total}}</span>人
        </div>
      </panel>
      <div class="scroll_wrapper">
        <scroll-view @scrolltolower="loadMore" class="list" scroll-y="true">
          <div :key="user.id" @tap="makeCall(user.mobilePhone)" v-for="(user,outerIndex) in listData">
            <div
              :class="{last:outerIndex===listData.length-1&&user.temperatureRecords.length-1===innerIndex}"
              :key="innerIndex"
              class="panel row-user"
              v-for="(item, innerIndex) in user.temperatureRecords"
            >
              <div class="user-main flex-300">
                <div class="name" v-if="innerIndex === 0">{{user.building}}栋{{user.unit}}单元{{user.roomNumber}}/{{user.userName}}</div>
                <div class="phone" v-if="innerIndex === 0">{{user.showPhone}}</div>
              </div>
              <div class="temperature">
                <span
                  :class="{normal: item.temperature< temps.dangerTempare, danger: item.temperature>= temps.dangerTempare && item.temperature < temps.unusual, unusual: item.temperature>= temps.unusual}"
                >{{item.temperature}}℃</span>
              </div>
              <div class="time-wrapper">
                <div class="time">{{item.time}}</div>
                <div class="date">{{item.date}}</div>
              </div>
            </div>
          </div>
          <div class="no_guard" v-if="!listData.length">暂无更多数据</div>
          <view class="loadMoreGif" wx:if="{{isLoading}}">
            <image class="loading-image" src="{{'./../static/icon/loading.gif'}}" />
            <text>正在加载中</text>
          </view>
        </scroll-view>
      </div>
      <div @tap="openModel = false" class="bg-modal" v-if="openModel">
        <div @tap.stop class="filter">
          <div class="filter-header">按条件查询</div>
          <div class="msg-con">
            <div class="operate-info-item">
              <span class="label" style="width:350rpx">体温： 大于等于</span>
              <div class="input-container">
                <input
                  class="input"
                  placeholder="请输入体温"
                  placeholder-style="font-size:32rpx;font-family: '-apple-system,BlinkMacSystemFont,SF UI Text,Helvetica Neue,PingFang SC,Hiragino Sans GB,Microsoft YaHei UI,Microsoft YaHei,Arial,sans-serif';"
                  type="digit"
                  v-model="copyFilter.temperature"
                />
                <span class="temperature">
                  <span>℃</span>
                </span>
              </div>
            </div>
            <div class="operate-info-item">
              <span class="label">日期：</span>
              <div class="input-container">
                <picker @change="bindDateChange($event,'startDate')" mode="date" value="{{copyFilter.startDate}}">
                  <view class="picker-view">{{copyFilter.startDate}}</view>
                </picker>至
                <picker @change="bindDateChange($event,'endDate')" mode="date" value="{{copyFilter.endDate}}">
                  <view class="picker-view">{{copyFilter.endDate}}</view>
                </picker>
              </div>
            </div>
          </div>
          <div class="btns">
            <div @tap="cancles" class="msg-btn cancel">取消</div>
            <div @tap="sureFilter" class="msg-btn sure">确认</div>
          </div>
        </div>
      </div>
      <div class="buttons">
        <div :class="{active:activeFilter === 'today'}" @tap="changeFilterType('today')" class="botton-item today">今日</div>
        <div :class="{active:activeFilter === 'yesterday'}" @tap="changeFilterType('yesterday')" class="botton-item yesterday">昨日</div>
        <div :class="{active:activeFilter === 'filter'}" @tap="openFilterModer" class="botton-item set-filter">按条件查询</div>
      </div>
    </div>
  </div>
</template>
      
<script>
import wepy from '@wepy/core'
import getters from '@/store/getters'
import store from '../store'
import { getTempatureList } from '@/api/manage'
import { formate, compareVersion } from '@/utils/common.js'

wepy.page({
  store,
  config: {
    navigationBarTitleText: 'W11123'
  },
  hooks: {},
  data: {
    userName: '',
    openModel: false,
    activeFilter: 'today',
    errorShow: false,
    error: '',
    isLoading: false,
    currentPage: 1,
    limit: 10,
    filters: {
      startDate: '',
      endDate: '',
      temperature: '',
      estateId: ''
    },
    copyFilter: {
      startDate: '',
      endDate: '',
      temperature: '',
      estateId: ''
    },
    showDate1: null,
    showDate2: null,
    temps: {
      dangerTempare: 0,
      unusual: 0
    },

    total: -1,
    listData: []
  },
  onShow() {
    // 基础库2.9.5才支持
    if (compareVersion('2.9.5') > -1) {
      wepy.wx.hideHomeButton()
    }
  },
  onLoad(option) {
    this.filters.estateId = this.store_housingEstateId
    this.filters.temperature = option.unusual || 37.3
    this.temps.unusual = option.unusual || 37.3
    this.temps.dangerTempare = option.danger || 37.1
    this.initData()
  },
  computed: {
    ...getters()
  },
  watch: {
    'filters.startDate': {
      handler(ne) {
        this.showDate1 = formate(ne, 'MM月dd日')
      }
    },
    'filters.endDate': {
      handler(ne) {
        this.showDate2 = formate(ne, 'MM月dd日')
      }
    }
  },
  methods: {
    loadMore() {
      if (this.isLoading || (this.listData.length >= this.total && this.total !== -1)) {
        this.isLoading = false
        return
      }
      console.log('页数：', this.currentPage) // console打印页数
      this.getlistData() // 调用截取方法
    },
    changeFilterType(type) {
      this.activeFilter = type
      this.filters.temperature = this.temps.unusual
      this.initData(type)
    },
    initData(type) {
      this.currentPage = 1
      this.listData = []
      let dateStr = formate(new Date().getTime(), 'yyyy/MM/dd')
      if (type === 'yesterday') {
        dateStr = formate(new Date().getTime() - 24 * 60 * 60 * 1000, 'yyyy/MM/dd')
      }
      this.filters.startDate = dateStr
      this.filters.endDate = dateStr
      this.getlistData()
    },
    openFilterModer() {
      this.openModel = true
      this.copyFilter = { ...this.filters }
    },
    bindDateChange: function(e, prop) {
      this.copyFilter[prop] = e.$wx.detail.value.split('-').join('/')
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
    },
    makeCall(num) {
      wx.makePhoneCall({
        phoneNumber: num,
        complete() {
          // self.showContact = false
        }
      })
    },
    cancles() {
      this.openModel = false
    },
    sureFilter() {
      if (this.copyFilter.temperature) {
        let value = this.copyFilter.temperature
        if (value >= 50 || value < 25) {
          return this.openError('温度范围异常，请输入25-50之前的温度值')
        }
        if (value && !/^\d+(\.\d+)?$/.test(value)) {
          return this.openError('温度只能填入数字')
        }
      }
      this.activeFilter = 'filter'
      this.filters = { ...this.copyFilter }
      this.showDate1 = formate(this.filters.startDate, 'MM月dd日')
      this.showDate2 = formate(this.filters.endDate, 'MM月dd日')
      this.currentPage = 1
      this.listData = []
      this.getlistData()
      this.cancles()
    },
    getShowMonth(date) {
      console.log(formate(date, 'MM月dd日'))
      return formate(date, 'MM月dd日')
    },
    getlistData() {
      this.isLoading = true
      getTempatureList({ ...this.filters, page: this.currentPage, limit: this.limit })
        .then(res => {
          const { total = 0, rows = [] } = res.data.data || {}
          this.total = total || 0
          if (rows && rows.length) {
            this.listData = [...this.listData, ...this.dealTime(rows || [])]
            this.currentPage++
          }
          if (rows && rows.length < this.limit) {
            this.isLoading = false
          }
        })
        .catch(e => {
          this.openError('服务器不知道去哪了,正在紧急查找中...')
          this.isLoading = false
        })
    },
    dealTime(list = []) {
      list.forEach(el => {
        if (el.mobilePhone) {
          el.showPhone = el.mobilePhone.substr(0, 6) + '****' + el.mobilePhone.substr(-1)
        }
        if (el.temperatureRecords && el.temperatureRecords.length) {
          el.temperatureRecords.forEach(item => {
            let { crtTime } = item
            let arr = (crtTime = crtTime.split(' '))
            item.date = arr[0]
            item.time = arr[1]
          })
        }
      })
      return list
    },
    onErrorHidden() {
      this.error = ''
      this.errorShow = false
    },
    openError(msg) {
      this.error = msg
      this.errorShow = true
    }
  }
})
</script>
<config>
  {
    navigationBarTitleText: '体温异常名单',
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
  font-family: PingFangSC;
  height: 100%;
}
.scroll_wrapper {
  display: flex;
  height: calc(100% - 190rpx);
  .loadMoreGif {
    text-align: center;
    display: flex;
    // margin-left: 20rpx;
    // margin-right: 20rpx;
    background: #fff;
    // flex-direction: column;
    justify-content: center;
    align-items: center;
    font-size: 24rpx;
    color: #aaa;
    .loading-image {
      width: 28rpx;
      height: 28rpx;
      margin-right: 20rpx;
    }
  }
}
.highlight {
  color: #3a6eff;
}
.required {
  color: #f00;
  vertical-align: middle;
  margin-left: 5rpx;
}
.mgt-20 {
  margin-top: 20rpx;
}
.flex-300 {
  flex: 0 0 300rpx;
}
.container {
  color: #222;
  padding: 20rpx;
  display: flex;
  flex-direction: column;
  // margin-top: 20rpx;
  margin-bottom: 20rpx;
  height: 100%;
  box-sizing: border-box;
  .panel {
    display: flex;
    background-color: #fff;

    overflow: hidden;
    &.header {
      padding: 20rpx;
      justify-content: space-between;
      font-size: 24rpx;
      .communety {
        font-size: 32rpx;
        font-weight: 600;
      }
      .bg-right {
        text-align: right;
        line-height: 84rpx;
      }
    }
    &.row-user {
      padding: 0 20rpx;
      box-sizing: border-box;
      height: 110rpx;
      width: 100%;
      color: #222;
      font-size: 24rpx;
      line-height: 30rpx;
      align-items: center;
      // line-height: 110rpx;
      justify-content: space-between;
      font-size: 32rpx;
      .flex_140 {
        flex: 0 0 140rpx;
      }
      .user-main {
        line-height: 44rpx;
        font-weight: 600;
        .phone {
          color: #999;
          line-height: 34rpx;
        }
      }
      .temperature {
        .normal {
          color: #3a6eff;
        }
        .danger {
          color: #fcb000;
        }
        .unusual {
          color: #ff220d;
        }
      }
      .time-wrapper {
        text-align: right;
        line-height: 38rpx;
      }
    }

    &:not(.last) {
      border-radius: 0;
      border-bottom: 1rpx solid #f0f0f0;
    }
  }
  .no_guard {
    line-height: 320rpx;
    font-size: 32rpx;
    // margin-top: 60rpx;
    color: #666;
    text-align: center;
    background: #fff;
  }
  .bg-modal {
    position: fixed;
    left: 0;
    width: 100vw;
    height: 100vh;
    top: 0;
    background: rgba(1, 1, 1, 0.4);
    display: flex;
    justify-content: center;
    align-items: center;

    .filter {
      width: 650rpx;
      height: 460rpx;
      background: #fff;
      border-radius: 8rpx;
      box-sizing: border-box;
      display: flex;
      justify-content: space-between;
      flex-direction: column;
      overflow: hidden;
      .filter-header {
        height: 100rpx;
        background: #3a6eff;
        color: #fff;
        font-size: 32rpx;
        text-align: center;
        line-height: 100rpx;
      }
      // height: 440rpx;
      // margin-top: 30rpx;
      // display: flex;
      // flex-direction: column;
      .operate-info-item + .operate-info-item {
        border-top: 2rpx solid #d8d8d8;
      }
      .operate-info-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        flex-direction: row;
        height: 100rpx;

        .label {
          font-size: 32rpx;
          color: #222222;
          display: block;
          width: 140rpx;
          text-align: left;
          padding-left: 20rpx;
        }

        .item-right {
          margin-left: 10px;
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
          font-size: 32rpx;
          flex-grow: 1;
          height: 32rpx;
          display: flex;
          align-items: center;
          justify-content: space-between;
          .input {
            text-align: right;
            height: 66rpx;
            font-size: 32rpx;
            flex-grow: 1;
            // font-family: '-apple-system,BlinkMacSystemFont,SF UI Text,Helvetica Neue,PingFang SC,Hiragino Sans GB,Microsoft YaHei UI,Microsoft YaHei,Arial,sans-serif';
          }
          .temperature {
            display: block;
            // height: 52rpx;
            width: 32rpx;
            font-size: 40rpx;
            // margin-bottom: 26rpx;
          }
        }
      }

      .msg-con {
        padding: 0 30rpx;
        box-sizing: border-box;
        text-align: center;
        font-size: 44rpx;
      }
      .btns {
        font-size: 32rpx;
        color: #222;
        display: flex;
        justify-content: stretch;
        .msg-btn {
          border-top: 1rpx solid #eee;
          text-align: center;
          line-height: 100rpx;
          width: 50%;
          &.sure {
            color: #3a6eff;
            border-left: 1rpx solid #eee;
          }
        }
      }
    }
  }
  .buttons {
    position: fixed;
    background: #fff;
    left: 0;
    bottom: 0;
    line-height: 100rpx;
    height: 100rpx;
    width: 100vw;
    display: flex;
    justify-content: stretch;
    .botton-item {
      flex-grow: 1;
      text-align: center;
      position: relative;
      &.active {
        color: #3a6eff;
      }
      &::after {
        content: ' ';
        border: 1rpx solid #eee;
        width: 1rpx;
        height: 60rpx;
        position: absolute;
        right: 0;
        top: 20rpx;
      }
      &.set-filter {
        &::after {
          content: '';
          border: none;
        }
      }
    }
  }
}
</style>
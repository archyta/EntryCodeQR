

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
            <span>{{' ' +showCheckText}}</span>
          </div>
        </div>
        <div class="bg-right">
          共计：
          <span class="highlight">{{total == -1? 0: total}}</span>人
        </div>
        <button @tap="exportData" class="right-btn">导出数据</button>
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
              <div class="user-main">
                <div class="name" v-if="innerIndex === 0">{{user.building}}栋{{user.unit}}单元{{user.roomNumber}}/{{user.userName}}</div>
                <div class="phone" v-if="innerIndex === 0">{{user.showPhone}}</div>
              </div>
              <!-- <div class="temperature">
                <span
                  :class="{normal: item.temperature< temps.dangerTempare, danger: item.temperature>= temps.dangerTempare && item.temperature < temps.unusual, unusual: item.temperature>= temps.unusual}"
                >{{item.temperature}}℃</span>
              </div>-->
              <div class="time-wrapper">
                <div class="time">{{item.date + item.showIntervalStr}}</div>
                <div class="date">未登记体温</div>
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
              <span class="label">包含</span>
              <div class="input-container">
                <checkbox-group @change="checkboxChange" class="checkbox-group">
                  <label class="checkbox">
                    <checkbox :checked="checkboxCopy.AM" color="#3a6eff" id="cehckbox_1" style="zoom:0.6" value="AM">
                      <label :class="{checked:checkboxCopy.AM}">上午</label>
                    </checkbox>
                  </label>
                  <label class="checkbox">
                    <checkbox :checked="checkboxCopy.PM" color="#3a6eff" id="cehckbox_2" style="zoom:0.6" value="PM">
                      <label :class="{checked:checkboxCopy.PM}">下午</label>
                    </checkbox>
                  </label>
                </checkbox-group>
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
        <div :class="{active:activeFilter === 'today' && checkbox.AM}" @tap="changeFilterType('today','AM')" class="botton-item today">
          <div>今日</div>
          <div>上午</div>
        </div>
        <div :class="{active:activeFilter === 'today' && checkbox.PM}" @tap="changeFilterType('today','PM')" class="botton-item today">
          <div>今日</div>
          <div>下午</div>
        </div>
        <div :class="{active:activeFilter === 'yesterday' && checkbox.AM}" @tap="changeFilterType('yesterday','AM')" class="botton-item yesterday">
          <div>昨日</div>
          <div>上午</div>
        </div>
        <div :class="{active:activeFilter === 'yesterday' && checkbox.PM}" @tap="changeFilterType('yesterday','PM')" class="botton-item yesterday">
          <div>昨日</div>
          <div>下午</div>
        </div>
        <div :class="{active:activeFilter === 'filter'}" @tap="openFilterModer" class="botton-item set-filter">
          <div>条件</div>
          <div>查询</div>
        </div>
      </div>
    </div>
  </div>
</template>
      
<script>
import wepy from '@wepy/core'
import getters from '@/store/getters'
import store from '../store'
import { getUnReportedTem, DownloadAndOpenUnUploadTemperature } from '@/api/manage'
import { formate, compareVersion } from '@/utils/common.js'
const strMap = { AM: '上午', PM: '下午' }
wepy.page({
  store,
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
      estateId: ''
    },
    copyFilter: {
      startDate: '',
      endDate: '',
      estateId: ''
    },
    showDate1: null,
    showDate2: null,
    temps: {
      dangerTempare: 0,
      unusual: 0
    },
    checkbox: {
      AM: true
    },
    checkboxCopy: {},

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
    this.initData()
  },
  computed: {
    ...getters(),
    timeInterval() {
      let res = []
      for(let key in this.checkbox) {
        if(this.checkbox[key]){
          res.push(key)
        }
      }
      return res
    },
    timeIntervalCopy(){
      let res = []
      for(let key in this.checkbox) {
        if(this.checkbox[key]){
          res.push(key)
        }
      }
      return res
    },
    showCheckText() {
      return this.timeInterval.map(el => strMap[el]).join('+')
    }
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
        return
      }
      console.log('页数：', this.currentPage) // console打印页数
      this.getlistData() // 调用截取方法
    },
    changeFilterType(type, m) {
      this.activeFilter = type
      this.checkbox = []
      this.$set(this.checkbox, m, true)
      // this.filters.temperature = this.temps.unusual
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
      this.checkboxCopy = { ...this.checkbox }
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
    checkboxChange(e) {
      if (e.$wx && e.$wx.detail && e.$wx.detail.value) {
        let m = ['AM', 'PM']
        m.forEach(el => {
          if (e.$wx.detail.value.indexOf(el) > -1) {
            this.$set(this.checkboxCopy, el, true)
          } else {
            this.$set(this.checkboxCopy, el, false)
          }
        })
      }
    },
    cancles() {
      this.openModel = false
    },
    sureFilter() {
      if (!this.timeIntervalCopy.length) {
        return this.openError('“上午”,“下午”筛选条件至少选择一个')
      }
      this.activeFilter = 'filter'
      this.filters = { ...this.copyFilter }
      this.checkbox = { ...this.checkboxCopy }
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
      let timeInterval = this.timeInterval
      wx.showToast({
        title: '操作成功',
        icon: 'success',
        duration: 2000
      })
      getUnReportedTem({ ...this.filters, timeInterval, page: this.currentPage, limit: this.limit })
        .then(res => {
          wx.hideLoading()
          if (res.data.status === 200) {
            const { total = 0, rows = [] } = res.data.data || {}
            this.total = total || 0
            if (rows && rows.length) {
              this.listData = [...this.listData, ...this.dealTime(rows || [])]
              this.currentPage++
            }
          }
          this.isLoading = false
        })
        .catch(e => {
          this.openError('网络繁忙，请稍后再试')
          this.isLoading = false
          wx.hideLoading()
        })
    },
    // 导出数据
    exportData() {
      let timeInterval = this.timeInterval
      wx.showLoading({
        title: '加载中',
        mask: true
      })
      DownloadAndOpenUnUploadTemperature({ ...this.filters, timeInterval })
        .then(res => {
          wx.hideLoading()
        })
        .catch(err => {
          wx.hideLoading()
        })
    },
    dealTime(list = []) {
      list.forEach(el => {
        if (el.mobilePhone) {
          el.showPhone = el.mobilePhone.substr(0, 6) + '****' + el.mobilePhone.substr(-1)
        }
        if (el.temperatureRecords && el.temperatureRecords.length) {
          let str = el.temperatureRecords.forEach(item => {
            let { crtTime } = item
            let arr = (crtTime = crtTime.split(' '))
            item.date = arr[0]
            item.time = arr[1]
            item.showIntervalStr = strMap[item.timeInterval]
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
    navigationBarTitleText: '未报体温名单',
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
    &.last {
      border-radius: 0 0 8rpx 8rpx;
    }
    &:not(.last) {
      border-bottom: 1rpx solid #f0f0f0;
    }

    overflow: hidden;
    &.header {
      padding: 20rpx;
      justify-content: space-between;
      font-size: 24rpx;
      align-items: center;
      border-radius: 8rpx 8rpx 0 0;
      .communety {
        font-weight: 600;
        font-size: 24rpx;
      }

      .bg-right {
        text-align: right;
        line-height: 84rpx;
      }
      button.right-btn {
        font-size: 24rpx;
        margin: 0;
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
      justify-content: space-between;
      & > view {
        flex: 1;
      }
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
      .time-wrapper {
        text-align: right;
        line-height: 38rpx;
      }
    }
  }
  .no_guard {
    line-height: 320rpx;
    font-size: 32rpx;
    // margin-top: 60rpx;
    color: #666;
    text-align: center;
    background: #fff;
    border-radius: 0 0 8rpx 8rpx;
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
    z-index: 1;

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
        .input-container {
          font-size: 32rpx;
          flex-grow: 1;
          height: 32rpx;
          display: flex;
          align-items: center;
          justify-content: space-between;
          .checkbox-group {
            flex-grow: 1;
            display: flex;
            justify-content: space-around;
            .checkbox {
              font-size: 32rpx;
              .checked {
                color: #3a6eff;
              }
            }
          }
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
    line-height: 34rpx;
    height: 100rpx;
    width: 100vw;
    display: flex;
    justify-content: stretch;
    font-size: 28rpx;
    .botton-item {
      flex-grow: 1;
      display: flex;
      text-align: center;
      position: relative;
      flex-direction: column;
      justify-content: center;
      &.active {
        color: #3a6eff;
      }
      &::after {
        content: ' ';
        border-right: 1rpx solid #eee;
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
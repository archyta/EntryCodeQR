

<template>
  <div class="main">
    <div class="container">
      <panel class="panel header">
        <div class="search_wrapper" @tap="changeFilterType">
          
        </div>
        <div class="bg-left">
          <div class="communety">{{store_housingName || '国人XXX富强小区'}}</div>
          <div class="filter-time">{{filters.startTime}}-{{filters.endTime}} {{filters.temperature}}℃</div>
        </div>
        <div class="bg-right">
          共计：
          <span class="highlight">{{total}}</span>人
        </div>
      </panel>
      <div class="scroll_wrapper">
        <scroll-view @scrolltolower="loadMore" class="list" scroll-y="true">
          <div :key="user.id" @tap="makeCall(user.phone)" v-for="(user,outerIndex) in listData">
            <div :class="{last: outerIndex === listData.length - 1 && user.detail.length === innerIndex - 1}" :key="innerIndex" class="panel row-user" v-for="(item, innerIndex) in user.listDetail">
              <div class="user-main flex-300">
                <div class="name" v-if="innerIndex === 0">{{user.builds}}栋{{user.unit}}单元{{user.roomNumber}}/{{user.name}}</div>
                <div class="phone" v-if="innerIndex === 0">{{user.phone}}</div>
              </div>
              <div class="temperature">
                <span>{{item.temperature}}℃</span>
              </div>
              <div class="time-wrapper">
                <div class="time">{{item.time}}</div>
                <div class="date">{{item.date}}</div>
              </div>
            </div>

            <!-- <div class="flex_140 status_desc">
          <span>{{['通过','不通过'][user.status]}}</span>
            </div>-->
            <!-- <div>
          <span @tap="showConfirm(user)" class="icon del">
            <mp-icon :size="16" color="#f00" icon="close"></mp-icon>
          </span>
            </div>-->
          </div>
          <div class="no_guard" v-if="!listData.length">暂无数据</div>
        </scroll-view>
      </div>
      <div @tap="openModel = false" class="bg-modal" v-if="openModel">
        <div @tap.stop class="filter">
          <div class="filter-header">按条件查询</div>

          <div class="msg-con">
            <div class="only-input">
              <input placeholder="输入任意关键字" type="text" />
            </div>
            <div class="operate-info-item">
              <span class="label">按固定时间段</span>
              <div class="input-container">
                <picker @change="bindDateChange,'endTime'" range-key="name" :range="allScope" value="{{filters.endTime}}">
                  <view class="picker-view">{{filters.endTime}}</view>
                </picker>
              </div>
            </div>
            <div class="operate-info-item">
              <span class="label">按状态</span>
              <div class="input-container">
                <picker @change="bindDateChange,'endTime'" range-key="name" :range="allStatus" value="{{filters.endTime}}">
                  <view class="picker-view">{{filters.endTime}}</view>
                </picker>
              </div>
            </div>
            <div class="operate-info-item">
              <span class="label">按人名</span>
              <div class="input-container">
                <input
                  @change="onTemperatureInputChange"
                  class="input"
                  placeholder="请输入"
                  placeholder-style="font-size:32rpx;font-family: '-apple-system,BlinkMacSystemFont,SF UI Text,Helvetica Neue,PingFang SC,Hiragino Sans GB,Microsoft YaHei UI,Microsoft YaHei,Arial,sans-serif';"
                  type="text"
                  v-model="filters.name"
                />
              </div>
            </div>

            <div class="operate-info-item">
              <span class="label">指定时间段</span>
              <div class="input-container space-bet">
                <picker @change="bindDateChange($event,'startTime')" end="2017-09-01" mode="date" start="2015-09-01" value="{{filters.startTime}}">
                  <view class="picker-view">{{filters.startTime}}</view>
                </picker>至
                <picker @change="bindDateChange,'endTime'" end="2017-09-01" mode="date" start="2015-09-01" value="{{filters.endTime}}">
                  <view class="picker-view">{{filters.endTime}}</view>
                </picker>
              </div>
            </div>
          </div>
          <div class="btns">
            <div @tap="cancles" class="msg-btn cancel">取消</div>
            <div @tap="sureDel" class="msg-btn sure">确认</div>
          </div>
        </div>
      </div>
      <div class="buttons">
        返回顶部
      </div>
    </div>
  </div>
</template>
      
<script>
import wepy from '@wepy/core'
import getters from '@/store/getters'
import store from '../store'
import { getlistData, delGuradById } from '@/api/manage'
import { compareVersion } from '@/utils/common.js'

wepy.page({
  store,
  config: {
    navigationBarTitleText: 'W11123'
  },
  hooks: {},
  data: {
    userName: '',
    qrdata: {},
    openModel: false,
    activeFilter: 'today',
    // store_housingName: '国人富强小区',
    filters: {
      startTime: '12月02日',
      endTime: '12月02日',
      temperature: '38'
    },
    allScope: [
      {
        name: '全部',
        value: ''
      },
      {
        name: '今天',
        value: 'today'
      },
      {
        name: '本周',
        value: 'week'
      },
      {
        name: '本月',
        value: 'month'
      },
      {
        name: '上月',
        value: 'lastMonth'
      }
    ],
    allStatus: [
      {
        name: '全部',
        value: ''
      },
      {
        name: '进门',
        value: 'today'
      },
      {
        name: '出门',
        value: 'week'
      },
      {
        name: '进门/劝返',
        value: 'month'
      },
      {
        name: '出门/劝返',
        value: 'lastMonth'
      }
    ],
    total: 35,
    listData: [
      {
        name: '老王',
        unit: '2',
        builds: '23',
        roomNumber: '300',
        phone: '18398851383',
        listDetail: [
          {
            temperature: 32,
            time: '8.20',
            date: '2012/2/20'
          },
          {
            temperature: 32,
            time: '8.20',
            date: '2012/2/20'
          }
        ]
      }
    ]
  },
  created() {
    // this.getlistData(this.store_housingEstateId)
  },
  onShow() {
    // 基础库2.9.5才支持
    if (compareVersion('2.9.5') > -1) {
      wepy.wx.hideHomeButton()
    }
  },
  computed: {
    ...getters()
  },
  methods: {
    getStatusStr(status) {
      return ['通过', '不通过'][status]
    },
    changeFilterType(type) {
      this.activeFilter = type
    },
    openFilterModer() {
      this.openModel = true
    },
    onTemperatureInputChange(e) {
      let value = e.$wx.detail.value
      let m = this.convertNumber(value, 2)
      if (m) {
        this.filters.temperature = m.toString()
      } else {
        this.filters.temperature = null
      }
    },
    bindDateChange: function(e, prop) {
      // console.log('picker发送选择改变，携带值为', e)
      // console.log('picker发送选择改变，携带值为', e.detail.value)
      this.filters[prop] = e.$wx.detail.value
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
    refresh() {
      this.getlistData(this.id)
    },
    sureDel() {
      this.refresh()
      this.cancles()
    },
    delGuard(id) {
      wx.showLoading({
        title: '加载中',
        mask: true
      })
      delGuradById(id, this.id)
        .then(res => {
          if (res.data.status === 200) {
            // this.listData = res.data.data
            this.refresh()
            this.openModel = null
            wx.hideLoading()
          } else {
            this.openError(res.data.message)
            wx.hideLoading()
          }
        })
        .catch(e => {
          // this.openError('服务器不知道去哪了,正在紧急查找中...')
          wx.hideLoading()
        })
    },
    getlistData(id) {
      getlistData(id)
        .then(res => {
          if (res.data.status === 200) {
            // this.listData = res.data.data
          } else {
            this.openError(res.data.message)
          }
        })
        .catch(e => {
          // this.openError('服务器不知道去哪了,正在紧急查找中...')
        })
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
    navigationBarTitleText: '人员进出记录',
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
  font-family: PingFangSC;
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
.addition-img {
  height: 400rpx;
  border: 1rpx solid #eee;
  border-radius: 8rpx;
  background: #fff;
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
  .panel {
    display: flex;
    background-color: #fff;
    overflow: hidden;
    &.header {
      padding: 20rpx;
      justify-content: space-between;
      font-size: 24rpx;
      .search_wrapper{
        width: 100%;
      }
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
        line-height: 34rpx;
        font-weight: 600;
        .phone {
          color: #999;
        }
      }
      .temperature {
        color: #ff220d;
      }
      .time-wrapper {
        text-align: right;
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
      height: 732rpx;
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
      .only-input {
        display: flex;
        justify-content: space-between;
        align-items: center;
        flex-direction: row;
        height: 100rpx;
        border: 1rpx solid #eee;
        border-radius: 50rpx;
        input {
          text-align: left;
          margin-left: 53rpx;
          font-size: 32rpx;
        }
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
          width: 220rpx;
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
          justify-content: flex-end;
          &.space-bet {
            justify-content: space-between;
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
        margin-top: 30rpx;
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
    text-align: center;
    height: 100rpx;
    width: 100vw;
    display: flex;
    justify-content: center;
  }
}
</style>
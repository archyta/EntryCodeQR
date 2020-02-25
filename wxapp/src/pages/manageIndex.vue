<template>
  <div class="main">
    <div class="container">
      <mp-toptips :delay="5000" :msg="error" :show="errorShow" @hide="onErrorHidden" type="error"></mp-toptips>
      <div class="panel">
        <button @tap="goDeatils" class="add_commuent" type="primary" v-if="hasLogin">
          <span class="text">管理我的小区</span>
        </button>
        <button @tap="goAdd" class="add_commuent" type="primary" v-else>
          <mp-icon :size="16" color="#fff" icon="add"></mp-icon>
          <span class="text">创建我的小区</span>
        </button>
      </div>
      <div class="panel total">
        <div class="total_header">
          <div>
            <span>{{estateName}}-进出人员统计</span>
            <span class="right-text" v-if="!hasLogin">模拟</span>
          </div>
          <div class="time_picker is-only-two">
            <span class="desc">时间段:</span>
            <picker :disabled="!hasLogin" :range="allScope" @change="totalScopeChange" class="desc value" range-key="name" v-model="totalScopeValue">{{totalScope}}</picker>
          </div>
        </div>
        <div class="total_counter">
          <div class="conut_item">
            <div class="counter_main-exception">{{exceptionCount}}</div>
            <div class="desc">体温异常人数</div>
          </div>
          <div class="conut_item">
            <div class="counter_main-reject">{{rejectCount}}</div>
            <div class="desc">累计劝返人数</div>
          </div>
        </div>
      </div>
      <div class="chart-item">
        <div class="item-header">
          <div>
            <span>体温异常人数统计</span>
            <span class="right-text" v-if="!hasLogin">模拟</span>
          </div>
          <div class="time_picker">
            <span class="desc">时间段:</span>
            <picker :disabled="!hasLogin" :range="allScope" @change="onTemperatureChange" class="desc value" range-key="name" v-model="temperatureScopeValue">{{temperatureScope}}</picker>
          </div>
          <button @tap.stop="goTemperatureDetail" class="btn-detail">查看详情</button>
        </div>
        <div style="width: 100%;height: 320rpx">
          <ec-canvas canvas-id="chart3" class="ec-canvas" ec="{{ ec }}" id="chart3"></ec-canvas>
        </div>
      </div>
      <div class="chart-item">
        <div class="item-header">
          <div>
            <span>小区人员体温上报率</span>
            <span class="right-text" v-if="!hasLogin">模拟</span>
          </div>
          <div class="time_picker is-only-two">
            <span class="desc">时间段:</span>
            <picker :disabled="!hasLogin" :range="allScope" @change="onTemperatureUploadChange" class="desc value" range-key="name" v-model="temperatureUploadScopeValue">{{temperatureUploadScope}}</picker>
          </div>
        </div>
        <div style="width: 100%;height: 320rpx">
          <ec-canvas canvas-id="chart4" class="ec-canvas" ec="{{ ec }}" id="chart4"></ec-canvas>
        </div>
      </div>
      <div class="chart-item">
        <div class="item-header">
          <div>
            <span>进出人次统计</span>
            <span class="right-text" v-if="!hasLogin">模拟</span>
          </div>
          <div class="time_picker is-only-two">
            <span class="desc">时间段:</span>
            <picker :disabled="!hasLogin" :range="allScope" @change="outInScopeChange" class="desc value" range-key="name" v-model="outInScopeValue">{{outInScope}}</picker>
          </div>
          <!-- <button class="btn-detail" @tap.stop="goOutInDetail">查看详情</button> -->
        </div>
        <div style="width: 100%;height: 320rpx">
          <ec-canvas canvas-id="chart1" class="ec-canvas" ec="{{ ec }}" id="chart1"></ec-canvas>
        </div>
      </div>
      <div class="chart-item" style>
        <div class="item-header">
          <div>
            <span>劝返人数统计</span>
            <span class="right-text" v-if="!hasLogin">模拟</span>
          </div>
          <div class="time_picker is-only-two">
            <span class="desc">时间段:</span>
            <picker :disabled="!hasLogin" :range="allScope" @change="rejectScopeChange" class="desc value" range-key="name" v-model="rejectScopeValue">{{rejectScope}}</picker>
          </div>
          <!-- <button class="btn-detail">查看详情</button> -->
        </div>
        <div style="width: 100%;height: 320rpx">
          <ec-canvas canvas-id="chart2" class="ec-canvas" ec="{{ ec }}" id="chart2"></ec-canvas>
        </div>
      </div>
      <!-- <div class="" > -->
      <button @tap="showContact = true" class="panel contact-us" v-if="contactPhone">
        <mp-icon :size="16" color="#fff" icon="add"></mp-icon>
        <span class="text">联系我们</span>
      </button>
      <!-- </div> -->
      <cover-view @tap.stop="showContact = false" class="bg_modal" v-if="showContact&&contactPhone">
        <cover-view class="contact_wrapper">
          <cover-view class="contact-title">
            <cover-view>“出入福安”与您共克时艰</cover-view>
            <!-- <mp-icon @tap.stop="showContact = false" :size="32" color="#222" icon="close"></mp-icon> -->
          </cover-view>
          <cover-view class="body_desc">
            <cover-view class="main_desc">{{contactName}} {{contactPhone}}</cover-view>
            <cover-view class="addition">服务支持或者问题解决</cover-view>
          </cover-view>
          <button @tap.stop="makeCall" class="button">拨打电话</button>
        </cover-view>
      </cover-view>
    </div>
  </div>
</template>
 
<script>
import wepy from '@wepy/core'
import eventHub from '../common/eventHub'
import store from '../store'
import getters from '@/store/getters'
import * as echarts from '@/libs/ec-canvas/echarts'
import { getContactPhone } from '@/api/common/index.js'
import { createRandomData, compareVersion } from '@/utils/common.js'
import { getTotalCount, getInOutCount, getRejectCount, getExceptionTemperatureData, getTemperatureUploadData } from '@/api/manage/index.js'

wepy.page({
  store,
  data: {
    ec: { lazyLoad: true },
    showContact: false,
    contactPhone: '',
    contactName: '',
    error: '',
    errorShow: false,
    hasEstate: false,
    allScope: [
      {
        name: '全部',
        value: ''
      },
      {
        name: '最近一月',
        value: 'month'
      },
      {
        name: '最近一周',
        value: 'week'
      },
      {
        name: '今日',
        value: 'today'
      },
      {
        name: '上月',
        value: 'lastMonth'
      }
    ],
    abnormal: 37.3,
    risk: 37.1,
    inOutCount: 120,
    exceptionCount: 20,
    rejectCount: 53,
    totalScope: '最近一周',
    totalScopeValue: 2,
    temperatureScope: '最近一周',
    temperatureScopeValue: 2,
    optionsTemperature: {
      color: ['#3a6eff', '#FCB000', '#FF220D'],
      grid: {
        top: 30,
        bottom: 20,
        left: 30,
        right: 10
      },
      legend: {
        // left: 40,
        data: ['正常＜37.1', '风险≥37.1', '异常≥37.3'],
        itemWidth: 7,
        itemHeight: 7,
        itemGap: 5,
        textStyle: {
          fontSize: 10
        }
      },
      tooltip: {
        show: true,
        // formatter:"{a0} {b0}日: {c0}人次\r\n{a1} {b1}日: {c1}人次\r\n{a2} {b2}日: {c2}人次",
        trigger: 'axis'
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: ['2/1', '2/2', '2/3', '2/4', '2/5', '2/6', '2/7', '2/8', '2/9', '2/10'],
        axisTick: {
          show: false
        },
        axisLine: {
          lineStyle: {
            color: '#ccc'
          }
        },
        splitLine: {
          show: true,
          lineStyle: {
            type: 'solid',
            color: '#eee'
          }
        }
        // show: false
      },
      yAxis: {
        x: 'center',
        type: 'value',
        name: '人',
        nameGap: 0,
        nameTextStyle: {
          padding: [0, 0, 0, 10]
        },
        axisLabel: {
          fontSize: 12
        },
        axisTick: {
          show: false
        },
        axisLine: {
          lineStyle: {
            color: '#ccc'
          }
        },
        splitLine: {
          lineStyle: {
            type: 'solid',
            color: '#eee'
          }
        }
      },
      series: [
        {
          name: '正常＜37.1',
          type: 'line',
          // smooth: true,
          data: createRandomData(10, 300, 40)
        },
        {
          name: '风险≥37.1',
          type: 'line',
          // smooth: true,
          data: createRandomData(10, 200, 50)
        },
        {
          name: '异常≥37.3',
          type: 'line',
          // smooth: true,
          data: createRandomData(10, 100, 100)
        }
      ]
    },
    temperatureUploadScope: '最近一周',
    temperatureUploadScopeValue: 2,
    optionsTemperatureUpload: {
      color: ['#3a6eff', '#FCB000', '#FF220D'],
      grid: {
        top: 20,
        bottom: 20,
        left: 30,
        right: 10
      },
      tooltip: {
        show: true,
        formatter: '{b} : {c}%',
        trigger: 'axis'
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: ['2/1', '2/2', '2/3', '2/4', '2/5', '2/6', '2/7', '2/8', '2/9', '2/10'],
        axisTick: {
          show: false
        },
        axisLine: {
          lineStyle: {
            color: '#ccc'
          }
        },
        splitLine: {
          show: true,
          lineStyle: {
            type: 'solid',
            color: '#eee'
          }
        }
        // show: false
      },
      yAxis: {
        x: 'center',
        type: 'value',
        name: '%',
        nameGap: 0,
        nameTextStyle: {
          padding: [0, 0, 0, 10]
        },
        axisLabel: {
          fontSize: 12
        },
        axisTick: {
          show: false
        },
        axisLine: {
          lineStyle: {
            color: '#ccc'
          }
        },
        splitLine: {
          lineStyle: {
            type: 'solid',
            color: '#eee'
          }
        }
      },
      series: [
        {
          // name: 'A',
          type: 'line',
          // smooth: true,
          data: createRandomData(10, 100, 20)
        }
      ]
    },
    outInScope: '最近一周',
    outInScopeValue: 2,
    optionsOutIn: {
      title: {
        text: '进出人数统计',
        left: 'left',
        show: false,
        style: {
          fontSize: 12
        }
      },
      color: ['#3a6eff', '#FCB000', '#FF220D'],
      grid: {
        top: 20,
        bottom: 20,
        left: 30,
        right: 10
      },
      tooltip: {
        show: true,
        formatter: '{b} : {c}人次',
        trigger: 'axis'
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: ['2/1', '2/2', '2/3', '2/4', '2/5', '2/6', '2/7', '2/8', '2/9', '2/10'],
        axisTick: {
          show: false
        },
        axisLine: {
          lineStyle: {
            color: '#ccc'
          }
        },
        splitLine: {
          show: true,
          lineStyle: {
            type: 'solid',
            color: '#eee'
          }
        }
        // show: false
      },
      yAxis: {
        x: 'center',
        type: 'value',
        name: '人/次',
        nameGap: 0,
        nameTextStyle: {
          padding: [0, 0, 0, 30]
        },
        axisLabel: {
          fontSize: 12
        },
        axisTick: {
          show: false
        },
        axisLine: {
          lineStyle: {
            color: '#ccc'
          }
        },
        splitLine: {
          lineStyle: {
            type: 'solid',
            color: '#eee'
          }
        }
      },
      series: [
        {
          // name: 'A',
          type: 'line',
          // smooth: true,
          data: createRandomData(10, 500, 200)
        }
      ]
    },
    rejectScope: '最近一周',
    rejectScopeValue: 2,
    optionsReject: {
      color: ['#3a6eff', '#FCB000', '#FF220D'],
      grid: {
        top: 20,
        bottom: 20,
        left: 30,
        right: 10
      },
      tooltip: {
        show: true,
        formatter: '{b} : {c}人次',
        trigger: 'axis'
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: ['2/1', '2/2', '2/3', '2/4', '2/5', '2/6', '2/7', '2/8', '2/9', '2/10'],
        axisTick: {
          show: false
        },
        axisLine: {
          lineStyle: {
            color: '#ccc'
          }
        },
        splitLine: {
          show: true,
          lineStyle: {
            type: 'solid',
            color: '#eee'
          }
        }
        // show: false
      },
      yAxis: {
        x: 'center',
        name: '人/次',
        type: 'value',
        nameGap: 0,
        nameTextStyle: {
          padding: [0, 0, 0, 30]
        },
        axisTick: {
          show: false
        },
        axisLabel: {
          fontSize: 12
        },
        axisLine: {
          lineStyle: {
            color: '#ccc'
          }
        },
        splitLine: {
          lineStyle: {
            type: 'solid',
            color: '#eee'
          }
        }
        // show: false
      },
      series: [
        {
          // name: 'A',
          type: 'line',
          // smooth: true,
          data: createRandomData(10, 80, 30)
        }
      ]
    },
    nullOption: {
      graphic: [
        {
          // 环形图中间添加文字
          type: 'text', // 通过不同top值可以设置上下显示
          left: 'center',
          top: '43%',
          style: {
            text: '暂无数据',
            textAlign: 'center',
            fill: '#666', // 文字的颜色
            width: 30,
            height: 30,
            fontSize: 40
          }
        }
      ]
    }
  },
  computed: {
    ...getters(),
    hasLogin() {
      // return false
      return this.store_hasLogin && this.store_role.indexOf('property') > -1
    },
    estateName() {
      if (this.hasLogin) {
        return this.store_housingName
      } else {
        return '龙都花园'
      }
    }
  },
  onLoad(option) {
    // 注册事件总线 用于登录时检查用户是否登录的情况下-自动加载界面
    eventHub.$on('login-success', (...args) => {
      this.loadCountData()
    })
    // 特殊处理一下 检查权限变更 有可能会删除保安角色
    if (this.hasLogin) {
      this.$store
        .dispatch('login', this.store_housingEstateId)
        .then(res => {
          if (!this.store_housingEstateId) {
            wepy.wx.redirectTo('/pages/main')
            return
          }
          if (this.store_role.indexOf('property') === -1 && this.store_role.indexOf('guard') > -1) {
            wepy.wx.redirectTo('/pages/securityIndex')
            return
          }
          if (this.store_role.indexOf('guard') == -1 && this.store_role.indexOf('property') == -1 && this.store_role.indexOf('owner') > -1) {
            wepy.wx.redirectTo('/pages/index')
            return
          }
        })
        .then(err => {})
    }

    // 加载联系电话
    this.loadPhoneData()
  },
  onShow() {
    // 基础库2.9.5才支持
    if (compareVersion('2.9.5') > -1) {
      wepy.wx.hideHomeButton()
    }

    this.loadCountData()
  },
  onReady() {
    if (!this.hasLogin) {
      this.initOutInChart(this.optionsOutIn)
      this.initRejectChart(this.optionsReject)
      this.initTemperatureChart(this.optionsTemperature)
      this.initTemperatureUploadChart(this.optionsTemperatureUpload)
    }
  },
  methods: {
    loadPhoneData() {
      getContactPhone().then(res => {
        if (res.data.status === 200 && res.data.data) {
          this.contactPhone = res.data.data.mobilePhone
          this.contactName = res.data.data.name
        } else {
          this.contactPhone = ''
          this.contactName = ''
        }
      })
    },
    goDeatils() {
      this.$navigate('/pages/estateDetails')
    },
    makeCall() {
      if (this.contactPhone) {
        wx.makePhoneCall({
          phoneNumber: this.contactPhone,
          complete: () => {
            this.showContact = false
          }
        })
      }
    },
    loadCountData() {
      // 请求数据

      // 更新数据
      // this.asyncUpdate('charts1',data)
      // this.asyncUpdate('charts2',data)
      this.loadTemperatureData()
      this.loadTemperatureUploadData()
      this.loadTotalData()
      this.loadOutInData()
      this.loadRejectData()
    },
    loadTemperatureData() {
      if (this.hasLogin) {
        // Todo 接口替换
        getExceptionTemperatureData(this.store_housingEstateId, this.allScope[this.temperatureScopeValue].value)
          .then(res => {
            if (res.data && res.data.status == 200 && res.data.data) {
              let { abnormalCharts: allData, estateAbnormalTemperature, estateRiskTemperature } = res.data.data || {}
              // 设置label名字
              this.risk = estateRiskTemperature
              this.abnormal = estateAbnormalTemperature
              if (allData.length > 0) {
                this.optionsTemperature.series[0].data = allData.map(el => {
                  return el.value
                })
                this.optionsTemperature.series[1].data = allData.map(el => {
                  return el.riskValue
                })
                this.optionsTemperature.series[2].data = allData.map(el => {
                  return el.abnormalValue
                })
                this.optionsTemperature.xAxis.data = allData.map(el => {
                  return el.date
                })

                this.optionsTemperature.legend.data = [`正常＜${risk}`, `风险≥${risk}`, `异常≥${estateAbnormalTemperature}`]
                this.optionsTemperature.series[0].name = `正常＜${risk}`
                this.optionsTemperature.series[1].name = `风险≥${risk}`
                this.optionsTemperature.series[2].name = `异常≥${estateAbnormalTemperature}`
                this.initTemperatureChart(this.optionsTemperature)
              } else {
                this.initTemperatureChart(this.nullOption)
              }
            } else {
              if (res.data && res.data.message) {
                this.error = res.data.message
              } else {
                this.error = '服务器好像出问题了,请稍后再打开'
              }
              this.errorShow = true
              this.initTemperatureChart(this.nullOption)
            }
          })
          .catch(err => {
            if (err.data && err.data.message) {
              this.error = err.data.message
            } else {
              this.error = '服务器好像出问题了,请稍后再打开'
            }
            this.errorShow = true
            this.initTemperatureChart(this.nullOption)
          })
      }
    },
    loadTemperatureUploadData() {
      if (this.hasLogin) {
        // Todo 接口替换
        getTemperatureUploadData(this.store_housingEstateId, this.allScope[this.temperatureUploadScopeValue].value)
          .then(res => {
            if (res.data && res.data.status == 200 && res.data.data) {
              let allData = res.data.data
              if (allData.length > 0) {
                this.optionsTemperatureUpload.series[0].data = allData.map(el => {
                  return el.rate * 100
                })
                this.optionsTemperatureUpload.xAxis.data = allData.map(el => {
                  return el.date
                })
                this.initTemperatureUploadChart(this.optionsTemperatureUpload)
              } else {
                this.initTemperatureUploadChart(this.nullOption)
              }
            } else {
              if (res.data && res.data.message) {
                this.error = res.data.message
              } else {
                this.error = '服务器好像出问题了,请稍后再打开'
              }
              this.errorShow = true
              this.initTemperatureUploadChart(this.nullOption)
            }
          })
          .catch(err => {
            if (err.data && err.data.message) {
              this.error = err.data.message
            } else {
              this.error = '服务器好像出问题了,请稍后再打开'
            }
            this.errorShow = true
            this.initTemperatureUploadChart(this.nullOption)
          })
      }
    },
    loadTotalData() {
      if (this.hasLogin) {
        getTotalCount(this.store_housingEstateId, this.allScope[this.totalScopeValue].value, this.store_userId)
          .then(res => {
            if (res.data && res.data.status == 200) {
              this.exceptionCount = res.data.data.abnormal || 0
              this.rejectCount = res.data.data.back || 0
            } else {
              if (res.data && res.data.message) {
                this.error = res.data.message
              } else {
                this.error = '服务器好像出问题了,请稍后再打开'
              }
              this.errorShow = true
            }
          })
          .catch(err => {
            if (err.data && err.data.message) {
              this.error = err.data.message
            } else {
              this.error = '服务器好像出问题了,请稍后再打开'
            }
            this.errorShow = true
          })
      }
    },
    loadOutInData() {
      if (this.hasLogin) {
        getInOutCount(this.store_housingEstateId, this.allScope[this.outInScopeValue].value)
          .then(res => {
            if (res.data && res.data.status == 200 && res.data.data) {
              let allData = res.data.data
              if (allData.length > 0) {
                this.optionsOutIn.series[0].data = allData.map(el => {
                  return el.value
                })
                this.optionsOutIn.xAxis.data = allData.map(el => {
                  return el.date
                })
                this.initOutInChart(this.optionsOutIn)
              } else {
                this.initOutInChart(this.nullOption)
              }
            } else {
              if (res.data && res.data.message) {
                this.error = res.data.message
              } else {
                this.error = '服务器好像出问题了,请稍后再打开'
              }
              this.errorShow = true
              this.initOutInChart(this.nullOption)
            }
          })
          .catch(err => {
            if (err.data && err.data.message) {
              this.error = err.data.message
            } else {
              this.error = '服务器好像出问题了,请稍后再打开'
            }
            this.errorShow = true
            this.initOutInChart(this.nullOption)
          })
      }
    },
    loadRejectData() {
      if (this.hasLogin) {
        getRejectCount(this.store_housingEstateId, this.allScope[this.rejectScopeValue].value)
          .then(res => {
            if (res.data && res.data.status == 200 && res.data.data) {
              let allData = res.data.data
              if (allData.length > 0) {
                this.optionsReject.series[0].data = allData.map(el => {
                  return el.value
                })
                this.optionsReject.xAxis.data = allData.map(el => {
                  return el.date
                })
                this.initRejectChart(this.optionsReject)
              } else {
                this.initRejectChart(this.nullOption)
              }
            } else {
              if (res.data && res.data.message) {
                this.error = res.data.message
              } else {
                this.error = '服务器好像出问题了,请稍后再打开'
              }
              this.errorShow = true
              this.initRejectChart(this.nullOption)
            }
          })
          .catch(err => {
            if (err.data && err.data.message) {
              this.error = err.data.message
            } else {
              this.error = '服务器好像出问题了,请稍后再打开'
            }
            this.errorShow = true
            this.initRejectChart(this.nullOption)
          })
      }
    },
    goAdd() {
      this.$navigate('/pages/manageAdd')
    },
    initTempeChart(options) {
      this.$wx.selectComponent('#chart1').init((canvas, width, height) => {
        const chart = echarts.init(canvas, null, {
          width: width,
          height: height
        })
        canvas.setChart(chart)
        chart.setOption(options)
        return chart
      })
    },
    initTemperatureChart(options) {
      this.$wx.selectComponent('#chart3').init((canvas, width, height) => {
        const chart = echarts.init(canvas, null, {
          width: width,
          height: height
        })
        canvas.setChart(chart)
        chart.setOption(options)
        return chart
      })
    },
    initTemperatureUploadChart(options) {
      this.$wx.selectComponent('#chart4').init((canvas, width, height) => {
        const chart = echarts.init(canvas, null, {
          width: width,
          height: height
        })
        canvas.setChart(chart)
        chart.setOption(options)
        return chart
      })
    },
    initOutInChart(options) {
      this.$wx.selectComponent('#chart1').init((canvas, width, height) => {
        const chart = echarts.init(canvas, null, {
          width: width,
          height: height
        })
        canvas.setChart(chart)
        chart.setOption(options)
        return chart
      })
    },
    initRejectChart(options) {
      this.$wx.selectComponent('#chart2').init((canvas, width, height) => {
        const chart = echarts.init(canvas, null, {
          width: width,
          height: height
        })
        canvas.setChart(chart)
        chart.setOption(options)
        return chart
      })
    },
    totalScopeChange(e) {
      let index = e.$wx.detail.value
      this.totalScope = this.allScope[index].name
      this.loadTotalData()
    },
    onTemperatureChange(e) {
      let index = e.$wx.detail.value
      this.temperatureScope = this.allScope[index].name
      this.loadTemperatureData()
    },
    onTemperatureUploadChange(e) {
      let index = e.$wx.detail.value
      this.temperatureUploadScope = this.allScope[index].name
      this.loadTemperatureUploadData()
    },
    outInScopeChange(e) {
      let index = e.$wx.detail.value
      this.outInScope = this.allScope[index].name
      this.loadOutInData()
    },
    rejectScopeChange(e) {
      let index = e.$wx.detail.value
      this.rejectScope = this.allScope[index].name
      this.loadRejectData()
    },
    onErrorHidden() {
      this.error = ''
      this.errorShow = false
    },
    // 跳转到查看详情界面
    goTemperatureDetail(e) {
      if (this.hasLogin) {
        this.$navigate({ url: `/pages/temperatureList?danger=${this.risk}&unusual=${this.abnormal}` })
      }
    },
    // 跳转到进出详情界面
    goOutInDetail(e) {},
    // 跳转到拒绝出入详情界面
    goRejectDetail(e) {}
  }
})
</script>
<config>
  {
    navigationBarTitleText: '小区人员出入统计',
    usingComponents: {
      'mp-icon':'module:weui-miniprogram/miniprogram_dist/icon/icon',
      'mp-toptips':'module:weui-miniprogram/miniprogram_dist/toptips/toptips',
      "ec-canvas": "../libs/ec-canvas/ec-canvas"
    }
  }
</config>
 
<style lang="less">
page {
  height: 100%;
  background-color: #f0f0f0;
}
.mgt-20 {
  margin-top: 20rpx !important;
}
.main {
  display: flex;
  min-height: 100%;
  flex-direction: column;
  .container {
    padding: 20rpx;
    .panel {
      display: flex;
      background-color: #fff;
      border-radius: 8rpx;
      box-shadow: 0px 4px 6px 0px rgba(57, 57, 57, 0.05);
      .add_commuent,
      .contact {
        // margin-top: 30rpx;
        width: 100%;
        height: 88rpx;
        background-color: #3a6eff;
        font-size: 32rpx;
        display: flex;
        justify-content: center;
        justify-items: center;
        box-shadow: 0px 4px 6px 0px rgba(57, 57, 57, 0.05);
        .text {
          margin-left: 10rpx;
          margin-top: 4rpx;
        }
      }
    }
    .contact-us {
      background-color: #fff;
      margin-top: 20rpx;
      border: none;
      color: #222;
      display: flex;
      justify-content: center;
      align-items: center;
    }
    .contact-us::after {
      border: none;
    }
    .total {
      margin-top: 20rpx;
      flex-direction: column;
      .total_header {
        padding: 10rpx 30rpx;
        font-size: 26rpx;
        display: flex;
        justify-content: space-between;
        align-items: center;
      }
      .total_counter {
        border-top: 1rpx solid #eee;
        padding: 20rpx 0;
        display: flex;
        justify-content: space-around;
        .conut_item {
          display: flex;
          align-items: center;
          flex-direction: column;
          .counter_main {
            font-size: 80rpx;
            color: #3a6eff;
          }
          .counter_main-exception {
            font-size: 68rpx;
            color: #ff220d;
          }
          .counter_main-reject {
            font-size: 68rpx;
            color: #3a6eff;
          }

          .desc {
            font-size: 24rpx;
            color: #666;
          }
          .value {
            color: #222;
          }
        }
      }
    }
    .chart-item {
      box-sizing: border-box;
      margin-top: 20rpx;
      padding: 20rpx 30rpx;
      background: #fff;
      font-size: 26rpx;
      border-radius: 8rpx;
      overflow: hidden;
      box-shadow: 0px 4px 6px 0px rgba(57, 57, 57, 0.05);
    }
    .item-header,
    .total_header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      .right-text {
        color: #3a6eff;
        margin-left: 20rpx;
      }
    }
    .btn-detail {
      font-size: 24rpx;
      color: #222;
      padding-left: 12rpx;
      padding-right: 12rpx;
      border: 1.5rpx solid #eaeaea;
      background-color: #fff;
      line-height: 52rpx;
    }

    .time_picker {
      flex-grow: 1;
      text-align: center;
      .desc {
        color: #666;
        display: inline-block;
      }
      .value {
        color: #222;
      }
    }
    .is-only-two {
      text-align: right;
    }
  }
}
.bg_modal {
  position: fixed;
  width: 100vw;
  height: 100vh;
  left: 0;
  top: 0;
  background: rgba(234, 234, 264, 0.93);
  display: flex;
  justify-content: center;
  align-items: center;
  .contact_wrapper {
    width: 670rpx;
    height: 380rpx;
    background: #fff;
    border-radius: 8rpx;
    box-sizing: border-box;
    display: flex;
    flex-direction: column;
    .contact-title {
      margin-top: 40rpx;
      padding-left: 20rpx;
      padding-right: 20rpx;
      font-size: 32rpx;
      display: flex;
      align-items: center;
      height: 52rpx;
      // justify-content: space-between;
    }
    .body_desc {
      color: #222;
      display: flex;
      font-weight: 600;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      padding: 30rpx;
      .main_desc {
        font-size: 44rpx;
        color: #3a6eff;
        height: 76rpx;
      }
      .addition {
        height: 40rpx;
        font-size: 30rpx;
        margin-top: 20rpx;
      }
    }
    .button {
      margin-top: 12rpx;
      width: 100%;
      background: #3a6eff;
      color: #fff;
      border-radius: 0;
    }
  }
}

.ec-canvas {
  width: 100%;
  height: 100%;
}
</style>

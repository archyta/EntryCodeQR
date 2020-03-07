<template>
  <div class="main">
    <div class="container">
      <mp-toptips :delay="5000" :msg="error" :show="errorShow" @hide="onErrorHidden" type="error"></mp-toptips>
      <panel class="panel banner" v-if="isShowInfo">
        <div class="banner-head">
          <span class="sub-title">
            {{rangeText}}
            <span style="margin-left:10rpx;margin-right:10rpx;" v-if="showAddress">{{ showAddress }}</span>
          </span>
          <span class="limit">{{limitText}}</span>
        </div>
        <div class="out-in-count">
          <div class="left-out-in-count">
            <span>进门</span>
            <span :class="{'text-warn':isForbiddenIn}" class="text-in-num">{{ inNum }}</span>
            <span>次</span>
          </div>
          <div class="right-out-in-count">
            <span>出门</span>
            <span :class="{'text-warn':isForbiddenOut}" class="text-out-num">{{ outNum }}</span>
            <span>次</span>
          </div>
        </div>
      </panel>

      <image class="banner-pic" src="/static/image/banner.png" v-else />

      <!--toast /-->

      <panel class="panel main-user" v-if="isShowInfo">
        <mp-slideview :buttons="mainUserSiliderButtons" :show="mainUserShow" @buttontap="slideButtonTap" @hide="mainUserMenuHide">
          <div class="main-user-container">
            <div class="main-user-left">
              <div class="main-user-left-top">
                <div class="left">
                  <div class="username-container">
                    <span class="username">
                      {{ mainUser.name }}
                      <span class="info" v-if="store_userId === famlilyInfo.mainUserId">户主</span>
                    </span>
                  </div>
                </div>
                <div class="right">
                  <button @tap.stop="registTemperature(mainUser)" class="btn-regist-temperature">登记体温</button>
                </div>
              </div>
              <div class="main-user-left-bottom">
                <span class="address">{{ showAddressFull }}</span>
              </div>
            </div>
            <div class="main-user-right">
              <button @tap.stop="showUserCode(mainUser)" class="btn" type="primary">
                <span class="text">
                  <span>出</span>
                  <span>示</span>
                  <!-- <span></span> -->
                  <!-- <span>码</span> -->
                </span>
                <image class="img" src="/static/icon/forbidden.png" v-if="isForbidden" />
                <span class="text">
                  <spn>通</spn>
                  <spn>行</spn>
                  <spn>证</spn>
                </span>
              </button>
            </div>
          </div>
        </mp-slideview>
      </panel>
      <button @tap.stop="addHomeUser" class="panel add-home-user" type="primary">
        <mp-icon :size="16" color="#fff" icon="add"></mp-icon>
        <span class="text">添加家庭成员</span>
      </button>
      <panel class="panel sub-user" v-if="!isShowInfo || !familyMembers || familyMembers.length === 0">
        <div class="sub-user-container">
          <span class="sub-user-no-tip">暂无家庭成员</span>
        </div>
      </panel>
      <panel v-else>
        <panel :key="user.id" class="panel sub-user" v-for="(user, index) in familyMembers">
          <mp-slideview :buttons="allSubSliderButtons[index]" :key="user.id" @buttontap="subUserButtonClick" @hide="mainUserMenuHide">
            <div class="sub-user-container">
              <div class="sub-user-name">
                <span class="username">
                  {{ user.name }}
                  <span class="info" v-if="user.id ===famlilyInfo.mainUserId">户主</span>
                </span>
              </div>

              <button @tap.stop="registTemperature(user)" class="btn-regist-temperature">登记体温</button>
              <button @tap.stop="showUserCode(user)" class="show-info-btn" type="primary">
                <span>出示通行证</span>
                <image class="img" src="/static/icon/forbidden.png" v-if="isForbidden" />
              </button>
            </div>
          </mp-slideview>
        </panel>
      </panel>
      <button @tap.stop="contactProperty" class="panel contact-property" v-if="propertyContacts.length>0">
        <span class="text">联系小区物业</span>
      </button>
      <panel class="panel out-in-record">
        <span class="title">最近三天体温记录</span>
        <div class="record-content">
          <span class="record-null-content" v-if="!isShowInfo || !temperatureRecord || temperatureRecord.length === 0">暂无记录</span>
          <div v-else>
            <div :key="index" class="record-content-item" v-for="(item, index) in temperatureRecord">
              <span :style="{'reject':item.status === '2'}" class="date">{{ item.crtTime }}</span>
              <span :style="{'reject':item.status === '2'}" class="username">{{ item.userName }}</span>
              <span class="status out" v-if="item.status === '0' && item.temperature">{{item.temperature}}℃</span>
              <span class="status out" v-else-if="item.status === '1' && item.temperature">{{item.temperature}}℃</span>
              <span class="status reject" v-else-if="item.status === '2' && item.temperature">{{item.temperature}}℃</span>
              <span class="status reject" v-else>未知</span>
            </div>
          </div>
        </div>
      </panel>
      <panel class="panel out-in-record">
        <span class="title">最近三天出入记录</span>
        <div class="record-content">
          <span class="record-null-content" v-if="!isShowInfo || !records || records.length === 0">暂无记录</span>
          <div v-else>
            <div :key="index" class="record-content-item" v-for="(item, index) in records">
              <span class="date">{{ item.crtTime }}</span>
              <span class="username">{{ item.userName }}</span>
              <span class="status in" v-if="item.status === '0'">进门</span>
              <span class="status out" v-else-if="item.status === '1'">出门</span>
              <span class="status reject" v-else-if="item.status === '2'">进门/劝返</span>
              <span class="status reject" v-else-if="item.status === '3'">出门/劝返</span>
              <span class="status reject" v-else>未知</span>
            </div>
          </div>
        </div>
      </panel>
      <mp-dialog :show="isShowContact" @close="onCloseContact" class="contact-dialog">
        <div class="title" slot="title">
          <span>在您身边，为您服务</span>
        </div>
        <div class="content">
          <div :key="index" @tap.stop="makeCall(item.mobilePhone)" class="content-item" v-for="(item,index) in propertyContacts">
            <span>{{item.name}}</span>
            <span>{{item.mobilePhone}}</span>
          </div>
        </div>
      </mp-dialog>
      <mp-dialog :buttons="[{text: '取消'}, {text: '确定'}]" :show="showDangerTip" @buttontap="clickDangerButton" @close="onCloseContact" class="danger-dialog">
        <div class="title" slot="title">
          <span>删除警告</span>
        </div>
        <div class="content">确定删除该成员？</div>
      </mp-dialog>
    </div>
  </div>
</template>

<script>
import wepy from '@wepy/core'
import eventHub from '../common/eventHub'
import store from '../store'
import getters from '@/store/getters'
import { getEstateInfo } from '@/api/login'
import { getBaseInfo, getOutInRecord, getTemperatureRecord, getGuardsContact, deleteUser, checkIsDeleteUser } from '@/api/owner'
import { compareVersion } from '@/utils/common.js'

wepy.page({
  store,
  onShareAppMessage: function(res) {
    let agentId = wepy.wx.getStorageSync('agentId')
    if (agentId) {
      return {
        title: '出入福安 我为抗疫出份力！',
        path: `/pages/main?agentId=${agentId}`
      }
    } else {
      return {
        title: '出入福安 我为抗疫出份力！',
        path: '/pages/main'
      }
    }
  },
  hooks: {},
  data: {
    outNum: 0,
    inNum: 0,
    error: '',
    errorShow: false,
    accessType: '本人',
    accessDays: 2,
    accessCount: 3,
    guardControlPattern: '0',
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
    familyMembers: [],
    temperatureRecord: [],
    records: [
      // {
      //   id: '1',
      //   crtTime: '2020/12/12 19:30',
      //   userName: '张三',
      //   status: '0'
      // }
    ],
    optionData: {
      estateId: '',
      estateName: ''
    },
    propertyContacts: [
      // {
      //   name: '张三',
      //   phone: '18682732229'
      // },
      // {
      //   name: '李四',
      //   phone: '18682732229'
      // },
      // {
      //   name: '王五',
      //   phone: '18682732229'
      // }
    ],
    isShowContact: false,
    showDangerTip: false,
    mainUserSiliderButtons: [
      {
        type: 'warn',
        text: '编辑',
        extClass: 'silider-edit-class'
      }
    ],
    subOtherUserSliderButtons: [
      {
        type: 'warn',
        text: '编辑',
        extClass: 'silider-edit-class'
      },
      {
        text: '删除',
        extClass: 'silider-delete-class'
      }
    ],
    subMainUserSliderButtons: [
      {
        type: 'warn',
        text: '编辑',
        extClass: 'silider-edit-class'
      },
      {
        text: '删除',
        extClass: 'silider-delete-class'
      }
    ],
    allSubSliderButtons: [],
    mainUserShow: false,
    activeDealUser: {}
  },
  computed: {
    ...getters(),
    isShowInfo: function() {
      return (
        // 有登录凭证 且 扫码进入的小区ID与本地缓存一致
        this.store_hasLogin && this.optionData.estateId === this.store_housingEstateId
      )
    },
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
    },
    showAddressFull: function() {
      let str = ''
      if (this.famlilyInfo) {
        if (this.famlilyInfo.estateName) {
          str += this.famlilyInfo.estateName
        }
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
    isForbiddenOut: function() {
      return this.outNum >= this.accessCount && this.accessCount !== -1
    },
    isForbiddenIn: function() {
      return this.inNum >= this.accessCount && this.accessCount !== -1
    },

    isForbidden: function() {
      return this.isForbiddenOut && this.isForbiddenIn
    }
  },
  watch: {
    isShowInfo: {
      handler: function(nl, ol) {
        if (nl) {
          wepy.wx.setNavigationBarTitle('小区出入防控')
        } else {
          wepy.wx.setNavigationBarTitle('小区出入登记')
        }
      },
      immediate: true
    }
  },
  onLoad(options) {
    // 注册事件总线 用于登录时检查用户是否登录的情况下-自动加载界面
    eventHub.$on('login-success', (...args) => {
      // 重新登录的情况
      if (options && options.estateId) {
        this.optionData.estateId = options.estateId
        // 若当前已存在小区了 且重新通过二维码扫码进来的
        if (this.optionData.estateId != this.store_housingEstateId) {
          let existEstate = this.store_allEstates.filter(el => {
            return el.id === this.optionData.estateId
          })
          if (existEstate.length > 0) {
            // 若直接存在小区 则加载小区Id
            this.$store.dispatch('switchEnstate', this.optionData.estateId)
          }
        }
      }
      this.loadPageData()
    })
    // 如果是扫二维码 带参数进入的情况
    /**
     * 情况一  已是本小区人员 --直接显示本小区界面  this.optionData.estateId = 本地状态管理的id
     * 情况二  非本小区人员 当前登录状态不一致  this.optionData.estateId != 本地状态管理的id
     */
    if (options && options.estateId) {
      this.optionData.estateId = options.estateId
      // 若当前已存在小区了 且重新通过二维码扫码进来的
      if (this.optionData.estateId != this.store_housingEstateId) {
        let existEstate = this.store_allEstates.filter(el => {
          return el.id === this.optionData.estateId
        })
        if (existEstate.length > 0) {
          // 若直接存在小区 则加载小区Id
          this.$store.dispatch('switchEnstate', this.optionData.estateId)
          if (this.store_userId && this.store_role && Array.isArray(this.store_role)) {
            // 若存在管理权限更高的角色 则切换到更高的角色界面去
            if (this.store_role.indexOf('property') > -1) {
              wepy.wx.redirectTo('/pages/manageIndex')
              return
            } else if (this.store_role.indexOf('guard') > -1) {
              wepy.wx.redirectTo('/pages/securityIndex')
              return
            }
          }
        } else {
          // 不存在小区  已有其他小区身份的情况下  扫新小区的用户码加入的情况
          getEstateInfo(options.estateId)
            .then(res => {
              if (res.data && res.data.status === 200) {
                this.optionData.estateName = res.data.data.name
              }
            })
            .catch(err => {
              this.error = '网络繁忙，请稍后再试'
              this.errorShow = true
            })
        }
      } else {
        if (this.store_userId && this.store_role && Array.isArray(this.store_role)) {
          // 若存在管理权限更高的角色 则切换到更高的角色界面去
          if (this.store_role.indexOf('property') > -1) {
            wepy.wx.redirectTo('/pages/manageIndex')
            return
          } else if (this.store_role.indexOf('guard') > -1) {
            wepy.wx.redirectTo('/pages/securityIndex')
            return
          }
        }
        this.optionData.estateId = this.store_housingEstateId
        this.optionData.estateName = this.store_housingName
      }
    } else {
      this.optionData.estateId = this.store_housingEstateId
      this.optionData.estateName = this.store_housingName
    }
    if (this.isShowInfo) {
      this.mainUserShow = true
      setTimeout(() => {
        this.mainUserShow = false
      }, 1500)
    }
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
      if (this.isShowInfo && this.store_role.indexOf('owner') > -1 && this.store_familyId) {
        checkIsDeleteUser(this.store_housingEstateId, this.store_familyId, this.store_userId)
          .then(() => {
            console.log('userstatus ok')
          })
          .catch(() => {
            console.log('user deleted')
          })
        // 加载用户记录
        getBaseInfo(this.store_userId, this.store_familyId)
          .then(res => {
            if (res.data.status === 200) {
              this.famlilyInfo = res.data.data
              // 将门牌，单元 房号信息顺便存储起来
              this.$store.dispatch('setFamilyInfo', {
                building: this.famlilyInfo.building,
                unit: this.famlilyInfo.unit,
                roomNumber: this.famlilyInfo.roomNumber
              })
              //装载数据
              if (this.famlilyInfo.familyMembers && this.famlilyInfo.familyMembers.length > 0) {
                let currentuser = this.famlilyInfo.familyMembers.filter(el => el.id === this.store_userId)
                if (currentuser.length > 0) {
                  this.mainUser = currentuser[0]
                } else {
                  // 清除缓存数据 并从新加载
                  // 后端数据异常了 此时需要重新加载小程序
                  try {
                    wepy.wx.clearStorageSync()
                    wepy.wx.reLaunch('/pages/main')
                  } catch {}
                }
                this.familyMembers = this.famlilyInfo.familyMembers.filter(el => el.id !== this.store_userId)
                this.allSubSliderButtons = []
                this.familyMembers.forEach(el => {
                  if (this.famlilyInfo.mainUserId === el.id) {
                    this.allSubSliderButtons.push([
                      {
                        type: 'warn',
                        text: '编辑',
                        extClass: 'silider-edit-forbidden-class',
                        data: {
                          user: el,
                          type: 'edit'
                        }
                      },
                      {
                        text: '删除',
                        extClass: 'silider-delete-forbidden-class',
                        data: {
                          user: el,
                          type: 'delete'
                        }
                      }
                    ])
                  } else {
                    this.allSubSliderButtons.push([
                      {
                        type: 'warn',
                        text: '编辑',
                        extClass: 'silider-edit-class',
                        data: {
                          user: el,
                          type: 'edit'
                        }
                      },
                      {
                        text: '删除',
                        extClass: 'silider-delete-class',
                        data: {
                          user: el,
                          type: 'delete'
                        }
                      }
                    ])
                  }
                })
              } else {
                this.allSubSliderButtons = []
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
        getOutInRecord(this.store_familyId, this.store_housingEstateId, this.store_userId)
          .then(res => {
            if (res.data.status === 200 && res.data.data) {
              this.inNum = res.data.data.inCount
              this.outNum = res.data.data.outCount
              this.records = res.data.data.tripRecords
              this.accessType = res.data.data.accessRule.accessType
              this.accessDays = Number(res.data.data.accessRule.accessDays)
              this.accessCount = Number(res.data.data.accessRule.accessCount)
              this.guardControlPattern = res.data.data.guardControlPattern
            } else {
              this.error = '网络繁忙，请稍后再试'
              this.errorShow = true
            }
          })
          .catch(err => {
            this.error = '网络繁忙，请稍后再试'
            this.errorShow = true
          })
        getTemperatureRecord(this.store_familyId, this.store_housingEstateId, 1, 10000, 3)
          .then(res => {
            if (res.data.status === 200 && res.data.data) {
              this.temperatureRecord = res.data.data.rows
            } else {
              this.temperatureRecord = []
              this.error = '网络繁忙，请稍后再试'
              this.errorShow = true
            }
          })
          .catch(err => {
            this.error = '网络繁忙，请稍后再试'
            this.errorShow = true
          })
        this.getContacts()
      }
    },
    addHomeUser() {
      if (!this.isShowInfo) {
        if (this.optionData.estateId) {
          this.$navigate({
            url: `/pages/baseinfo?isRegist=true&estateId=${this.optionData.estateId}&estateName=${this.optionData.estateName}`
          })
        } else {
          this.error = '网络繁忙，请稍后再试'
          this.errorShow = true
        }
      } else {
        this.$navigate({ url: '/pages/baseinfo?isRegist=false' })
      }
    },
    contactProperty() {
      this.isShowContact = true
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
    showUserCode(user) {
      if (this.guardControlPattern === '0') {
        let m = {
          familyId: this.store_familyId,
          userId: user.id,
          name: user.name,
          isFobidden: this.isForbidden,
          // openId: this.store_openId,
          expiredDate: new Date().getTime() + 300 * 1000
        }
        this.$navigate({
          url: '/pages/qrcode' + this.serialize(m)
        })
      } else if (this.guardControlPattern === '1') {
        if (!this.isForbidden) {
          let m = {
            familyId: this.store_familyId,
            userId: user.id,
            name: user.name,
            // openId: this.store_openId,
            expiredDate: new Date().getTime() + 300 * 1000,
            outCount: this.outNum,
            inCount: this.inNum,
            rangeText: this.rangeText,
            limitCount: this.accessCount // 若该值为-1 则不限制进出
          }
          this.$navigate({
            url: '/pages/simpleCode' + this.serialize(m)
          })
        } else {
          wx.showToast({
            title: '出入次数已用完',
            icon: 'none',
            duration: 1000
          })
          setTimeout(() => {
            wx.hideToast()
          }, 2500)
        }
      }
    },
    onErrorHidden() {
      this.error = ''
      this.errorShow = false
    },
    onCloseContact() {
      this.isShowContact = false
      this.showDangerTip = false
    },
    clickDangerButton(e) {
      let index = e.$wx.detail.index
      if (index) {
        this.deleteUser(this.activeDealUser)
      }
      this.showDangerTip = false
    },
    registTemperature(user) {
      this.$navigate(`/pages/submitTempature?userName=${user.name}&userId=${user.id}`)
    },
    getContacts() {
      getGuardsContact(this.store_housingEstateId).then(res => {
        if (res.data.status === 200 && res.data.data) {
          this.propertyContacts = res.data.data
        } else {
          this.error = '网络繁忙，请稍后再试'
          this.errorShow = true
        }
      })
    },
    makeCall(num) {
      wx.makePhoneCall({
        phoneNumber: num,
        complete() {
          // self.showContact = false
        }
      })
    },
    // 自己的滑块编辑
    slideButtonTap(e) {
      // todo jump to edit page
      let user = this.mainUser
      let familyId = this.famlilyInfo.id
      this.$navigate(`/pages/editBaseinfo?userId=${user.id}`)
    },
    mainUserMenuHide(e) {
      this.mainUserShow = false
    },
    // 家庭成员滑块
    subUserButtonClick(e) {
      let data = e.$wx.detail.data
      if (data.user.id != this.famlilyInfo.mainUserId) {
        if (data.type === 'edit') {
          // todo jump to edit page
          let user = data.user
          let familyId = this.famlilyInfo.id
          this.$navigate(`/pages/editBaseinfo?userId=${user.id}`)
        } else {
          // todo delete
          let user = data.user
          let familyId = this.famlilyInfo.id
          this.showDangerTip = true
          this.activeDealUser = user
        }
      }
    },
    deleteUser(user) {
      wx.showLoading({
        title: '删除中...',
        mask: true
      })
      let familyId = this.famlilyInfo.id
      deleteUser(this.store_housingEstateId, familyId, user.id)
        .then(res => {
          wx.hideLoading()
          if (res.data.status === 200) {
            wx.showToast({
              title: '删除成功',
              icon: 'success',
              duration: 2000
            })
            this.loadPageData()
          } else {
            wx.showToast({
              title: '删除失败',
              icon: 'error',
              duration: 2000
            })
          }
          setTimeout(() => {
            wx.hideToast()
          }, 1500)
        })
        .catch(() => {
          wx.hideLoading()
          wx.showToast({
            title: '删除失败',
            icon: 'error',
            duration: 2000
          })
          setTimeout(() => {
            wx.hideToast()
          }, 1500)
        })
    }
  },
  created() {}
})
</script>
<config>
  {
    navigationBarTitleText: '小区出入防控',
    usingComponents: {
      'mp-icon':'module:weui-miniprogram/miniprogram_dist/icon/icon',
      'mp-toptips':'module:weui-miniprogram/miniprogram_dist/toptips/toptips',
      'mp-dialog':'module:weui-miniprogram/miniprogram_dist/dialog/dialog',
      'mp-slideview':'module:weui-miniprogram/miniprogram_dist/slideview/slideview'
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
      .silider-edit-class {
        .weui-slideview__btn {
          background-color: #a5acaf;
          font-size: 32rpx;
          // border-top-right-radius: 8rpx;
          // border-bottom-right-radius: 8rpx;
        }
      }
      .silider-delete-class {
        .weui-slideview__btn {
          background-color: #fe3a3a;
          font-size: 32rpx;
          // border-top-right-radius: 8rpx;
          // border-bottom-right-radius: 8rpx;
        }
      }
      .silider-edit-forbidden-class,
      .silider-delete-forbidden-class {
        .weui-slideview__btn {
          background-color: #a5acaf;
          color: #d0d4dd;
          font-size: 32rpx;
          // border-top-right-radius: 8rpx;
          // border-bottom-right-radius: 8rpx;
        }
      }
    }

    .banner {
      height: 220rpx;
      display: flex;
      flex-direction: column;
      .banner-head {
        height: 66rpx;
        width: 100%;
        border-bottom: 1rpx solid #d8d8d8;
        display: flex;
        align-items: center;
        justify-content: space-between;
        .sub-title {
          color: #222222;
          font-size: 26rpx;
          line-height: 26rpx;
          margin-left: 30rpx;
        }
        .limit {
          color: #222222;
          font-size: 26rpx;
          font-weight: bold;
          margin-right: 30rpx;
        }
      }
      .out-in-count {
        flex-grow: 1;
        width: 100%;
        display: flex;
        flex-direction: row;
        .left-out-in-count {
          width: 50%;
          font-size: 40rpx;
          color: #222222;
          border-right: 1rpx solid #d8d8d8;
          // border:1rpx solid red;
          display: flex;
          align-items: center;
          justify-content: center;
          .text-in-num {
            color: #2dc168;
            font-size: 76rpx;
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
          .text-out-num {
            color: #3a6eff;
            font-size: 76rpx;
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
    .banner-pic {
      margin-top: 20rpx;
      height: 220rpx;
      width: 734rpx;
      margin-left: 10rpx;
    }
    .main-user {
      margin-top: 20rpx;
      height: 220rpx;
      display: flex;
      overflow: hidden;
      .weui-slideview__left {
        height: 100%;
      }
      .main-user-container {
        display: flex;
        height: 100%;
        .main-user-left {
          flex-grow: 1;
          height: 100%;
          padding-left: 30rpx;
          // padding-right: 30rpx;
          .main-user-left-top {
            border-bottom: 1rpx solid #d8d8d8;
            font-size: 40rpx;
            color: #222222;
            display: flex;
            align-items: center;
            justify-content: space-between;
            height: 50%;
            .left {
              flex-grow: 1;

              .username-container {
                // display: inline-flex;
                // align-items: center;
                // flex-direction: column;
                display: flex;
                align-items: center;
                .username {
                  font-weight: bold;
                  position: relative;
                  .info {
                    margin-left: 10rpx;
                    font-size: 20rpx;
                    color: #fff;
                    padding-left: 16rpx;
                    padding-right: 16rpx;
                    border-radius: 20rpx;
                    line-height: 30rpx;
                    background-color: #ff3e3e;
                    position: absolute;
                    top: 20rpx;
                    right: -80rpx;
                    z-index: 10;
                  }
                }
              }
            }
          }
          .main-user-left-bottom {
            height: 50%;
            display: flex;
            align-items: center;
            font-size: 28rpx;
            .address {
            }
          }
        }
        .main-user-right {
          width: 154rpx;
          display: border-box;
          padding: 20rpx;
          .btn {
            width: 100%;
            height: 100%;
            background-color: #02ba61;

            .text {
              font-size: 36rpx;
              line-height: 36rpx;
              width: 110rpx;
              display: flex;
              justify-content: space-between;
              justify-items: center;
            }
            .text + .text {
              margin-top: 10rpx;
            }
            .img {
              width: 44rpx;
              height: 44rpx;
              position: absolute;
            }
          }
          .btn {
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
          }
        }
      }
    }
    .add-home-user {
      margin-top: 30rpx;
      height: 88rpx;
      background-color: #3a6eff;
      font-size: 32rpx;
      display: flex;
      justify-content: center;
      justify-items: center;
      box-shadow: 0rpx 4rpx 6rpx 0rpx rgba(57, 57, 57, 0.05);
      .text {
        margin-left: 10rpx;
        margin-top: 4rpx;
      }
    }
    .sub-user {
      margin-top: 20rpx;
      height: 120rpx;
      display: flex;
      overflow: hidden;
      .weui-slideview__left {
        height: 100%;
      }
      .sub-user-container {
        height: 100%;
        padding-left: 30rpx;
        padding-right: 20rpx;
        display: flex;
        flex-direction: row;
        justify-content: space-between;
        align-items: center;
        align-content: center;
        .sub-user-name {
          flex-grow: 1;
          .username {
            color: #222222;
            font-size: 40rpx;

            font-weight: bold;
            position: relative;
            .info {
              margin-left: 10rpx;
              font-size: 20rpx;
              color: #fff;
              padding-left: 16rpx;
              padding-right: 16rpx;
              border-radius: 20rpx;
              line-height: 30rpx;
              background-color: #ff3e3e;
              position: absolute;
              top: 14rpx;
              right: -80rpx;
              z-index: 10;
            }
          }
        }

        .show-info-btn {
          background-color: #02ba61;
          // width: 200rpx;
          line-height: 74rpx;
          font-size: 32rpx;
          border-color: #a4a4a4;
          position: relative;
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          flex-shrink: 0;
          .img {
            width: 44rpx;
            height: 44rpx;
            position: absolute;
          }
        }
        .show-info-btn::after {
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
        }
        .sub-user-no-tip {
          font-size: 40rpx;
          line-height: 100%;
        }
      }
    }
    .contact-property {
      margin-top: 20rpx;
      font-size: 28rpx;
      color: #222222;
      background-color: #fff;
      line-height: 92rpx;
      display: flex;
      align-items: center;
      justify-content: center;
    }
    .contact-property::after {
      border: none;
    }
    .out-in-record {
      margin-top: 20rpx;
      min-height: 280rpx;
      // padding: 30rpx;
      padding-left: 30rpx;
      padding-right: 30rpx;
      padding-top: 30rpx;
      padding-bottom: 0;
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
    .btn-regist-temperature {
      font-size: 32rpx;
      background-color: #fff;
      border: 1rpx solid #eaeaea;
      color: #222222;
      margin-right: 20rpx;
      line-height: 72rpx;
      flex-shrink: 0;
    }
    .btn-regist-temperature::after {
      border: 1rpx solid #eaeaea;
    }
    .danger-dialog {
      .weui-dialog {
        width: 710rpx;
        border-radius: 12rpx;
      }
    }
    .contact-dialog {
      .weui-dialog {
        width: 710rpx;
        border-radius: 12rpx;
        .weui-dialog__hd {
          height: 104rpx;
          padding: 0;
          display: flex;
          justify-content: center;
          align-items: center;
          background-color: #3a6eff;
        }
      }

      .weui-dialog__ft {
        height: 0;
        line-height: 0;
        min-height: 0;
      }
      .weui-dialog__bd {
        line-height: 0;
        min-height: 0;
        margin-bottom: 0;
        padding-left: 0;
        padding-right: 0;
        .content {
          margin-left: 30rpx;
          margin-right: 30rpx;
          .content-item {
            height: 110rpx;
            display: flex;
            justify-content: space-between;
            align-items: center;
            font-size: 32rpx;
          }
          .content-item + .content-item {
            border-top: 1rpx solid #eaeaea;
          }
        }
      }
      .title {
        color: #fff;
        font-size: 36rpx;
        font-weight: normal;
      }
    }
  }
}
</style>

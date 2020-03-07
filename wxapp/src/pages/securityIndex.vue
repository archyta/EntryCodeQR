
<template>
  <div class="main">
    <div class="container">
      <mp-toptips :msg="error" :delay="5000" @hide="onErrorHidden" type="error" :show="errorShow"></mp-toptips>
      <div class="scroll_wrapper">
        <scroll-view scroll-y="true" @scrolltolower="loadMore" class="list">
          <panel class="panel main-user" v-if="hasLogin">
            <div class="main-user_item">
              <span class="community">{{estateName}}</span>
            </div>
            <div class="main-user_item username">{{username}}</div>
            <div class="main-user_item href_btn" @tap="goDetails">
              查看
              <image style="width: 30rpx;height: 30rpx;margin-left:2rpx;" src="{{'./../static/icon/arror-right.png'}}" />
            </div>
          </panel>
          <div class="btns-tips" v-else>
            <div class="tips">点击下侧按钮，须登记门卫信息，才可正常使用！</div>
            <button @tap.stop="goEditInfo" class="panel add-home-user" type="primary">
              <span class="text">门卫登记本人信息</span>
            </button>
          </div>

          <div class="qr-code-wrapper" :class="{'disabled':!hasLogin}">
            <div class="qr-btns">
              <div class="qr-btn qr-btn-1" @tap="goQrcode">出示二维码</div>
              <div class="qr-btn qr-btn-2" @tap="scanQrCode">扫二维码</div>
            </div>
            <div class="qr-desc">
              <div class="desc-con">出示二维码，登记住户信息</div>
              <div class="desc-con">扫二维码，确认住户身份</div>
            </div>
          </div>

          <panel class="panel out-in-record">
            <div class="record-header">
              <span class="title">居民出入记录</span>
              <span>
                今天进出
                <span class="hight-light">{{total == -1? 0:total}}</span>人次
              </span>
            </div>
            <div class="record-content">
              <span class="record-null-content" v-if="!hasLogin||records.length===0">
                <view class="loadMoreGif" v-if="isLoading">
                  <image class="loading-image" src="{{'./../static/icon/loading.gif'}}" />
                  <text>正在加载中</text>
                </view>
                <span v-else class="empty-text">暂无记录</span>
              </span>
              <div v-else>
                <div class="record-content-item" :key="index" v-for="(item,index) in records">
                  <span class="date">{{item.crtTime}}</span>
                  <span class="username">{{item.userName}}</span>
                  <span class="status out" v-if="item.status==='0'">进门</span>
                  <span class="status in" v-else-if="item.status==='1'">出门</span>
                  <span class="status reject" v-else-if="item.status==='2'">进门/劝返</span>
                  <span class="status reject" v-else-if="item.status==='3'">出门/劝返</span>
                </div>
              </div>
            </div>
          </panel>
          <view class="loadMoreGif" wx:if="{{hasLogin && isLoading&&records.length>0}}">
            <image class="loading-image" src="{{'./../static/icon/loading.gif'}}" />
            <text>正在加载中</text>
          </view>
        </scroll-view>
      </div>
    </div>
  </div>
</template>

<script>
import wepy from '@wepy/core'
import eventHub from '../common/eventHub'
import { mapState, mapActions, mapGetter } from '@wepy/x'
import getters from '@/store/getters'
import store from '../store'
import { decodeObj } from '@/utils/encode.js'
import { getAllRecored, getSecurityInfo } from '@/api/guards'
import { getEstateInfo } from '@/api/login'
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
    errorShow: false,
    error: '',
    total: -1,
    isLoading: false,
    currentPage: 1,
    limit: 10,
    inNum: 2,
    records: [],
    load: false,
    optionData: {
      estateId: null,
      estateName: null,
      streetOfficeName: null
    }
  },
  watch: {
    hasLogin: {
      handler: function(nl, ol) {
        if (!nl) {
          wepy.wx.setNavigationBarTitle('门卫防控')
        } else {
          if (this.store_housingName) {
            wepy.wx.setNavigationBarTitle(this.store_housingName)
          } else {
            wepy.wx.setNavigationBarTitle('门卫防控')
          }
        }
      },
      immediate: true
    }
  },
  computed: {
    ...getters(),
    username() {
      return this.store_userName
    },
    hasLogin() {
      if (this.optionData.estateId) {
        return this.store_hasLogin && this.store_role.indexOf('guard') > -1 && this.optionData.estateId === this.store_housingEstateId
      } else {
        return this.store_hasLogin && this.store_role.indexOf('guard') > -1
      }
    },
    estateName() {
      return this.store_housingName
    }
  },

  onShow() {
    // 基础库2.9.5才支持
    if (compareVersion('2.9.5') > -1) {
      wepy.wx.hideHomeButton()
    }
    this.getDetails()
  },
  onLoad(options) {
    // 注册事件总线 用于登录时检查用户是否登录的情况下-自动加载界面
    eventHub.$on('login-success', (...args) => {
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
      this.getDetails()
    })
    eventHub.$on('operate-success', (...args) => {
      this.records = []
      this.currentPage = 1
      this.total = -1
      this.getCountDetails()
    })
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
            }
          }
          this.optionData.estateName = this.store_housingName
          this.optionData.streetOfficeName = this.store_streetOfficeName
        } else {
          // 不存在小区  已有其他小区身份的情况下  扫新小区的用户码加入的情况
          getEstateInfo(options.estateId)
            .then(res => {
              if (res.data && res.data.status === 200) {
                this.optionData.estateName = res.data.data.name
                this.optionData.streetOfficeName = res.data.data.streetOfficeName
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
          }
        }
        this.optionData.estateId = this.store_housingEstateId
        this.optionData.estateName = this.store_housingName
        this.optionData.streetOfficeName = this.store_streetOfficeName
      }
    } else {
      // 特殊处理一下 检查权限变更 有可能会删除保安角色
      this.$store
        .dispatch('login', this.store_housingEstateId)
        .then(res => {
          if (!this.store_housingEstateId) {
            wepy.wx.redirectTo('/pages/main')
            return
          }
          if (this.store_role.indexOf('property') > -1) {
            wepy.wx.redirectTo('/pages/manageIndex')
            return
          }
          if (this.store_role.indexOf('guard') == -1 && this.store_role.indexOf('property') == -1 && this.store_role.indexOf('owner') > -1) {
            wepy.wx.redirectTo('/pages/index')
            return
          }
          this.optionData.estateId = this.store_housingEstateId
          this.optionData.estateName = this.store_housingName
          this.optionData.streetOfficeName = this.store_streetOfficeName
        })
        .then(err => {})
    }
  },
  methods: {
    goEditInfo() {
      this.$navigate(`/pages/securityInfoAdd?estateName=${this.optionData.estateName}&estateId=${this.optionData.estateId}&streetOfficeName=${this.optionData.streetOfficeName}`)
    },
    loadMore() {
      if (!this.hasLogin || this.isLoading || (this.records.length >= this.total && this.total !== -1)) {
        return
      }

      console.log('页数：', this.currentPage) // console打印页数
      this.getCountDetails() // 调用截取方法
    },
    getDetails() {
      if (this.hasLogin) {
        getSecurityInfo(this.store_userId, this.store_housingEstateId).then(res => {
          if (res && res.data && res.data.data && res.data.data.guardUser) {
            let name = res.data.data.guardUser.name
            // 登录接口 自动获取了  用户id 角色  小区  小区名字 街道
            // 此处需要更新的仅仅只有名字
            this.$store.dispatch('setUserName', name)
            if (!this.load) {
              this.load = true
              this.getCountDetails()
            }
          } else {
            if (res.data && res.data.message) {
              this.error = res.data.message
            } else {
              this.error = '网络繁忙，请稍后再试'
            }
          }
        })
      }
    },
    goDetails() {
      this.$navigate('/pages/securityInfo')
    },
    // 小区出入统计
    // 小区出入统计option
    getCountDetails() {
      //
      if (this.hasLogin) {
        this.isLoading = true
        getAllRecored(this.store_housingEstateId, this.currentPage, this.limit)
          .then(res => {
            const { total = 0, rows = [] } = res.data.data || {}
            this.total = total || 0
            if (rows && rows.length) {
              this.records = [...this.records, ...(rows || [])]
              this.currentPage++
            }
            this.isLoading = false
          })
          .catch(e => {
            this.isLoading = false
          })
      }
    },

    onErrorHidden() {
      this.error = ''
      this.errorShow = false
    },
    openError(msg) {
      this.error = msg
      this.errorShow = true
    },
    scanQrCode() {
      if (!this.hasLogin) {
        return
      }
      let self = this
      wx.scanCode({
        scanType: 'qrCode',
        success(res) {
          if ('scanCode:ok' === res.errMsg) {
            /* 
        familyId: this.store_familyId,
        userId: user.id,
        name: user.name,
        expiredDate: 000mm
        */
            try {
              const { familyId, userId, expiredDate } = decodeObj(JSON.parse(res.result))
              if (!expiredDate || !familyId || !userId) {
                self.openError('请扫描对应业主出示的二维码')
                return
              }
              if (new Date().getTime() > expiredDate) {
                self.openError('二维码已经超时，请提醒用户重新生成')
              } else {
                // if()
                self.$navigate(`/pages/securityOperate?familyId=${familyId}&userId=${userId}`)
              }
            } catch (error) {
              self.openError('请扫描对应业主出示的二维码')
            }
          }
        }
      })
    },
    // 出示小区二维码
    goQrcode() {
      if (!this.hasLogin) {
        return
      }
      this.$navigate('/pages/securitycode')
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
  height: 100%;
}
.container {
  height: 100%;
  display: flex;
  flex-direction: column;
  // margin-top: 20rpx;
  // margin-bottom: 20rpx;
  .btns-tips {
    margin-top: 10rpx;
    .tips {
      font-size: 24rpx;
      text-align: center;
      color: #3a6eff;
    }
  }
  .scroll_wrapper {
    display: flex;
    height: 100%;
  }
  .qr-code-wrapper {
    padding: 20rpx;
    .qr-btns {
      display: flex;
      justify-content: space-between;
      .qr-btn {
        width: 300rpx;
        height: 140rpx;
        line-height: 140rpx;
        font-size: 50rpx;
        border-radius: 15rpx;
        color: #fff;

        text-align: center;
        flex: 1;
        &.qr-btn-1 {
          background: #02c165;
          box-shadow: 0rpx 4rpx 7rpx 0rpx rgba(2, 199, 104, 0.3);
        }
        &.qr-btn-2 {
          background: #3a6eff;
          margin-left: 40rpx;
          box-shadow: 0rpx 4rpx 7rpx 0rpx rgba(58, 110, 255, 0.3);
        }
      }
    }
    &.disabled {
      .qr-btn-1 {
        background: #5dd89d !important;
      }
      .qr-btn-2 {
        background: #a2b8f6 !important;
      }
    }
    .qr-desc {
      margin-top: 14rpx;
      font-size: 24rpx;
      color: #868686;
      text-align: center;
      display: flex;
      justify-content: space-evenly;
      .desc-con {
        flex: 1;
      }
    }
  }
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
    height: 220rpx;
    display: flex;
    flex-direction: column;
    .banner-head {
      height: 66rpx;
      width: 100%;
      border-bottom: 1rpx solid #d8d8d8;
      .sub-title {
        color: #222222;
        font-size: 26rpx;
        line-height: 26rpx;
        margin-left: 30rpx;
        margin-top: 20rpx;
        margin-bottom: 20rpx;
      }
    }
  }
  .main-user {
    margin-top: 20rpx;
    padding: 0 30rpx;
    height: 100rpx;
    color: #222;
    display: flex;
    font-size: 36rpx;
    justify-content: space-between;
    align-items: center;
    .main-user_item {
      width: 33%;
    }
    .username {
      font-weight: 600;
      text-align: center;
    }
    .href_btn {
      color: #a4a4a4;
      text-align: right;
    }
    // .main-user-right
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
      width: 200rpx;
      height: 80rpx;
      font-size: 32rpx;
      border-color: #a4a4a4;
    }
    .sub-user-no-tip {
      font-size: 40rpx;
    }
  }
  .out-in-record {
    min-height: 300rpx;
    padding: 30rpx;
    display: flex;
    flex-direction: column;
    font-size: 28rpx;
    .record-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 30rpx;
      .title {
        display: block;
        font-size: 32rpx;
        color: #222222;
      }
      .hight-light {
        color: #3a6eff;
      }
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
      .empty-text {
        color: #aaa;
      }
    }
  }
  .loadMoreGif {
    text-align: center;
    display: flex;
    margin-left: 20rpx;
    margin-right: 20rpx;
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
</style>
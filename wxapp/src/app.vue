<style lang="less">
@import (css) './static/style/weui.wxss';
</style>

<script>
import wepy from '@wepy/core'
import eventHub from './common/eventHub'
import promisify from '@wepy/use-promisify'
import store from './store'
import { serverAddress } from '@/utils/request'
wepy.use(promisify)
wepy.app({
  hooks: {
    // App 级别 hook，对整个 App 生效
    // 同时存在 Page hook 和 App hook 时，优先执行 Page hook，返回值再交由 App hook 处
    'before-setData': function(dirty) {
      console.log('setData dirty: ', dirty)
      return dirty
    }
  },
  globalData: {
    userInfo: null
  },
  onLaunch() {
    this.checkIsUpdate()
    this.autoUpdate()
    // this.testAsync();
    store.dispatch('getLocalInfo')
    // 在本地数据获取到后 检查是否是登录状态，如果不是新设备状态，则先拉取一次数据
    if (!store.state.userId) {
      this.getLoginData()
    }
  },
  methods: {
    getLoginData() {
      store
        .dispatch('login')
        .then(res1 => {
          //登录后自动跳转界面
          let currentPages = getCurrentPages()
          if (store.state.userId && store.state.role && Array.isArray(store.state.role) && store.state.role.indexOf('property') > -1) {
            if (currentPages.length > 0) {
              if (currentPages[0].route !== 'pages/manageIndex') {
                wepy.wx.redirectTo('/pages/manageIndex')
              } else {
                eventHub.$emit('login-success')
              }
            }
          } else if (store.state.userId && store.state.role && Array.isArray(store.state.role) && store.state.role.indexOf('guard') > -1) {
            if (currentPages.length > 0) {
              if (currentPages[0].route !== 'pages/securityIndex') {
                wepy.wx.redirectTo('/pages/securityIndex')
              } else {
                eventHub.$emit('login-success')
              }
            }
          } else if (store.state.userId && store.state.role && Array.isArray(store.state.role) && store.state.role.indexOf('owner') > -1) {
            if (currentPages.length > 0) {
              if (currentPages[0].route !== 'pages/index') {
                wepy.wx.redirectTo('/pages/index')
              } else {
                eventHub.$emit('login-success')
              }
            }
          }
        })
        .catch(err1 => {
          wepy.wx
            .showModal({
              title: '提示',
              content: '服务器好像出了点问题，可以重试一下哟！',
              showCancel: true,
              confirmText: '重试'
            })
            .then(res => {
              if (res.confirm) {
                this.getLoginData()
              } else {
                wepy.wx.clearStorage()
              }
            })
            .catch(error => {})
        })
    },
    /**
     * 在加载初始检查
     * 检查清除版本缓存规则，若路由地址版本不一致，则清除缓存
     */
    checkIsUpdate() {
      let addressKey = 'serverAddress'
      let address = wepy.wx.getStorageSync(addressKey)
      if (address !== serverAddress) {
        wepy.wx.clearStorage()
        wepy.wx.setStorage(addressKey, serverAddress)
      }
    },
    autoUpdate: function() {
      let _this = this
      // 获取小程序更新机制的兼容，由于更新的功能基础库要1.9.90以上版本才支持，所以此处要做低版本的兼容处理
      if (wx.canIUse('getUpdateManager')) {
        // wx.getUpdateManager接口，可以获知是否有新版本的小程序、新版本是否下载好以及应用新版本的能力，会返回一个UpdateManager实例
        const updateManager = wx.getUpdateManager()
        // 检查小程序是否有新版本发布，onCheckForUpdate：当小程序向后台请求完新版本信息，会通知这个版本告知检查结果
        updateManager.onCheckForUpdate(function(res) {
          // 请求完新版本信息的回调
          if (res.hasUpdate) {
            // 检测到新版本，需要更新，给出提示
            wx.showModal({
              title: '更新提示',
              content: '检测到新版本，是否下载新版本并重启小程序',
              success: function(res) {
                if (res.confirm) {
                  // 用户确定更新小程序，小程序下载和更新静默进行
                  _this.downLoadAndUpdate(updateManager)
                } else if (res.cancel) {
                  // 若用户点击了取消按钮，二次弹窗，强制更新，如果用户选择取消后不需要进行任何操作，则以下内容可忽略
                  wx.showModal({
                    title: '提示',
                    content: '本次版本更新涉及到新功能的添加，旧版本将无法正常使用',
                    showCancel: false, // 隐藏取消按钮
                    confirmText: '确认更新', // 只保留更新按钮
                    success: function(res) {
                      if (res.confirm) {
                        // 下载新版本，重启应用
                        _this.downLoadAndUpdate(updateManager)
                      }
                    }
                  })
                }
              }
            })
          }
        })
      } else {
        // 在最新版本客户端上体验小程序
        wx.showModal({
          title: '提示',
          content: '当前微信版本过低，无法使用该功能，请升级到最新微信版本后重试'
        })
      }
    },
    // 下载小程序最新版本并重启
    downLoadAndUpdate: function(updateManager) {
      wx.showLoading()
      // 静默下载更新小程序新版本，onUpdateReady：当新版本下载完成回调
      updateManager.onUpdateReady(function() {
        wx.hideLoading()
        // applyUpdate：强制当前小程序应用上新版本并重启
        updateManager.applyUpdate()
      })
      // onUpdateFailed：当新版本下载失败回调
      updateManager.onUpdateFailed(function() {
        wx.hideLoading()
        // 下载新版本失败
        wx.showModal({
          title: '已有新版本',
          content: '新版本已经上线了，请删除当前小程序，重新搜索打开'
        })
      })
    }
  }
})
</script>

<config>
{
    pages: [
      'pages/main',   // 第一个 作为首页
      'pages/index',
      'pages/baseinfo',
      'pages/qrcode',
      'pages/securityIndex',
      'pages/securitycode',
      'pages/securityInfo',
      'pages/securityOperate',
      'pages/securityInfoAdd',
      'pages/manageAdd',
      'pages/manageStep3',
      'pages/estateDetails',
      'pages/manageStep2',
      'pages/examples',
      'pages/guardList',
      'pages/manageIndex',
      'pages/guardPoster',
      'pages/userPoster',
      'pages/temperatureList',
      'pages/submitTempature',
      'pages/inOutRecordList',
      'pages/editEstateInfo',
      'pages/unReportList',
      'pages/simpleCode',
      'pages/userDetail',
      'pages/editBaseinfo',
      'pages/rules',
    ],
    window: {
      backgroundTextStyle: 'light',
      navigationBarBackgroundColor: '#fff',
      navigationBarTitleText: '小区出入管理系统',
      navigationBarTextStyle: 'black',
      backgroundColor:'#f0f0f0'
    }
}
</config>

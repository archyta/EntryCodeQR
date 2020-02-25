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

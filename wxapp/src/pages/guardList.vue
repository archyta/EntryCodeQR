

<template>
  <div class="main">
    <div class="container">
      <panel class="panel">
        <div class="bg-header">{{store_housingName}}</div>
      </panel>
      <div :class="index === guardList.length - 1" :key="guard.id" class="panel row-guard" v-for="(guard,index) in guardList">
        <div :class="{manager: guard.manager}" class="flex_140">
          <span>{{guard.name}}</span>
        </div>
        <div>
          <span>{{guard.mobilePhone}}</span>
        </div>
        <!-- <div class="flex_140 status_desc">
          <span>{{['通过','不通过'][guard.status]}}</span>
        </div>-->
        <div @tap="showConfirm(guard)" style="width:60rpx;display: flex">
          <span class="icon del" style="margin-top: -20rpx;">
            <!-- <mp-icon :size="16" color="#f00" icon="close"></mp-icon> -->
            ...
          </span>
        </div>
      </div>
      <div class="no_guard" v-if="!guardList.length">暂无数据</div>
      <div @tap="showfirm = false" class="bg-modal" v-if="showfirm">
        <div @tap.stop class="message">
          <div class="msg-con">确定删除保安{{activeGuard.name}}？</div>
          <div class="btns">
            <div @tap="cancles" class="msg-btn cancel">取消</div>
            <div @tap="sureDel" class="msg-btn sure">确认</div>
          </div>
        </div>
      </div>
      <mp-actionSheet :actions="groups" :show="actionshow" @actiontap="btnClick" @close="actionshow = false" title="人员操作" v-model="clickBack" />
    </div>
  </div>
</template>
      
<script>
import wepy from '@wepy/core'
import getters from '@/store/getters'
import store from '../store'
import { getGuardList, deleteMember, setManage, cancleManage } from '@/api/manage'
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
    actionshow: false,
    showfirm: false,
    activeGuard: null,
    guardList: []
  },
  onLoad(option) {
    this.getGuardList(this.store_housingEstateId)
  },
  onShow() {
    // 基础库2.9.5才支持
    if (compareVersion('2.9.5') > -1) {
      wepy.wx.hideHomeButton()
    }
  },
  computed: {
    ...getters(),
    groups() {
      // if(this.activeGuard.ty)
      let [a, b, c] = [
        {
          type: '',
          text: '设为管理员',
          value: '0'
        },
        {
          type: '',
          text: '取消管理员',
          value: '1'
        },
        {
          type: 'warn',
          text: '删除人员',
          value: '2'
        }
      ]
      return this.activeGuard && this.activeGuard.manager ? [b, c] : [a, c]
    }
  },
  methods: {
    getStatusStr(status) {
      return ['通过', '不通过'][status]
    },
    showConfirm(guard) {
      this.activeGuard = guard
      this.actionshow = true
    },
    btnClick(e) {
      let actionType = e.$wx.detail.value
      let api = [setManage, cancleManage][actionType]
      if (api) {
        api(this.store_housingEstateId, this.activeGuard.id)
          .then(res => {
            this.actionshow = false
            if (res.data.status === 200) {
              if (actionType == '1') {
                this.redirectMethod()
              }
              this.refresh()
            } else {
              this.openError(res.data.message || '服务器异常，请稍后再试')
            }
          })
          .catch(err => {
            this.actionshow = false
            this.openError(err.data.message || '服务器异常，请稍后再试')
          })
      } else {
        this.showfirm = true
        this.actionshow = false
      }
    },
    cancles() {
      this.showfirm = false
    },
    refresh() {
      this.getGuardList(this.store_housingEstateId)
    },
    redirectMethod() {
      if (this.activeGuard.id === this.store_userId) {
        this.$store
          .dispatch('login', this.store_housingEstateId)
          .then(res => {
            if (!this.store_housingEstateId) {
              wepy.wx.reLaunch('/pages/main')
              return
            }
            if (this.store_role.indexOf('guard') > -1) {
              wepy.wx.reLaunch('/pages/securityIndex')
              return
            }
            if (this.store_role.indexOf('guard') == -1 && this.store_role.indexOf('property') == -1 && this.store_role.indexOf('owner') > -1) {
              wepy.wx.reLaunch('/pages/index')
              return
            }
          })
          .then(err => {})
      }
    },
    sureDel() {
      this.delGuard(this.activeGuard.id)
      this.cancles()
    },
    delGuard(id) {
      wx.showLoading({
        title: '加载中',
        mask: true
      })
      deleteMember(this.store_housingEstateId, id)
        .then(res => {
          if (res.data.status === 200) {
            // this.guardList = res.data.data
            this.redirectMethod()
            this.refresh()
            wx.hideLoading()
          } else {
            this.openError(res.data.message)
            wx.hideLoading()
          }
        })
        .catch(e => {
          this.openError('服务器不知道去哪了,正在紧急查找中...')
          wx.hideLoading()
        })
    },
    getGuardList(id) {
      getGuardList(id)
        .then(res => {
          if (res.data.status === 200) {
            this.guardList = res.data.data
          } else {
            this.openError(res.data.message)
          }
        })
        .catch(e => {
          this.openError('服务器不知道去哪了,正在紧急查找中...')
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
    navigationBarTitleText: '保安登记名单',
    usingComponents: {
      'mp-actionSheet':'module:weui-miniprogram/miniprogram_dist/actionSheet/actionSheet',
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
    .bg-header {
      background: #3a6eff;
      height: 112rpx;
      line-height: 112rpx;
      border-radius: 8rpx 8rpx 0 0;
      color: #fff;
      text-align: center;
      width: 100%;
    }
    &.row-guard {
      padding: 0 40rpx 0 30rpx;
      box-sizing: border-box;
      height: 110rpx;
      width: 100%;
      align-items: center;
      // line-height: 110rpx;
      justify-content: space-between;
      font-size: 32rpx;
      .manager {
        position: relative;
        &::after {
          text-align: center;
          content: '管理员';
          position: absolute;
          bottom: 100%;
          left: 0;
          // margin-left: -34rpx;
          width: 68rpx;
          height: 24rpx;
          background: #ff220d;
          border-radius: 12rpx;
          line-height: 24rpx;
          color: #fff;
          font-size: 14.5rpx;
        }
      }
      .flex_140 {
        flex: 0 0 140rpx;
      }
      .status_desc {
        text-align: center;
        border: 1rpx solid #eee;
        height: 60rpx;
        line-height: 60rpx;
      }
    }

    &:not(.last) {
      border-radius: 0;
      border-bottom: 1rpx solid #ccc;
    }
    &.danger {
      border-bottom: 1rpx solid #f00;
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

    .message {
      width: 650rpx;
      height: 260rpx;
      background: #fff;
      border-radius: 8rpx;
      box-sizing: border-box;
      display: flex;
      justify-content: space-between;
      flex-direction: column;
      .msg-con {
        padding: 30rpx;
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
}
</style>
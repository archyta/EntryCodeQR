<template>
  <cover-view class="full-screen">
    <cover-view class="rules-title">法律声明及隐私政策</cover-view>
    <cover-view class="rules-content">
      这里是内容很
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容这里是内容很
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容这里是内容很
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容这里是内容很
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容这里是内容很
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容这里是内容很
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容这里是内容很
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容这里是内容很
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容这里是内容很
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容这里是内容很
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容这里是内容很
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容这里是内容很
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容这里是内容很
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容这里是内容很
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容这里是内容很
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容这里是内容很
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容这里是内容很
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容这里是内容很
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容这里是内容很
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容这里是内容很
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容这里是内容很
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容这里是内容很
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容这里是内容很
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容这里是内容很
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容这里是内容很
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容这里是内容很
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
      多内容
    </cover-view>
    <button class="rules-btn" @tap="cancle">确定</button>
  </cover-view>
</template>

<script>
import wepy from '@wepy/core'

wepy.component({
  data: {
    show: false
  },
  methods: {
    cancle(){
      this.$emit('cancle')
    }
  }
})
</script>

<style lang="less" scoped>
.full-screen{
  width: 100vw;
  height: 100vh;
  position: fixed;
  box-sizing: border-box;
  padding-bottom: 120rpx;
  top: 0;
  left: 0;
  background: #fff;
  text-align: center;
  overflow: scroll;
  .rules-title{
    font-size: 28rpx;
    color: #222;
  }
  .rules-content{
    font-size: 24rpx;
    color: #666;
    white-space: pre-wrap;
    line-height: 28rpx;
    margin-top: 24rpx;
  }
  .rules-btn{
    position: absolute;
    bottom: 0;
    width: 100%;
    font-size: 32rpx;
    border-radius: 8rpx;
    color: #fff;
    background: #3a6eff;
    line-height: 100rpx;
  }
}
</style>
<template>
  <div class="container">
    <div class="func">
      <div class="balance" @click="onPurse">
        <div class="title">余额</div>
        <div class="text">￥{{balance}}</div>
      </div>
      <div class="points" @click="onPurse">
        <div class="title">积分</div>
        <div class="text">{{points}}</div>
      </div>
      <div class="collection" @click="onCollection">
        <div class="title">收藏</div>
        <div class="text">{{collection}}</div>
      </div>
    </div>
  </div>
</template>

<script>
import { queryAccount, queryCollection } from '@/api/user.js'
export default {
  name: 'Me-Func',
  data() {
    return {
      balance: 0.00,
      points: 0,
      collection: 0
    }
  },
  methods: {
    onPurse() {
      this.$router.push('/purse')
    },
    onCollection() {
      this.$router.push('/collection')
    }
  },
  created() {
    queryAccount().then(res => {
      const data = res.data
      if (data.success) {
        this.balance = data.data.balance
        this.points = data.data.points
      }
    })
    queryCollection().then(res => {
      const data = res.data
      if (data.success) {
        this.collection = data.data.length
      }
    })
  }
}
</script>

<style lang="less" scoped>
.container{
  height: 50px;
  margin: 0 10px;
  background-color: rgba(0, 0, 0, 0.5);
  border-radius: 10px;
  .func{
    padding: 5px 0;
    display: flex;
    .balance,.points,.collection{
      flex: 1;
      text-align: center;
      .title{
        color: rgba(255, 255, 255, 0.7);
      }
      .text{
        color: orange;
      }
    }
  }
}
</style>

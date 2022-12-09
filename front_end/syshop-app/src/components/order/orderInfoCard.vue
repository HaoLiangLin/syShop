<template>
  <div class="container">
    <div class="image">
      <van-image
        width="90px"
        height="90px"
        fit="cover"
        :src="setIcon(orderItem.image)"
      />
    </div>
    <div class="info">
      <div class="name">
        <div class="text">{{orderItem.goodsName}}</div>
        <div>
          <van-button size="small" type="default" v-if="orderItem.isComment === 0" @click="onEvaluation(orderItem.orderItemId)">去评价</van-button>
          <van-button size="small" type="default" v-if="orderItem.isComment === 1" disabled>已评价</van-button>
        </div>
      </div>
      <div class="type">
        <span v-if="orderItem.color">颜色:{{orderItem.color}}; </span>
        <span v-if="orderItem.size">大小:{{orderItem.size}}; </span>
        <span v-if="orderItem.combo">套餐:{{orderItem.combo}}; </span>
        <span v-if="orderItem.edition">版本:{{orderItem.edition}}; </span>
      </div>
      <div class="number">
        <div class="price"><span>￥</span>{{orderItem.price}}</div>
        <div class="quantity"><span>x</span>{{orderItem.quantity}}</div>
      </div>
    </div>
  </div>
</template>

<script>
import { getImage } from '@/utils/resources.js'
export default {
  name: 'OrderInfo-Card',
  props: ['orderItem'],
  methods: {
    setIcon(icon) {
      return getImage(icon)
    },
    onEvaluation(id) {
      this.$router.push(`/evaluation/${id}`)
    }
  }
}
</script>

<style lang="less" scoped>
.container{
  display: flex;
  .image{
    flex: 3;
  }
  .info{
    flex: 7;
    .name{
      display: flex;
      justify-content: space-between;
      .text{
        font-size: 14px;
        font-weight: bold;
        line-height: 30px;
        max-width: 190px;
        // 强制不换行
        white-space:nowrap;
        // 溢出隐藏
        overflow: hidden;
      }
    }
    .type{
      span{
        color: #999;
        font-size: 13px;
      }
    }
    .number{
      display: flex;
      justify-content: space-between;
      margin-top: 18px;
      .price{
        color: red;
        font-size: 18px;
      }
      .quantity{
        color: #999;
        font-size: 14px;
        padding-top: 5px;
      }
    }
  }
}
</style>

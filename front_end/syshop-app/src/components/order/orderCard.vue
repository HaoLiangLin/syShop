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
      <div class="name">{{orderItem.goodsName}}</div>
      <div class="type">
        <span v-if="orderItem.color">颜色:{{orderItem.color}}; </span>
        <span v-if="orderItem.size">大小:{{orderItem.size}}; </span>
        <span v-if="orderItem.combo">套餐:{{orderItem.combo}}; </span>
        <span v-if="orderItem.edition">版本:{{orderItem.edition}}; </span>
      </div>
    </div>
    <div class="number">
      <div class="price"><span>￥</span>{{orderItem.price}}</div>
      <div class="quantity"><span>x</span>{{orderItem.quantity}}</div>
    </div>
  </div>
</template>

<script>
import { getImage } from '@/utils/resources.js'
export default {
  name: 'Order-Card',
  props: ['orderItem'],
  methods: {
    setIcon(icon) {
      return getImage(icon)
    }
  }
}
</script>

<style lang="less" scoped>
.container{
  display: flex;
  margin-bottom: 5px;
  .image{
    flex: 3;
    /deep/.van-image__img{
      border-radius: 10px;
    }
  }
  .info{
    flex: 7;
    margin-left: 10px;
    .name{
      font-size: 14px;
      font-weight: bold;
      line-height: 30px;
      max-width: 190px;
      // 强制不换行
      white-space:nowrap;
      // 溢出隐藏
      overflow: hidden;
    }
    .type{
      span{
        color: #999;
        font-size: 13px;
      }
    }
  }
  .number{
    flex: 1;
    div{
      text-align: end;
    }
    .price{
      line-height: 30px;
      font-size: 13px;
      span{
        font-size: 12px;
      }
    }
    .quantity{
      font-size: 12px;
      color: #999;
    }
  }
}
</style>

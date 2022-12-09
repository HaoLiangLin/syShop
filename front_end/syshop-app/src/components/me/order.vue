<template>
  <div class="container">
    <van-cell-group>
      <van-cell title="我的订单" value="查看全部订单" center title-style="padding-left: 5px;" border>
        <template #icon>
          <van-icon name="orders-o" color="orange" size="16px" />
        </template>
      </van-cell>
    </van-cell-group>
    <van-grid column-num="4" :border="false">
      <van-grid-item icon="paid" text="待付款" :badge="unpaidOrderSize > 0 ? unpaidOrderSize : null" :to="{name: 'order', query: { id: 1 }}"/>
      <van-grid-item icon="logistics" text="待发货" :badge="beShippedOrderSize > 0 ? beShippedOrderSize : null" :to="{name: 'order', query: { id: 2 }}"/>
      <van-grid-item icon="gift-o" text="待收货" :badge="undeliveredOrderSize > 0 ? undeliveredOrderSize : null" :to="{name: 'order', query: { id: 3 }}"/>
      <van-grid-item icon="cart-o" text="已完成" :to="{name: 'order', query: { id: 4 }}"/>
    </van-grid>
  </div>
</template>

<script>
import { unpaidOrder, beShippedOrder, undeliveredOrder } from '@/api/orders.js'
export default {
  name: 'Me-Order',
  data() {
    return {
      jwt: localStorage.getItem('authorization'),
      unpaidOrderSize: null,
      beShippedOrderSize: null,
      undeliveredOrderSize: null
    }
  },
  created() {
    if (this.jwt) {
      const promises = [
        unpaidOrder(),
        beShippedOrder(),
        undeliveredOrder()
      ]

      Promise.all(promises).then(([r1, r2, r3]) => {
        const d1 = r1.data
        const d2 = r2.data
        const d3 = r3.data
        if (d1.success) {
          this.unpaidOrderSize = d1.data.length
        }
        if (d2.success) {
          this.beShippedOrderSize = d2.data.length
        }
        if (d3.success) {
          this.undeliveredOrderSize = d3.data.length
        }
      })
    }
  }
}
</script>

<style lang="less" scoped>
</style>

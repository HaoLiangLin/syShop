<template>
  <div class="containerx">
    <van-nav-bar
      title="我的订单"
      left-arrow
      :fixed="true"
      :placeholder="true"
      @click-left="goBack"
    />
    <van-tabs v-model="active" @click="onNavBarClick">
      <van-tab title="待付款">
        <div class="box">
          <div class="order" v-for="order in orders" :key="order.orderId" @click="onOrderCard(order.orderId)">
            <OrderCard v-for="item in order.orderItems" :key="item.gid" :orderItem="item"/>
            <div class="text">
              <span class="t1">含物流运输服务 </span>
              <span class="t2">需付款<a>￥</a>{{order.price}}</span>
            </div>
            <div class="menu">
              <div class="left">取消订单</div>
              <div class="function">
                <van-button plain round size="small" style="margin-right: 10px">修改地址</van-button>
                <van-button plain type="warning" round size="small">前往支付</van-button>
              </div>
            </div>
          </div>
          <van-empty v-if="!orders.length" description="您还没有相关的订单" />
        </div>
      </van-tab>
      <van-tab title="待发货">
        <div class="box">
          <div class="order" v-for="order in orders" :key="order.orderId" @click="onOrderCard(order.orderId)">
            <OrderCard v-for="item in order.orderItems" :key="item.gid" :orderItem="item"/>
            <div class="menu">
              <div class="left">更多</div>
              <div class="function">
                <van-button plain round size="small" style="margin-right: 10px">申请退款</van-button>
                <van-button plain type="warning" round size="small">催发货</van-button>
              </div>
            </div>
          </div>
          <van-empty v-if="!orders.length" description="您还没有相关的订单" />
        </div>
      </van-tab>
      <van-tab title="待收货">
        <div class="box">
          <div class="order" v-for="order in orders" :key="order.orderId" @click="onOrderCard(order.orderId)">
            <OrderCard v-for="item in order.orderItems" :key="item.gid" :orderItem="item"/>
            <div class="menu">
              <div class="left">更多</div>
              <div class="function">
                <van-button plain round size="small" style="margin-right: 10px">查看物流</van-button>
                <van-button plain type="warning" round size="small">确认收货</van-button>
              </div>
            </div>
          </div>
          <van-empty v-if="!orders.length" description="您还没有相关的订单" />
        </div>
      </van-tab>
      <van-tab title="已完成">
        <div class="box">
          <div class="order" v-for="order in orders" :key="order.orderId" @click="onOrderCard(order.orderId)">
            <OrderCard v-for="item in order.orderItems" :key="item.gid" :orderItem="item"/>
            <div class="menu">
              <div class="left">更多</div>
              <div class="function">
                <van-button plain type="warning" round size="small">查看订单</van-button>
              </div>
            </div>
          </div>
          <van-empty v-if="!orders.length" description="您还没有相关的订单" />
        </div>
      </van-tab>
    </van-tabs>
  </div>
</template>

<script>
import { unpaidOrder, beShippedOrder, undeliveredOrder, completeOrder } from '@/api/orders.js'
export default {
  name: 'Order',
  data() {
    return {
      active: 0,
      orders: []
    }
  },
  components: {
    OrderCard: () => import('@/components/order/orderCard.vue')
  },
  methods: {
    goBack() {
      this.$router.back()
    },
    // 未付款
    onUnpaidOrder() {
      this.orders = []
      unpaidOrder().then(res => {
        const data = res.data
        if (data.success) {
          data.data.forEach(order => {
            this.orders.push(order)
          })
        }
      })
    },
    // 未发货
    onBeShippedOrder() {
      this.orders = []
      beShippedOrder().then(res => {
        const data = res.data
        if (data.success) {
          data.data.forEach(order => {
            this.orders.push(order)
          })
        }
      })
    },
    // 待收货
    onUndeliveredOrder() {
      this.orders = []
      undeliveredOrder().then(res => {
        const data = res.data
        if (data.success) {
          data.data.forEach(order => {
            this.orders.push(order)
          })
        }
      })
    },
    // 已完成
    onCompleteOrder() {
      this.orders = []
      completeOrder().then(res => {
        const data = res.data
        if (data.success) {
          data.data.forEach(order => {
            this.orders.push(order)
          })
        }
      })
    },
    // 标签栏切换
    onChange(id) {
      this.orders = []
      switch (id) {
        case 0:
          this.active = 0
          this.onUnpaidOrder()
          break
        case 1:
          this.active = 1
          this.onBeShippedOrder()
          break
        case 2:
          this.active = 2
          this.onUndeliveredOrder()
          break
        case 3:
          this.active = 3
          this.onCompleteOrder()
          break
      }
    },
    // 标签栏点击事件
    onNavBarClick(name) {
      this.onChange(name)
    },
    // 点击卡片
    onOrderCard(id) {
      this.$router.push(`/orderInfo/${id}`)
    }
  },
  created() {
    const id = Number(this.$route.query.id)
    this.active = id - 1
    this.orders = []
    switch (id) {
      case 1:
        this.onUnpaidOrder()
        break
      case 2:
        this.onBeShippedOrder()
        break
      case 3:
        this.onUndeliveredOrder()
        break
      case 4:
        this.onCompleteOrder()
        break
    }
  }
}
</script>

<style lang="less" scoped>
.containerx{
  .box{
    padding: 0 5px;
    .order{
      padding: 10px;
      background-color: #fff;
      margin-top: 10px;
      .text{
        font-size: 13px;
        text-align: end;
        .t1{
          color: #999;
        }
        .t2{
          font-weight: bold;
          a{
            font-size: 12px;
          }
        }
      }
      .menu{
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding-top: 10px;
        .left{
          font-size: 12px;
          color: #999;
        }
        .function{
        }
      }
    }
  }
}
</style>

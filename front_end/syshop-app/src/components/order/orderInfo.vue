<template>
  <div class="container">
    <van-nav-bar
      title="订单详情"
      left-arrow
      :fixed="true"
      :placeholder="true"
      @click-left="goBack"
    />
    <div class="head" v-if="orderData">
      <div class="content">
        <div class="text">
          <div class="status">{{orderData.status === '已完成' ? orderData.status : orderData.isPay === '未付款' ? '等待付款' : orderData.isPay}}</div>
          <div>{{orderData.logisticsStatus}}</div>
        </div>
        <van-image
          v-if="orderData.isPay === '未付款'"
          width="75"
          height="75"
          :src="require('@/assets/等待付款.png')"
        />
        <van-image
          v-if="orderData.isPay === '已付款' && orderData.logisticsStatus === '未发货'"
          width="75"
          height="75"
          :src="require('@/assets/待发货.png')"
        />
        <van-image
          v-if="orderData.isPay === '已付款' && orderData.logisticsStatus === '待收货'"
          width="75"
          height="75"
          :src="require('@/assets/待收货.png')"
        />
        <van-image
          v-if="orderData.status === '已完成'"
          width="75"
          height="75"
          :src="require('@/assets/订单详情.png')"
        />
      </div>
    </div>
    <div class="infoBox" v-if="orderData">
      <div class="title">
        物流信息
        <van-button v-if="orderData.isPay === '未付款'" size="mini" plain type="warning" @click="onUpdateAddress">修改地址</van-button>
      </div>
      <div class="content">
        <table>
          <tr>
            <td>收货地址：</td>
            <td>{{orderData.address}}</td>
          </tr>
          <tr>
            <td>收货人：</td>
            <td>{{orderData.name}} {{orderData.phone}}</td>
          </tr>
          <tr v-if="orderData.shippingTime">
            <td>发货时间：</td>
            <td>{{orderData.shippingTime}}</td>
          </tr>
        </table>
      </div>
    </div>
    <div class="infoBox" v-if="orderData">
      <div class="title">
        商品信息
      </div>
      <div class="content">
        <Card v-for="item in orderData.orderItems" :key="item.gid" :orderItem="item"/>
      </div>
    </div>
    <div class="infoBox" v-if="orderData">
      <div class="title">
        订单信息
      </div>
      <div class="content">
        <table>
          <tr>
            <td>订单编号：</td>
            <td>{{orderData.orderId}}</td>
          </tr>
          <tr>
            <td>下单时间：</td>
            <td>{{orderData.time}}</td>
          </tr>
          <tr v-if="orderData.paymentMethods">
            <td>支付方式：</td>
            <td>{{orderData.paymentMethods}}</td>
          </tr>
          <tr>
            <td>物流运费：</td>
            <td>{{orderData.postage}}</td>
          </tr>
          <tr>
            <td>订单备注：</td>
            <td>
              <van-field
                rows="1"
                autosize
                type="textarea"
                v-model="orderData.remarks"
                center
                clearable
                v-if="orderData.isPay === '未付款'"
                placeholder="订单备注"
              >
                <template #button>
                  <van-button size="mini" plain type="warning" @click="onUpdateRemark">修改</van-button>
                </template>
              </van-field>
              <span v-else>{{orderData.remarks}}</span>
            </td>
          </tr>
        </table>
        <div class="price">
          <span>实付款：</span>
          ￥{{orderData.price}}
        </div>
        <div class="func">
          <van-button type="danger" size="small" v-if="orderData.isPay === '未付款'" @click="onCancelOrder">取消订单</van-button>
          <van-button type="default" size="small" v-if="orderData.isPay === '未付款'" @click="onPaymentOrder">去支付</van-button>
          <van-button type="default" size="small" v-if="orderData.isPay === '已付款' && orderData.logisticsStatus === '未发货'" @click="onCancelOrder">申请退款</van-button>
          <van-button type="default" size="small" v-if="orderData.isPay === '已付款' && orderData.logisticsStatus === '未发货'">催发货</van-button>
          <van-button type="default" size="small" v-if="orderData.isPay === '已付款' && orderData.logisticsStatus === '待收货'">查看物流</van-button>
          <van-button type="default" size="small" v-if="orderData.isPay === '已付款' && orderData.logisticsStatus === '待收货'" @click="onComplete">确认收货</van-button>
          <van-button type="danger" size="small" v-if="orderData.status === '已完成'" @click="onDelOrder">删除订单</van-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getOrder, updateOrder, cancelOrder, paymentOrder, complete, delOrder } from '@/api/orders.js'
export default {
  namr: 'Order-Info',
  data() {
    return {
      orderData: null
    }
  },
  components: {
    Card: () => import('@/components/order/orderInfoCard.vue')
  },
  methods: {
    goBack() {
      this.$router.back()
    },
    onUpdateAddress() {
      const id = this.orderData.orderId
      this.$router.push({
        name: 'address',
        query: {
          orderId: id
        }
      })
    },
    onUpdateRemark() {
      if (!this.orderData.remarks) {
        this.$toast.fail('备注不能为空')
      }
      updateOrder(this.orderData.orderId, null, this.orderData.remarks).then(res => {
        const data = res.data
        if (data.success) {
          this.$toast.success('修改成功')
        }
      }).catch(err => {
        this.$notify({ type: 'danger', message: err.message })
      })
    },
    onCancelOrder() {
      cancelOrder(this.orderData.orderId).then(res => {
        const data = res.data
        if (data.success) {
          this.$toast.success(data.message)
        } else {
          this.$toast.fail(data.message)
        }
      }).catch(err => {
        this.$notify({ type: 'danger', message: err.message })
      })
    },
    onPaymentOrder() {
      paymentOrder(this.orderData.orderId).then(res => {
        const data = res.data
        if (data.success) {
          this.$toast.success(data.message)
          this.$router.back()
        } else {
          this.$toast.fail(data.message)
        }
      }).catch(err => {
        this.$notify({ type: 'danger', message: err.message })
      })
    },
    onComplete() {
      complete(this.orderData.orderId).then(res => {
        const data = res.data
        if (data.success) {
          this.$toast.success(data.message)
        } else {
          this.$toast.fail(data.message)
        }
      }).catch(err => {
        this.$notify({ type: 'danger', message: err.message })
      })
    },
    onDelOrder() {
      this.$dialog.confirm({
        title: '提示',
        message: '是否确认删除订单'
      })
        .then(() => {
          delOrder(this.orderData.orderId).then(res => {
            const data = res.data
            if (data.success) {
              this.$toast.success(data.message)
            } else {
              this.$toast.fail(data.message)
            }
          }).catch(err => {
            this.$notify({ type: 'danger', message: err.message })
          })
        })
        .catch(() => {
          // on cancel
        })
    }
  },
  created() {
    const orderId = this.$route.params.id
    this.orderData = []
    getOrder(orderId).then(res => {
      const data = res.data
      if (data.success) {
        this.orderData = data.data
      }
    })
  },
  updated() {
    this.$bus.$on('getOderInfo', (value) => {
      this.orderData = value
    })
  }
}
</script>

<style lang="less" scoped>
.container{
  .head{
    height: 25vh;
    width: 100%;
    margin-bottom: -60px;

    background: #fdc830;  /* fallback for old browsers */
    background: -webkit-linear-gradient(to left, rgb(248,200,105), rgb(229,164,48));  /* Chrome 10-25, Safari 5.1-6 */
    background: linear-gradient(to left, rgb(248,200,105), rgb(229,164,48)); /* W3C, IE 10+/ Edge, Firefox 16+, Chrome 26+, Opera 12+, Safari 7+ */
    .content{
      padding: 20px 40px;
      display: flex;
      justify-content: space-between;
      align-items: center;
      .text{
        color: #fff;
        .status{
          font-size: 24px;
          padding-bottom: 10px;
        }
      }
    }
  }
  .infoBox{
    margin: 0 10px;
    background-color: #fff;
    border-radius: 5px;
    // position: relative;
    // top: -50px;
    margin-bottom: 10px;
    padding: 0 10px;

    .title{
      height: 20px;
      padding: 10px 5px;
      border-bottom: 1px solid #ccc;

      /deep/.van-button{
        float: right;
      }
    }
    .content{
      padding: 10px 5px;
      table{
        tr{
          padding: 7px 0;
          td{
            font-size: 14px;
            &:first-child{
              width: 70px;
              color: rgb(106, 106, 106);
            }
            /deep/.van-field{
              padding: 0;
            }
          }
        }
      }
      .price{
        text-align: right;
        color: red;
        font-size: 18px;
        span{
          color: rgb(106, 106, 106);
          font-size: 14px;
        }
      }
    }
    .func{
      margin-top: 10px;
      text-align: right;
      /deep/.van-button{
        margin-left: 10px;
        border-radius: 5px;
      }
    }
  }
}
</style>

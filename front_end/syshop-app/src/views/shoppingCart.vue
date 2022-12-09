<template>
  <div class="containerx">
    <van-nav-bar
      :title="`购物车(${shoppingCartLength})`"
      :right-text="manage ? '取消' : '管理'"
      @click-right="onManage"
    />
    <van-empty :image="require('@/assets/购物车空.png')" v-if="shoppingCartData.length < 1" description="空空如也~" />
    <div class="content" v-for="item in shoppingCartData" :key="item.id">
      <div class="checkBox">
        <van-checkbox v-model="item.isSelect" checked-color="orange" @change="onCheckBoxChange(item)"></van-checkbox>
      </div>
      <ShoppingCartCard :shoppingCartItem="item" @onUpdateCard="res => onUpdateCard(item, res)"></ShoppingCartCard>
      <div class="delete" v-if="manage">
        <van-icon name="delete" color="red" size="22px" @click="onDelete(item)"/>
      </div>
    </div>
    <div style="height: 50px"></div>
    <van-submit-bar v-if="!manage && shoppingCartData.length > 0" style="bottom: 50px" :price="finalPrice" :disabled="!isSumbit" button-text="结算" @submit="onSubmit">
      <van-checkbox v-model="allSelect" checked-color="orange" @change="onAllSelect">全选</van-checkbox>
    </van-submit-bar>
    <div class="deleteBar" v-if="manage">
      <div>
        <van-checkbox v-model="allSelect" checked-color="orange" @change="onAllSelect" icon-size="20">全选</van-checkbox>
      </div>
      <div class="box">
        <span class="clear" @click="onEmpty">清空购物车</span>
        <van-button type="danger" round @click="onSomeDelete">删除</van-button>
      </div>
    </div>
    <Tabbar/>
  </div>
</template>

<script>
import { queryShoppingCart, deleteShoppingCart, bulkDeleteShoppingCart, emptyShoppingCart, queryDefaultAddress, queryAddress } from '@/api/user.js'
import { submitOrder } from '@/api/orders.js'
export default {
  name: 'ShoppingCart',
  components: {
    ShoppingCartCard: () => import('@/components/shoppingCart/shoppingCartCard.vue'),
    Tabbar: () => import('@/components/tabbar.vue')
  },
  data() {
    return {
      shoppingCartData: [],
      manage: false,
      allSelect: false
    }
  },
  methods: {
    onManage() {
      if (this.shoppingCartData.length > 0) {
        this.manage = !this.manage
      }
    },
    onCheckBoxChange(item) {
      if (this.allSelect && this.shoppingCartData.length > 1) {
        this.allSelect = false
        setTimeout(() => {
          item.isSelect = true
        }, 50)
      }
      const result = this.shoppingCartData.some(item => item.isSelect === false)
      if (!result) {
        this.allSelect = true
      } else {
        this.allSelect = false
      }
    },
    onUpdateCard(item, res) {
      item.id = res.id
      item.name = res.name
      item.goodsId = res.goodsId
      item.goodsItemId = res.goodsItemId
      item.icon = res.icon
      item.color = res.color
      item.size = res.size
      item.combo = res.combo
      item.edition = res.edition
      item.quantity = res.quantity
      item.stock = res.stock
      item.unitPrice = res.unitPrice
      item.postage = res.postage
      item.price = res.price
    },
    onDelete(item) {
      let i = 0
      this.shoppingCartData.forEach((cart, index) => {
        if (cart.id === item.id) {
          i = index
        }
      })
      deleteShoppingCart(item.id).then(res => {
        const data = res.data
        if (data.success) {
          this.shoppingCartData.splice(i, 1)
          this.$toast.success('删除成功')
        } else {
          this.$toast.fail(data.message)
        }
      }).catch(err => {
        this.$notify({ type: 'danger', message: err.message })
      })
    },
    onSubmit() {
      const items = [] // goodsItemId  quantity
      this.shoppingCartData.forEach(item => {
        if (item.isSelect) {
          items.push({
            gid: item.goodsItemId,
            quantity: item.quantity
          })
        }
      })

      const pormises = [
        queryDefaultAddress(),
        queryAddress()
      ]

      let aid = null
      Promise.all(pormises).then(([r1, r2]) => {
        const d1 = r1.data
        const d2 = r2.data
        if (d1.success) {
          if (d1.data) {
            aid = d1.data.id
            this.onBuy(aid, items)
          } else {
            if (d2.success) {
              if (d2.data[0]) {
                aid = d2.data[0].id
                this.onBuy(aid, items)
              } else {
                this.$toast.fail('请先添加收货地址')
              }
            }
          }
        } else {
          if (d2.success) {
            if (d2.data[0]) {
              aid = d2.data[0].id
              this.onBuy(aid, items)
            } else {
              this.$toast.fail('请先添加收货地址')
            }
          }
        }
      }).catch(err => {
        this.$notify({ type: 'danger', message: err.message })
      })
    },
    onBuy(aid, items) {
      submitOrder(aid, items, null).then(res => {
        const data = res.data
        if (data.success) {
          const orderId = data.data
          this.$toast.loading({
            message: '提交订单中...',
            forbidClick: true
          })
          setTimeout(() => {
            this.$router.push(`/orderInfo/${orderId}`)
          }, 1000)
        }
      })
    },
    onAllSelect(val) {
      this.shoppingCartData.forEach(item => {
        item.isSelect = val
      })
    },
    onEmpty() {
      this.$dialog.confirm({
        title: '提示',
        message: '是否确定删除购物车'
      })
        .then(() => {
          emptyShoppingCart().then(res => {
            const data = res.data
            if (data.success) {
              this.shoppingCartData = []
              this.manage = false
            }
          })
        })
        .catch(() => {
          // on cancel
        })
    },
    onSomeDelete() {
      const ids = []
      const indexs = []
      this.shoppingCartData.forEach((item, index) => {
        if (item.isSelect) {
          ids.push(item.id)
          indexs.push(index)
        }
      })
      bulkDeleteShoppingCart(ids).then(res => {
        const data = res.data
        if (data.success) {
          indexs.forEach(item => {
            this.shoppingCartData.splice(item, 1)
          })
          this.$toast.success('删除成功')
        } else {
          this.$toast.fail(data.message)
        }
      }).catch(err => {
        this.$notify({ type: 'danger', message: err.message })
      })
    }
  },
  computed: {
    shoppingCartLength() {
      return this.shoppingCartData.length
    },
    finalPrice() {
      let price = 0
      this.shoppingCartData.forEach(item => {
        if (item.isSelect) {
          price += item.price
        }
      })
      return price * 100
    },
    isSumbit() {
      return this.shoppingCartData.some(item => item.isSelect === true)
    }
  },
  created() {
    queryShoppingCart().then(res => {
      const data = res.data
      if (data.success) {
        data.data.forEach(item => {
          this.shoppingCartData.push({
            id: item.id,
            name: item.name,
            goodsId: item.goodsId,
            goodsItemId: item.goodsItemId,
            icon: item.icon,
            color: item.color,
            size: item.size,
            combo: item.combo,
            edition: item.edition,
            quantity: item.quantity,
            stock: item.stock,
            unitPrice: item.unitPrice,
            postage: item.postage,
            price: item.price,
            isSelect: false
          })
        })
      }
    })
  }
}
</script>

<style lang="less" scoped>
.containerx{
  .content{
    position: relative;
    display: flex;
    align-items: center;
    background-color: #fff;
    border-radius: 15px;
    margin: 10px 5px;
    padding: 10px;
    padding-left: 0;
    .checkBox{
      flex: 1;
      padding-right: 5px;
      /deep/.van-checkbox{
        justify-content: center;
      }
    }
    .delete{
      background-color: #000;
      width: 50px;
      position: absolute;
      right: 0;
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: 0 15px 15px 0;
      background: linear-gradient(to right,rgba(247, 247, 247, 0.896),#ffffff);
    }
  }
  .deleteBar{
    height: 30px;
    width: 100%;
    padding: 10px 16px;
    background-color: #fff;
    position: fixed;
    bottom: 50px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    /deep/.van-checkbox{
      flex: 1;
      font-size: 14px;
    }
    .box{
      flex: 4;
      padding-right: 16px;
      display: flex;
      align-items: center;
      justify-content: space-between;
      .clear{
        font-size: 14px;
        padding-left: 20px;
        color: #999999;
      }
      /deep/.van-button{
        height: 40px;
        width: 100px;
        margin-right: 16px;
      }
    }
  }
}
</style>

<template>
  <div class="containerx">
    <van-nav-bar title="收货地址列表" left-arrow @click-left="onClickLeft" :fixed="true" :placeholder="true">
      <template #right>
        <div v-if="isSelect" style="color: #1989fa" @click="onReturn">确认</div>
      </template>
    </van-nav-bar>
    <van-address-list
      v-model="chosenAddressId"
      :list="list"
      :switchable="isOption"
      default-tag-text="默认"
      @add="onAdd"
      @edit="onEdit"
      @select="onSelect"/>
  </div>
</template>

<script>
import { queryAddress } from '@/api/user.js'
import { updateOrder } from '@/api/orders.js'
export default {
  name: 'Address',
  data() {
    return {
      chosenAddressId: 0,
      // 是否选择
      isSelect: false,
      // 是否显示复选框
      isOption: false,
      selectData: {},
      list: [],
      orderId: null
    }
  },
  methods: {
    onClickLeft() {
      this.$router.back()
    },
    onAdd() {
      this.$router.push('/saveAddress')
    },
    onEdit(item) {
      this.$router.push(`/updateAddress/${item.id}`)
    },
    onSelect(item) {
      this.selectData = item
      this.chosenAddressId = item.id
    },
    onReturn() {
      if (this.orderId) {
        updateOrder(this.orderId, this.chosenAddressId, null).then(res => {
          const data = res.data
          if (data.success) {
            this.$toast.success('修改成功')
            this.$bus.$emit('getOderInfo', data.data)
          } else {
            this.$toast.fail(data.message)
          }
        }).catch(err => {
          this.$notify({ type: 'danger', message: err.message })
        })
        this.$router.back()
        return
      }
      setTimeout(() => { // 加上异步，使得第一次可以监听到值
        this.$bus.$emit('getAddressId', this.chosenAddressId)
      }, 200)
      this.$router.back()
    }
  },
  created() {
    const addressId = this.$route.query.id
    const orderId = this.$route.query.orderId
    queryAddress().then(res => {
      const data = res.data
      if (data.success) {
        const addressList = data.data
        addressList.forEach(address => {
          this.list.push({
            id: address.id,
            name: address.name,
            tel: address.phone,
            address: address.province + ' ' + address.city + ' ' + address.district + ' ' + address.address,
            isDefault: address.isDefault
          })
          if (address.isDefault === 1) {
            this.chosenAddressId = address.id
          }
          if (addressId) {
            this.isSelect = true
            this.isOption = true
            this.chosenAddressId = addressId
          }
          if (orderId) {
            this.isSelect = true
            this.isOption = true
            this.orderId = orderId
          }
        })
      }
    })
  }
}
</script>

<style lang="less" scoped>

</style>

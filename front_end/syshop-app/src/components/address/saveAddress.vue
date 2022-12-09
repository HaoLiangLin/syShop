<template>
  <div class="containerx">
    <van-nav-bar title="新建收货地址" left-arrow @click-left="onClickLeft" :fixed="true" :placeholder="true" />
    <van-address-edit :area-list="areaList" show-set-default show-search-result :area-columns-placeholder="['请选择', '请选择', '请选择']" @save="onSave" />
  </div>
</template>

<script>
// npm i @vant/area-data
import { areaList } from '@vant/area-data'
import { saveAddress } from '@/api/user.js'
export default {
  name: 'Address-SaveAddress',
  data() {
    return {
      areaList
    }
  },
  methods: {
    onClickLeft() {
      this.$router.back()
    },
    onSave(content) {
      saveAddress(content.name, content.tel, content.province, content.city, content.county, content.addressDetail, content.isDefault ? 1 : 0).then(res => {
        const data = res.data
        if (!data.success) {
          this.$notify({ type: 'warning', message: data.message })
        } else {
          this.$notify({ type: 'success', message: data.message })
          this.$router.back()
        }
      }).catch(err => {
        this.$notify({ type: 'danger', message: err.message })
      })
    }
  }
}
</script>

<style lang="less" scoped>
</style>

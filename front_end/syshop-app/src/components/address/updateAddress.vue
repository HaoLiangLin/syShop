<template>
  <div class="containerx">
    <van-nav-bar title="编辑收货地址" left-arrow @click-left="onClickLeft" :fixed="true" :placeholder="true" />
    <van-address-edit
      :area-list="areaList"
      :address-info="addressInfo"
      show-delete
      show-set-default
      show-search-result
      :area-columns-placeholder="['请选择', '请选择', '请选择']"
      @save="onSave"
      @delete="onDelete"
    />
  </div>
</template>

<script>
// npm i @vant/area-data
import { areaList } from '@vant/area-data'
import { queryAddressById, updateAddress, deleteAddress } from '@/api/user.js'
export default {
  name: 'AddressEdit',
  data() {
    return {
      addressInfo: {},
      areaList
    }
  },
  methods: {
    onClickLeft() {
      this.$router.back()
    },
    onSave(content) {
      updateAddress(content.id, content.uid, content.name, content.tel, content.province, content.city, content.county, content.addressDetail, content.isDefault ? 1 : 0).then(res => {
        const data = res.data
        if (!data.success) {
          this.$notify({ type: 'warning', message: data.message })
        } else {
          this.$notify({ type: 'success', message: data.message })
        }
      }).catch(err => {
        this.$notify({ type: 'danger', message: err.message })
      })
    },
    onDelete(content) {
      deleteAddress(content.id).then(res => {
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
  },
  created() {
    queryAddressById(this.$route.params.id).then(res => {
      const data = res.data
      if (!data.success) {
        this.$notify({ type: 'warning', message: data.message })
      } else {
        const address = data.data

        let county = null
        for (const key in this.areaList.county_list) {
          if (this.areaList.county_list[key] === address.district) {
            county = key
          }
        }

        this.addressInfo = {
          id: address.id,
          uid: address.uid,
          name: address.name,
          tel: address.phone,
          province: address.province,
          city: address.city,
          county: address.district,
          areaCode: county,
          addressDetail: address.address,
          isDefault: address.isDefault === 1
        }
      }
    })
  }
}
</script>

<style lang="less" scoped>

</style>

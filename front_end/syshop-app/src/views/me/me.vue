<template>
  <div class="containerx">
    <div class="header" :style="'background-image: url('+ require('@/assets/me.jpg')+')'">
      <MyHeader/>
      <User/>
      <Func v-if="jwt"/>
    </div>
    <Order/>
    <List/>
    <van-divider dashed>&copy;广东水利电力职业技术学院-林武泰</van-divider>
    <Tabbar/>
  </div>
</template>

<script>
export default {
  name: 'Me',
  components: {
    MyHeader: () => import('@/components/me/header.vue'),
    User: () => import('@/components/me/user.vue'),
    Func: () => import('@/components/me/func.vue'),
    Order: () => import('@/components/me/order.vue'),
    List: () => import('@/components/me/list.vue'),
    Tabbar: () => import('@/components/tabbar.vue')
  },
  data() {
    return {
      jwt: localStorage.getItem('authorization')
    }
  }
}
</script>

<style lang="less" scoped>
.containerx{
  .header{
    background-size: cover;
    background-repeat: no-repeat;
    padding-bottom: 5px;
  }
}
</style>

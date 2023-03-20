<template>
  <div class="containerx" :style="'background-image: url('+ require('@/assets/login.jpg')+')'">
    <Card/>
  </div>
</template>

<script>
export default {
  name: 'Login',
  components: {
    Card: () => import('@/components/login/card.vue')
  }
}
</script>

<style lang="less" scoped>
.containerx{
  height: 100vh;
  background-size: cover;
  background-repeat: no-repeat;
  display: flex;
  align-items: center;
  .footer{
    position: absolute;
    bottom: 0;
  }
}
</style>

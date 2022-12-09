<template>
  <div class="containerx" :style="'background-image: url('+ require('@/assets/login.jpg')+')'">
    <Content/>
  </div>
</template>

<script>
export default {
  name: 'Login',
  components: {
    Content: () => import('@/components/login/content.vue')
  }
}
</script>

<style lang="less" scoped>
.containerx{
  height: 100vh;
  background-size: cover;
  background-repeat: no-repeat;
}
</style>

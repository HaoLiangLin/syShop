<template>
  <div class="containerx" :style="'background-image: url('+ require('@/assets/logo.jpg')+')'">
    <AboutUs/>
  </div>
</template>

<script>
export default {
  name: 'About-Us',
  components: {
    AboutUs: () => import('@/components/aboutUs/aboutUs.vue')
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

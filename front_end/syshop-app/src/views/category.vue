<template>
  <div class="containerx">
    <CategoryList/>
    <Tabbar/>
  </div>
</template>

<script>
export default {
  name: 'Category',
  components: {
    CategoryList: () => import('@/components/category/categoryList.vue'),
    Tabbar: () => import('@/components/tabbar.vue')
  }
}
</script>

<style lang="less" scoped>
.containerx{
  height: 100vh;
}
</style>

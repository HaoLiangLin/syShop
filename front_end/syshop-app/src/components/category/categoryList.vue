<template>
  <div class="container">
    <van-tree-select
      :items="firstCategory"
      :main-active-index.sync="activeIndex"
      @click-nav="onClickNav"
      height="100vh"
    >
      <template #content>
        <CategoryCard :categorys="categorys"/>
      </template>
    </van-tree-select>
  </div>
</template>

<script>
import { queryGoodsCategoryFirst, queryGoodsCategoryByCid } from '@/api/goods.js'
export default {
  name: 'Category',
  components: {
    CategoryCard: () => import('@/components/category/categoryCard.vue')
  },
  data() {
    return {
      activeIndex: 0,
      firstCategory: [],
      categorys: []
    }
  },
  methods: {
    onClickNav(index) {
      for (let i = 0; i < this.firstCategory.length; i++) {
        if (i === index) {
          const id = this.firstCategory[i].id
          this.setCategory(id)
        }
      }
    },
    setCategory(cid) {
      queryGoodsCategoryByCid(cid).then(res => {
        const data = res.data
        if (data.success) {
          this.categorys = []
          data.data.forEach(item => {
            this.categorys.push(item)
          })
        }
      })
    }
  },
  created() {
    queryGoodsCategoryFirst().then(res => {
      const data = res.data
      if (data.success) {
        const categoryList = data.data
        let firstCid = null
        categoryList.forEach(item => {
          if (!firstCid) {
            firstCid = item.id
          }
          this.firstCategory.push({
            id: item.id,
            text: item.name
          })
        })
        this.setCategory(firstCid)
      }
    }).catch(err => {
      this.$notify({ type: 'danger', message: err.message })
    })
  }
}
</script>

<style lang="less" scoped>

</style>

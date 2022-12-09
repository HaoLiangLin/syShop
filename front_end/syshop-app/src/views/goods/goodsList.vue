<template>
  <div class="containerx">
    <!-- 顶部导航栏 -->
    <van-nav-bar
      title="商品列表"
      left-text="返回"
      left-arrow
      @click-left="onClickLeft"
      @click-right="onClickRight"
    >
      <template #right>
        <van-icon name="search" size="18" />
      </template>
    </van-nav-bar>
    <!-- 头部标签页 -->
    <van-tabs v-model="headerOption" @click="onTab">
      <van-tab title="综合"></van-tab>
      <van-tab title="新品"></van-tab>
      <van-tab>
        <template #title>
          价格 <van-image v-if="isPrice" width="12" height="12" @click="onSortIcon" :src="require(`@/assets/${sortIcon}.png`)"/>
        </template>
      </van-tab>
    </van-tabs>
    <!-- 商品列表 -->
    <van-cell center v-for="good in goodsData" :key="good.goods.id">
      <Card :item="good"/>
    </van-cell>
  </div>
</template>

<script>
import { queryGoodsPage } from '@/api/goods.js'
export default {
  name: 'GoodsList',
  components: {
    Card: () => import('@/components/goods/goodsCard.vue')
  },
  data() {
    return {
      // 分类子类id
      cid: null,
      // 搜索参数
      searchText: null,
      // 头部标签栏下标
      headerOption: 0,
      goodsData: [],
      isPrice: false,
      priceSort: 'Asc',
      sortIcon: '升序'
    }
  },
  methods: {
    // 点击返回
    onClickLeft() {
      this.$router.back()
    },
    // 点击搜索
    onClickRight() {
      this.$router.push('/search')
    },
    onTab(name) {
      switch (name) {
        case 0: this.onTab1(); break
        case 1: this.onIsNew(); break
        case 2: this.onPrice()
      }
    },
    onTab1() {
      this.isPrice = false
      queryGoodsPage(1, 10, this.searchText, this.cid, null, null, null, null).then(res => {
        const data = res.data
        if (data.success) {
          this.goodsData = []
          data.data.records.forEach(item => {
            this.goodsData.push(item)
          })
        }
      }).catch(err => {
        this.$notify({ type: 'danger', message: err.message })
      })
    },
    onIsNew() {
      this.isPrice = false
      queryGoodsPage(1, 10, this.searchText, this.cid, null, null, 1, null).then(res => {
        const data = res.data
        if (data.success) {
          this.goodsData = []
          data.data.records.forEach(item => {
            this.goodsData.push(item)
          })
        }
      }).catch(err => {
        this.$notify({ type: 'danger', message: err.message })
      })
    },
    onPrice() {
      this.isPrice = true
      queryGoodsPage(1, 10, this.searchText, this.cid, null, null, null, this.priceSort).then(res => {
        const data = res.data
        if (data.success) {
          this.goodsData = []
          data.data.records.forEach(item => {
            this.goodsData.push(item)
          })
        }
      }).catch(err => {
        this.$notify({ type: 'danger', message: err.message })
      })
    },
    onSortIcon() {
      if (this.priceSort === 'Des') {
        this.priceSort = 'Asc'
        this.sortIcon = '升序'
        queryGoodsPage(1, 10, this.searchText, this.cid, null, null, null, this.priceSort).then(res => {
          const data = res.data
          if (data.success) {
            this.goodsData = []
            data.data.records.forEach(item => {
              this.goodsData.push(item)
            })
          }
        }).catch(err => {
          this.$notify({ type: 'danger', message: err.message })
        })
      } else {
        this.priceSort = 'Des'
        this.sortIcon = '降序'
        queryGoodsPage(1, 10, this.searchText, this.cid, null, null, null, this.priceSort).then(res => {
          const data = res.data
          if (data.success) {
            this.goodsData = []
            data.data.records.forEach(item => {
              this.goodsData.push(item)
            })
          }
        }).catch(err => {
          this.$notify({ type: 'danger', message: err.message })
        })
      }
    }
  },
  created() {
    this.cid = this.$route.query.cid
    this.searchText = this.$route.query.name
    queryGoodsPage(1, 10, this.searchText, this.cid, null, null, null, null).then(res => {
      const data = res.data
      if (data.success) {
        this.goodsData = []
        data.data.records.forEach(item => {
          this.goodsData.push(item)
        })
      }
    }).catch(err => {
      this.$notify({ type: 'danger', message: err.message })
    })
  }
}
</script>

<style lang="less" scoped>
</style>

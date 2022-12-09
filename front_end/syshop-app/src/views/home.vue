<template>
  <div class="containerx">
    <van-nav-bar
      title="水院商城"
    >
      <template #right>
        <van-icon name="ellipsis" size="18" color="#000"/>
      </template>
    </van-nav-bar>
    <!-- 搜索框 -->
    <van-search @click="onSearch" disabled placeholder="请输入搜索关键词" />
    <!-- 轮播图 -->
    <van-swipe class="my-swipe" :autoplay="5000" indicator-color="white">
      <van-swipe-item v-for="event in events" :key="event.id">
        <van-image
        fit="fill"
        :src="setImage(event.icon)"
        @click="onEvents(event)"
      />
      </van-swipe-item>
    </van-swipe>
    <!-- 一级分类 -->
    <van-grid :border="false" column-num="5">
      <van-grid-item v-for="item in categoryList" :key="item.id" :text="item.name" :to="{path: '/goodsList', query: {cid: item.id}}">
        <template #icon>
          <van-image :src="setImage(item.icon)" height="50px" width="50px" :round="true"></van-image>
        </template>
      </van-grid-item>
      <van-grid-item text="更多分类" to="/category">
        <template #icon>
          <van-image :src="require('@/assets/更多.png')" height="50px" width="50px" :round="true"></van-image>
        </template>
      </van-grid-item>
    </van-grid>
    <!-- 公告 -->
    <van-notice-bar
      left-icon="volume-o"
      :text="notices"
    />
    <div class="goodsList">
      <div class="head">
        <div class="title">上架新品</div>
        <div class="text">更多新品</div>
      </div>
      <div class="content">
        <Card v-for="item in newsGoods" :key="item.goods.id" :item="item"/>
      </div>
    </div>
    <div class="goodsList">
      <div class="head">
        <div class="title">精心推荐</div>
        <div class="text">更多推荐</div>
      </div>
      <div class="content">
        <Card v-for="item in recommendGoods" :key="item.goods.id" :item="item"/>
      </div>
    </div>
    <Tabbar/>
  </div>
</template>

<script>
import { quertEvents, queryNotices } from '@/api/system.js'
import { queryGoodsCategoryFirst, queryGoodsPage } from '@/api/goods.js'
import { getImage } from '@/utils/resources.js'
export default {
  name: 'Home',
  data() {
    return {
      events: [],
      notices: '水院商城公告',
      categoryList: [],
      newsGoods: [],
      recommendGoods: []
    }
  },
  components: {
    Card: () => import('@/components/goods/goodsCard.vue'),
    Tabbar: () => import('@/components/tabbar.vue')
  },
  methods: {
    onSearch() {
      this.$router.push('/search')
    },
    setImage(icon) {
      return getImage(icon)
    },
    onEvents(event) {
      console.log(event)
    }
  },
  created() {
    quertEvents().then(res => {
      const data = res.data
      if (data.success) {
        data.data.forEach(events => {
          this.events.push(events)
        })
      }
    }).catch(err => {
      this.$notify({ type: 'danger', message: err.message })
    })
    queryNotices(null, null, 'Dec').then(res => {
      const data = res.data
      if (data.success) {
        const notice = data.data[1]
        this.notices = `[${notice.title}] ${notice.content}`
      }
    })
    queryGoodsCategoryFirst().then(res => {
      const data = res.data
      if (data.success) {
        const categoryList = data.data
        for (let i = 0; i <= 8; i++) {
          this.categoryList.push(categoryList[i])
        }
      }
    })
    queryGoodsPage(1, 10, null, null, null, 1, null).then(res => {
      const data = res.data
      if (data.success) {
        this.recommendGoods = []
        data.data.records.forEach(item => {
          this.recommendGoods.push(item)
        })
      }
    })
    queryGoodsPage(1, 10, null, null, null, null, 1).then(res => {
      const data = res.data
      if (data.success) {
        this.newsGoods = []
        data.data.records.forEach(item => {
          this.newsGoods.push(item)
        })
      }
    })
  }
}
</script>

<style lang="less" scoped>
.containerx{
  /deep/.van-swipe{
    .van-swipe-item{
      color: #fff;
      height: 180px;
      font-size: 20px;
      text-align: center;
      background-color: #999999;
    }
  }
  /deep/.van-notice-bar{
    margin-top: 10px;
  }
  .goodsList{
    margin-top: 10px;
    padding: 5px 15px;
    background-color: #fff;
    .head{
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 5px;
      .title{
        font-size: 18px;
      }
      .text{
        font-size: 12px;
        padding: 2px 10px;
        border-radius: 10px;
        background-color: rgba(0, 0, 0, 0.1);
        color: rgb(232, 0, 0);
      }
    }
  }
}
</style>

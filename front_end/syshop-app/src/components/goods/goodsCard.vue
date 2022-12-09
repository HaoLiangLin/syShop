<template>
  <div class="container" @click="onClickGoods">
     <div class="image">
      <van-image
        fit="fill"
        width="110px"
        :src="setImage(getimage(goods.images))"
      />
    </div>
    <div class="content">
      <div class="name">{{goods.name}}</div>
      <div class="price">
        <span class="newPrice"><a>￥</a>{{item.discount * item.price}}</span>
        <span class="oldPrice" v-if="item.discount < 1">￥{{item.price}}</span>
      </div>
      <div class="say">{{evaluationCount > 0 ? `评价：${evaluationCount}` : '暂无评论'}}</div>
      <div class="info">
        <span class="sales">月销量: {{goods.monthSale}}</span>
        <span class="address">发货地: {{goods.city}}</span>
      </div>
    </div>
  </div>
</template>

<script>
import { getImage } from '@/utils/resources.js'
import { queryEvaluation } from '@/api/goods.js'
export default {
  name: 'GoodsCard',
  props: ['item'],
  data() {
    return {
      goods: this.item.goods,
      evaluationCount: 0
    }
  },
  methods: {
    setImage(image) {
      return getImage(image)
    },
    getimage(images) {
      const list = images.split(',')
      return list[0]
    },
    onClickGoods() {
      this.$router.push(`/goods/${this.goods.id}`)
    }
  },
  created() {
    queryEvaluation(this.goods.id, 1).then(res => {
      const data = res.data
      if (data.success) {
        this.evaluationCount = data.data.length
      }
    })
  }
}
</script>

<style lang="less" scoped>
.container{
  display: flex;
  .image{
    flex: 1;
  }
  .content{
    flex: 3;
    margin-left: 15px;
    .name{
      font-family: Arial, sans-serif;
      font-size: 18px;
      font-weight: bold;
      margin: 10px 0;
    }
    .price{
      margin-top: 15px;
      .newPrice{
        color: red;
        font-size: 20px;
        a{
          font-size: 14px;
        }
      }
      .oldPrice{
        color: #999999;
        font-size: 12px;
        text-decoration: line-through;
      }
    }
    .say{
      font-size: 12px;
    }
    .info{
      font-size: 12px;
      display: flex;
      justify-content: center;
      color: #999999;
      .sales{
        flex: 1;
      }
      .address{
        font-size: 12px;
      }
    }
  }
}
</style>

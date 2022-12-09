<template>
  <div class="container">
    <!-- 头部导航栏 -->
    <van-nav-bar :fixed="true" :placeholder="true">
      <template #title>
        <van-search
          placeholder="请输入搜索关键词"
          shape="round"
          @click="onSearch"
          readonly
        >
        </van-search>
      </template>
      <template #left>
        <div class="searchLeft">
          <van-icon name="arrow-left" size="18" @click="onSearchLeft"/>
        </div>
      </template>
      <template #right>
        <div class="searchAction">
          <van-icon name="ellipsis" size="20"/>
        </div>
      </template>
    </van-nav-bar>
    <!-- 商品评价 -->
    <div class="evaluation">
      <div class="content">
        <div class="fail"  v-if="evaluations.length < 1">
          <van-empty description="该商品暂无评价" />
        </div>
        <div class="success" v-for="(item, index) in evaluations" :key="item.id">
          <div class="user">
            <div class="info">
              <van-image
                round
                width="30px"
                height="30px"
                :src="setImage(item.userIcon)"
              />
              <span>{{item.nickname}}</span>
            </div>
            <span class="time">{{item.time}}</span>
          </div>
          <div class="stars">
            <van-rate
              v-model="item.stars"
              :readonly="true"
              color="#ffd21e"
              void-icon="star"
              void-color="#eee"
            />
          </div>
          <div class="parameter">
            已购商品：{{item.parameter}}
          </div>
          <div class="text">{{item.content}}</div>
          <div class="image">
            <van-image
              v-for="(image, index) in item.images" :key="index"
              width="120"
              height="120"
              fit="contain"
              :src="setImage(image)"
            />
          </div>
          <div class="func">
            <span v-if="item.isMe" @click="onDelete(item.id,index)"><van-icon name="delete-o" /> 删除</span>
            <span @click="onLiked(item)"><van-icon name="good-job-o" v-if="item.isLiked === 0"/><van-icon name="good-job" v-if="item.isLiked === 1" color="red"/> {{item.liked}}</span>
            <span><van-icon name="chat-o" /> {{item.comment > 0 ? item.comment : '评论'}}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { queryEvaluation, evaluationLiked } from '@/api/goods.js'
import { delEvaluation } from '@/api/orders.js'
import { getImage } from '@/utils/resources.js'
export default {
  name: 'Goods-Evaluation',
  data() {
    return {
      gid: '',
      evaluations: []
    }
  },
  methods: {
    // 点击返回
    onSearchLeft() {
      this.$router.back()
    },
    // 搜索页跳转
    onSearch() {
      this.$router.push('/search')
    },
    setImage(icon) {
      return getImage(icon)
    },
    // 删除评价
    onDelete(id, index) {
      this.$dialog.confirm({
        title: '提示',
        message: '是否删除该评价'
      })
        .then(() => {
          // on confirm
          delEvaluation(id).then(res => {
            const data = res.data
            if (data.success) {
              this.$toast.success(data.message)
              this.evaluations.splice(index, 1)
            } else {
              this.$toast.fail(data.message)
            }
          })
        })
        .catch(() => {
          // on cancel
        })
    },
    // 评价点赞
    onLiked(item) {
      evaluationLiked(item.id).then(res => {
        const data = res.data
        if (data.success) {
          if (item.isLiked === 1) {
            item.isLiked = 0
            item.liked--
          } else {
            item.isLiked = 1
            item.liked++
          }
        } else {
          this.$toast.fail(data.message)
        }
      }).catch(err => {
        this.$notify({ type: 'danger', message: err.message })
      })
    }
  },
  created() {
    this.gid = this.$route.params.gid
    queryEvaluation(this.gid, 1).then(res => {
      const data = res.data
      if (data.success) {
        this.evaluations = []
        data.data.forEach(item => {
          this.evaluations.push(item)
        })
      } else {
        this.$toast.fail(data.message)
      }
    }).catch(err => {
      this.$notify({ type: 'danger', message: err.message })
    })
  }
}
</script>

<style lang="less" scoped>
.container{
  /deep/.van-nav-bar{
    .van-search{
      padding: 0;
    }
    .searchAction{
      display: inline-block;
      margin-left: 10px;
      &:first-child{
        margin: 0;
      }
    }
  }
  .evaluation{
    margin: 0 5px;
    border-radius: 10px;
    padding: 10px;
    background-color: #fff;

    .content{
      .success{
        width: 100%;
        padding-top: 10px;
        .user{
          height: 40px;
          display: flex;
          align-items: center;
          justify-content: space-between;
          .info{
            display: flex;
            align-items: center;
            span{
              padding-left: 10px;
            }
          }
          .time{
            font-size: 13px;
            color: #999999;
            float: right;
          }
        }
        .parameter{
          padding-left: 5px;
          font-size: 12px;
          color: #ccc;
          border-left: 2px solid #ccc;
        }
        .image{
          display: flex;
          /deep/.van-image{
            margin-right: 10px;
          }
        }
        .text{
          margin-top: 10px;
          font-size: 14px;
          padding-bottom: 10px;
        }
        .func{
          padding: 10px 0;
          text-align: right;
          span{
            padding-left: 20px;
            font-size: 14px;
          }
        }
      }
    }
  }
}
</style>

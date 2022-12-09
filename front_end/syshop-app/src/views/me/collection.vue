<template>
  <div class="containerx">
    <van-nav-bar
      title="我的收藏"
      left-arrow
      :fixed="true"
      :placeholder="true"
      @click-left="goBack"
    />
    <div class="collectionBox">
      <div class="info" v-for="(item, index) in collection" :key="item.gid">
        <div class="image">
          <van-image
            width="90px"
            height="90px"
            fit="cover"
            :src="setIcon(item.images)"
          />
        </div>
        <div class="content">
          <div class="name">{{item.name}}</div>
          <div class="price">￥{{item.price}}</div>
          <div class="btn"><van-button size="small" type="default" @click="onDel(item, index)">移除</van-button></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { queryCollection, deleteCollection } from '@/api/user.js'
import { getImage } from '@/utils/resources.js'
export default {
  name: 'Collection',
  data() {
    return {
      collection: []
    }
  },
  methods: {
    goBack() {
      this.$router.back()
    },
    setIcon(icon) {
      const image = icon.split(',')
      return getImage(image)
    },
    onDel(item, index) {
      deleteCollection(item.gid).then(res => {
        const data = res.data
        if (data.success) {
          this.collection.splice(index, 1)
        } else {
          this.$toast.fail(data.message)
        }
      }).catch(err => {
        this.$notify({ type: 'danger', message: err.message })
      })
    }
  },
  created() {
    queryCollection().then(res => {
      const data = res.data
      this.collection = []
      if (data.success) {
        data.data.forEach(item => {
          this.collection.push(item)
        })
      }
    })
  }
}
</script>

<style lang="less" scoped>
.containerx {
  .collectionBox{
    padding: 0 10px;
    .info{
      display: flex;
      background-color: #ffffff;
      padding: 10px;
      margin-bottom: 10px;
      .image{
        flex: 1;
      }
      .content{
        flex: 4;
        padding-left: 10px;
        .name{
          font-size: 18px;
          padding-bottom: 10px;
        }
        .price{
          font-size: 16px;
          color: red;
        }
        .btn{
          text-align: right;
        }
      }
    }
  }
}
</style>

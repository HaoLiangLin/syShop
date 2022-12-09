<template>
  <div class="container">
    <van-nav-bar
      title="订单评价"
      left-arrow
      :fixed="true"
      :placeholder="true"
      @click-left="goBack"
    />
    <div class="orderItemBox" v-if="orderItem">
      <div class="image">
        <van-image
          width="90px"
          height="90px"
          fit="cover"
          :src="setIcon(orderItem.image)"
        />
      </div>
      <div class="info">
        <div class="name">
          <div class="text">{{orderItem.goodsName}}</div>
        </div>
        <div class="type">
          <span v-if="orderItem.color">颜色:{{orderItem.color}}; </span>
          <span v-if="orderItem.size">大小:{{orderItem.size}}; </span>
          <span v-if="orderItem.combo">套餐:{{orderItem.combo}}; </span>
          <span v-if="orderItem.edition">版本:{{orderItem.edition}}; </span>
        </div>
        <div class="number">
          <div class="price"><span>￥</span>{{orderItem.price}}</div>
          <div class="quantity"><span>x</span>{{orderItem.quantity}}</div>
        </div>
      </div>
    </div>
    <van-field
      v-model="content"
      rows="3"
      autosize
      label="商品评价"
      type="textarea"
      placeholder="请输入评价"
      show-word-limit
    />
    <van-field name="uploader" label="上传图片">
      <template #input>
        <van-uploader v-model="fileList" :multiple="true"/>
      </template>
    </van-field>
    <van-field name="rate" label="商品评分">
      <template #input>
        <van-rate
          v-model="stars"
          color="#ffd21e"
          void-icon="star"
          void-color="#eee"
        />
      </template>
    </van-field>
    <van-button type="primary" block @click="onSumbit">提交</van-button>
  </div>
</template>

<script>
import { queryOrderItemById, uploadImages, saveEvaluation } from '@/api/orders.js'
import { getImage } from '@/utils/resources.js'
export default {
  name: 'Evaluation',
  data() {
    return {
      orderItemId: null,
      orderItem: null,
      content: '',
      stars: 1,
      fileList: []
    }
  },
  methods: {
    goBack() {
      this.$router.back()
    },
    setIcon(icon) {
      return getImage(icon)
    },
    onSumbit() {
      // 新建表单对象
      const formdata = new window.FormData()
      this.fileList.forEach(file => {
        // 向表单添加数据
        formdata.append('files', file.file)
      })

      console.log(formdata.getAll('files'))

      uploadImages(this.orderItemId, formdata).then(res => {
        const data = res.data
        if (data.success) {
          saveEvaluation(this.orderItemId, this.stars, data.data, this.content).then(res => {
            const result = res.data
            if (result.success) {
              this.$toast.success(result.message)
              this.$router.back()
            } else {
              this.$toast.fail(result.message)
            }
          })
        } else {
          this.$toast.fail(data.message)
        }
      }).catch(err => {
        this.$notify({ type: 'danger', message: err.message })
      })
    }
  },
  created() {
    this.orderItemId = this.$route.params.id
    queryOrderItemById(this.orderItemId).then(res => {
      const data = res.data
      if (data.success) {
        this.orderItem = data.data
      }
    })
  }
}
</script>

<style lang="less" scoped>
.container{
  .orderItemBox{
    display: flex;
    margin-bottom: 10px;
    padding: 0 10px;
    background-color: #fff;
    .image{
      flex: 3;
    }
    .info{
      flex: 7;
      .name{
        display: flex;
        justify-content: space-between;
        .text{
          font-size: 14px;
          font-weight: bold;
          line-height: 30px;
          max-width: 190px;
          // 强制不换行
          white-space:nowrap;
          // 溢出隐藏
          overflow: hidden;
        }
      }
      .type{
        span{
          color: #999;
          font-size: 13px;
        }
      }
      .number{
        display: flex;
        justify-content: space-between;
        margin-top: 18px;
        .price{
          color: red;
          font-size: 18px;
        }
        .quantity{
          color: #999;
          font-size: 14px;
          padding-top: 5px;
        }
      }
    }
  }
  /deep/.van-button{
    margin-top: 30px;
  }
}
</style>

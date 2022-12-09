<template>
  <div class="container">
    <van-grid :border="false" column-num="3" gutter="10">
      <van-grid-item v-for="item in categorys" :key="item.id" :to="{path: '/goodsList', query: {cid: item.id}}">
        <template #icon>
          <van-image :src="setIcon(item.icon)" alt="图片" radius="5px"></van-image>
        </template>
        <template #text>
          <div class="name">
            <span class="text">{{item.name}}</span>
          </div>
        </template>
      </van-grid-item>
    </van-grid>
  </div>
</template>

<script>
import { getImage } from '@/utils/resources.js'
export default {
  name: 'classificationCard',
  props: ['categorys'],
  methods: {
    setIcon(icon) {
      return getImage(icon)
    }
  }
}
</script>

<style lang="less" scoped>
.container{
  .name{
    .text{font-size: 12px;}
  }
}
</style>

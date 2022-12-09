<template>
  <div class="container">
    <van-cell-group>
      <van-cell title="常用功能" center title-style="padding-left: 5px;" border>
        <template #icon>
          <van-icon name="apps-o" color="#3CB371" size="16px" />
        </template>
      </van-cell>
    </van-cell-group>
    <van-grid :border="false">
      <van-grid-item text="我的账户" to="/purse">
        <template #icon>
          <van-icon name="vip-card-o" size="26px" />
        </template>
      </van-grid-item>
      <van-grid-item text="收货地址" to="/address">
        <template #icon>
          <van-icon name="records" size="26px" />
        </template>
      </van-grid-item>
      <van-grid-item text="我的收藏" to="/collection">
        <template #icon>
          <van-icon name="star-o" size="26px" />
        </template>
      </van-grid-item>
      <van-grid-item text="浏览记录">
        <template #icon>
          <van-icon name="todo-list-o" size="26px" />
        </template>
      </van-grid-item>
      <van-grid-item text="联系客服">
        <template #icon>
          <van-icon name="service-o" size="26px" />
        </template>
      </van-grid-item>
      <van-grid-item text="问题反馈">
        <template #icon>
          <van-icon name="comment-o" size="26px" />
        </template>
      </van-grid-item>
      <van-grid-item text="关于我们" to="/aboutUs">
        <template #icon>
          <van-icon name="friends-o" size="26px"/>
        </template>
      </van-grid-item>
      <van-grid-item text="设置" to="/setting">
        <template #icon>
          <van-icon name="setting-o" size="26px" />
        </template>
      </van-grid-item>
    </van-grid>
  </div>
</template>

<script>
export default {
  name: 'Me-List'
}
</script>

<style lang="less" scoped>
.container {
  margin-top: 10px;
}
</style>

<template>
  <div id="app">
    <keep-alive include="Goods">
      <router-view/>
    </keep-alive>
  </div>
</template>

<style lang="less">
// 去除密码框自带的眼睛
input[type='password']::-ms-reveal {
  display: none;
}
// 全局背景色
html{
  background-color: #f4f4f4;
}
</style>

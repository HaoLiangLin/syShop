<template>
  <div id="app">
    <router-view/>
    <div class="footer">
      <a href="https://beian.miit.gov.cn/" target="_blank">粤ICP备2023011016号-1</a>
    </div>
  </div>
</template>

<style lang="less">
*{
  padding: 0;
  margin: 0;
}
// 全局背景色
html{
  background-color: #EFF0F4;
  .footer{
    position: absolute;
    bottom: 10px;
    padding: 0 10px;
    a{
      font-size: 12px;
      color: #aaaaaa;
      text-decoration: none;
    }
  }
}
</style>

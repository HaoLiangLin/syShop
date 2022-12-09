<template>
  <div class="container">
    <van-nav-bar
      title="关于我们"
      left-arrow
      :fixed="true"
      :placeholder="true"
      @click-left="goBack"
    />
    <img :src="require('@/assets/关于我们.jpg')" alt="" height="20%" width="100%">
    <div class="content">
      <div class="row">
        <div class="lebal">项目名称</div>
        <div class="text">SyShop水院商城</div>
      </div>
      <div class="row">
        <div class="lebal">开发者</div>
        <div class="text">林武泰</div>
      </div>
      <div class="row">
        <div class="lebal">开发时间</div>
        <div class="text">约2个月</div>
      </div>
      <div class="row">
        <div class="lebal">开发地点</div>
        <div class="text">
          <a href="https://www.gdsdxy.edu.cn/" target="_blank">广东水利电力职业技术学院（天河校区）</a>
        </div>
      </div>
      <div class="row">
        <div class="lebal">个人信息</div>
        <div class="text">
          <span>班级：20计应2班</span><br>
          <span>性别：男</span><br>
          <span>年龄：21岁</span><br>
          <span>学号：200114216</span><br>
          <span>专业：计算机应用</span><br>
          <span>爱好：计算机编程、写作、游戏</span><br>
        </div>
      </div>
      <div class="row">
        <div class="lebal">联系手机</div>
        <div class="text">13071510830</div>
      </div>
      <div class="row">
        <div class="lebal">电子邮箱</div>
        <div class="text">2786794141@qq.com</div>
      </div>
      <div class="row">
        <div class="lebal">前端技术</div>
        <div class="text">
          HTML、CSS、Less、JavaScript、Vue、Axios、Vant-UI
        </div>
      </div>
      <div class="row">
        <div class="lebal">后端技术</div>
        <div class="text">
          Spring、SpringMVC、SpringBoot、Spring Security、MySQL、MybatisPlus、Redis、Jwt
        </div>
      </div>
      <div class="row">
        <div class="lebal">个人总结</div>
        <div class="text" style="font-size: 12px">独自开发一个前后端项目，受益匪浅。只有自己亲自动手敲代码，进行调试，才会发现，自己的进步，还有不足。才能知道自己，还需学什么，才会更有动力的进行学习<br>加油！学无止境，越敲越强！</div>
      </div>
    </div>
  </div>
</template>

<script>
import { getImage } from '@/utils/resources.js'
export default {
  name: 'About-Us',
  methods: {
    // 返回按钮
    goBack() {
      this.$router.back()
    },
    setUrl(name) {
      return getImage(name)
    }
  }
}
</script>

<style lang="less" scoped>
.container{
  .content{
    padding: 0 5px;
    margin-top: -5px;
    background-color: rgba(255, 255, 255, 0.8);
    .row{
      display: flex;
      width: 100%;
      line-height: 30px;
      .lebal{
        flex: 1;
      }
      .text{
        flex: 3;
      }
    }
  }
}
</style>

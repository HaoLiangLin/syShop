<template>
  <div class="containerx">
    <el-container>
      <el-aside :width="width">
        <div class="title" v-if="!isCollapse">
          <el-image
            style="width: 32px; height: 32px"
            :src="require('@/assets/syLogo.png')"
            fit="fit"></el-image>
          <span>水院商城后台</span>
        </div>
        <div class="image" v-if="isCollapse">
          <el-image
            style="width: 32px; height: 32px"
            :src="require('@/assets/syLogo.png')"
            fit="fit"></el-image>
        </div>
        <div class="menu">
          <el-menu
            class="el-menu-vertical-demo"
            :default-active="selectIndex"
            @select="onSelect"
            :collapse="isCollapse"
            :collapse-transition="false"
            :unique-opened="true"
            :router="true">
            <el-menu-item index="/home/home">
              <i class="el-icon-s-home"></i>
              <span slot="title">主页</span>
            </el-menu-item>
            <el-submenu index="2">
              <template #title>
                <i class="el-icon-user-solid"></i>
                <span slot="title">会员管理</span>
              </template>
              <el-menu-item-group>
                <el-menu-item index="/home/user">查询用户</el-menu-item>
                <el-menu-item index="/home/userList">用户列表</el-menu-item>
              </el-menu-item-group>
            </el-submenu>
            <el-submenu index="3">
              <template #title>
                <i class="el-icon-s-goods"></i>
                <span slot="title">商品管理</span>
              </template>
              <el-menu-item-group>
                <el-menu-item index="/home/goodsCategory">商品分类</el-menu-item>
                <el-menu-item index="/home/goods">查询商品</el-menu-item>
                <el-menu-item index="/home/goodsList">商品列表</el-menu-item>
                <el-menu-item index="/home/saveGoods">新增商品</el-menu-item>
              </el-menu-item-group>
            </el-submenu>
            <el-submenu index="4">
              <template #title>
                <i class="el-icon-s-order"></i>
                <span slot="title">订单管理</span>
              </template>
              <el-menu-item-group>
                <el-menu-item index="/home/3-1">查询订单</el-menu-item>
                <el-menu-item index="/home/orderList">订单列表</el-menu-item>
                <el-menu-item index="/home/3-2">订单设置</el-menu-item>
                <el-menu-item index="/home/3-3">退货申请处理</el-menu-item>
                <el-menu-item index="/home/3-4">退货原因设置</el-menu-item>
              </el-menu-item-group>
            </el-submenu>
            <el-submenu index="5">
              <template #title>
                <i class="el-icon-s-tools"></i>
                <span slot="title">系统管理</span>
              </template>
              <el-menu-item-group>
                <el-menu-item index="/home/5-1">公告类型</el-menu-item>
                <el-menu-item index="/home/5-2">公告列表</el-menu-item>
                <el-menu-item index="/home/5-3">活动列表</el-menu-item>
                <el-menu-item index="/home/5-4">公告商品</el-menu-item>
                <el-menu-item index="/home/5-5">充值套餐</el-menu-item>
              </el-menu-item-group>
            </el-submenu>
          </el-menu>
        </div>
      </el-aside>
      <el-main>
        <div class="head">
          <div class="func">
            <i class="el-icon-s-fold" v-if="!isCollapse" @click="onCollapse"></i>
            <i class="el-icon-s-unfold" v-if="isCollapse" @click="onCollapse"></i>
            <i class="el-icon-refresh-right" @click="onInit"></i>
          </div>
          <div class="user">
            <el-button icon="el-icon-switch-button" circle @click="dialogVisible = true"></el-button>
            <span>{{username}}</span>
          </div>
        </div>
        <div class="content">
          <router-view/>
        </div>
      </el-main>
    </el-container>
    <el-dialog
      title="提示"
      :visible="dialogVisible"
      width="30%"
      :show-close="false"
      :before-close="handleClose">
      <span>确认登出？</span>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="onLogOut">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { me, logout } from '@/api/user.js'
export default {
  name: 'Home',
  data() {
    return {
      width: '300px',
      isCollapse: false,
      username: '',
      dialogVisible: false,
      selectIndex: sessionStorage.getItem('selectIndex')
    }
  },
  methods: {
    onCollapse() {
      this.isCollapse = !this.isCollapse
      if (this.isCollapse) {
        this.width = '65px'
      } else {
        this.width = '300px'
      }
    },
    onInit() {
      this.$bus.$emit('init', 1)
    },
    handleClose(done) {
      this.$confirm('确认登出？')
        .then(_ => {
          done()
        })
        .catch(_ => {})
    },
    onSelect(index) {
      sessionStorage.setItem('selectIndex', index)
      setTimeout(() => {
        this.selectIndex = index
      }, 200)
    },
    onLogOut() {
      logout().then(res => {
        const data = res.data
        if (data.success) {
          this.dialogVisible = false
          sessionStorage.removeItem('authorization')
          this.$notify({
            title: '成功',
            message: '退出登录成功',
            type: 'success'
          })
          this.$router.replace('/login')
        }
      })
    }
  },
  created() {
    me().then(res => {
      const data = res.data
      if (data.success) {
        this.username = data.data.username
        this.$notify({
          title: '欢迎',
          message: `${this.username}，欢迎登录水院商城后台`,
          type: 'success'
        })
      } else {
        this.$router.replace('/login')
      }
    }).catch(err => {
      this.$notify.error({
        title: '错误',
        message: err.message
      })
    })
  }
}
</script>

<style lang="less" scoped>
.containerx{
  height: 100vh;
  /deep/.el-container{
    height: 100%;
    .el-aside{
      background-color: #fff;
      .title{
        padding: 15px 0;
        padding-left: 20px;
        border: 1px solid rgba(0, 0, 0, 0.1);
        display: flex;
        align-items: center;

        span{
          padding-left: 10px;
          font-size: 20px;
        }
      }
      .image{
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 10px 0;
      }
    }
    .el-main{
      padding: 8px 10px;
      .head{
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 14px;
        .func{
          i {
            font-size: 22px;
            color: rgb(99, 99, 99);

            &:hover{
              color: #1989FA;
            }

            &:first-child{
              padding-left: 10px;
              padding-right: 15px;
            }
          }
        }
        .user{
          span{
            padding: 5px;
            color: rgb(179, 179, 179);
          }
        }
      }
    }
  }
}
</style>

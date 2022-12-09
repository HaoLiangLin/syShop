<template>
  <div class="container">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item>会员管理</el-breadcrumb-item>
      <el-breadcrumb-item>查询用户</el-breadcrumb-item>
    </el-breadcrumb>
    <div class="func">
      <div class="input">
        <el-input placeholder="请输入检索内容" v-model="searchText" class="input-with-select">
        <el-select v-model="select" slot="prepend" placeholder="请选择">
          <el-option label="用户ID" value="1"></el-option>
          <el-option label="用户名" value="2"></el-option>
          <el-option label="手机号" value="3"></el-option>
        </el-select>
        <el-button slot="append" icon="el-icon-search" @click="onSearch"></el-button>
      </el-input>
      </div>
    </div>
    <div class="infoBox">
      <el-descriptions v-if="user" title="查询结果" direction="vertical" :column="3" border>
        <template slot="extra">
          <el-button type="primary" size="small" @click="onUpdate" v-if="!user.isUpdate">修改</el-button>
          <el-button type="warning" size="small" @click="onDelete" v-if="!user.isUpdate">删除</el-button>
          <el-button type="success" size="small" @click="onSave" v-if="user.isUpdate">保存</el-button>
          <el-button type="default" size="small" @click="onCancel" v-if="user.isUpdate">取消</el-button>
        </template>
        <el-descriptions-item label="用户头像">
          <el-image
          v-if="user"
          style="width: 100px; height: 100px"
          :src="setIcon(user.icon)"
          fit="fit"></el-image>
        </el-descriptions-item>
        <el-descriptions-item label="用户ID">{{user.id}}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{user.username}}</el-descriptions-item>
        <el-descriptions-item label="手机号">
          <div v-if="!user.isUpdate">{{user.phone}}</div>
          <el-input v-if="user.isUpdate" v-model="user.phone" placeholder="请输入手机号"></el-input>
        </el-descriptions-item>
        <el-descriptions-item label="昵称">
          <div v-if="!user.isUpdate">{{user.nickname}}</div>
          <el-input v-if="user.isUpdate" v-model="user.nickname" placeholder="请输入昵称"></el-input>
        </el-descriptions-item>
        <el-descriptions-item label="账号状态">
          <div v-if="!user.isUpdate">
              <el-tag type="success" v-if="user.status">正常</el-tag>
              <el-tag type="info" v-else-if="user.isDel === 1">注销</el-tag>
              <el-tag type="danger" v-else-if="!user.status">停用</el-tag>
            </div>
            <div v-if="user.isUpdate">
              <el-switch
                v-model="user.status"
                active-color="#13ce66"
                inactive-color="#ff4949">
              </el-switch>
            </div>
        </el-descriptions-item>
        <el-descriptions-item label="注册时间">{{user.createTime}}</el-descriptions-item>
      </el-descriptions>
      <el-result title="404" v-else subTitle="查无记录哦">
        <template slot="icon">
          <el-image :src="require('@/assets/暂无记录.png')"></el-image>
        </template>
      </el-result>
    </div>
  </div>
</template>

<script>
import { queryUser, deleteUser, updateUser } from '@/api/user.js'
import { getImage } from '@/utils/resources.js'
export default {
  name: 'User',
  data() {
    return {
      searchText: '',
      select: '1',
      user: null
    }
  },
  methods: {
    setUser(res) {
      const data = res.data
      if (data.success) {
        const item = data.data[0]
        if (item) {
          let status = true
          if (item.status === 1) {
            status = false
          }
          this.user = {
            id: item.id,
            username: item.username,
            phone: item.phone,
            icon: item.icon,
            nickname: item.nickname,
            createTime: item.createTime,
            status,
            isUpdateUsername: item.isUpdate,
            isDel: item.isDel,
            isUpdate: false
          }
        } else {
          this.user = null
        }
      }
    },
    setIcon(icon) {
      return getImage(icon)
    },
    onSearch() {
      if (this.select === '1') {
        queryUser(this.searchText, null, null, null).then(res => {
          this.setUser(res)
        })
      }
      if (this.select === '2') {
        queryUser(null, this.searchText, null, null).then(res => {
          this.setUser(res)
        })
      }
      if (this.select === '3') {
        queryUser(null, null, this.searchText, null).then(res => {
          this.setUser(res)
        })
      }
    },
    onUpdate() {
      this.user.isUpdate = true
    },
    onDelete() {
      this.$confirm('此操作将删除该用户, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteUser(this.user.id).then(res => {
          const data = res.data
          if (data.success) {
            this.$notify({
              title: '成功',
              message: data.message,
              type: 'success'
            })
            this.user = null
          } else {
            this.$notify({
              title: '失败',
              message: data.message,
              type: 'warning'
            })
          }
        }).catch(err => {
          this.$message.error(err.message)
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        })
      })
    },
    onSave() {
      this.user.isUpdate = false
      updateUser(this.user.id, this.user.phone, this.user.nickname, this.user.status ? 0 : 1).then(res => {
        const data = res.data
        if (data.success) {
          this.$notify({
            title: '成功',
            message: data.message,
            type: 'success'
          })
        } else {
          this.$notify({
            title: '失败',
            message: data.message,
            type: 'warning'
          })
        }
      }).catch(err => {
        this.$message.error(err.message)
      })
    },
    onCancel() {
      this.user.isUpdate = false
    }
  },
  mounted() {
    this.$bus.$on('init', (val) => {
      if (val) {
        this.searchText = ''
        this.user = null
      }
    })
  }
}
</script>

<style lang="less" scoped>
.container{
  /deep/.el-breadcrumb{
    margin-bottom: 10px;
  }
  .func{
    margin-top: 10px;
    /deep/.el-select{
      width: 100px;
    }
  }
  .infoBox{
    margin-top: 10px;
  }
}
</style>

<template>
  <div class="container">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item>会员管理</el-breadcrumb-item>
      <el-breadcrumb-item>用户列表</el-breadcrumb-item>
    </el-breadcrumb>
    <div class="func">
      <table class="input">
        <tr>
          <td>
            <el-input
              size="small"
              placeholder="用户ID"
              v-model="userId">
            </el-input>
          </td>
          <td>
            <el-input
              size="small"
              placeholder="用户名"
              v-model="username">
            </el-input>
          </td>
          <td>
            <el-input
              size="small"
              placeholder="手机号"
              v-model="phone">
            </el-input>
          </td>
          <td>
            <el-select v-model="status" placeholder="请选择" size="small">
              <el-option
                v-for="item in statusOption"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </td>
        </tr>
      </table>
      <div class="btn">
        <el-button type="success" icon="el-icon-search" size="small" @click="onSearch">搜索</el-button>
      </div>
    </div>
    <div class="table">
      <el-table
        :data="userData"
        stripe
        :border="true"
        style="width: 100%">
        <el-table-column
          prop="id"
          label="ID"
          width="180">
        </el-table-column>
        <el-table-column
          prop="username"
          label="用户名"
          width="180">
        </el-table-column>
        <el-table-column
          label="手机号">
          <template slot-scope="scope">
            <div v-if="!scope.row.isUpdate">{{scope.row.phone}}</div>
            <el-input v-if="scope.row.isUpdate" v-model="scope.row.phone" placeholder="请输入手机号"></el-input>
          </template>
        </el-table-column>
        <el-table-column
          prop="icon"
          label="头像"
          width="100px">
          <template slot-scope="scope">
            <el-image
              style="width: 75px; height: 75px"
              :src="setIcon(scope.row.icon)"
              fit="fit"></el-image>
          </template>
        </el-table-column>
        <el-table-column
          label="昵称">
          <template slot-scope="scope">
            <div v-if="!scope.row.isUpdate">{{scope.row.nickname}}</div>
            <el-input v-if="scope.row.isUpdate" v-model="scope.row.nickname" placeholder="请输入昵称"></el-input>
          </template>
        </el-table-column>
        <el-table-column
          prop="createTime"
          label="注册时间"
          width="100px">
        </el-table-column>
        <el-table-column
          prop="status"
          label="账号状态"
          width="80px">
          <template slot-scope="scope">
            <div v-if="!scope.row.isUpdate">
              <el-tag type="success" v-if="scope.row.status">正常</el-tag>
              <el-tag type="info" v-else-if="scope.row.isDel === 1">注销</el-tag>
              <el-tag type="danger" v-else-if="!scope.row.status">停用</el-tag>
            </div>
            <div  v-if="scope.row.isUpdate">
              <el-switch
                v-model="scope.row.status"
                active-color="#13ce66"
                inactive-color="#ff4949">
              </el-switch>
            </div>
          </template>
        </el-table-column>
        <el-table-column
          fixed="right"
          label="操作"
          width="120">
          <template slot-scope="scope">
            <el-button
              v-if="!scope.row.isUpdate"
              @click="onUpdate(scope.row)"
              type="text"
              size="small">
              修改
            </el-button>
            <el-button
              v-if="!scope.row.isUpdate"
              @click="onDelete(scope.row)"
              type="text"
              size="small">
              删除
            </el-button>
            <el-button
              v-if="scope.row.isUpdate"
              @click="onSave(scope.row)"
              type="text"
              size="small">
              保存
            </el-button>
            <el-button
              v-if="scope.row.isUpdate"
              @click="onCancel(scope.row)"
              type="text"
              size="small">
              取消
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div class="block">
      <el-pagination
        layout="total, prev, pager, next, jumper"
        background
        :total="total"
        :page-size="size"
        v-show="isHidePage"
        :current-page="current"
        @current-change="onChangePage">
      </el-pagination>
    </div>
  </div>
</template>

<script>
import { queryUserListPage, updateUser, deleteUser } from '@/api/user.js'
import { getImage } from '@/utils/resources.js'
export default {
  name: 'UserList',
  data() {
    return {
      size: 5,
      userId: null,
      username: null,
      phone: null,
      status: 0,
      isSearch: false,
      statusOption: [{ value: 0, label: '正常' }, { value: 1, label: '禁用' }],
      select: '1',
      userData: [],
      isHidePage: true,
      total: 0,
      current: 1
    }
  },
  methods: {
    initData() {
      this.userId = null
      this.username = null
      this.phone = null
      this.status = 0
      this.isSearch = false
      queryUserListPage(1, this.size, null, null, null, null).then(res => {
        const data = res.data
        if (data.success) {
          data.data.records.forEach(item => {
            let status = true
            if (item.status === 1) {
              status = false
            }
            this.userData.push({
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
            })
          })
          this.total = data.data.total
          this.current = data.data.current
        } else {
          this.$notify({
            title: '失败',
            message: data.message,
            type: 'warning'
          })
        }
      })
    },
    setIcon(icon) {
      return getImage(icon)
    },
    setUserDate(res) {
      const data = res.data
      if (data.success) {
        this.userData = []
        data.data.records.forEach(item => {
          let status = true
          if (item.status === 1) {
            status = false
          }
          this.userData.push({
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
          })
        })
        this.total = data.data.total
        this.current = data.data.current
      } else {
        this.$notify({
          title: '失败',
          message: data.message,
          type: 'warning'
        })
      }
    },
    onSearch() {
      this.isSearch = true
      queryUserListPage(1, this.size, this.userId, this.username, this.phone, this.status).then(res => {
        this.setUserDate(res)
      })
    },
    onUpdate(row) {
      row.isUpdate = true
    },
    onDelete(row) {
      this.$confirm('此操作将删除该用户, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteUser(row.id).then(res => {
          const data = res.data
          if (data.success) {
            this.userData.forEach((item, index) => {
              if (item.id === row.id) {
                this.userData.splice(index, 1)
              }
            })
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
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        })
      })
    },
    onSave(row) {
      row.isUpdate = false
      updateUser(row.id, row.phone, row.nickname, row.status ? 0 : 1).then(res => {
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
    onCancel(row) {
      row.isUpdate = false
    },
    onChangePage(page) {
      if (this.isSearch) {
        queryUserListPage(page, this.size, this.userId, this.username, this.phone, this.status).then(res => {
          this.setUserDate(res)
        })
        return
      }
      queryUserListPage(page, this.size, null, null, null, null).then(res => {
        this.setUserDate(res)
      })
    }
  },
  created() {
    this.initData()
  },
  mounted() {
    this.$bus.$on('init', (val) => {
      if (val) {
        this.userData = []
        this.initData()
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
    margin-bottom: 10px;
    display: flex;
    .input{
      width: 80%;
    }
    .btn{
      width: 20%;
    }
  }
}
</style>

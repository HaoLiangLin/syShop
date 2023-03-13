<template>
  <div class="container">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item>用户管理</el-breadcrumb-item>
      <el-breadcrumb-item>用户列表</el-breadcrumb-item>
    </el-breadcrumb>
    <div class="func">
      <table class="input">
        <tr>
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
            <el-select v-model="userType" clearable v-if="isSystem"  placeholder="用户类型" size="small">
              <el-option
                v-for="item in userTypeOption"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </td>
          <td>
            <el-select v-model="createTimeSort" clearable  placeholder="创建时间排序" size="small">
              <el-option
                v-for="item in sortOrder"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </td>
          <td>
            <el-select v-model="lastLoginSort" clearable  placeholder="最后登录排序" size="small">
              <el-option
                v-for="item in sortOrder"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </td>
          <td>
            <el-select v-model="status" clearable  placeholder="账号状态" size="small">
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
        :border="true">
        <el-table-column
          prop="id"
          label="ID"
          width="180">
        </el-table-column>
        <el-table-column
          prop="username"
          label="用户名">
        </el-table-column>
        <el-table-column
          label="手机号"
          prop="phone">
        </el-table-column>
        <el-table-column
          prop="icon"
          label="头像"
          width="98px">
          <template slot-scope="scope">
            <el-image
              style="width: 75px; height: 75px"
              :src="setIcon(scope.row.icon)"
              fit="fit"
              :previewSrcList="setpreviewSrc(scope.row.icon)"></el-image>
          </template>
        </el-table-column>
        <el-table-column
          label="昵称"
          prop="nickname">
        </el-table-column>
        <el-table-column
          prop="createTime"
          label="注册时间"
          width="100">
        </el-table-column>
        <el-table-column
          prop="lastLogin"
          label="最后登录"
          width="100">
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
          label="操作">
          <template slot-scope="scope">
            <el-upload
              v-if="isSystem"
              :action="setUploadAction(scope.row.id)"
              :multiple="false"
              :limit="1"
              :show-file-list="false"
              accept="image/*"
              :headers="{authorization}"
              :on-success="onUploadSuccess"
              :on-error="onUploadError">
              <el-button size="small" type="text">上传或修改头像</el-button>
            </el-upload>
            <el-button
              v-if="!scope.row.isUpdate"
              @click="onUpdate(scope.row)"
              type="text"
              size="small">
              修改
            </el-button>
            <el-button
              v-if="!scope.row.isUpdate"
              @click="onMore(scope.row)"
              type="text"
              size="small">
              更多
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
    <el-dialog
      title="更多信息"
      :visible.sync="userMoreVisible"
      :before-close="onDialogClose"
      width="65%">
      <el-descriptions title="账号信息" :column="4" size="" v-if="!Object.keys(user).length==0">
        <template slot="extra">
          <el-button type="warning" size="mini" @click="onRemoveIcon(user.id)">删除用户头像</el-button>
          <el-button type="primary" size="mini" @click="onUpdateUser(user)">修改账号信息</el-button>
        </template>
        <el-descriptions-item label="用户ID">{{user.id}}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{user.username}}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{user.phone}}</el-descriptions-item>
        <el-descriptions-item label="用户身份">
          <el-tag size="small">{{user.userType === 0 ? '管理员' : '用户'}}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="昵称">{{user.nickname}}</el-descriptions-item>
        <el-descriptions-item label="注册时间">{{user.createTime}}</el-descriptions-item>
        <el-descriptions-item label="登录时间">{{user.lastLogin}}</el-descriptions-item>
        <el-descriptions-item label="账号状态">
          <el-tag type="success" v-if="user.status">正常</el-tag>
          <el-tag type="info" v-else-if="user.isDel === 1">注销</el-tag>
          <el-tag type="danger" v-else-if="!user.status">停用</el-tag>
        </el-descriptions-item>
      </el-descriptions>
      <el-descriptions title="用户信息" :column="4" size="" v-if="!Object.keys(userInfo).length==0" border>
        <!-- <template slot="extra">
          <el-button type="primary" size="small">操作</el-button>
        </template> -->
        <el-descriptions-item label="名称">{{userInfo.fullName}}</el-descriptions-item>
        <el-descriptions-item label="性别">{{userInfo.gender}}</el-descriptions-item>
        <el-descriptions-item label="年龄">{{userInfo.age}}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{userInfo.email}}</el-descriptions-item>
        <el-descriptions-item label="生日">{{userInfo.birthday}}</el-descriptions-item>
        <el-descriptions-item label="QQ">{{userInfo.qq}}</el-descriptions-item>
        <el-descriptions-item label="等级">{{userInfo.level}}</el-descriptions-item>
      </el-descriptions>
      <el-descriptions title="登录信息" :column="4" size="" v-if="!Object.keys(loginInfo).length==0" border>
        <el-descriptions-item label="登录系统">{{loginInfo.systemName}}</el-descriptions-item>
        <el-descriptions-item label="登录浏览器">{{loginInfo.browserName}}</el-descriptions-item>
        <el-descriptions-item label="浏览器类型">{{loginInfo.browserType}}</el-descriptions-item>
        <el-descriptions-item label="浏览器版本">{{loginInfo.browserVersion}}</el-descriptions-item>
        <el-descriptions-item label="登录时间">{{loginInfo.time}}</el-descriptions-item>
        <el-descriptions-item label="当前状态">
          <el-tag type="success">在线</el-tag>
        </el-descriptions-item>
      </el-descriptions>
      <el-alert
        v-if="!Object.keys(blockUpInfo).length==0"
        title="禁用告警"
        type="error"
        :description="blockUpInfo.message + ',禁用时长' + (blockUpInfo.expire + 1) + blockUpInfo.timeUnit"
        show-icon
        close-text="解除禁用"
        @close="onCancelBlockUp(user.id)">
      </el-alert>
      <div class="funBtn">
        <el-button type="warning" v-if="!Object.keys(loginInfo).length==0" @click="onCoerceLogout(user.id, loginInfo.token)">强制下线</el-button>
        <el-button type="danger" v-if="Object.keys(blockUpInfo).length==0" @click="onBlockUp(user.id)">禁用用户</el-button>
      </div>
    </el-dialog>
    <!-- 禁用用户弹窗 -->
    <el-dialog
      title="禁用用户"
      :visible.sync="blockUpVisible"
      width="20%"
      :before-close="onBlockUpClose">
      <div class="blockUpBox">
        <span>禁用时长(分钟)</span><br/>
        <el-input-number v-model="blockUpExpire" :min="1"  label="禁用时长"></el-input-number>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="blockUpVisible = false">取 消</el-button>
        <el-button type="primary" @click="onBlockUpUser">确 定</el-button>
      </span>
    </el-dialog>
    <!-- 修改用户弹窗 -->
    <el-dialog
      title="修改用户"
      :visible.sync="updateUserVisible"
      width="30%"
      :before-close="onUpdateUserClose">
      <el-form :model="updateUser">
        <el-form-item label="用户编号" :label-width="formLabelWidth">
          <el-input v-model="updateUser.id" :disabled="true"></el-input>
        </el-form-item>
        <el-form-item label="用户名" :label-width="formLabelWidth">
          <el-input v-model="updateUser.username"></el-input>
        </el-form-item>
        <el-form-item label="手机号" :label-width="formLabelWidth">
          <el-input v-model="updateUser.phone"></el-input>
        </el-form-item>
        <el-form-item label="用户昵称" :label-width="formLabelWidth">
          <el-input v-model="updateUser.nickname" :disabled="!isSystem"></el-input>
        </el-form-item>
        <el-form-item label="用户类型">
            <el-select v-model="updateUser.userType" clearable placeholder="用户类型" size="small" :disabled="!isSystem">
              <el-option
                v-for="item in userTypeOption"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
        <el-form-item label="账号状态" :label-width="formLabelWidth">
          <el-switch
            v-model="updateUser.status"
            active-color="#13ce66"
            inactive-color="#ff4949"
            active-text="正常"
            inactive-text="停用">
          </el-switch>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="updateUserVisible = false">取 消</el-button>
        <el-button type="primary" @click="onUpdateUser2">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { findUserListForPage, findSystemUserListForPage, updateUser, updateSytsemUser, queryUserById, coerceLogout, blockUpUser, cancelBlockUpUser, uploadOrUpdateUserIcon as removeUserIcon } from '@/api/user.js'
import { getImage, uploadOrUpdateUserIcon } from '@/utils/resources.js'
export default {
  name: 'UserList',
  data() {
    return {
      authorization: window.sessionStorage.getItem('authorization'),
      size: 5,
      username: null,
      phone: null,
      status: null,
      createTimeSort: null,
      lastLoginSort: null,
      isSearch: false, // 是否搜索
      isSystem: false, // 是否系统
      userTypeOption: [{ value: 0, label: '管理员' }, { value: 1, label: '用户' }],
      userType: null,
      statusOption: [{ value: 0, label: '正常' }, { value: 1, label: '停用' }],
      sortOrder: [{ value: 1, label: '升序' }, { value: 0, label: '降序' }],
      select: '1',
      userData: [],
      userMoreVisible: false,
      user: {}, // 用户账号信息
      updateUserVisible: false,
      updateUser: {},
      formLabelWidth: '70px',
      userInfo: {}, // 用户信息
      loginInfo: {}, // 登录信息
      blockUpInfo: {}, // 禁用信息
      blockUpVisible: false,
      blockUpUserId: null,
      blockUpExpire: 1,
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
      this.status = null
      this.createTimeSort = null
      this.lastLoginSort = null
      this.userMoreVisible = null
      this.user = {} // 用户账号信息
      this.userInfo = {} // 用户信息
      this.loginInfo = {} // 登录信息
      this.blockUpInfo = {} // 禁用信息
      this.blockUpUserId = null
      this.blockUpVisible = false
      this.isSearch = false
      findSystemUserListForPage(1, this.size, null, null, null, null, null, null, null).then(res => {
        const data = res.data
        if (data.code === 20011) {
          this.isSystem = true
          this.setUserDate(res)
        } else {
          findUserListForPage(1, this.size, null, null, null, null, null, null).then(res => {
            this.setUserDate(res)
          }).catch(err => {
            this.$notify.error({
              title: '错误',
              message: err.message
            })
          })
        }
      }).catch(err => {
        this.$notify.error({
          title: '错误',
          message: err.message
        })
      })
    },
    setIcon(icon) {
      return getImage(icon)
    },
    setpreviewSrc(icon) {
      return [getImage(icon)]
    },
    // ############上传图片（起始）###############
    setUploadAction(userId) {
      return uploadOrUpdateUserIcon(userId)
    },
    onUploadSuccess(res) {
      if (res.code === 20011) {
        this.$notify({
          title: '成功',
          message: res.message,
          type: 'success'
        })
      } else {
        this.$notify({
          title: '失败',
          message: res.message,
          type: 'warning'
        })
      }
    },
    onUploadError(err) {
      this.$notify.error({
        title: '错误',
        message: err.message
      })
    },
    // ############上传图片（截止）###############
    setUserDate(res) {
      const data = res.data
      if (data.code === 20011) {
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
            userType: item.userType,
            createTime: item.createTime,
            lastLogin: item.lastLogin,
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
        this.$router.back()
      }
    },
    onSearch() {
      this.isSearch = true
      if (this.isSystem) {
        findSystemUserListForPage(1, this.size, null, this.username, this.phone, this.userType, this.status, this.createTimeSort, this.lastLoginSort).then(res => {
          this.setUserDate(res)
        }).catch(err => {
          this.$notify.error({
            title: '错误',
            message: err.message
          })
        })
      } else {
        findUserListForPage(1, this.size, null, this.username, this.phone, this.status, this.createTimeSort, this.lastLoginSort).then(res => {
          this.setUserDate(res)
        }).catch(err => {
          this.$notify.error({
            title: '错误',
            message: err.message
          })
        })
      }
    },
    onUpdate(row) {
      row.isUpdate = true
    },
    onSave(row) {
      row.isUpdate = false
      if (this.isSystem) {
        updateSytsemUser(row.id, null, null, null, null, row.status ? 0 : 1).then(res => {
          const data = res.data
          if (data.code === 20011) {
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
          this.$notify.error({
            title: '错误',
            message: err.message
          })
        })
        return
      }
      updateUser(row.id, null, null, row.status ? 0 : 1).then(res => {
        const data = res.data
        if (data.code === 20011) {
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
        this.$notify.error({
          title: '错误',
          message: err.message
        })
      })
    },
    // 删除用户头像
    onRemoveIcon(userId) {
      removeUserIcon(userId, null).then(res => {
        const data = res.data
        if (data.code === 20011) {
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
        this.$notify.error({
          title: '错误',
          message: err.message
        })
      })
    },
    // 修改用户
    onUpdateUser(user) {
      this.updateUser = user
      this.updateUserVisible = true
    },
    onUpdateUser2() {
      let username = this.updateUser.username
      if (username === this.user.username) {
        username = null
      }
      let phone = this.updateUser.phone
      if (phone === this.user.phone) {
        phone = null
      }
      if (this.isSystem) {
        updateSytsemUser(this.updateUser.id, username, phone, this.updateUser.nickname, this.userType, this.updateUser.status ? 0 : 1).then(res => {
          const data = res.data
          if (data.code === 20011) {
            this.$notify({
              title: '成功',
              message: data.message,
              type: 'success'
            })
            this.user.id = this.updateUser.id
            this.user.username = this.updateUser.username
            this.user.phone = this.updateUser.phone
            this.user.nickname = this.updateUser.nickname
            this.user.status = this.updateUser.status
            this.updateUser = {}
            this.updateUserVisible = false
          } else {
            this.$notify({
              title: '失败',
              message: data.message,
              type: 'warning'
            })
          }
        }).catch(err => {
          this.$notify.error({
            title: '错误',
            message: err.message
          })
        })
        return
      }
      updateUser(this.updateUser.id, username, phone, this.updateUser.status ? 0 : 1).then(res => {
        const data = res.data
        if (data.code === 20011) {
          this.$notify({
            title: '成功',
            message: data.message,
            type: 'success'
          })
          this.user.id = this.updateUser.id
          this.user.username = this.updateUser.username
          this.user.phone = this.updateUser.phone
          this.user.status = this.updateUser.status
          this.updateUser = {}
          this.updateUserVisible = false
        } else {
          this.$notify({
            title: '失败',
            message: data.message,
            type: 'warning'
          })
        }
      }).catch(err => {
        this.$notify.error({
          title: '错误',
          message: err.message
        })
      })
    },
    onUpdateUserClose(done) {
      this.updateUser = {}
      done()
    },
    // 查看更多
    onMore(row) {
      queryUserById(row.id).then(res => {
        const data = res.data
        if (data.code === 20011) {
          const resultData = data.data
          if (resultData.userInfo != null) {
            this.userInfo = resultData.userInfo
          }
          if (resultData.loginInfo != null) {
            this.loginInfo = resultData.loginInfo
          }
          if (resultData.blockUpInfo != null) {
            this.blockUpInfo = resultData.blockUpInfo
          }
        }
      }).catch(err => {
        this.$notify.error({
          title: '错误',
          message: err.message
        })
      })
      this.userMoreVisible = true
      this.user = row
    },
    // 强制下线
    onCoerceLogout(id, token) {
      coerceLogout(id, token).then(res => {
        const data = res.data
        if (data.code === 20011) {
          this.$message({
            message: data.message,
            type: 'success'
          })
          this.loginInfo = {}
        } else {
          this.$message({
            message: data.message,
            type: 'warning'
          })
        }
      }).catch(err => {
        this.$notify.error({
          title: '错误',
          message: err.message
        })
      })
    },
    // 禁用用户
    onBlockUp(id) {
      this.blockUpExpire = 1
      this.blockUpVisible = true
      this.blockUpUserId = id
    },
    onBlockUpClose(done) {
      this.blockUpExpire = 1
      this.blockUpUserId = null
      done()
    },
    onBlockUpUser() {
      blockUpUser(this.blockUpUserId, this.blockUpExpire).then(res => {
        const data = res.data
        if (data.code === 20011) {
          this.$message({
            message: data.message,
            type: 'success'
          })
          queryUserById(this.blockUpUserId).then(res => {
            const data = res.data
            if (data.code === 20011) {
              const resultData = data.data
              if (resultData.userInfo != null) {
                this.userInfo = resultData.userInfo
              }
              if (resultData.loginInfo != null) {
                this.loginInfo = resultData.loginInfo
              }
              if (resultData.blockUpInfo != null) {
                this.blockUpInfo = resultData.blockUpInfo
              }
            }
            this.blockUpVisible = false
          }).catch(err => {
            this.$notify.error({
              title: '错误',
              message: err.message
            })
          })
          this.blockUpUserId = null
          this.blockUpExpire = 1
        } else {
          this.$message({
            message: data.message,
            type: 'warning'
          })
        }
      }).catch(err => {
        this.$notify.error({
          title: '错误',
          message: err.message
        })
      })
    },
    onCancelBlockUp(id) {
      cancelBlockUpUser(id).then(res => {
        const data = res.data
        if (data.code === 20011) {
          this.$message({
            message: data.message,
            type: 'success'
          })
          this.blockUpInfo = {}
        } else {
          this.$message({
            message: data.message,
            type: 'warning'
          })
        }
      }).catch(err => {
        this.$notify.error({
          title: '错误',
          message: err.message
        })
      })
    },
    onDialogClose(done) {
      this.user = {} // 用户账号信息
      this.userInfo = {} // 用户信息
      this.loginInfo = {} // 登录信息
      this.blockUpInfo = {} // 禁用信息
      done()
    },
    onCancel(row) {
      row.isUpdate = false
    },
    onChangePage(page) {
      if (this.isSearch) {
        if (this.isSystem) {
          findSystemUserListForPage(page, this.size, null, this.username, this.phone, this.userType, this.status, this.createTimeSort, this.lastLoginSort).then(res => {
            this.setUserDate(res)
          }).catch(err => {
            this.$notify.error({
              title: '错误',
              message: err.message
            })
          })
        } else {
          findUserListForPage(page, this.size, null, this.username, this.phone, this.status, this.createTimeSort, this.lastLoginSort).then(res => {
            this.setUserDate(res)
          }).catch(err => {
            this.$notify.error({
              title: '错误',
              message: err.message
            })
          })
        }
        return
      }
      if (this.isSystem) {
        findSystemUserListForPage(page, this.size, null, null, null, null, null, null, null).then(res => {
          this.setUserDate(res)
        }).catch(err => {
          this.$notify.error({
            title: '错误',
            message: err.message
          })
        })
      } else {
        findUserListForPage(page, this.size, null, null, null, null, null, null).then(res => {
          this.setUserDate(res)
        }).catch(err => {
          this.$notify.error({
            title: '错误',
            message: err.message
          })
        })
      }
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
  },
  beforeDestroy() {
    this.$bus.$off('init')
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
  /deep/.el-dialog{
    .funBtn {
      text-align: right;
      margin-top: 15px;
    }
    .blockUpBox{
      text-align: center;
    }
  }
}
</style>

<template>
  <div class="container">
    <div class="box">
      <div class="leftBox">
        <div class="countBox">
          <div class="title">个人信息</div>
          <div class="content" v-if="userInfo != null">
            <table>
              <tr>
                <td>用户名</td>
                <td>{{userInfo.username}}</td>
              </tr>
              <tr>
                <td>登录时间</td>
                <td>{{userInfo.lastLogin}}</td>
              </tr>
            </table>
          </div>
        </div>
        <div class="countBox">
          <div class="title">今日访问量</div>
          <div class="content">
            <div class="today">
              {{todayPv}}
            </div>
          </div>
        </div>
        <div class="countBox">
          <div class="title">昨日访问量</div>
          <div class="content">
            <div class="yesterday">
              {{yesterdayPv}}
            </div>
          </div>
        </div>
      </div>
      <div class="rigthBox">
        <div class="rCountBox">
          <div class="onlineCount" @click="loginUserVisible = true">
            <div class="content">
              {{loginUserCount.onlineNumber}}
            </div>
            <div class="title">今日在线用户</div>
          </div>
        </div>
        <div class="rCountBox">
          <div>
            <el-statistic group-separator="," :precision="2" :value="billCount.incomeAll" title="月收入">
              <template slot="prefix">
                <i class="el-icon-caret-top" style="color: green"></i>
              </template>
            </el-statistic>
          </div>
          <div>
            <el-statistic group-separator="," :precision="2" :value="billCount.disburseAll" title="月支出">
              <template slot="prefix">
                <i class="el-icon-caret-bottom" style="color: red"></i>
              </template>
            </el-statistic>
          </div>
        </div>
      </div>
    </div>
    <div>
      <PvCount/>
    </div>
    <div>
      <OrderCount/>
    </div>
    <el-dialog
      :title="`在线用户 (${loginUserCount.onlineNumber})`"
      :visible.sync="loginUserVisible"
      width="70%">
      <el-table
        :data="loginUserCount.onlineUsers"
        style="width: 100%"
        height="50vh">
        <el-table-column
          prop="id"
          label="用户ID"
          width="180">
        </el-table-column>
        <el-table-column
          prop="username"
          label="用户名"
          width="180">
        </el-table-column>
        <el-table-column
          prop="phone"
          label="联系电话">
        </el-table-column>
        <el-table-column
          label="昵称"
          prop="nickname">
        </el-table-column>
        <el-table-column
          prop="lastLogin"
          label="登录时间"
          width="100">
        </el-table-column>
        <el-table-column
          fixed="right"
          label="操作">
          <template slot-scope="scope">
            <el-button
              @click="onCoerceLogout(scope.row.id)"
              type="text"
              size="small"
              style="color:#F56C6C;">
              强制下线
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import { me, userPvCount, userLoginCount, queryUserById, coerceLogout } from '@/api/user.js'
import { billCount } from '@/api/system.js'
export default {
  name: 'Home-Home',
  data() {
    return {
      userInfo: {},
      todayPv: 0,
      yesterdayPv: 0,
      loginUserCount: {},
      loginUserVisible: false,
      billCount: {}
    }
  },
  components: {
    PvCount: () => import('@/components/home/UserCount.vue'),
    OrderCount: () => import('@/components/home/OrderCount.vue')
  },
  methods: {
    initData() {
      // 查询个人账号信息
      me().then(res => {
        const data = res.data
        if (data.code === 20011) {
          this.userInfo = data.data
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
      // 获取当前时间
      const date = new Date()
      // 获取当前年份
      const year = date.getFullYear()
      // 获取当前月份
      const month = date.getMonth()
      // 获取今年本月一号时间戳
      const nowYear = new Date(year, month, 1).getTime()
      // 当前时间戳
      const nowTime = date.getTime()
      // 一天的时间戳
      const oneDayTime = 24 * 60 * 60 * 1000
      // 今日用户访问
      userPvCount(nowTime, nowTime).then(res => {
        const data = res.data
        if (data.code === 20011) {
          this.todayPv = data.data.total
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
      // 昨日用户访问
      userPvCount(nowTime - oneDayTime, nowTime - oneDayTime).then(res => {
        const data = res.data
        if (data.code === 20011) {
          this.yesterdayPv = data.data.total
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
      // 在线用户统计
      userLoginCount().then(res => {
        const data = res.data
        if (data.code === 20011) {
          this.loginUserCount = data.data
          console.log('在线用户统计', this.loginUserCount)
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
      // 账单统计 今年本月至今的账单
      billCount(nowYear, nowTime).then(res => {
        const data = res.data
        if (data.code === 20011) {
          this.billCount = data.data
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
    // 强制下线
    onCoerceLogout(userId) {
      queryUserById(userId).then(res => {
        const data = res.data
        if (data.code === 20011) {
          const resultData = data.data
          if (resultData.loginInfo != null) {
            const loginInfo = resultData.loginInfo
            coerceLogout(userId, loginInfo.token).then(res => {
              const data = res.data
              if (data.code === 20011) {
                this.$message({
                  message: data.message,
                  type: 'success'
                })
                this.loginUserCount.onlineNumber--
                this.loginUserCount.onlineUsers.forEach((user, index) => {
                  if (user.id === userId) {
                    this.loginUserCount.onlineUsers.splice(index, 1)
                  }
                })
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
          }
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
    }
  },
  created() {
    // 初始化数据
    this.initData()
  },
  mounted() {
    this.$bus.$on('init', (val) => {
      if (val) {
        this.userInfo = {}
        this.todayPv = 0
        this.yesterdayPv = 0
        this.loginUserCount = {}
        this.billCount = {}
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
  width: 100%;
  .box{
    display: flex;
    height: 12vh;
    .leftBox{
      flex: 2;
      display: flex;
      margin: 0 25px;
      .countBox{
        .title{
          font-weight: bolder;
          color: #474747;
        }
        .content{
          padding: 10px;
          table{
            width: 100%;
            td{
              &:nth-child(1){
                width: 30%;
                font-size: 14px;
                text-align: right;
                color: #999999;
              }
              &:nth-child(2){
                width: 70%;
                font-size: 14px;
                font-weight: bold;
                padding-left: 5px;
                color: #FAC858;
              }
            }
          }
          .today{
            width: 100%;
            height: 100%;
            text-align: center;
            color: #91CC75;
            font-weight: bolder;
            font-size: 24px;
          }
          .yesterday{
            width: 100%;
            height: 100%;
            text-align: center;
            color: #5470C6;
            font-weight: bolder;
            font-size: 24px;
          }
        }
        flex: 1;
        border-bottom: 2px solid #FAC858;
        border-right: 2px solid #FAC858;
        margin: 0 2px;
        &:nth-child(2){
          border-bottom: 2px solid #91CC75;
          border-right: 2px solid #91CC75;
        }
        &:nth-child(3){
          border-bottom: 2px solid #5470C6;
          border-right: 2px solid #5470C6;
        }
      }
    }
    .rigthBox{
      flex: 1;
      display: flex;
      .rCountBox{
        flex: 1;
        .onlineCount{
          cursor: pointer;
          text-align: center;
          .content{
            color: #91CC75;
            font-size: 24px;
            padding: 10px;
          }
          .title{
            font-size: 12px;
            color:#777777;
          }
        }
        /deep/.el-statistic{
          display: flex;
          align-items: center;
          justify-content: center;
          .head{
            flex:1
          }
          .con{
            flex:2;
          }
        }
      }
    }
  }
}
</style>

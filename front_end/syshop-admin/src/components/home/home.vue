<template>
  <div class="container">
    <div class="box">
      <div class="leftBox">
        <div class="countBox">今日访问量</div>
        <div class="countBox">昨日访问量</div>
        <div class="countBox">
          管理员信息
        </div>
      </div>
      <div class="rigthBox">
        <div class="rCountBox">

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
  </div>
</template>

<script>
import { me } from '@/api/user.js'
import { billCount } from '@/api/system.js'
export default {
  name: 'Home-Home',
  data() {
    return {
      userInfo: {},
      registerUserCount: {},
      unfilledOrder: {},
      orderCount: {},
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
          console.log('管理员账号信息', data.data)
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
      // 账单统计 今年本月至今的账单
      billCount(nowYear, nowTime).then(res => {
        const data = res.data
        if (data.code === 20011) {
          console.log('本月账单统计', data.data)
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
    }
  },
  created() {
    // 初始化数据
    this.initData()
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
      .countBox{
        flex: 1;
      }
    }
    .rigthBox{
      flex: 1;
      display: flex;
      .rCountBox{
        flex: 1;
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

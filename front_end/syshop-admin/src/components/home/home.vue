<template>
  <div class="container">
    <div class="box">
      <div class="leftBox">
        <div>日访问量</div>
        <div>月盈利</div>
        <div>月收入支出</div>
      </div>
      <div class="rigthBox">
        管理员信息
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
import { queryOrder } from '@/api/order.js'
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
      // 获取今年一月一号时间戳
      const nowYear = new Date(year, 0, 1).getTime()
      // 当前时间戳
      const nowTime = date.getTime()
      // 查询待发货订单
      queryOrder(1, 5, null, null, 1, 0, 0, 'Asc', null).then(res => {
        const data = res.data
        if (data.code === 20011) {
          // console.log('待发货订单', data.data)
          this.unfilledOrder = data.data
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
      // 账单统计 今年一月至今的账单
      billCount(nowYear, nowTime).then(res => {
        const data = res.data
        if (data.code === 20011) {
          // console.log('账单统计', data.data)
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
    .leftBox{
      flex: 2;
    }
    .rigthBox{
      flex: 1;
    }
  }
}
</style>

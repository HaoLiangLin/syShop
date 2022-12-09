<template>
  <div class="container">
    <van-nav-bar
        title="账单"
        left-arrow
        :fixed="true"
        :placeholder="true"
        @click-left="goBack"
      />
    <div class="dateBtn">
      <van-button type="default" round @click="onShowPopup">
        {{year}}年{{month}}月
        <van-icon name="arrow-down" />
      </van-button>
    </div>
    <div class="billBox">
      <div class="billInfo" v-for="(bills, index) in billList" :key="index">
        <div class="title">
          {{bills.date}}
        </div>
        <div class="bill" v-for="(bill, index) in bills.bill" :key="index">
          <div class="info">
            <div class="type">{{bill.type}}</div>
            <div class="time">{{bill.time}}</div>
          </div>
          <div class="amount">
            {{bill.amount}}
          </div>
        </div>
      </div>
    </div>

    <van-popup v-model="show" position="bottom" :style="{ height: '40%' }">
      <van-datetime-picker
        v-model="selectData"
        type="year-month"
        title="选择年月"
        :min-date="minDate"
        :max-date="currentDate"
        @cancel="onCalcel"
        @confirm="onConfirm"
      />
    </van-popup>
  </div>
</template>

<script>
import { me, queryBill } from '@/api/user.js'
export default {
  name: 'Furse-Bill',
  data() {
    return {
      year: '1997',
      month: '1',
      billList: [
        {
          date: '', bill: []
        }
      ],
      show: false,
      minDate: null,
      currentDate: null,
      selectData: null
    }
  },
  methods: {
    goBack() {
      this.$router.back()
    },
    onShowPopup() {
      this.show = true
    },
    onCalcel() {
      this.show = false
    },
    onConfirm(time) {
      const year = time.getFullYear()
      const month = time.getMonth() + 1
      this.year = year
      this.month = month < 10 ? `0${month}` : month

      queryBill(year, month, null).then(res => {
        const data = res.data
        if (data.success) {
          this.billList = []
          if (data.data) {
            const bills = data.data
            bills.forEach(bill => {
              this.billList.push({
                date: bill.date,
                bill: bill.bill
              })
            })
          }
        }
      }).catch(err => {
        this.$notify({ type: 'danger', message: err.message })
      })
    }
  },
  created() {
    me().then(res => {
      const data = res.data
      if (data.success) {
        this.minDate = new Date(data.data.createTime)
        this.currentDate = new Date()
        this.selectData = new Date()

        const now = new Date()
        const month = now.getMonth() + 1

        this.year = now.getFullYear()
        this.month = month < 10 ? `0${month}` : month

        queryBill(this.year, this.month, null).then(res => {
          const data = res.data
          if (data.success) {
            this.billList = []
            const bills = data.data
            bills.forEach(bill => {
              this.billList.push({
                date: bill.date,
                bill: bill.bill
              })
            })
          }
        })
      }
    }).catch(err => {
      this.$notify({ type: 'danger', message: err.message })
    })
  }
}
</script>

<style lang="less" scoped>
.container{
  .dateBtn{
    padding: 10px;
    text-align: right;
    /deep/.van-button{
      height: 30px;
    }
  }
  .billBox{
    background-color: #fff;
    margin: 0 10px;
    min-height: 85vh;
    .billInfo{
      padding: 10px;
      .title{
        font-size: 20px;
        border-bottom: 1px solid #ccc;
        padding: 5px 0;
        margin-bottom: 5px;
      }
      .bill{
        display: flex;
        margin-bottom: 10px;
        padding: 5px 0;
        .info{
          flex: 2;
          .type{
            font-size: 18px;
          }
          .time{
            font-size: 14px;
          }
        }
        .amount{
          flex: 1;
          text-align: right;
          font-size: 20px;
          color: red;
        }

        &:last-child{
          margin-bottom: 0px;
        }
      }
    }
  }
}
</style>

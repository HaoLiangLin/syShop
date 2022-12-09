<template>
  <div class="containerx">
    <div class="head" :style="'background-image: url('+ require('@/assets/purse.jpg')+')'">
      <van-nav-bar
        title="我的钱包"
        left-arrow
        right-text="账单"
        :fixed="true"
        :placeholder="true"
        @click-left="goBack"
        @click-right="onBill"
      />
      <div class="purseBox">
        <div class="balance" @click="onBalance">
          <div class="text">钱包余额</div>
          <div class="content">￥{{balance}}</div>
        </div>
        <div class="points">
          <div class="text">可用积分</div>
          <div class="content">{{points}}</div>
        </div>
      </div>
      <div class="infoBox">
        <div class="purseInfo">
          <div class="text">累计充值 <span>￥{{recharge}}</span></div>
          <div class="text">累计消费 <span>￥{{spending}}</span></div>
        </div>
        <div class="pointsInfo">
          <div class="signIn">
            <van-button round type="info" @click="onSignIn" :disabled="isSignIn">{{isSignIn ? '今日已签到' : '签到送10积分'}}</van-button>
          </div>
          <div class="signInCount">已连续签到{{signInCount}}天</div>
        </div>
      </div>
    </div>
    <div class="billBox">
      <van-tabs v-model="active">
        <van-tab title="今日流水">
          <div class="content">
            <div class="bill" v-for="(bill, index) in billList" :key="index">
              <div class="info">
                <div class="type">{{bill.type}}</div>
                <div class="time">{{bill.time}}</div>
              </div>
              <div class="amount">
                {{bill.amount}}
              </div>
            </div>
          </div>
        </van-tab>
        <van-tab title="充值">
          <div class="content">
            <div class="comboBox">
              <div
                class="combo"
                v-for="combo in comboList"
                :key="combo.id"
                :style="combo.isOption ? 'border: 2px solid #ff4d00;' : ''"
                @click="onCombo(combo)">
                <div class="name">{{combo.name}}</div>
                <div class="price">售价：{{combo.price}}元</div>
              </div>
              <div class="diyCombo" :style="isDiy ? 'border: 2px solid #ff4d00;' : ''" @click="onDiy">
                自定义
              </div>
            </div>
            <van-field v-if="isDiy" v-model="amount" label="充值金额" />
            <van-button type="primary" @click="onRecharge" block>充值</van-button>
          </div>
        </van-tab>
      </van-tabs>
    </div>
  </div>
</template>

<script>
import { signIn, signInToday, signInCount, queryAccount, queryBill, recharge } from '@/api/user.js'
import { queryRechargeCombo } from '@/api/system.js'
export default {
  name: 'Purse',
  data() {
    return {
      balance: 0,
      points: 0,
      recharge: 0,
      spending: 0,
      isSignIn: false,
      signInCount: 0,
      active: 0,
      billList: [],
      comboList: [],
      rechargeComboId: '',
      isDiy: false,
      amount: 0
    }
  },
  methods: {
    goBack() {
      this.$router.back()
    },
    init() {
      queryAccount().then(res => {
        const data = res.data
        if (data.success) {
          this.balance = data.data.balance
          this.points = data.data.points
          this.recharge = data.data.recharge
          this.spending = data.data.spending
        }
      }).catch(err => {
        this.$notify({ type: 'danger', message: err.message })
      })
      signInToday().then(res => {
        const data = res.data
        if (data.success) {
          this.isSignIn = true
        }
      })
      signInCount().then(res => {
        const data = res.data
        if (data.success) {
          this.signInCount = data.data
        }
      })
      const date = new Date()
      queryBill(date.getFullYear(), date.getMonth() + 1 > 9 ? date.getMonth() + 1 : `0${date.getMonth() + 1}`, date.getDate() > 9 ? date.getDate() : `0${date.getDate()}`).then(res => {
        const data = res.data
        if (data.success) {
          if (data.data) {
            this.billList = []
            const bills = data.data[0].bill
            bills.forEach(bill => {
              this.billList.push({
                type: bill.type,
                time: bill.time,
                amount: bill.amount
              })
            })
          }
        }
      })
    },
    onBill() {
      this.$router.push('/bill')
    },
    onSignIn() {
      signIn().then(res => {
        const data = res.data
        if (!data.success) {
          this.$notify({ type: 'warning', message: data.message })
        } else {
          this.$toast.success(data.message)
          this.init()
        }
      }).catch(err => {
        this.$notify({ type: 'danger', message: err.message })
      })
    },
    onBalance() {
      this.active = 1
    },
    onCombo(combo) {
      this.isDiy = false
      if (combo.isOption) {
        this.comboList.forEach(item => {
          item.isOption = false
        })
        return
      }
      this.comboList.forEach(item => {
        item.isOption = false
      })

      combo.isOption = !combo.isOption
      if (combo.isOption) {
        this.rechargeComboId = combo.id
      }
    },
    onDiy() {
      if (this.isDiy) {
        this.comboList.forEach(item => {
          item.isOption = false
          this.amount = 0
        })
        this.isDiy = false
        return
      }
      this.comboList.forEach(item => {
        item.isOption = false
      })
      this.isDiy = !this.isDiy
    },
    onRecharge() {
      if (this.isDiy) {
        recharge(null, this.amount).then(res => {
          const data = res.data
          if (!data.success) {
            this.$notify({ type: 'warning', message: data.message })
          } else {
            this.$notify({ type: 'success', message: data.message })
            this.init()
          }
        }).catch(err => {
          this.$notify({ type: 'danger', message: err.message })
        })
      } else {
        recharge(this.rechargeComboId, null).then(res => {
          const data = res.data
          if (!data.success) {
            this.$notify({ type: 'warning', message: data.message })
          } else {
            this.$notify({ type: 'success', message: data.message })
            this.init()
          }
        }).catch(err => {
          this.$notify({ type: 'danger', message: err.message })
        })
      }
    }
  },
  created() {
    this.init()
    queryRechargeCombo().then(res => {
      const data = res.data
      if (data.success) {
        this.comboList = []
        const combos = data.data
        combos.forEach(combo => {
          this.comboList.push({
            id: combo.id,
            name: combo.name,
            price: combo.price,
            points: combo.points,
            discount: combo.discount,
            isOption: false
          })
        })
      }
    })
  }
}
</script>

<style lang="less" scoped>
.containerx{
  .head{
    height: 38vh;
    background-size: cover;
    background-repeat: no-repeat;

    /deep/.van-nav-bar{
      span{
        color: black;
      }
    }
    .purseBox{
      margin-top: 60px;
      padding: 0 30px;
      display: flex;

      .balance,.points{
        flex: 1;
         text-align: center;
        .text{
          color: rgba(255, 255, 255, 0.7);
          font-size: 15px;
        }
        .content{
          font-size: 28px;
          color: orange;
        }
      }
    }
    .infoBox{
      margin-top: 20px;
      padding: 0 30px;
      padding-bottom: 20px;
      display: flex;
      .purseInfo{
        flex: 1;
        .text{
          color: rgba(255, 255, 255, 0.7);
          span{
            color: rgba(255, 255, 255, 0.9);
          }
        }
      }
      .pointsInfo{
        flex: 1;
        position: relative;
        top: -10px;
        .signIn{
          text-align: center;
          /deep/.van-button{
            height: 28px;
            padding: 0 10px;

            border: transparent;

            background-image: linear-gradient(to right, #F7971E 0%, #FFD200  51%, #F7971E  100%);
            text-align: center;
            text-transform: uppercase;
            transition: 0.5s;
            background-size: 200% auto;
            color: white;
            box-shadow: 0 0 20px #eee;
            border-radius: 10px;

            &:hover {
              background-position: right center; /* change the direction of the change here */
              color: #fff;
              text-decoration: none;
            }
          }
        }
        .signInCount{
          margin-top: 10px;
          text-align: center;
          font-size: 14px;
          color: #eee;
        }
      }
    }
  }
  .billBox{
    min-height: 62vh;
    margin: 0 10px;
    position: relative;
    top: -10px;
    background-color: #fff;
    box-shadow: 0 5px 5px 5px rgba(0, 0, 0, 0.1);
    .content{
      padding: 20px;
      .bill{
        display: flex;
        margin-bottom: 10px;
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
      .comboBox{
        width: 100%;
        display: flex;
        flex-wrap: wrap;
        justify-content: space-between;
        text-align: center;
        .combo{
          width: 48%;
          height: 60px;
          line-height: 30px;
          margin-bottom: 10px;
          border: 2px solid transparent;

          background-image: linear-gradient(to right, #1FA2FF 0%, #12D8FA  51%, #1FA2FF  100%);
          text-align: center;
          text-transform: uppercase;
          transition: 0.5s;
          background-size: 200% auto;
          color: white;
          border-radius: 5px;

          &:hover {
            background-position: right center; /* change the direction of the change here */
            color: #fff;
            text-decoration: none;
          }

          .name{
            font-size: 18px;
          }
          .price{
            font-size: 16px;
            color: orange;
          }
        }
        .diyCombo{
          width: 48%;
          height: 60px;
          line-height: 60px;
          margin-bottom: 10px;
          border: 2px solid transparent;

          background-image: linear-gradient(to right, #B993D6 0%, #8CA6DB  51%, #B993D6  100%);
          text-align: center;
          text-transform: uppercase;
          transition: 0.5s;
          background-size: 200% auto;
          color: white;
          box-shadow: 0 0 20px #eee;
          border-radius: 5px;

          &:hover {
            background-position: right center; /* change the direction of the change here */
            color: #fff;
            text-decoration: none;
          }
        }

        &::after{
          content: "";
          width: 48%;
        }
      }
      /deep/.van-field{
        border: 1px solid orange;
      }
      /deep/.van-button{
        margin-top: 15px;
        border-radius: 5px;
        font-size: 16px;
      }
    }
  }
}
</style>

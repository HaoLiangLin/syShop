<template>
  <div class="container">
    <van-nav-bar
      title="设置"
      left-arrow
      @click-left="onClickLeft"
      :fixed="true"
      :placeholder="true"
    />
    <div class="index-a">
      <van-cell-group>
        <van-cell is-link center v-if="jwt" :to="{name: 'userInfo'}">
          <template #title>
            <div class="title">
              <div class="headImg">
                <van-image :src="setIcon(icon)" alt="头像" height="50px" width="50px" :round="true"></van-image>
              </div>
              <div class="info">
                <div>{{nickname}}</div>
                <span>账户: {{username}}</span>
              </div>
            </div>
          </template>
        </van-cell>
        <van-cell title="我的收货地址" v-if="jwt" is-link :to="{name: 'address'}"/>
      </van-cell-group>
    </div>
    <div class="index-b">
      <van-cell-group>
        <van-cell title="账户与安全" v-if="jwt" is-link :to="{name: 'accountSecurity'}"/>
        <van-cell title="支付" v-if="jwt" value="支付宝账户、免密支付等" @click="onUndeveloped" value-class="value" is-link/>
      </van-cell-group>
    </div>
    <div class="index-b">
      <van-cell-group>
        <van-cell title="消息通知" @click="onUndeveloped" is-link/>
        <van-cell title="隐私" @click="onUndeveloped" is-link/>
        <van-cell title="通用" @click="onUndeveloped" value="地区、音效、辅助功能等" value-class="value" is-link/>
      </van-cell-group>
    </div>
    <div class="index-b">
      <van-cell-group>
        <van-cell title="帮助与反馈" @click="onUndeveloped" is-link/>
        <van-cell title="关于我们" is-link :to="{name: 'aboutUs'}"/>
      </van-cell-group>
    </div>
    <div class="index-b">
      <van-cell-group>
        <van-cell title="商家入驻" @click="onUndeveloped" v-if="jwt" is-link/>
      </van-cell-group>
    </div>
    <div class="index-c">
      <van-button type="default" @click="onUndeveloped" v-if="jwt" block>切换账户</van-button>
    </div>
    <div class="index-c">
      <van-button type="default" v-if="jwt" block @click="onLogout">退出登录</van-button>
    </div>
  </div>
</template>

<script>
import { logout } from '@/api/login.js'
import { me } from '@/api/user.js'
import { getImage } from '@/utils/resources.js'
export default {
  name: 'Me-Setting',
  data() {
    return {
      icon: '',
      nickname: '',
      username: '',
      jwt: localStorage.getItem('authorization')
    }
  },
  methods: {
    onClickLeft() {
      this.$router.back()
    },
    onLogout() {
      logout()
      window.localStorage.removeItem('authorization')
      this.$router.replace('/me')
    },
    setIcon(icon) {
      return getImage(icon)
    },
    onUndeveloped() {
      this.$toast('功能未开发')
    }
  },
  created() {
    me().then(res => {
      const data = res.data.data
      if (data) {
        this.icon = data.icon
        this.nickname = data.nickname
        this.username = data.username
      }
    }).catch(err => {
      this.$notify({ type: 'danger', message: err.message })
    })
  }
}
</script>

<style lang="less" scoped>
.container{
  .value{
    font-size: 12px;
  }
  .index-a{
    .title{
      display: flex;
      .headImg{
        flex: 1;
      }
      .info{
        flex: 4;
        padding-top: 4px;
        div{
          font-size: 15px;
          font-weight: bold;
        }
        span{
          font-size: 12px;
          color: rgb(126, 126, 126);
        }
      }
    }
  }
  .index-b{
    margin-top: 10px;
  }
  .index-c{
    margin-top: 10px;
  }
  /deep/.van-popup{
    background-color: #efefef;
    .popupBox{
      width: 100%;
      .content{
        .van-grid-item{
          .van-grid-item__content{
            background-color: #efefef;
          }
        }
        .myIcon{
          width: 50px;
          height: 50px;
          text-align: center;
          line-height: 50px;
          font-size: 28px;
          background-color: #fff;
          border-radius: 50%;
          margin-bottom: 5px;
        }
      }
      .backBtn{
        padding: 0 5px;
        padding-bottom: 5px;
      }
    }
  }
}
</style>

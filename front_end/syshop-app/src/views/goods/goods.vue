<template>
  <div class="containerx">
    <!-- 头部导航栏 -->
    <van-nav-bar :fixed="true" :placeholder="true">
      <template #title>
        <van-search
          placeholder="请输入搜索关键词"
          shape="round"
          @click="onSearch"
          readonly
        >
        </van-search>
      </template>
      <template #left>
        <div class="searchLeft">
          <van-icon name="arrow-left" size="18" @click="onSearchLeft"/>
        </div>
      </template>
      <template #right>
        <div class="searchAction">
          <van-icon name="ellipsis" size="20"/>
        </div>
      </template>
    </van-nav-bar>
    <!-- 轮播图 -->
    <van-swipe @change="onSwipeChange">
      <van-swipe-item v-for="(icon,index) in goodsItemImage" :key="index">
        <van-image
        fit="contain"
        :src="setImage(icon)"
      />
      </van-swipe-item>
      <template #indicator>
        <div class="custom-indicator">{{ current + 1 }}/{{imageLength}}</div>
      </template>
    </van-swipe>
    <!-- 商品信息 -->
    <div class="goodsInfo" v-if="goods">
      <div class="price" v-if="goodsItem">
        <a>￥</a>
        {{defaultOption.price * defaultOption.discount}}
      </div>
      <div class="name">{{goods.name}}({{defaultOption.color}})</div>
      <div class="postage">
        <span>总销量：{{goods.sale}}</span>
        <span>
          <van-icon name="location-o"/>
          {{goods.city}}
        </span>
      </div>
    </div>
    <div class="goodsInfo">
      <div class="parameter">
        <label for="option">选择</label>
        <div id="option">
          <van-cell is-link center @click="onOpenGoodsOption">
            <template>
              <div>
                <span>已选: </span>
                <span class="color" v-if="color">{{color}}</span>
                <span class="color" v-if="size">{{size}}</span>
                <span class="color" v-if="combo">{{combo}}</span>
                <span class="color" v-if="edition">{{edition}}</span>
              </div>
            </template>
          </van-cell>
        </div>
      </div>
      <div class="parameter">
        <label for="send">发货</label>
        <div id="send">
          <van-cell is-link center @click="goAddress">
            <template>
              <div class="send" v-if="goods">
                <div>
                  <span>{{goods.city}}</span>
                  &nbsp;|&nbsp;
                  <span>快递：{{goods.postage > 0 ? `￥${goods.postage}` : '免运费'}}</span>
                </div>
                <div>
                  <span>配送至：{{address ? address : '当前未登录'}}</span>
                </div>
              </div>
            </template>
          </van-cell>
        </div>
      </div>
      <div class="parameter">
        <label for="param">保障</label>
        <div id="param">
          <van-cell is-link center>
            <template>
              <div>
                <span class="color" v-if="goods">保修：{{goods.warrantyTime}}天</span>
                <span class="color" v-if="goods">包退：{{goods.refundTime}}天</span>
                <span class="color" v-if="goods">包换：{{goods.changerTime}}天</span>
              </div>
            </template>
          </van-cell>
        </div>
      </div>
    </div>
    <!-- 商品评价 -->
    <div class="evaluation">
      <div class="head">
        <div class="title">商品评价</div>
        <div class="text" @click="onAllEvaluation">查看全部 <van-icon name="arrow" /></div>
      </div>
      <div class="content">
        <div class="fail"  v-if="evaluations.length < 1">
          <van-empty description="该商品暂无评价" />
        </div>
        <div class="success" v-for="item in evaluations" :key="item.id">
          <div class="user">
            <div class="info">
              <van-image
                round
                width="30px"
                height="30px"
                :src="setImage(item.userIcon)"
              />
              <span>{{item.nickname}}</span>
            </div>
            <span class="time">{{item.time}}</span>
          </div>
          <div class="text">{{item.content}}</div>
          <div class="image">
            <van-image
              v-for="(image, index) in item.images" :key="index"
              width="100"
              height="100"
              fit="contain"
              :src="setImage(image)"
            />
          </div>
        </div>
      </div>
    </div>
    <!-- 商品导航 -->
    <div style="height:50px">
      <van-goods-action>
        <van-goods-action-icon icon="chat-o" text="客服" color="#ee0a24" />
        <van-goods-action-icon icon="cart-o" @click="goCart" text="购物车" />
        <van-goods-action-icon v-if="isCollection" icon="star" @click="onDelCollection" text="已收藏" color="#ff5000" />
        <van-goods-action-icon v-if="!isCollection" icon="star-o" @click="onSaveCollection" text="未收藏" color="#ff5000" />
        <van-goods-action-button type="warning" @click="onSavaShoppingCart" text="加入购物车" />
        <van-goods-action-button type="danger" @click="onBuy" text="立即购买" />
      </van-goods-action>
    </div>
    <!-- 商品规格选择 -->
    <van-popup
      v-model="showGoodOption"
      closeable
      position="bottom"
      round
      :style="{ height: '80%' }"
      @click-overlay="onCloseGoodOption"
      @click-close-icon="onCloseGoodOption"
      @opened="goodsOptionOpen">
      <div class="content">
        <div class="goodsTypeInfo">
          <div class="header" ref="typeHeader">
            <div class="headerbox1">
              <van-image
                v-if="defaultOption"
                width="96px"
                height="96px"
                fit="cover"
                :src="setImage(icon)"
              />
            </div>
            <div class="headerbox2" v-if="defaultOption">
              <div class="price"><a>￥</a>{{defaultOption.price * defaultOption.discount}}</div>
              <div class="headerInfo">
                <span>剩余{{defaultOption.stock}}件</span>
              </div>
              <div class="headerInfo">
                <span>已选择</span>
                <span v-if="color"> {{color}} </span>
                <span v-if="size"> {{size}} </span>
                <span v-if="combo"> {{combo}} </span>
                <span v-if="edition"> {{edition}} </span>
              </div>
            </div>
          </div>
          <div ref="tempHeader"></div>
          <div class="content" v-if="colorAndIcons.length > 0">
            <div class="contentTitle">颜色</div>
            <div class="colorbox">
              <div class="option" v-for="(item, index) in colorAndIcons" :class="{isSelect: item.isOption, notSelect: !item.isOption, isSellOut: item.isSellOut}" :key="index" @click="onSelectColor(item)">
                <van-image
                  width="24px"
                  height="24px"
                  fit="cover"
                  :src="setImage(item.icon)"
                />
                <div class="typeColor">{{item.color}}</div>
              </div>
            </div>
          </div>
          <div class="content" v-if="sizes.length > 0">
            <div class="contentTitle">大小</div>
            <div class="contentbox">
              <div class="option" v-for="(item, index) in sizes" :class="{isSelect: item.isOption, notSelect: !item.isOption, isSellOut: item.isSellOut}" :key="index" @click="onSelectSize(item)">
                <div class="typeName">{{item.size}}</div>
              </div>
            </div>
          </div>
          <div class="content" v-if="combos.length > 0">
            <div class="contentTitle">套餐</div>
            <div class="contentbox">
              <div class="option" v-for="(item, index) in combos" :class="{isSelect: item.isOption, notSelect: !item.isOption, isSellOut: item.isSellOut }" :key="index" @click="onSelectCombo(item)">
                <div class="typeName">{{item.combo}}</div>
              </div>
            </div>
          </div>
          <div class="content" v-if="editions.length > 0">
            <div class="contentTitle">版本</div>
            <div class="contentbox">
              <div class="option" v-for="(item, index) in editions" :class="{isSelect: item.isOption, notSelect: !item.isOption, isSellOut: item.isSellOut}" :key="index" @click="onSelectEdition(item)">
                <div class="typeName">{{item.edition}}</div>
              </div>
            </div>
          </div>
          <div class="content">
            <div class="numBox">
              <div class="numberTitle">购买数量</div>
              <div class="number">
                <van-stepper v-model="quantity" min="1" :max="inventory" :integer="true"/>
              </div>
            </div>
          </div>
          <div style="height: 40px"></div>
          <van-goods-action>
            <van-goods-action-button type="warning" @click="onSavaShoppingCart" text="加入购物车" />
            <van-goods-action-button type="danger" @click="onBuy" text="立即购买" />
          </van-goods-action>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script>
import { queryAddressById, queryDefaultAddress, saveShoppingCart, queryCollectionById, saveCollection, deleteCollection } from '@/api/user.js'
import { queryGoodsById, queryGoodsItemByGid, queryGoodsItem, queryEvaluation } from '@/api/goods.js'
import { submitOrder } from '@/api/orders.js'
import { getImage } from '@/utils/resources.js'
export default {
  name: 'Goods',
  data() {
    return {
      gid: '',
      goods: null,
      isCollection: false,
      goodsImages: [],
      defaultOption: null,
      // 商品类型图片(轮播图)
      goodsItemImage: [],
      current: 0,
      // 是否选择商品
      showGoodOption: false,
      // 颜色与图片选择
      colorAndIcons: [],
      // 大小选择
      sizes: [],
      // 套餐选择
      combos: [],
      // 版本选择
      editions: [],
      // 已选择的
      color: null,
      icon: null,
      size: null,
      combo: null,
      edition: null,
      // 购买数量
      quantity: 1,
      inventory: null,
      // 收货地址
      addressId: null,
      address: null,
      // 商品评价
      evaluations: []
    }
  },
  methods: {
    // 点击返回
    onSearchLeft() {
      this.$router.back()
    },
    // 搜索页跳转
    onSearch() {
      this.$router.push('/search')
    },
    initData() {
      this.gid = this.$route.params.gid
      const promiseAll = [
        queryGoodsById(this.gid),
        queryGoodsItemByGid(this.gid)
      ]
      Promise.all(promiseAll).then(([r1, r2]) => {
        this.setGoods(r1)
        this.setGoodsItem(r2)
      }).catch(err => {
        this.$notify({ type: 'danger', message: err.message })
      })

      queryDefaultAddress().then(res => {
        const data = res.data
        if (data.success) {
          this.addressId = data.data.id
          this.address = data.data.province + data.data.city + data.data.district + data.data.address
        } else {
          this.address = data.message
        }
      })

      queryEvaluation(this.gid, 1).then(res => {
        const data = res.data
        this.evaluations = []
        if (data.success) {
          let dataLength = data.data.length
          if (dataLength > 2) {
            dataLength = 2
          }
          for (let i = 0; i < dataLength; i++) {
            this.evaluations.push(data.data[i])
          }
        }
      })

      queryCollectionById(this.gid).then(res => {
        const data = res.data
        if (data.success) {
          this.isCollection = true
        } else {
          this.isCollection = false
        }
      })
    },
    onSwipeChange(index) {
      this.current = index
    },
    setImage(image) {
      return getImage(image)
    },
    onOpenGoodsOption() {
      this.showGoodOption = true
    },
    // 打开商品选择
    goodsOptionOpen() {
      const height = this.$refs.typeHeader.offsetHeight
      this.$refs.tempHeader.style.height = `${height}px`
    },
    // 关闭商品选择
    onCloseGoodOption() {
      this.showGoodOption = false
    },
    // 颜色选择
    onSelectColor(item) {
      if (item.isSellOut) {
        this.$toast('已售罄')
        return
      }
      queryGoodsItem(this.gid, item.color, null, null, null).then(res => {
        this.setGoodsItem(res)
      }).catch(err => {
        this.$notify({ type: 'danger', message: err.message })
      })
    },
    // 大小选择
    onSelectSize(item) {
      if (item.isSellOut) {
        this.$toast('已售罄')
        return
      }
      queryGoodsItem(this.gid, this.color, item.size, null, null).then(res => {
        this.setGoodsItem(res)
      }).catch(err => {
        this.$notify({ type: 'danger', message: err.message })
      })
    },
    // 套餐选择
    onSelectCombo(item) {
      if (item.isSellOut) {
        this.$toast('已售罄')
        return
      }
      queryGoodsItem(this.gid, this.color, this.size, item.combo, null).then(res => {
        this.setGoodsItem(res)
      }).catch(err => {
        this.$notify({ type: 'danger', message: err.message })
      })
    },
    // 版本选择
    onSelectEdition(item) {
      if (item.isSellOut) {
        this.$toast('已售罄')
        return
      }
      queryGoodsItem(this.gid, this.color, this.size, this.combo, item.edition).then(res => {
        this.setGoodsItem(res)
      }).catch(err => {
        this.$notify({ type: 'danger', message: err.message })
      })
    },
    goAddress() {
      const jwt = localStorage.getItem('authorization')
      if (!jwt) {
        this.$toast.fail('当前未登录')
        return
      }
      this.$router.push({
        name: 'address',
        query: {
          id: this.addressId
        }
      })
    },
    setGoods(res) {
      const data = res.data
      if (data.success) {
        this.goods = data.data.goods
        const images = this.goods.images.split(',')
        this.goodsImages = []
        images.forEach(item => {
          this.goodsImages.push(item)
        })
      }
    },
    setGoodsItem(res) {
      const data = res.data
      if (data.success) {
        this.goodsItem = data.data
        this.defaultOption = data.data.defaultOption

        this.goodsItemImage = []
        this.goodsImages.forEach(item => {
          this.goodsItemImage.push(item)
        })
        data.data.colorAndIcon.forEach(item => {
          this.goodsItemImage.push(item.icon)
        })

        this.colorAndIcons = []
        data.data.colorAndIcon.forEach(item => {
          let isOption = false
          let isSellOut = false
          if (item.isSellOut) {
            isSellOut = true
          }
          if (item.color === this.defaultOption.color) {
            this.color = item.color
            this.icon = item.icon
            isOption = true
          }
          this.colorAndIcons.push({
            color: item.color,
            icon: item.icon,
            isOption,
            isSellOut
          })
        })
        this.sizes = []
        if (data.data.size) {
          data.data.size.forEach(item => {
            let isOption = false
            let isSellOut = false
            if (item.isSellOut) {
              isSellOut = true
            }
            if (item.size === this.defaultOption.size) {
              this.size = item.size
              isOption = true
            }
            this.sizes.push({
              size: item.size,
              isOption,
              isSellOut
            })
          })
        }
        this.combos = []
        if (data.data.combo) {
          data.data.combo.forEach(item => {
            let isOption = false
            let isSellOut = false
            if (item.isSellOut) {
              isSellOut = true
            }
            if (item.combo === this.defaultOption.combo) {
              this.combo = item.combo
              isOption = true
            }
            this.combos.push({
              combo: item.combo,
              isOption,
              isSellOut
            })
          })
        }
        this.editions = []
        if (data.data.edition) {
          data.data.edition.forEach(item => {
            let isOption = false
            let isSellOut = false
            if (item.isSellOut) {
              isSellOut = true
            }
            if (item.edition === this.defaultOption.edition) {
              this.edition = item.edition
              isOption = true
            }
            this.editions.push({
              edition: item.combo,
              isOption,
              isSellOut
            })
          })
        }
        this.inventory = this.defaultOption.stock
      }
    },
    goCart() {
      const jwt = localStorage.getItem('authorization')
      if (!jwt) {
        this.$toast('暂无法访问，请先登录')
        return
      }
      this.$router.push('/shoppingCart')
    },
    onSaveCollection() {
      saveCollection(this.gid).then(res => {
        const data = res.data
        if (data.success) {
          this.isCollection = true
        } else {
          this.$toast.fail(data.message)
        }
      }).catch(err => {
        this.$notify({ type: 'danger', message: err.message })
      })
    },
    onDelCollection() {
      deleteCollection(this.gid).then(res => {
        const data = res.data
        if (data.success) {
          this.isCollection = false
        } else {
          this.$toast.fail(data.message)
        }
      }).catch(err => {
        this.$notify({ type: 'danger', message: err.message })
      })
    },
    onSavaShoppingCart() {
      const jwt = localStorage.getItem('authorization')
      if (!jwt) {
        this.$toast.fail('当前未登录')
        return
      }
      saveShoppingCart(this.defaultOption.id, this.quantity).then(res => {
        const data = res.data
        if (data.success) {
          this.$toast.success(data.message)
        } else {
          this.$toast.fail(data.message)
        }
      }).catch(err => {
        this.$notify({ type: 'danger', message: err.message })
      })
    },
    onBuy() {
      const jwt = localStorage.getItem('authorization')
      if (!jwt) {
        this.$toast.fail('当前未登录')
        return
      }
      const items = []
      items.push({
        gid: this.defaultOption.id,
        quantity: this.quantity
      })
      submitOrder(this.addressId, items, null).then(res => {
        const data = res.data
        if (data.success) {
          const orderId = data.data
          this.$toast.loading({
            message: '提交订单中...',
            forbidClick: true
          })
          setTimeout(() => {
            this.$router.push(`/orderInfo/${orderId}`)
          }, 1000)
        }
      })
    },
    onAllEvaluation() {
      this.$router.push(`/goodsEvaluation/${this.gid}`)
    }
  },
  computed: {
    // 设置轮播图长度
    imageLength() {
      return this.goodsItemImage.length
    }
  },
  created() {
    this.goodsItemImage = []
    this.initData()
  },
  activated() {
    if (this.$route.meta.isBack) {
      this.$bus.$on('getAddressId', (id) => {
        queryAddressById(id).then(res => {
          const data = res.data
          if (data.success) {
            this.addressId = data.data.id
            this.address = data.data.province + data.data.city + data.data.district + data.data.address
          }
        })
      })
      this.$route.meta.isBack = false
      return
    }
    this.goodsItemImage = []
    this.initData()
  },
  beforeRouteLeave(to, from, next) {
    if (to.name !== 'address') {
      this.$destroy('goods')
    }
    next()
  }
}
</script>

<style lang="less" scoped>
.containerx{
  /deep/.van-nav-bar{
    .van-search{
      padding: 0;
    }
    .searchAction{
      display: inline-block;
      margin-left: 10px;
      &:first-child{
        margin: 0;
      }
    }
  }
  /deep/.van-swipe{
    .van-swipe-item{
      color: #fff;
      height: 375px;
      font-size: 20px;
      text-align: center;
      background-color: #999999;
    }
    .custom-indicator {
      position: absolute;
      border-radius: 10px;
      right: 10px;
      bottom: 10px;
      padding: 5px 12px;
      font-size: 12px;
      color: #fff;
      background: rgba(0, 0, 0, 0.1);
    }
  }
  .goodsInfo{
    margin: 0 5px;
    border-radius: 10px;
    padding: 10px;
    background-color: #fff;
    .name{
      font-size: 18px;
    }
    .price{
      color: red;
      font-size: 24px;
      margin: 5px 0;
      a{
        font-size: 16px;
      }
    }
    .postage{
      color: #999999;
      padding: 5px 0;
      font-size: 14px;
      span{
        &:last-child{
          float: right;
        }
      }
    }
    .parameter{
      display: flex;
      align-items: center;
      label{
        flex: 1;
        font-size: 12px;
        color: #999999;
      }
      div{
        flex: 9;
        font-size: 12px;
      }
      .send{
        div{
          &:last-child{
            color: #999999;
          }
        }
      }
      .color{
        border-radius: 3px;
        padding: 3px 5px;
        background-color: #F5F5F5;
        color: #999999;
      }
    }
  }
  .evaluation{
    margin: 0 5px;
    border-radius: 10px;
    padding: 10px;
    background-color: #fff;
    .head{
      display: flex;
      justify-content: space-between;
      .title{
        font-size: 16px;
        font-weight: 600;
      }
      .text{
        font-size: 12px;
        color: red;
      }
    }
    .content{
      .success{
        width: 100%;
        padding-top: 10px;
        .user{
          height: 40px;
          display: flex;
          align-items: center;
          justify-content: space-between;
          .info{
            display: flex;
            align-items: center;
            span{
              padding-left: 10px;
            }
          }
          .time{
            font-size: 13px;
            color: #999999;
            float: right;
          }
        }
        .image{
          display: flex;
          /deep/.van-image{
            margin-right: 10px;
          }
        }
        .text{
          font-size: 14px;
          padding-bottom: 10px;
        }
      }
    }
  }
  /deep/.van-popup{
    .van-popup__close-icon{
      position: fixed;
      top: 22%;
      z-index: 4;
    }
    .content{
      .goodsTypeInfo{
        padding: 0 20px;
        .header{
          display: flex;
          position: fixed;
          width: 90%;
          padding: 10px 0;
          z-index: 3;
          background-color: #fff;
          .headerbox1{
            flex: 1;
          }
          .headerbox2{
            flex: 2;
            padding-top: 10px;
            .price{
              color: red;
              font-size: 22px;
              a{
                font-size: 16px;
              }
            }
            .headerInfo{
              margin-top: 8px;
              font-size: 12px;
              color: #969799;
            }
          }
        }
        .content{
          padding: 12px 0;
          border-top: 1px solid rgba(144, 144, 144, 0.3);
          .contentTitle{
            padding-bottom: 12px;
          }
          .colorbox{
            display: flex;
            flex-wrap: wrap;
            .option{
              height: 32px;
              border-radius: 3px;
              padding: 0 5px;
              margin-bottom: 5px;
              margin-right: 3px;
              display: flex;
              width: auto;
              align-items: center;
              /deep/.van-image{
                flex: 1;
              }
              .typeColor{
                flex: 9;
                font-size: 12px;
                padding: 8px 0;
                padding-left: 5px;
              }
            }
          }
          .contentbox{
            display: flex;
            flex-wrap: wrap;
            .option{
              height: 32px;
              border-radius: 3px;
              padding: 0 5px;
              margin-bottom: 5px;
              margin-right: 3px;
              width: auto;
              .typeName{
                font-size: 12px;
                padding: 8px 5px;
              }
            }
          }
          .numBox{
            display: flex;
            justify-content: space-between;
          }
        }
        /deep/.van-goods-action{
          position: fixed;
          bottom: 0;
        }
      }
    }
  }
  .isSelect{
    background-color: #FDE7EA;
    color: #ee0a24;
    border: 1px solid #ee0a24;
  }
  .notSelect{
    background-color: #f5f5f5;
    border: 1px solid transparent;
  }
  .isSellOut{
    background-color: rgba(255, 255, 255, 0.7);
    color: #ccc;
    border: 1px solid #ccc;
  }
}
</style>

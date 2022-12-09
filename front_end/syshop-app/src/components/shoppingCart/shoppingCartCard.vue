<template>
  <div class="container">
    <div class="image">
      <van-image
        width="90px"
        height="90px"
        fit="cover"
        :src="setIcon(shoppingCartData.icon)"
        @click="toGoods"
      />
    </div>
    <div class="info">
      <div class="name" @click="toGoods" v-if="shoppingCartData.name">{{shoppingCartData.name}}</div>
      <div class="type" @click="showGoodOption = true">
        <div class="text">
          <span v-if="shoppingCartData.color">{{shoppingCartData.color}} </span>
          <span v-if="shoppingCartData.size">{{shoppingCartData.size}} </span>
          <span v-if="shoppingCartData.combo">{{shoppingCartData.combo}} </span>
          <span v-if="shoppingCartData.edition">{{shoppingCartData.edition}}</span>
        </div>
        <div class="icon">
          <van-icon name="arrow-down" size="12"/>
        </div>
      </div>
      <div class="priceBox">
        <div class="price">
          <a>￥</a><span>{{shoppingCartData.unitPrice}}</span><span class="postage">邮费：￥{{shoppingCartData.postage}}</span>
        </div>
        <div class="quantity">
          <van-stepper v-model="shoppingCartData.quantity" min="1" :max="shoppingCartData.stock" :integer="true" @change="onStepChange" button-size="24px"/>
        </div>
      </div>
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
                :src="setIcon(icon)"
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
                  :src="setIcon(item.icon)"
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
            <van-goods-action-button @click="onPopupOk" type="danger" text="确认" />
          </van-goods-action>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script>
import { getImage } from '@/utils/resources.js'
import { queryGoodsItem } from '@/api/goods.js'
import { updateShoppingCart, queryShoppingCartById } from '@/api/user.js'
export default {
  name: 'ShoppingCart-Card',
  props: ['shoppingCartItem'],
  data() {
    return {
      shoppingCartData: this.shoppingCartItem,
      gid: this.shoppingCartItem.goodsId,
      showGoodOption: false,
      defaultOption: null,
      // 颜色与图片选择
      colorAndIcons: [],
      // 大小选择
      sizes: [],
      // 套餐选择
      combos: [],
      // 版本选择
      editions: [],
      // 已选择的
      color: this.shoppingCartItem.color,
      icon: this.shoppingCartItem.icon,
      size: this.shoppingCartItem.size,
      combo: this.shoppingCartItem.combo,
      edition: this.shoppingCartItem.edition,
      // 购买数量
      quantity: this.shoppingCartItem.quantity,
      inventory: this.shoppingCartItem.stock
    }
  },
  methods: {
    setIcon(icon) {
      return getImage(icon)
    },
    toGoods() {
      this.$router.push(`/goods/${this.shoppingCartData.goodsId}`)
    },
    onStepChange(value) {
      updateShoppingCart(this.shoppingCartData.id, this.shoppingCartData.goodsItemId, Number(value)).then(res => {
        const data = res.data
        this.quantity = Number(value)
        if (data.success) {
          queryShoppingCartById(this.shoppingCartData.id).then(res => {
            if (res.data.success) {
              this.$emit('onUpdateCard', res.data.data)
            }
          })
        }
      })
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
    setGoodsItem(res) {
      const data = res.data
      if (data.success) {
        this.goodsItem = data.data
        this.defaultOption = data.data.defaultOption

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
    onPopupOk() {
      updateShoppingCart(this.shoppingCartData.id, this.defaultOption.id, this.quantity).then(res => {
        const data = res.data
        if (data.success) {
          queryShoppingCartById(this.shoppingCartData.id).then(res => {
            if (res.data.success) {
              this.$emit('onUpdateCard', res.data.data)
              this.onCloseGoodOption()
            }
          })
        }
      })
    }
  },
  created() {
    queryGoodsItem(this.gid, this.color, this.size, this.combo, this.edition).then(res => {
      this.setGoodsItem(res)
    }).catch(err => {
      this.$notify({ type: 'danger', message: err.message })
    })
  }
}
</script>

<style lang="less" scoped>
.container{
  flex: 10;
  display: flex;
  .image{
    flex: 3;
    /deep/.van-image__img{
      border-radius: 10px;
    }
  }
  .info{
    flex: 7;
    padding-left: 5px;
    .name{
      font-size: 14px;
      font-weight: bold;
      margin: 5px 0;
    }
    .type{
      background-color: #F5F5F5;
      padding: 5px;
      margin: 5px 0;
      border-radius: 5px;
      font-size: 12px;
      display: flex;
      align-items: center;
      .text{
        flex: 11;
        max-width: 190px;
        // 强制不换行
        white-space:nowrap;
        // 溢出隐藏
        overflow: hidden;
      }
      .icon{
        flex: 1;
        text-align: center;
      }
    }
    .priceBox{
      margin: 5px 0;
      display: flex;
      align-items: center;
      justify-content: space-between;
      .price{
        color: red;
        a{
          font-size: 14px;
        }
        span{
          font-size: 22px;
        }
        .postage{
          font-size: 12px;
          color: #ccc;
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

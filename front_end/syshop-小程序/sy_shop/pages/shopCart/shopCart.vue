<template>
	<view class="shop-cart">
		<!-- 如果购物车有数据则显示此内容 -->
		<template v-if=' list.length > 0'>
			<!-- 自定义导航 [在App中] -->
			<!-- <uniNavBar title='购物车' :right-text=' isNavBar?"完成":"编辑" ' fixed='true' status-bar='true' @clickRight=' isNavBar = !isNavBar'></uniNavBar> -->
			<view class="uniNavBar">
				<uniNavBar :right-text=' isNavBar?"完成":"编辑" ' fixed='true' status-bar='true' @clickRight=' isNavBar = !isNavBar'></uniNavBar>
			</view>
			
			<!-- 商品内容 -->
			<view class="shop-list">
				<view class="shop-item" v-for='(item,index) in shopCartData' :key="index">
					<label class="radio" @tap="selectedItem(index)">
						<radio value="" color="#FF3333" :checked="item.checked"/><text></text>
					</label>
					<image class="shop-img" :src="setIcon(item.icon)" mode=""></image>
					<view class="shop-text">
						<view class="shop-name">{{item.name}}</view>
						<view class="shop-color f-color">规格：
							<span v-if="item.color">{{item.color}}</span>
							<span v-if="item.combo">/{{item.combo}}</span>
							<span v-if="item.edition">/{{item.edition}}</span>
							<span v-if="item.size">/{{item.size}}</span>
						</view>
						<view class="shop-det">
							<view class="shop-pice">￥{{item.price}}</view>
							
							<!-- 未进行编辑时： -->
							<template v-if='!isNavBar'>
								<view class="shop-sum">x{{item.quantity}}</view>
							</template>
							<!-- 若点击编辑时，修改购物车内容 -->
							<template v-else>
								<!-- 加减按钮的数字输入框  其中：$event表示当前输入的 ；index表示下标-->
								<uniNumberBox :value='item.quantity' min="1" @change="changeNumber($event,index)" background="#FFF"></uniNumberBox>
							</template>
							
						</view>
					</view>
				</view>
			</view>
		</template>
		
		<!-- 如果购物车没有数据则显示此内容 -->
		<template v-else>
			<!-- 自定义导航 [在App中] -->
			<!-- <uniNavBar title='购物车' fixed='true' status-bar='true'></uniNavBar> -->
			<view class="shop-else-list">
				<image class="null-img" src="../../static/img/NullShopCart.png"></image>
				<view class="null-conta">囧~购物车居然是空的</view>
				<view class="null-contb">再忙，也要记得买点什么犒劳自己~~</view>
			</view>
		</template>
		
		<!-- 底部内容 -->
		<view class="shop-foot">
			<label class="radio foot-radio" @tap="checkedAllFn">
				<radio value="" color="#FF3333" :checked="checkedAll"></radio><text>全选</text>
			</label>
			
			<!-- 未进行编辑时： -->
			<template v-if='!isNavBar'>
				<view class="foot-total">
					<view class="foot-count">合计：<text class="foot-countColor">￥{{totalCount.price}}</text></view>
					<view class="foot-num" @tap="getTotalCount">结算({{totalCount.num}})</view>
				</view>
			</template>
			<!-- 若点击编辑时，修改购物车内容 -->
			<template v-else>
				<view class="foot-collect">移入收藏夹</view>
				<view class="foot-clear" @tap="delGoodsFn">删除</view>
			</template>
			
		</view>
		
	</view>
</template>

<script>
	import { getImage } from '../../utils/resources.js'
	import uniNavBar from '@/components/uni/uni-nav-bar/components/uni-nav-bar/uni-nav-bar.vue'
	import uniNumberBox from '@/components/uni/uni-number-box/components/uni-number-box/uni-number-box.vue' 
	import {mapState,mapActions,mapGetters,mapMutations} from 'vuex'
	export default {
		data() {
			return {
				isNavBar:false,
				shopCartData:[]
			}
		},
		computed:{
			...mapState({
				list:state=>state.cart.list
			}),
			...mapGetters(['checkedAll','totalCount'])
		},
		components:{
			uniNavBar,
			uniNumberBox
		},
		created() {
			this.getShopCartList()
		},
		methods: {
			getShopCartList(){
				uni.request({
					url: 'http://43.139.55.209:8081/api/shoppingCart/query',
					method: 'GET',
					header:{
						"authorization":uni.getStorageSync('token')
					},
					success: (res) =>{
						const data = res.data
						// console.log(data)
					     if (data.success) {
					        data.data.forEach(item => {
					          this.shopCartData.push({
					            id: item.id,
					            name: item.name,
					            goodsId: item.goodsId,
					            goodsItemId: item.goodsItemId,
					            icon: item.icon,
					            color: item.color,
					            size: item.size,
					            combo: item.combo,
					            edition: item.edition,
					            quantity: item.quantity,
					            stock: item.stock,
					            unitPrice: item.unitPrice,
					            postage: item.postage,
					            price: item.price,
					            isSelect: false
					          })
					        })
						}
						console.log(this.shopCartData)
					}
				})
			},
			// 获取图片
			setIcon(icon) {
				return getImage(icon)
			},
			// 结算
			getTotalCount(){
				uni.switchTab({
					url:'/pages/index/index'
				})
				uni.showToast({
					title: '结算成功',
					duration: 2000,
				})
			},
			...mapActions(['checkedAllFn','delGoodsFn']),
			...mapMutations(['selectedItem']),
			// 修改输入框的值，改变商品的购买数量
			changeNumber(value,index){
				this.shopCartData[index].quantity = value;
			},
			// 关闭页面下拉刷新效果的处理函数
			onPullDownRefresh() {
				uni.stopPullDownRefresh()
			},
		}
	}
</script>

<style scoped>
	.shop-list{
		padding-bottom: 100rpx;
	}
	.shop-else-list{
		background-color: #F7F7F7;
	}
	.uniNavBar{
		margin-top: -70rpx;
	}
	.null-img{
		position: absolute;
		left: 0;
		top: 150rpx;
		right: 0;
		bottom: 0;
	}
	.null-conta{
		position: absolute;
		left: 0;
		top: 680rpx;
		right: 0;
		bottom: 0;
		text-align: center;
		font-size: 36rpx;
	}
	.null-contb{
		position: absolute;
		left: 0;
		top: 725rpx;
		right: 0;
		bottom: 0;
		text-align: center;
		font-size: 31rpx;
		font-weight: bold;
	}
	.shop-item{
		display: flex;
		margin-bottom: 10rpx;
		padding: 10rpx;
		align-items: center;
		background-color: #F7F7F7;
	}
	.shop-img{
		width: 200rpx;
		height: 220rpx;
	}
	.shop-text{
		flex: 1;
		padding-left: 20rpx;
	}
	.shop-name{
		font-size: 38rpx;
		color: black;
	}
	.shop-color{
		font-size: 30rpx;
		padding: 10rpx;
	}
	.shop-det{
		display: flex;
		justify-content: space-between;/* 左右排开 */
	}
	.shop-pice{
		font-size: 35rpx;
		color: orangered;
	}
	.shop-sum{
		font-size: 35rpx;
	}
	/* 底部样式 */
	.shop-foot{
		display: flex;
		justify-content: space-between;
		position: fixed;
		bottom: 0;
		left: 0;
		width: 100%;
		height: 100rpx;
		align-content: center;
		background-color: #F7F7F7;
		border-top:2rpx solid #F7F7F7;
	}
	.foot-radio{
		padding-left: 20rpx;
		line-height: 100rpx;
		font-size: 33rpx;
	}
	.foot-total{
		display: flex;
		font-size: 33rpx;
	}
	.foot-count{
		line-height: 100rpx;
		padding: 0 20rpx;
	}
	.foot-countColor{
		color: orangered;
	}
	.foot-num{
		background-color: red;
		color: #FFFFFF;
		padding: 0 60rpx;
		line-height: 100rpx;
	}
	.foot-collect{
		background-color: #2979FF;
		color: #FFFFFF;
		padding: 0 60rpx;
		margin-right: -130rpx;
		line-height: 100rpx;
	}
	.foot-clear{
		background-color: red;
		color: #FFFFFF;
		padding: 0 60rpx;
		margin-left: -15rpx;
		line-height: 100rpx;
	}
</style>
<template>
	<view class="order-details">
		
		<view class="shop-cot">
			<view>
				<view class="fonta">等待付款</view>
				<view class="fontb">
					<view style="padding-right: 4rpx;">剩余</view>
					<!-- 倒计时：不显示天数 -->
					<countDown :show-day="false" :hour="23" :minute="59" :second="59"></countDown>
					<view style="padding-left: 4rpx;">&nbsp;&nbsp;自动取消订单</view>
				</view>
			</view>
			<image src="../../static/img/pay.png"></image>
		</view>
		
		<view class="goods-content">
			<view class="goods-cot">
				<image v-if="orderData.defaultOption" :src="setIcon(orderData.defaultOption.icon)"></image>
				<view class="goods-mes">
					<view class="goods-name">{{orderData.name}}</view>
					<view class="goods-size" v-if="orderData.defaultOption">{{orderData.defaultOption.color ? orderData.defaultOption.color:''}}/{{orderData.defaultOption.size ? orderData.defaultOption.size:''}}/{{orderData.defaultOption.combo ? orderData.defaultOption.combo:''}}/{{orderData.defaultOption.edition ? orderData.defaultOption.edition:''}}</view>
					<view class="goods-mesx">
						<view class="goods-price">￥{{orderData.defaultOption.price}}</view>
						<view class="goods-num">X{{orderData.num}}</view>
					</view>
				</view>
			</view>
		</view>
		
		<view class="shop-tex">
			<view class="shop-cord">订单号：134545612315686</view>
			<view class="shop-time">下单时间：2022-11-09 17:15:40</view>
		</view>
		
		<view class="people">
			<view class="people-msg">
				<text>收货人：刘晓燕</text>
				<text style="margin-left: 100rpx;">联系电话：18124498467</text>
			</view>
			<view class="people-address">收货地址：广东省广州市天河区林和街道天寿路122号广东水利电力职业技术学院</view>
		</view>
		
		<view>
			<view style="margin: 40rpx;margin-left: 60rpx;">
				<text style="font-size: 36rpx;">商品总价：</text>
				<text style="margin-left: 300rpx;font-size: 36rpx;color: #d81e06;">￥{{orderData.defaultOption.price}}</text>
			</view>
			<view style="margin-left: 60rpx;margin-bottom: 30rpx;">
				<text>商品运费：</text>
				<text style="margin-left: 340rpx;">免运费</text>
			</view>
			<view style="margin-left: 400rpx;">
				<text style="font-size: 40rpx;">实付：</text>
				<text style="font-size: 50rpx;color: #d81e06;font-weight: bold;">￥{{orderData.defaultOption.price}}</text>
			</view>
		</view>
		
		<view class="order-bottom">
			<view class="order-cancel" @tap="getdeleteOrder">取消订单</view>
			<view class="order-pay" @tap="getPayment">立即支付</view>
		</view>
		
	</view>
</template>

<script>
	import { getImage } from '../../utils/resources.js'
	import countDown from '../../components/uni/uni-countdown/components/uni-countdown/uni-countdown.vue'
	export default {
		data() {
			return {
				orderData:[]
			}
		},
		components:{
			countDown
		},
		methods: {
			// 获取图片
			setIcon(icon) {
				return getImage(icon)
			},
			// 取消订单
			getdeleteOrder(){
				uni.switchTab({
					url:'/pages/index/index'
				})
				uni.showToast({
					title: '取消订单成功',
					duration: 2000,
				})
			},
			// 立即支付
			getPayment(){
				const item = {name: this.orderData.name, price: this.orderData.price, defaultOption: this.orderData.defaultOption, num: this.orderData.num}
				uni.redirectTo({
					url:'/pages/orderDetails/orderFinish?orderData=' + encodeURIComponent(JSON.stringify(item))
				})
				uni.showToast({
					title: '订单支付成功',
					duration: 2000,
				})
			}
		},
		onLoad: function (option) {
			const item = JSON.parse(decodeURIComponent(option.shopData));
			console.log(item)
			this.orderData=item
		}
	}
</script>

<style scoped>
	.shop-cot{
		display: flex;
		width: 100%;
		height: 200rpx;
		background-color: #f4ad5d;
	}
	.fonta{
		margin: 50rpx; 
		margin-bottom: 20rpx;
		color: #d81e06;
		font-size: 36rpx;
		font-weight: bold;
	}
	.fontb{
		margin-left: 50rpx;
		font-size: 28rpx;
		color: #e7e5e4;
		display: flex;
	}
	.shop-cot image{
		width: 150rpx;
		height: 150rpx;
		margin-left: 120rpx;
		margin-top: 30rpx;
	}
	.goods-content{
		margin: 6rpx;
		border: 1rpx solid #dcdcda;
		border-radius: 15rpx;
	}
	.goods-cot{
		display: flex;
		margin: 2rpx;
	}
	.goods-cot image{
		width: 200rpx;
		height: 200rpx;
	}
	.goods-mes{
		margin: 8rpx;
	}
	.goods-name{
		font-size: 34rpx;
		font-weight: bold;
	}
	.goods-size{
		font-size: 30rpx;
		color: #bfbebd;
		margin-top: 20rpx;
	}
	.goods-mesx{
		display: flex;
		margin-top: 20rpx;
	}
	.goods-price{
		color: #d81e06;
		font-weight: bold;
	}
	.goods-num{
		margin-left: 330rpx;
	}
	.shop-tex{
		margin: 6rpx;
		height: 140rpx;
		border: 1rpx solid #dcdcda;
		border-radius: 15rpx;
		background-color: #f9f9f9;
	}
	.shop-cord{
		margin: 20rpx;
		margin-left: 35rpx;
		font-size: 30rpx;
	}
	.shop-time{
		margin-left: 35rpx;
		font-size: 30rpx;
	}
	.people{
		margin: 6rpx;
		height: 180rpx;
		border: 1rpx solid #dcdcda;
		border-radius: 15rpx;
		background-color: #f9f9f9;
	}
	.people-msg{
		margin: 20rpx;
		margin-left: 35rpx;
		font-size: 30rpx;
	}
	.people-address{
		margin-left: 35rpx;
		font-size: 30rpx;
	}
	.order-bottom{
		display: flex;
		margin-top: 30rpx;
		margin-left: 230rpx;
		padding-bottom: 100rpx;
	}
	.order-cancel{
		width: 220rpx;
		height: 60rpx;
		font-size: 36rpx;
		color: #bfbebd;
		margin: 10rpx;
		padding-top: 5rpx;
		border: 1rpx solid #bfbebd;
		border-radius: 40rpx;
		text-align: center;
	}
	.order-pay{
		width: 220rpx;
		height: 60rpx;
		font-size: 36rpx;
		color: #ff6a59;
		margin: 10rpx;
		padding-top: 5rpx;
		border: 1rpx solid #ff6a59;
		border-radius: 40rpx;
		text-align: center;
	}
</style>

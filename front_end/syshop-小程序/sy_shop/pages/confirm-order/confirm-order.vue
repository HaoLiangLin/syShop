<template>
	<view class="confirm-order">
		<!-- 地址 -->
		<view class="order-map" @click="goAddress">
			<view class="map-title">
				<view class="title-name">收件人: {{path.name}}</view>
				<view>{{path.phone}}</view>
				<viw>></viw>
			</view>
			<view class="map-city">收货地址：{{path.city}} {{path.detail}}</view>
		</view>

		<!-- 商品 -->
		<view class="goods-list">
			<view class="goods-item">
				<view class="goods-content">
					<image src="../../static/logo.png" class="goods-imag"></image>
					<view class="goods-pro">
						<view class="goods-text">商品名称商品名称</view>
						<view class="goods-size">商品规格商品规格</view>
					</view>
				</view>
				<view class="total-price">
					共计<text> 1 </text>件商品 <text style="color: orangered;">￥200</text>
				</view>
			</view>
			
			<view class="goods-item">
				<view class="goods-content">
					<image src="../../static/logo.png" class="goods-imag"></image>
					<view class="goods-pro">
						<view class="goods-text">商品名称商品名称</view>
						<view class="goods-size">商品规格商品规格</view>
					</view>
				</view>
				<view class="total-price">
					共计<text> 1 </text>件商品 <text style="color: orangered;">￥200</text>
				</view>
			</view>
			
		</view>
		
		<!-- 底部 提交订单 -->
		<view class="order-foot">
			<view>合计：<text>￥999</text></view>
			<view class="confirm-btn">提交订单</view>
		</view>
	</view>
</template>

<script>
	import {mapGetters} from 'vuex'
	export default {
		data() {
			return {
				path:{}
			}
		},
		methods: {
			goAddress(){
				uni.navigateTo({
					url:"/pages/myAddress/myAddress?type=selectPath"
				})
			}
		},
		computed:{
			...mapGetters(['defaultPath'])
		},
		onLoad() {
			//如果有默认地址的一个赋值
			if(this.defaultPath.length>0){
				console.log(this.defaultPath[0])
				this.path = this.defaultPath[0]
			}
			//如果触发自定义事件，on去接受值
			uni.$on("selectPathItem",(res)=>{
				this.path = res;
			})
		},
		onUnload() {
			uni.$off("selectPathItem",()=>{
				console.log("移除了selectPathItem")
			})
		},
	}
</script>

<style>
	.confirm-order {
		width: 100%;
		height: 100vh;
		background-color: whitesmoke;
	}

	.order-map {
		padding: 0rpx 30rpx;
		background-color: white;
		line-height: 100rpx;
		margin-bottom: 30rpx;
	}

	.map-title {
		display: flex;
		justify-content: space-between;
	}

	.map-name {
		font-weight: bold;
	}
	.goods-list{
		background-color: white;
		padding: 20rpx 20rpx;
	}
	.goods-content {
		background-color: #F7F7F7;
		padding: 10rpx 20rpx;
		display: flex;
		justify-content: space-evenly;
		align-items: center;
	}

	.goods-imag {
		width: 160rpx;
		height: 160rpx;
	}

	.goods-pro {
		width: 500rpx;
		padding: 0 10rpx;
	}

	.goods-text {
		font-size: 32rpx;
		color: black;
	}

	.goods-size {
		font-size: 24rpx;
		color: lightgray;
	}

	.total-price {
		display: flex;
		justify-content: flex-end;
		padding: 20rpx;
		font-size: 32rpx;
	}
	.order-foot{
		width: 100%;
		height: 100rpx;
		position: fixed;
		bottom: 0;
		left: 0;
		background-color: white;
		display: flex;
		justify-content: space-between;
		align-items: center;
		color: orangered;
	}
	.confirm-btn{
		color: white;
		background-color: orangered;
		padding: 10rpx 30rpx;
		border-radius: 30rpx;
	}
</style>

<template>
	<view class="user-purse">
		
		<view class="my-header">
			<view class="header-main">
				<view class="title">账户余额:</view>
				<view class="title2">{{balance}}元</view>
			</view>
		</view>
		
		<view class="main">
			<view class="main-item" @click="goRecharge">
				<view class="item-header">
					<view class="img1"></view>
					<view>立即充值</view>
				</view>
				<view>></view>
			</view>
			<view class="main-item">
				<view class="item-header">
					<view class="img2"></view>
					<view>交易记录</view>
				</view>
				<view>></view>
			</view>
			<view class="main-item">
				<view class="item-header">
					<view class="img3"></view>
					<view>常见问题</view>
				</view>
				<view>></view>
			</view>
		</view>
		
	</view>
</template>

<script>
	export default {
		data() {
			return {
				balance:""
			}
		},
		methods: {
			goRecharge(){
				uni.navigateTo({
					url:"/pages/user-purse/user-recharge"
				})
			}
		},
		onLoad() {
			let res = uni.getStorageSync('userpurse')
			console.log(res)
			this.balance = res.balance
		}
	}
</script>

<style>
.my-header{
	background-image: linear-gradient(-60deg, #ff5858 0%, #f09819 100%);
	width: 100%;
	height: 300rpx;
	margin: auto;
	padding: auto;
	text-align: center;
}
.header-main{
	height: 400rpx;
	width: 100%;
	color: white;
	font-weight: bold;
	padding-top: 100rpx;
}
.title2{
	font-size: 54rpx;
}
.title{
	font-size: 30rpx;
}
.main{
	padding: 20rpx 10rpx;
}
.main-item{
	display: flex;
	justify-content: space-between;
	align-items: center;
	height: 120rpx;
}
.item-header{
	display: flex;
	align-items: center;
	justify-content: center;
	font-weight: bold;
	color: gray;
}
.img1{
	width: 80rpx;
	height: 80rpx;
	background-color: limegreen;
	border-radius: 20rpx;
	margin-right: 15rpx;
}
.img2{
	width: 80rpx;
	height: 80rpx;
	background-color: skyblue;
	border-radius: 20rpx;
	margin-right: 15rpx;
}
.img3{
	width: 80rpx;
	height: 80rpx;
	background-color: lightyellow;
	border-radius: 20rpx;
	margin-right: 15rpx;
}
</style>

<template>
	<view class="user-purse">
		
		<view class="my-header">
			<view class="header-main">
				<view class="title">积分余额:</view>
				<view class="title2">{{points}}分</view>
			</view>
		</view>
		
		<view class="main">
			<view class="main-item">
				<view class="item-header">
					<view class="img1"></view>
					<view>兑换积分</view>
				</view>
				<view>></view>
			</view>
			<view class="main-item">
				<view class="item-header">
					<view class="img4"></view>
					<view>积分提现</view>
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
				points:""
			}
		},
		methods: {
			
		},
		onLoad() {
			this.points = uni.getStorageSync('userpurse').points
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
	border-bottom: 2rpx solid gainsboro;
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
.img4{
	width: 80rpx;
	height: 80rpx;
	background-color: orange;
	border-radius: 20rpx;
	margin-right: 15rpx;
}
</style>

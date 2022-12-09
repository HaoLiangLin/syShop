<template>
	<view>
		
		<view class="login-other">
			<view class="other-text">
				<view>或用以下登录方式登录</view>
			</view>
			<view class="other">
				<view class="other-item">
					<image src="../../static/img/wx.png"></image>
					<view>微信登录</view>
				</view>
				<view class="other-item" @click="goLoginCode">
					<image src="../../static/img/code.png"></image>
					<view>验证码登录</view>
				</view>
			</view>
		</view>
		
	</view>
</template>

<script>
	export default {
		data() {
			return {}
		},
		methods:{
			goLoginCode(){
				uni.navigateTo({
					url:"/pages/login-code/login-code"
				})
			}
		}
	}
</script>

<style>
	// 其他登录方式
	.login-other{
		padding: 10rpx 0;
	}
	.other-text{
		display: flex;
		padding: 50rpx 0;
	}
	.other-text view{
		line-height: 0rpx;
		padding: 0 10rpx;
	}
	.other-text:after{
		flex: 1;
		content:'';
		height:2rpx;
		background-color: #CCCCCC;
	}
	.other-text:before{
		flex: 1;
		content:'';
		height:2rpx;
		background-color: #CCCCCC;
	}
	.other{
		display: flex;
		justify-content:space-around;
	}
	.other-item{
		display: flex;
		flex-direction: column;
		justify-content: center;
		align-items: center;
	}
	.other-item image{
		width: 80rpx;
		height: 80rpx;
	}
</style>
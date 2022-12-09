<template>
	<!-- 商城系统主页活动信息 -->
	<view class="event">
		<view class="eventLeft">
			<image class="imgA" src="../../static/img/event1.jpg"></image>
		</view>
		<view class="eventRight">
			<image class="imgB" src="../../static/img/event2.jpg"></image>
			<image class="imgC" src="../../static/img/event3.jpg"></image>
		</view>
	</view>
</template>

<script>
</script>

<style scoped>
	.event{
		display: flex;
		
	}
	.eventLeft{
		width: 50%;
	}
	.imgA{
		width: 375rpx;
		height: 405rpx;
	}
	.eventRight{
		width: 50%;
	}
	.imgB{
		width: 375rpx;
		height: 200rpx;
	}
	.imgC{
		margin-top: -5rpx;
		width: 375rpx;
		height: 200rpx;
	}
</style>
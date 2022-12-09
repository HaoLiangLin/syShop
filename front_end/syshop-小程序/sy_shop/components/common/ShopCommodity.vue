<template>
	<view class="shopCommodity">
		<view class="shopCommodity-item">
			<view class="shopCommodity-left">
				<image class="shopCommodity-img" src="../../static/tabbar/my.png" mode=""></image>
			</view>
			<view class="shopCommodity-right">
				<view class="shopCommodity-content">
					<text class="shopCommodity-name">棉衣棉衣棉衣棉衣</text>
				</view>
				<view class="shopCommodity-text">
					<text class="price">￥200</text>
					<text class="market">月售量:2100</text>
					<text class="shopCart">加入购物车</text>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		
	}
</script>

<style scoped>
	.shopCommodity{
		display: flex;
		flex-wrap: wrap;
		width: 100%;
	}
	.shopCommodity-item{
		display: flex;
		padding-bottom: 10rpx;
		margin-bottom: 2rpx;
		
	}
	.shopCommodity-left{
		display: flex;
		width: 30%;
		position: relative;
		
	}
	.shopCommodity-img{
		height: 160rpx;
	}
	.shopCommodity-right{
		display: flex;
		flex-wrap: wrap;
		width: 70%;
	}
	.shopCommodity-content{
		display: flex;
		width: 90%;
		height: 120rpx;
	}
	.shopCommodity-text{
		display: flex;
		flex-wrap: wrap;
	}
	.shopCommodity-name{
		overflow: hidden;
		text-overflow: ellipsis;
		display: -webkit-box;
		-webkit-line-clamp: 2;
		-webkit-box-orient: vertical;
		color: #636263;
		word-break: break-all;
	}
	.price{
		font-size: 30rpx;
		color: #ff5733;
	}
	.market{
		font-size: 26rpx;
		margin-top: 3rpx;
		margin-left: 50rpx;
	}
	.shopCart{
		font-size: 30rpx;
		margin-left: 80rpx;
	}
</style>
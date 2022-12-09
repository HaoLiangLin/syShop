<template>
	
	<view class="goods-content">
		<image :src="setIcon(item.image)" mode="" class="goods-imag"></image><!-- :src="item.image" -->
		<view class="goods-pro">
			<view class="goods-text">{{item.goodsName}}</view>
			<view class="goods-size">{{item.color}} {{item.size}}</view>
			<view class="goods-text">数量:{{item.quantity}} 单价:{{item.unitPrice}}</view>
		</view>
	</view>
	
</template>

<script>
	import { baseURL } from '@/api/setting.js'
	import { getImage } from '@/utils/resources.js'
	export default{
		props:{
			item:Object,
			index:Number,
		},
		methods:{
			// 获取图片
			setIcon(icon) {
				if (icon) {
					const images = icon.split(',')
					return getImage(images[0])
				}
			},
		}
	}
</script>

<style>
	.goods-content{
		background-color: #F7F7F7;
		padding: 10rpx 20rpx;
		display: flex;
		justify-content:space-evenly;
		align-items: center;
	}
	.goods-imag{
		width: 160rpx;
		height: 160rpx;
	}
	.goods-pro{
		width: 500rpx;
		padding: 0 10rpx;
	}
	.goods-text{	
		font-size: 32rpx;
		color: black;
	}
	.goods-size{
		font-size: 24rpx;
		color: lightgray;
	}
</style>
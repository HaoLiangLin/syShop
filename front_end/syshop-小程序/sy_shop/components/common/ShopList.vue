<template>
	<!-- 商品列表页面 -->
	<view class="shop-list">
		<view class="word f-color">{{keyword}}</view>
		<Lines></Lines>
		<view class="shop-title f-color">		
			<view class="shop-item">
				<view>最新</view>
			</view>
			<view class="shop-item">
				<view>销售</view>
				<view class="shop-icon">
					<view class="iconfont icon-shangjiantou up"></view>
					<view class="iconfont icon-arrow-down down"></view>
				</view>
			</view>
			<view class="shop-item">
				<view>价格</view>
				<view class="shop-icon">
					<view class="iconfont icon-shangjiantou up"></view>
					<view class="iconfont icon-arrow-down down"></view>
				</view>
			</view>
		</view>
		<Lines></Lines>	
		<ShopCommodityList></ShopCommodityList>
	</view>
</template>

<script>
	import Lines from '@/components/common/Line.vue'
	import ShopCommodityList from '@/components/common/ShopCommodityList.vue'
	export default{
		props:{
			keyword:String
		},
		data() {
			return {
			}
		},
		components:{
			Lines,
			ShopCommodityList
		},
		methods: {
			
		}
	}
</script>

<style scoped>
	.word{
		text-align: center;
		font-size: 38rpx;
		margin-bottom: 10rpx;
	}
	 .shop-title{
	 	display: flex;
	 }
	 .shop-item{
	 	flex: 1;
	 	display: flex;
	 	justify-content: center;
	 	align-items: center;
	 	height: 80rpx;
	 }
	 .shop-icon{
	 	position: relative;
	 	margin-left: 10rpx;
	 }
	 .iconfont{
	 	width: 16rpx;
	 	height: 8rpx;
	 	position: absolute;
	 	left: 0;
	 }
	 .up{
	 	top: -25rpx;
	 }
	 .down{
	 	top: -10rpx;
	 }
</style>
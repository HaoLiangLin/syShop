<template>
	<view class="commodity" :style="'flex-wrap:'+wrap+';'">
		
		<!-- 单个商品组件 -->
		<view class="commodity-item" v-for="item in dataList" :key="item.goods.id" :style="'width:'+itemWidth+';'" @tap='goDetails(item)'>

			<image class="commodity-img" v-if="item.goods.images" :src="setIcon(item.goods.images)" mode="" :style="'height:'+imgHeight+';'"></image>
			<view class="commodity-content">
				<text class="commodity-name" :style="'font-size:'+nameSize+';'">{{item.goods.name}}</text>
			</view>
			<view>
				<text class="price">￥{{item.price}}</text>
				<text class="market" :style="'margin-left:'+marketLeft+';'">月售量:{{item.goods.sale}}</text>
			</view>
		</view>
		
	</view>
</template>

<script>
	import { getImage } from '@/utils/resources.js'
export default {
	props:{
		// 数据定义
		dataList:Array,
		// 定义默认换行
		wrap:{
			type:String,
			default:"wrap"
		},
		// 定义默认宽度
		itemWidth:{
			type:String,
			default:"375rpx"
		},
		// 定义默认高度
		imgHeight:{
			type:String,
			default:"375rpx"
		},
		// 定义默认字体大小
		nameSize:{
			type:String,
			default:"35rpx"
		},
		// 定义默认销售量距离左边距
		marketLeft:{
			type:String,
			default:"100rpx"
		}
	},
	methods:{
		goDetails(item){
			uni.navigateTo({
				url:`/pages/details/details?id=${item.goods.id}`
			})
		},
		// 获取图片
		setIcon(icon) {
			if (icon) {
				const images = icon.split(',')
				return getImage(images[0])
			}
		}
	}
}
</script>

<style scoped>
	.commodity{
		display: flex;
	}
	.commodity-item{
		padding-bottom: 20rpx;
	}
	.commodity-img{
		width: 100%;
	}
	.commodity-content{
		text-align: center;
	}
	.commodity-name{
		overflow: hidden;
		text-overflow: ellipsis;
		display: -webkit-box;
		-webkit-line-clamp: 2;
		-webkit-box-orient: vertical;
		color: #3333333;
		word-break: break-all;
		padding: 5rpx 20rpx;
	}
	.price{
		font-size: 30rpx;
		margin-left: 25rpx;
		color: #ff5733;
	}
	.market{
		font-size: 26rpx;
	}
</style>
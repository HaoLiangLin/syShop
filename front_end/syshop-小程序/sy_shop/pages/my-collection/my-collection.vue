<template>
	<view>
		
		<search class="header"></search>
		
		<view class="shop-tit f-color">
			<view class="shop-item">
				<view @click="getShopIsNewList">最新</view>
			</view>
			<view class="shop-item">
				<view>销售</view>
				<view class="shop-icon">
					<view class="iconfont icon-shangjiantou up" @click="getShopSaleSortAscList"></view>
					<view class="iconfont icon-arrow-down down" @click="getShopSaleSortDesList"></view>
				</view>
			</view>
			<view class="shop-item">
				<view>价格</view>
				<view class="shop-icon">
					<view class="iconfont icon-shangjiantou up" @click="getShopPriceSortAscList"></view>
					<view class="iconfont icon-arrow-down down" @click="getShopPriceSortDesList"></view>
				</view>
			</view>
		</view>
		<!-- @tap='goDetails(item)' -->
		<view class="menWear" v-for="item in shopCategoryData" :key="item.gid">
			<view class="thump">
				<image :src="setIcon(item.images)"></image>
			</view>
			<view class="info">
				<text class="shop-title">{{item.name}}</text>
				<text class="shop-price">￥{{item.price}}</text>
				<text>收藏编号：<text class="shop-score">{{item.collection}}</text></text>
				<view class="del-btn" @click.stop="delCollection(index)">移除收藏</view>
			</view>
		</view>
		
	</view>
</template>

<script>
	import { baseURL } from '@/api/setting.js'
	import search from '@/components/search/search2.vue'
	import { getImage } from '@/utils/resources.js'
	export default {
		data() {
			return {
				page:1,
				size:10,
				shopCategoryData:[],
				uid:''
			}
		},
		onLoad() {
			this.uid = uni.getStorageSync('userinfo').id
			this.getUserCollection()
		},
		methods: {
			//查询用户收藏
			getUserCollection(){
				uni.request({
					url:baseURL+"/collection/query",
					method:"GET",
					header:{
					    "authorization":uni.getStorageSync('token')
					},
					success:(res)=> {
						const data = res.data
						console.log(data.data)
						if(data.success){
							data.data.forEach(item => {
								this.shopCategoryData.push(item)
							})
						}
					}
				})
			},
			// 获取图片
			setIcon(icon) {
				if (icon) {
					const images = icon.split(',')
					return getImage(images[0])
				}
			},
			//移除收藏
			delCollection(index){
				const gid = this.shopCategoryData[index].gid;
				uni.request({
					url:baseURL+"/collection/delete/"+gid+"",
					method:"DELETE",
					header:{
					    "authorization":uni.getStorageSync('token')
					},
					data:{
						uid:this.uid
					},
					success:(res)=> {
						console.log(res.data)
						if(res.data.success){
							uni.showToast({
								title:"删除成功",
								icon:"success"
							})
						}else{
							uni.showToast({
								title:"删除失败",
								icon:"error"
							})
						}
					}
				})
			},
			//
		},
		components:{
			search,
		}
	}
</script>

<style>
.header{
	margin: 20rpx 0;
}
.shop-tit{
		display: flex;
		background-color: #F8F8F8;
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
		top: -27rpx;
	}
	.down{
		top: -7rpx;
	}
	.menWear{
		display: flex;
		padding: 10rpx;
		border: 1rpx solid #efefef;
		margin: 10rpx;
	}
	.thump image{
		width: 250rpx;
		height: 250rpx;
		display: block;/* 清除照片间的间距 */
		margin-right: 5rpx;
	}
	.info{
		display: flex;
		flex-direction: column;/* 定义纵向排序 */
		justify-content: space-evenly;/* 分散对齐 */
		font-size: 28rpx;
		margin-left: 26rpx;
	}
	.shop-title{
		font-size: 36rpx;
		font-weight: bold;
	}
	.shop-price{
		font-size: 34rpx;
		color: red;
		font-weight: bold;
	}
	.shop-score{
		color: red;
		font-weight: bold;
	}
	.del-btn{
		width: 200rpx;
		height: 50rpx;
		color: white;
		background-color: coral;
		border-radius: 24rpx;
		text-align: center;
		line-height: 50rpx;
	}
</style>

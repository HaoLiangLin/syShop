<template>
	<view>
		<Lines></Lines>
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
		<Lines></Lines>
		
		<view class="menWear" v-for="item in shopCategoryData" :key="item.goods.id" @tap='goDetails(item)'>
			<view class="thump">
				<image :src="setIcon(item.goods.images)"></image>
			</view>
			<view class="info">
				<text class="shop-title">{{item.goods.name}}</text>
				<text class="shop-price">￥{{item.price}}</text>
				<text>评分：<text class="shop-score">4.9</text></text>
				<view style="display: flex;">
					<view style="color: #9f9d9d;">月销量：{{item.goods.sale}}+</view>
					<view style="padding-left: 50rpx; color: #9f9d9d;">发货地：{{item.goods.city}}</view>
				</view>
			</view>
		</view>
	</view>
	
	
</template>

<script>
	import { getImage } from '@/utils/resources.js'
	import Lines from '@/components/common/Line.vue'
	import ShopCommodityList from '@/components/common/ShopCommodityList.vue'
	export default{
		data() {
			return {
				page:1,
				size:10,
				shopCategoryData:[],
				shopIsNewData:[]
			}
		},
		components:{
			Lines,
			ShopCommodityList
		},
		created() {
			this.getShopCategoryList(),
			this.getShopSaleSortAscList(),
			this.getShopSaleSortDesList(),
			this.getShopPriceSortAscList(),
			this.getShopPriceSortDesList(),
			this.getShopIsNewList()
		},
		methods: {
			goDetails(item){
				uni.navigateTo({
					url:`/pages/details/details?id=${item.goods.id}`
				})
			},
			// 获取商品列表数据的方法【商品的全部数据】
			getShopCategoryList(){
				this.shopCategoryData = []
				uni.request({
					url: `http://43.139.55.209:8081/api/goods/query/${this.page}/${this.size}`,
					method:'POST',
					success: (res) =>{
						const data = res.data
						// console.log(data)
						if(data.success){
							data.data.records.forEach(item => {
								this.shopCategoryData.push(item)
							})
						}
					}
				})
			},
			// 获取商品列表数据的方法【商品销量升序】
			getShopSaleSortAscList(){
				uni.request({
					url: `http://43.139.55.209:8081/api/goods/query/${this.page}/${this.size}`,
					method:'POST',
					data:{
						saleSort: 'Asc'
					},
					success: (res) =>{
						const data = res.data
						// console.log(data)
						if(data.success){
							this.shopCategoryData = []
							data.data.records.forEach(item => {
								this.shopCategoryData.push(item)
							})
						}
					}
				})
			},
			// 获取商品列表数据的方法【商品销量降序】
			getShopSaleSortDesList(){
				uni.request({
					url: `http://43.139.55.209:8081/api/goods/query/${this.page}/${this.size}`,
					method:'POST',
					data:{
						saleSort: 'Des'
					},
					success: (res) =>{
						const data = res.data
						// console.log(data)
						if(data.success){
							this.shopCategoryData = []
							data.data.records.forEach(item => {
								this.shopCategoryData.push(item)
							})
						}
					}
				})
			},
			// 获取商品列表数据的方法【商品价格升序】
			getShopPriceSortAscList(){
				uni.request({
					url: `http://43.139.55.209:8081/api/goods/query/${this.page}/${this.size}`,
					method:'POST',
					data:{
						priceSort: 'Asc'
					},
					success: (res) =>{
						const data = res.data
						// console.log(data)
						if(data.success){
							this.shopCategoryData = []
							data.data.records.forEach(item => {
								this.shopCategoryData.push(item)
							})
						}
					}
				})
			},
			// 获取商品列表数据的方法【商品价格降序】
			getShopPriceSortDesList(){
				uni.request({
					url: `http://43.139.55.209:8081/api/goods/query/${this.page}/${this.size}`,
					method:'POST',
					data:{
						priceSort: 'Des'
					},
					success: (res) =>{
						const data = res.data
						// console.log(data)
						if(data.success){
							this.shopCategoryData = []
							data.data.records.forEach(item => {
								this.shopCategoryData.push(item)
							})
						}
					}
				})
			},
			// 获取最新商品列表数据的方法
			getShopIsNewList(){
				uni.request({
					url: `http://43.139.55.209:8081/api/goods/query/${this.page}/${this.size}`,
					method:'POST',
					data:{
						isNew: 1
					},
					success: (res) =>{
						const data = res.data
						// console.log(data)
						if(data.success){
							this.shopCategoryData = []
							data.data.records.forEach(item => {
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
			}
		}
	}
</script>

<style scoped>
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
</style>
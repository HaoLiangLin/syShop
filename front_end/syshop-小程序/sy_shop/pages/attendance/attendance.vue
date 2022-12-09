<template>
	<view class="my">
		<!-- 头部 -->
		<view class="my-header">
			<view class="header-tip">？ 积分规则</view>
			<view class="header-main">
				<view class="title">{{points}}</view>
				<view class="title2">我的积分</view>
			</view>
			<!--  -->
			<view class="box">
				<view class="order-list">
					<view class="order-item">
						<view>已经连续签到</view>
						<view><text style="color: orange;">{{day}}</text>天</view>
					</view>
					<view class="order-item" @click="signin">
						<view class="attendance-btn">{{msg}}</view>
					</view>
				</view>
			</view>
		</view>
		<!-- 人气兑换 -->
		<view class="order">
			<view class="order-title">
				<view>人气兑换</view>
				<view>查看全部 ></view>
			</view>
			
			<view class="menWear" v-for="item in shopCategoryData" :key="item.goods.id" @tap='goDetails(item)'>
				<view class="thump">
					<image :src="setIcon(item.goods.images)"></image>
				</view>
				<view class="info">
					<text class="shop-title">{{item.goods.name}}</text>
					<text class="shop-price">￥{{item.price}}</text>
					<text>评分：<text class="shop-score">4.9</text></text>
					<view style="display: flex;">
						<view style="color: #9f9d9d;">月销量：{{item.goods.monthSale}}+</view>
						<view style="padding-left: 50rpx; color: #9f9d9d;">发货地：{{item.goods.city}}</view>
					</view>
				</view>
			</view>
			
		</view>
	</view>
</template>

<script>
	import { getImage } from '@/utils/resources.js'
	import { baseURL } from '@/api/setting.js'
	export default {
		data() {
			return {
				points:"",
				day:"",
				msg:"",
				attendStatus:false,
				page:1,
				size:10,
				shopCategoryData:[],
			}
		},
		onLoad() {
			this.getShopCategoryList();
			this.points = uni.getStorageSync('userpurse').points;
			//查询连续签到天数
			uni.request({
				url:baseURL+"/users/signIn/count",
				method:"GET",
				header:{
				    "authorization":uni.getStorageSync('token')
				},
				success:(res)=> {
					this.day = res.data.data
				}
			})
			//查询是否签到
			uni.request({
				url:baseURL+"/users/signIn/today",
				method:"GET",
				header:{
				    "authorization":uni.getStorageSync('token')
				},
				success:(res)=> {
					console.log(res.data)
					this.attendStatus = res.data.success,
					this.msg = res.data.message
				}
			})
		},
		methods:{
			// 获取图片
			setIcon(icon) {
				if (icon) {
					const images = icon.split(',')
					return getImage(images[0])
				}
			},
			//提交签到
			signin(){
				if(!this.attendStatus){
					uni.request({
						url:baseURL+"/users/signIn",
						method:"POST",
						header:{
						    "authorization":uni.getStorageSync('token')
						},
						success:(res)=> {						
							uni.showToast({
								title:"签到成功",
								icon:"success"
							},1500)
							this.msg = res.data.data;
							this.attendStatus = res.data.success;			
						}
					})
				}else{
					uni.showToast({
						title:"你已经签到了！",
						icon:"none"
					},1500)
				}
			},
			//获取商品图片
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
			//商品详情
			goDetails(item){
				uni.navigateTo({
					url:`/pages/details/details?id=${item.goods.id}`
				})
			},
		}
	}
</script>

<style>
.my-header{
	background-image: linear-gradient(-60deg, #ff5858 0%, #f09819 100%);
	width: 100%;
	height: 400rpx;
	position: relative;
}
.header-main{
	position: relative;
	margin: auto;
	padding: auto;
	text-align: center;
	top: 60rpx;
	height: 100%;
	color: white;
	font-weight: bold;
}
.title{
	font-size: 54rpx;
}
.title2{
	font-size: 30rpx;
}
.header-tip{
	position: relative;
	left: 80%;
	color: white;
	font-size: 24rpx;
}
.box{
	border: 1px solid gray;
	background-color: #FFFFFF;
	width: 600rpx;
	height: 160rpx;
	position: absolute;
	left: 50%;
	margin-left: -300rpx;
	margin-right: -300rpx;
	top: 80%;
	border-radius: 8px;
	box-shadow: 1px 2px 5px 0 rgba(0, 0, 0, .5);
}
.attendance-btn{
	color: white;
	background-color: #f09819;
	border-radius: 30rpx;
	padding: 20rpx 20rpx;
}
.order{
	padding-top: 100rpx;
}
.order-title{
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 20rpx;
}
.order-list{
	padding: 20rpx;
	display: flex;
}
.order-item{
	flex: 1;
	display: flex;
	flex-direction: column;
	justify-content: center;
	align-items: center;
	font-size: 30rpx;
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

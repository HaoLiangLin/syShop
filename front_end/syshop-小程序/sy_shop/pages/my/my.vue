<template>
	<view class="my">
		<!-- 头部 -->
		<view class="my-header">
			<view class="header-main">
				<view class="header-ava">
					<image :src="setIcon(ava)" class="ava-img"></image>
					<view class="ava-name" @click="goLogin">{{nickname}}</view>
				</view>
				<view class="header-config">
					<button class="header-btn" @click="goAttendance">签到送金币</button>
				</view>
			</view>
			<!--  -->
			<view class="box">
				<view class="order-list">
					<view class="order-item" @click="goPurse">
						<view>{{balance}}</view>
						<view>余额</view>
					</view>
					<view class="order-item" @click="goPoints">
						<view>{{points}}</view>
						<view>积分</view>
					</view>
					<!-- <view class="order-item">
						<view>0</view>
						<view>金币</view>
					</view> -->
				</view>
			</view>
		</view>
		<!-- 我的订单 我的服务-->
		<view class="order">
			<view class="order-title" @click="goOrder">
				<view>我的订单</view>
				<view>全部订单 ></view>
			</view>
			<view class="order-list" @click="goOrder">
				<view class="order-item">
					<image src="../../static/img/waitpay.png" class="icon"></image>
					<view>待付款</view>
				</view>
				<view class="order-item">
					<image src="../../static/img/waitpost.png" class="icon"></image>
					<view>待发货</view>
				</view>
				<view class="order-item">
					<image src="../../static/img/waitget.png" class="icon"></image>
					<view>待收货</view>
				</view>
				<view class="order-item">
					<image src="../../static/img/ok2.png" class="icon"></image>
					<view>已完成</view>
				</view>
				<view class="order-item">
					<image src="../../static/img/under-service.png" class="icon"></image>
					<view>退货/售后</view>
				</view>
			</view>
			<view class="order-title">
				<view>我的服务</view>
				<view></view>
			</view>
			<view class="order-list">
				<view class="order-item" @click="setAddress">
					<image src="../../static/img/add-path.png" class="icon"></image>
					<view>地址管理</view>
				</view>
				<view class="order-item" @click="goCollection">
					<image src="../../static/img/my-collection.png" class="icon"></image>
					<view>我的收藏</view>
				</view>
				<view class="order-item">
					<image src="../../static/img/quick-time.png" class="icon"></image>
					<view>我的秒杀</view>
				</view>
				<view class="order-item">
					<image src="../../static/img/introduction.png" class="icon"></image>
					<view>我的推广</view>
				</view>
				<view class="order-item">
					<image src="../../static/img/teams.png" class="icon"></image>
					<view>我的拼团</view>
				</view>
			</view>
		</view>
		<!-- 猜你喜欢 -->
		<view class="mylike">
			<view class="order-title">
				<view>猜你喜欢</view>
				<view></view>
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
						<view style="color: #9f9d9d;">月销量：{{item.goods.sale}}+</view>
						<view style="padding-left: 50rpx; color: #9f9d9d;">发货地：{{item.goods.city}}</view>
					</view>
				</view>
			</view>
			
		</view>
	</view>
</template>

<script>
	import { baseURL } from '@/api/setting.js'
	import {mapState} from "vuex"
	import { getImage } from '@/utils/resources.js'
	export default {
		data() {
			return {
				nickname:'登录/注册',
				balance:"无",
				points:"无",
				page:1,
				size:10,
				shopCategoryData:[],
				ava:''
			}
		},
		onLoad() {
			this.getShopCategoryList()
		},
		//发送请求头获取用户信息
		onShow() {
				if(uni.getStorageSync('token')){
					uni.request({
						url:baseURL+"/users/me",
						method:"GET",
						header:{
						    "authorization":uni.getStorageSync('token')
						},
						success:(res)=> {
							console.log(res.data.data),
							this.ava = res.data.data.icon
							this.nickname=res.data.data.nickname,
							uni.setStorageSync('userinfo',res.data.data)
						}
					})
					//获取用户钱包信息
					uni.request({
						url:baseURL+"/account/query",
						method:"GET",
						header:{
						    "authorization":uni.getStorageSync('token')
						},
						success:(res)=> {
							console.log(res.data)
							this.balance = res.data.data.balance,
							this.points = res.data.data.points,
							uni.setStorageSync('userpurse',res.data.data)
						}
					})
					
				}
				
		},	
		methods: {	
			goLogin(){
				if(uni.getStorageSync('token')){
					uni.navigateTo({
						url:'/pages/my-config/my-config',
					})
				}else{
					uni.navigateTo({
						url:"/pages/login/login"
					})
				}
			},
			//设置地址
			setAddress(){
				if(uni.getStorageSync('token')){
					uni.navigateTo({
						url:"/pages/myAddress/myAddress"
					})
				}
			},
			//订单
			goOrder(){
				if(uni.getStorageSync('token')){
					uni.navigateTo({
						url:"/pages/my-order/my-order"
					})
				}
			},
			//签到
			goAttendance(){
				if(uni.getStorageSync('token')){
					uni.navigateTo({
						url:"/pages/attendance/attendance"
					})
				}
			},
			//用户钱包
			goPurse(){
				if(uni.getStorageSync('token')){
					uni.navigateTo({
						url:"/pages/user-purse/user-purse"
					})
				}
			},
			//积分钱包
			goPoints(){
				if(uni.getStorageSync('token')){
					uni.navigateTo({
						url:"/pages/user-purse/points-purse/points-purse"
					})
				}
			},
			//我的收藏
			goCollection(){
				if(uni.getStorageSync('token')){
					uni.navigateTo({
						url:"/pages/my-collection/my-collection"
					})
				}
			},
			//商品详情
			goDetails(item){
				uni.navigateTo({
					url:`/pages/details/details?id=${item.goods.id}`
				})
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
			// 获取图片
			setIcon(icon) {
				if (icon) {
					const images = icon.split(',')
					return getImage(images[0])
				}
			}
		},
		// 关闭页面下拉刷新效果的处理函数
		onPullDownRefresh() {
			uni.stopPullDownRefresh()
		}
	}
</script>

<style>
.my-header{
	background-image: linear-gradient(-60deg, #ff5858 0%, #f09819 100%);
	width: 100%;
	height: 300rpx;
	position: relative;
}
.header-main{
	position: relative;
	top: 60rpx;
	height: 100%;
}
.header-config{
	position: absolute;
	left: 70%;
	right: 5%;
	top: 10%;
}
.header-btn{
	background-color:transparent;
	border: 2px solid gainsboro;
	width: 200rpx;
	font-size: 24rpx;
	height: 60rpx;
	color: white;
}
.header-ava{
	position: absolute;
	width:350rpx; 
	left: 10%;
	margin-left: -60rpx;
	display: flex;
	justify-content: space-between;
	align-items: center;
}
.ava-img{
	width: 120rpx;
	height: 120rpx;
	border: 2px solid #FFFFFF;
	border-radius: 50%;
	background-color: gray;
}
.ava-name{
	color: #FFFFFF;
	font-size: 40rpx;
	font-weight: bold;
	width: 200rpx;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}
.box{
	border: 1px solid gray;
	background-color: #FFFFFF;
	width: 700rpx;
	height: 130rpx;
	position: absolute;
	left: 50%;
	margin-left: -350rpx;
	margin-right: -350rpx;
	top: 80%;
	border-radius: 8px;
	box-shadow: 1px 2px 5px 0 rgba(0, 0, 0, .5);
}
.order{
	padding-top: 80rpx;
}
.order-title{
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 20rpx;
	font-weight: bold;
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
.icon{
	width: 80rpx;
	height: 80rpx;
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

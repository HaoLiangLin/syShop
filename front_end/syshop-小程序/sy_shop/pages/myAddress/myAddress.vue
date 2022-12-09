<template>
	<view class="my-address-list">
		
		<view class="address-list">
			
			<view v-for="(path,index) in list" :key="index" @click="updatePath(index)">
				<view class="address-item" @click="goOrder(path)">
					<view class="item-main">
						<view class="main-name">{{path.name}}</view>
						<view>{{path.phone}}</view>
						<view class="active" v-if="path.isDefault">默认</view>
						<view class="del-btn" @click.stop="delPath(index)">一</view>
					</view>
					<view class="item-main">
						<view>{{path.province}} {{path.city}} {{path.district}} {{path.address}}</view>
					</view>
				</view>
			</view>
			
		</view>
		
		<view class="add-address">
			<view class="add-btn" @click="addAddress">+新增地址</view>
		</view>
		
	</view>
</template>

<script>
	import { baseURL } from '@/api/setting.js'
	import {mapState,mapMutations} from 'vuex'
	export default {
		data() {
			return {
				isSelectPath:false,
				pathId:""
			}
		},
		methods: {
			...mapMutations(['getUserPath']),
			//添加地址
			addAddress(){
				uni.navigateTo({
					url:"/pages/add-address/add-address"
				})
			},
			//修改地址
			updatePath(index){
				if(!this.isSelectPath){
					let pathObj = JSON.stringify({
						index:index,
						item:this.list[index]
					})
					uni.navigateTo({
						url:"/pages/add-address/add-address?data="+pathObj+""
					})
				}
			},
			//删除地址
			delPath(index){
				uni.showModal({
					title:"提示",
					content:"确认删除这个地址吗",
					success:(res)=> {
						if(res.confirm){
							this.pathId = this.list[index].id;
							uni.request({
								url: baseURL+"/address/delete/"+this.pathId+"",
								method:"DELETE",
								header:{
								    "authorization":uni.getStorageSync('token')
								},
								success:(res2)=> {
									console.log(res2.data)
								},
							})
						}
					}
				})
			},
			// 返回确认订单页面
			goOrder(path){
				// 从订单页过来才会执行
				if(this.isSelectPath){
					uni.$emit("selectPathItem",path)
					uni.navigateBack({
						delta:1
					})
				}
			}
		},
		computed:{
			...mapState({
				list:state=>state.path.list
			})
		},
		onLoad(e) {
			if(e.type==='selectPath'){
				this.isSelectPath = true
			}
			//进入页面获取地址
			uni.request({
				url: baseURL+"/address/list",
				method:"GET",
				header:{
				    "authorization":uni.getStorageSync('token')
				},
				success:(res)=> {
					console.log(res.data)
					this.getUserPath(res.data.data)
				}
			})
		}
	}
</script>

<style>
.add-address{
	width: 100%;
	display: flex;
	justify-content: center;
}
.add-btn{
	border: 2rpx solid coral;
	background-color: coral;
	color: white;
	border-radius: 30rpx;
	padding: 10rpx 120rpx;
}
.address-list{
	padding-left: 20rpx;
	padding-bottom: 10rpx;
}
.address-item{
	padding: 10rpx 0;
	border: 2rpx solid gainsboro;
}
.item-main{
	display: flex;
	justify-content: space-between;
}
.main-name{
	padding-right: 20rpx;
}
.active{
	padding: 2rpx 6rpx;
	margin: 0 10rpx;
	background-color: lightblue;
	color: #FFFFFF;
	border-radius: 10rpx;
	font-size: 32rpx;
	text-align: center;
}
.del-btn{
	background-color: red;
	color: white;
	border-radius:12rpx;
	font-size: 32rpx;
	text-align: center;
	font-weight: bold;
	padding: 0 10rpx;
}
</style>

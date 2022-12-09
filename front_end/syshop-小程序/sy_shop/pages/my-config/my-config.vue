<template>
	<view class="body">
		
		<view class="my-config">
			<view class="config-item" @click="goUpdateImg">
				<view>头像</view>
				<view class="config-header">
					<image :src="setIcon(userImg)" class="userImg"></image>
					<view class="config-mark">></view>
				</view>
			</view>
			<view class="config-item" v-for="(path,index) in text" :key="index" @click="goUpateInfo(index)">
				<view>{{path.name}}</view>
				<view class="config-mark">{{path.content}} ></view>
			</view>
			<view class="config-item">
				<view>性别：</view>
				<view class="config-mark">{{sex}}</view>
			</view>
			<view class="config-item" @click="goUpdatePhone">
				<view>手机号:</view>
				<view class="config-mark">{{phone}} ></view>
			</view>
			<view class="config-item" @click="goUpdatePwd">
				<view>修改登录密码</view>
				<view class="config-mark">></view>
			</view>
		</view>
		
<!-- 		<view class="my-config">
		</view> -->
		
		<view class="login-out1" @click="goPerson">修改个人信息</view>
		<view class="login-out2" @click="loginOut">退出登录</view>
		
	</view>
</template>

<script>
	import { baseURL } from '@/api/setting.js'
	import { getImage } from '@/utils/resources.js'
	export default {
		data() {
			return {
				userImg:'',
				text:[
					{name:"用户名",content:''},
					{name:"昵称",content:''},
				],
				sex:"",
				phone:""
			}
		},
		methods: {
			//退出登录
			loginOut(){
				uni.removeStorageSync('token'),
				uni.removeStorageSync('userinfo'),
				uni.removeStorageSync('userpurse'),
				uni.reLaunch({
					url:"/pages/my/my"
				})
			},
			// 获取图片
			setIcon(icon) {
				if (icon) {
					const images = icon.split(',')
					return getImage(images[0])
				}
			},
			//修改头像
			goUpdateImg(){
				uni.navigateTo({
					url:"/pages/update-userinfo/update-img"
				})
			},
			//修改个人信息(昵称，用户名)
			goUpateInfo(index){
				let pathObj = JSON.stringify({
					index:index,
					item:this.text[index]
				})
				uni.navigateTo({
					url:"/pages/update-userinfo/update-userinfo?data="+pathObj+""
				})
			},
			goUpdatePhone(){
				uni.navigateTo({
					url:"/pages/update-userinfo/update-phone?data="+this.phone+""
				})
			},
			goUpdatePwd(){
				uni.navigateTo({
					url:"/pages/update-userinfo/update-password",
				})
			},
			goPerson(){
				uni.navigateTo({
					url:"/pages/update-userinfo/update-personinfo"
				})
			}
		},
		onLoad() {
			uni.request({
				url:baseURL+"/userInfo/me",
				method:"GET",
				header:{
				    "authorization":uni.getStorageSync('token')
				},
				success:(res)=> {
					this.sex = res.data.data.gender
				}
			});
			let obj=uni.getStorageSync('userinfo')
			this.userImg=obj.icon//this.userImg+=obj.icon
			this.text[0].content=obj.username
			this.text[1].content=obj.nickname
			this.phone=obj.phone
		}
	}
</script>

<style scoped>
.body{
	background-color: whitesmoke;
	width: 100%;
	height: 100vh;
}
.my-config{
	padding-bottom: 50rpx;
	margin-bottom: 50rpx;
	border-radius: 30rpx;
	background-color: white;
}
.config-item{	
	width: 100%;
	display: flex;
	justify-content: space-between;
	padding: 20rpx 0 20rpx 20rpx;
	border-bottom: 2rpx solid lightgray;
	line-height: 80rpx;
}
.config-header{
	display: flex;
	justify-content: center;
	align-items: center; 
}
.userImg{
	width: 80rpx;
	height: 80rpx;
	background-color: gray;
	border: 2px solid #FFFFFF;
	border-radius: 50%;
}
.config-mark{
	padding-right: 40rpx;
}
.login-out1{
	background-color: white;
	width: 100%;
	line-height: 100rpx;
	color: black;
	border-radius: 30rpx;
	text-align: center;
	font-weight: bold;
	margin-top: 20rpx;
}
.login-out2{
	background-color: lightpink;
	width: 100%;
	line-height: 100rpx;
	color: black;
	border-radius: 30rpx;
	text-align: center;
	font-weight: bold;
	margin-top: 20rpx;
}
</style>

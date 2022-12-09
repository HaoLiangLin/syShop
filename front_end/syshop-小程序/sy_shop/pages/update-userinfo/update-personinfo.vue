<template>
	<view class="body">
		
		<view class="my-config">
			
			<view class="config-item" v-for="(pathObj,index) in list" :key="index">
				<view>
					{{pathObj.header}}
				</view>
				<view class="config-mark">
					<input v-model="pathObj.content" placeholder="请输入"/>
				</view>
			</view>
			
			<view class="config-item">
				<view>
					性别：
				</view>
				<view class="config-mark">
					<picker @change="bindPickerChange" :value="index" :range="sex" range-key="name">
						<view style="background-color: white;">{{ sex[index].name }} ></view>
					</picker>
				</view>
			</view>
			
		</view>
				
		<view class="login-out1" @click="commit">保存</view>
		
	</view>
</template>

<script>
	import { baseURL } from '@/api/setting.js'
	export default {
		data() {
			return {
				list:[
					{header:"姓名",content:""},
					{header:"年龄",content:18},
					{header:"电子邮箱",content:""},
					// {header:"生日",content:""},
					{header:"QQ",content:""},
				],
				sex:[
					{id:0,name:"保密"},
					{id:1,name:"男"},
					{id:2,name:"女"}
				],
				index:0
			}
		},
		methods: {
			//选择性别
			bindPickerChange: function(e) {
				this.index = e.detail.value;
			},
			//提交信息
			commit(){
				uni.request({
					url:baseURL+"/userInfo/update",
					method:"PUT",
					header:{
					    "authorization":uni.getStorageSync('token')
					},
					data:{
						fullName:this.list[0].content,
						gender:this.sex[this.index].name,
						age:this.list[1].content,
						email:this.list[2].content,
						birthday:"",
						// this.list[3].content
						qq:this.list[4].content
					},
					success:(res)=> {
						console.log(res.data)
						uni.navigateBack({
							delta:1
						})
					}
				})
			}
		},
		onLoad() {
			
		},
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

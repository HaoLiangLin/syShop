<template>
	<view class="body">
		
		<view class="my-config">
			
			<view class="config-item">
				<view>请输入旧密码:</view>
				<view class="config-mark">
					<input v-model="oldpwd"/>
				</view>
			</view>
			
			<view class="config-item">
				<view>请输入新密码:</view>
				<view class="config-mark">
					<input v-model="newpwd"/>
				</view>
			</view>
		</view>
		
		<view class="login-out1" @click="commit">保存</view>
		
	</view>
</template>

<script>
	export default {
		data() {
			return {
				oldpwd:"",
				newpwd:""
			}
		},
		methods: {
			commit(){
				uni.request({
					url:"http://43.139.55.209:8081/api/users/setPassword",
					method:"PUT",
					header:{
					    "authorization":uni.getStorageSync('token')
					},
					data:{
						oldPassword:this.oldpwd,
						newPassword:this.newpwd
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

<template>
	<view class="body">
		
		<view class="my-config">
			
			<view class="config-item">
				<view>
					{{pathObj.name}} :
				</view>
				<view class="config-mark">
					<input v-model="pathObj.content"/>
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
				pathObj:{
					name:"",
					content:""
				},
				i:-1
			}
		},
		methods: {
			commit(){
				//修改用户名
				if(this.i===0){
					uni.request({
						url:"http://43.139.55.209:8081/api/users/updateUsername",
						method:"PUT",
						header:{
						    "authorization":uni.getStorageSync('token')
						},
						data:{
							username:this.pathObj.content
						},
						success:(res)=> {
							console.log(res.data)
							uni.navigateBack({
								delta:1
							})
						}
					})
				}
				//修改昵称
				if(this.i===1){
					uni.request({
						url:"http://43.139.55.209:8081/api/users/updateNickName",
						method:"PUT",
						header:{
						    "authorization":uni.getStorageSync('token')
						},
						data:{
							nickname:this.pathObj.content
						},
						success:(res)=> {
							console.log(res.data)
							uni.navigateBack({
								delta:1
							})
						}
					})
				}
			}
		},
		onLoad(e) {
			if(e.data){
				let result=JSON.parse(e.data);
				console.log(result)		
				this.pathObj = result.item;
				this.i=result.index;
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

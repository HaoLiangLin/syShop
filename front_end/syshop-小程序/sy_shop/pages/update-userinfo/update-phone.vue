<template>
	<view class="body">
		
		<view class="my-config">
			
			<view class="config-item">
				<view>手机号:</view>
				<view class="config-mark">
					<input v-model="phone"/>
				</view>
			</view>
			
			<view class="config-item">
				<view>请输入验证码:</view>
				<view class="config-mark">
					<input v-model="code"/>
				</view>
			</view>
		</view>
		
		<button class="login-out2" @click="sendCode" :disabled="disabled">{{codeMsg}}</button>
		<view class="login-out1" @click="commit">保存</view>
		
	</view>
</template>

<script>
	export default {
		data() {
			return {
				phone:"",
				code:"",
				//倒计时时间
				codeNum:10,
				//显示的文本
				codeMsg:"发送验证码",
				//按钮是否禁用
				disabled:false,		
			}
		},
		methods: {
			//点击验证码发送
			sendCode(){
				//请求接口返回验证码
				uni.request({
					url:"http://43.139.55.209:8081/api/users/codePhone",
					method:"GET",
					header:{
					    "authorization":uni.getStorageSync('token')
					},
					data:{
						phone:this.phone
					},
					success:(res)=> {
						console.log(res)
					}
				})
				//重新发送验证码倒计时
				this.disabled = true;
				this.codeMsg = '重新发送('+this.codeNum+')';
				let timer = setInterval(()=>{
					--this.codeNum
					this.codeMsg = '重新发送('+this.codeNum+')';
				},1000);
				setTimeout(()=>{
					clearInterval(timer)
					this.codeNum=10;
					this.disabled = false;
					this.codeMsg = '重新发送';
				},10000)
			},
			//提交修改信息
			commit(){
				uni.request({
					url:"http://43.139.55.209:8081/api/users/updatePhone/"+this.phone+"/"+this.code+"",
					method:"PUT",
					header:{
					    "authorization":uni.getStorageSync('token')
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
		onLoad(e) {
			let res = e.data;
			console.log(res)
			this.phone = res
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

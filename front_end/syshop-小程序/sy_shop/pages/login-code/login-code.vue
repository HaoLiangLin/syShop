<template>
	<view>
		
		<view class="login-tel">
			<view class="logo">
				<view class="title">
					<h1>水院商城</h1>
				</view>
			</view>
			<view class="tel-main2">
				<view class="register">
					<view class="register-item2">
						<image src="../../static/img/phone.png"></image>
						<input placeholder="请输入手机号" type="text" v-model="userphone"/>
					</view>
					<view class="register-item2" style="width: 480rpx;margin-left: 95rpx;">
						<image src="../../static/img/code2.png"></image>
						<input placeholder="请输入验证码" type="text" v-model="code"/>
						<button class="code" :disabled="disabled" @click="sendCode">{{codeMsg}}</button>
					</view>
					<view class="register-item2">
						<button class="btn" @click="loginCode">登录</button>
					</view>
				</view>
			</view>			
		</view>
		
		
		
	</view>
</template>

<script>
	export default {
		data() {
			return {
				userphone:"",
				code:"",
				loginType:1,
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
					url:"http://43.139.55.209:8081/api/users/code",
					method:"GET",
					data:{
						phone:this.userphone
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
			//验证码登录
			loginCode(){
				uni.request({
					url:"http://43.139.55.209:8081/api/users/login",
					method:"POST",
					data:{
						phone:this.userphone,
						code:this.code,
						loginType:this.loginType
					},
					success:(res)=> {
						console.log(res.data)
						uni.setStorageSync('token',res.data.data)
						if(res.data.success){
							uni.switchTab({
								url:"/pages/my/my"
							})
						}else{
							uni.showModal({
								title:"系统提示",
								content:res.data.message,
							})
						}
					}
				})
			},
		},
	}
</script>

<style>
.login-tel{
		width: 100vw;
		height: 100vh;
		position: relative;
	}
	.tel-main2{
		position: absolute;
		background-color: white;
		width: 700rpx;
		height: 450rpx;
		left: 50%;
		margin-left: -350rpx;
		margin-right: -350rpx;
		border: 1px solid black;
		border-radius: 8px;
		box-shadow: 1px 2px 5px 0 rgba(0, 0, 0, .5);
		top:30%
	}
	.logo{
		background-image: linear-gradient(-60deg, #ff5858 0%, #f09819 100%);
		justify-content: center;
		width: 100%;
		height: 400rpx;
		margin: auto;
		padding: auto;
		line-height: 400rpx;
		text-align: center;
		font-size: 80rpx;
		font-weight: bold;
	}
.register-item2{
		display: flex;
		justify-content: center;
		align-items: center;
		padding: 30rpx 0;
	}
	.register-item2 image{
		width: 60rpx;
		height: 60rpx;
	}
	.register-item2 input{
		border-bottom: 1px solid gainsboro;
	}
	.btn {
		background-color: coral;
		color: white;
		width: 90%;
		height: 100rpx;
		border-radius: 8rpx;
	}

	.code{
		font-size: 24rpx;
		border: 1px solid orange;
		background-color:#ffaf23;
		border-radius: 20rpx;
		width: 250rpx;
		height: 80rpx;
		text-align: center;
		line-height: 80rpx;
		color: white;
	}
	.tips{
		position: absolute;
		top: 95%;
		left: 50%;
		margin-left: -240rpx;
		margin-right: -250rpx;
		width: 500rpx;
		font-size: 32rpx;
		color: #757070;
	}
</style>

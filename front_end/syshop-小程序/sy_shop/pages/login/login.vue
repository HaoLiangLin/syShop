<template>
	
	<view class="login">
		<swiper vertical="true" style="height: 100vh;">
			<swiper-item>
				<scroll-view>
					<!-- 注册页 -->
					<view class="login-tel">
						<view class="logo">
							<view class="title">
								<h1>水院商城</h1>
							</view>
						</view>
						<view class="tel-main">
							<view class="register">
								<view class="register-item">
									<image src="../../static/img/user.png"></image>
									<input placeholder="请输入用户名" type="text" v-model="username"/>
								</view>
								<view class="register-item">
									<image src="../../static/img/phone.png"></image>
									<input placeholder="请输入手机号" type="text" v-model="userphone"/>
								</view>
								<view class="register-item" style="width: 480rpx;margin-left: 95rpx;">
									<image src="../../static/img/code2.png"></image>
									<input placeholder="请输入验证码" type="text" v-model="code">
									<button class="code" :disabled="disabled" @click="sendCode">{{codeMsg}}</button>
								</view>
								<view class="register-item">
									<image src="../../static/img/pwd.png"></image>
									<input placeholder="请输入密码" type="safe-password" v-model="password"/>
								</view>
								<view class="register-item">
									<button class="btn" @click="register">注册</button>
								</view>
							</view>
						</view>
						<view class="tips">
							<view>已有账号？向下滑动进入登录页面</view>
						</view>
					</view>

				</scroll-view>
			</swiper-item>
			<swiper-item>
				<scroll-view scroll-y="true">
					<!-- 登录页 -->
					<view class="login-tel">
						<view class="logo">
							<view class="title">
								<h1>水院商城</h1>
							</view>
						</view>
						<view class="tel-main2">
							<view class="register">
								<view class="register-item2">
									<image src="../../static/img/user.png"></image>
									<input placeholder="请输入用户名" type="text" v-model="username"/>
								</view>
								<view class="register-item2">
									<image src="../../static/img/pwd.png"></image>
									<input placeholder="请输入密码" type="password" v-model="password"/>
								</view>
								<view class="register-item2">
									<button class="btn" @click="login">登录</button>
								</view>
							</view>
						</view>
						<LoginOther class="other-login"></LoginOther>
						<view class="tips">
							<view>没有账号？向上滑动进入注册页面</view>
						</view>
					</view>
					
				</scroll-view>
			</swiper-item>
		</swiper>

		
	</view>
</template>

<script>
	import LoginOther from '@/components/login/login-other.vue'
	import { baseURL } from '@/api/setting.js'
	export default {
		name: "my-login",
		data() {
			return {
				username:'',
				userphone:"",
				password:'',
				code:'',
				loginType:0,
				//验证的规则
				rules:{
					username:{
						rule:/\S/,
						msg:"账号不能为空"
					},
					password:{
						rule:/^[0-9a-zA-z]{6,16}$/,
						msg:"密码应该为6-16位字符"
					},
					userphone:{
						rule:/^1[3456789]\d{9}$/,
						msg:"请输入11位手机号"
					}
				},
				//倒计时时间
				codeNum:10,
				//显示的文本
				codeMsg:"发送验证码",
				//按钮是否禁用
				disabled:false,	
			}
		},
		components:{
			LoginOther
		},
		methods: {
			//点击注册验证码发送
			sendCode(){
				//请求接口返回验证码
				uni.request({
					url:baseURL+"/users/register/code",
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
			//
			login(){
				if( !this.valiDate("username") ) return;
				if( !this.valiDate("password") ) return;
				uni.showLoading({
					title:"登录中..."
				})			
				uni.request({
					url:baseURL+"/users/login",
					method:"POST",
					data:{
						username:this.username,
						password:this.password,
						loginType:this.loginType
					},
					success:(res)=>{
						console.log(res.data.data)
						uni.setStorageSync('token',res.data.data)
						if(res.data){
							uni.navigateBack({
								delta:1
							})
						}
					}
				})
			},
			register(){
				if( !this.valiDate("username") ) return;
				if( !this.valiDate("userphone") ) return;
				if( !this.valiDate("password") ) return;
				uni.request({
					url:baseURL+"/users/register",
					method:"POST",
					data:{
						username:this.username,
						phone:this.userphone,
						code:this.code,
						password:this.password
					},
					success:(res)=> {
						uni.showToast({
							title:res.data.data
						},2000)
						this.login()
					}
				})
				
			},
			// 判断验证是否符合要求
			valiDate(key){
				let bool = true;
				if( !this.rules[key].rule.test(this[key]) ){
					uni.showToast({
						title:this.rules[key].msg,
						icon:"none"
					})
					bool = false;
					return false;
				}
				return bool;
			},
			
		}
	}
</script>

<style lang="scss">
	.login-tel{
		width: 100vw;
		height: 100vh;
		position: relative;
	}
	.tel-main{
		position: absolute;
		background-color: white;
		width: 600rpx;
		height: 650rpx;
		left: 50%;
		margin-left: -300rpx;
		margin-right: -300rpx;
		border: 1px solid black;
		border-radius: 8px;
		box-shadow: 1px 2px 5px 0 rgba(0, 0, 0, .5);
		top:30%
	}
	.tel-main2{
		position: absolute;
		background-color: white;
		width: 600rpx;
		height: 400rpx;
		left: 50%;
		margin-left: -300rpx;
		margin-right: -300rpx;
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
	.register-item{
		display: flex;
		justify-content: center;
		align-items: center;
		padding: 30rpx 0;
	}
	.register-item image{
		width: 60rpx;
		height:60rpx;
	}
	.register-item input{
		border-bottom: 1px solid gainsboro;
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
	.code{
		font-size: 26rpx;
		border: 1px solid orange;
		background-color:#ffaf23;
		border-radius: 20rpx;
		width: 300rpx;
		height: 80rpx;
		text-align: center;
		line-height: 80rpx;
		color: white;
	}
	.btn {
		background-color: coral;
		color: white;
		width: 90%;
		height: 100rpx;
		border-radius: 8rpx;
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
	.other-login{
		position: absolute;
		width: 800rpx;
		left: 50%;
		margin-left: -400rpx;
		margin-right: -400rpx;
		top:70%
	}
	
</style>

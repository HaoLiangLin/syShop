<template>
	<view class="user-purse">
		
		<view class="my-header">
			<view class="header-main">
				<view class="title">账户余额:</view>
				<view class="title2">{{balance}}元</view>
			</view>
		</view>
		
		<view class="main">
			<view class="main-item">
				<view class="combo" hover-class="click" hover-stay-time="1000" @click="pay100">
					<view class="text-type">{{combo[0].price}}</view>
					<view class="text-active">{{combo[0].name}}</view>
				</view>
				<view class="combo" hover-class="click" hover-stay-time="1000" @click="pay200">
					<view class="text-type">{{combo[1].price}}</view>
					<view class="text-active">{{combo[1].name}}</view>
				</view>
			</view>
			<view class="main-item">
				<view class="combo" hover-class="click" hover-stay-time="1000" @click="pay500">
					<view class="text-type">{{combo[2].price}}</view>
					<view class="text-active">{{combo[2].name}}</view>
				</view>
				<view class="combo" hover-class="click" hover-stay-time="1000" @click="pay1000">
					<view class="text-type">{{combo[3].price}}</view>
					<view class="text-active">{{combo[3].name}}</view>
				</view>
			</view>
		</view>
		
		<view class="tip">
			<view style="color: gray;">
				支付即代表同意
				<view style="color: coral;">《账户充值协议》</view>
			</view>
		</view>
		
		<view class="btn" @click="commit">确认支付 ￥{{pay}}</view>
		
	</view>
</template>

<script>
	import { baseURL } from '@/api/setting.js'
	export default {
		data() {
			return {
				balance:"",
				combo:[
					// {id:1,type:100,active:"赠送10积分"},
					// {id:2,type:200,active:"赠送24积分"},
					// {id:3,type:500,active:"赠送75积分"},
					// {id:4,type:1000,active:"赠送200积分"}
				],
				pay:0,
				comboid:""
			}
		},
		methods: {
			pay100(){
				this.pay = this.combo[0].price
				this.comboid = this.combo[0].id
			},
			pay200(){
				this.pay = this.combo[1].price
				this.comboid = this.combo[1].id
			},
			pay500(){
				this.pay = this.combo[2].price
				this.comboid = this.combo[2].id
			},
			pay1000(){
				this.pay = this.combo[3].price
				this.comboid = this.combo[3].id
			},
			//提交充值信息
			commit(){
				uni.showModal({
						title: '确认支付弹窗',
						content: '确认要充值'+this.pay+'元吗？',
						success: (res)=> {
						if (res.confirm) {
							uni.request({
								url:baseURL+"/account/recharge",
								method:"PUT",
								header:{
								    "authorization":uni.getStorageSync('token')
								},
								data:{
									rechargeComboId:this.comboid,
									recharge:this.pay
								},
								success:(res)=> {
									console.log(res.data)
									uni.navigateBack({
										delta:1
									})
								}
							})
						} else {
							return
						}
					}
				})
			}
		},
		onLoad() {
			let res = uni.getStorageSync('userpurse')
			console.log(res)
			this.balance = res.balance;
			uni.request({
				url:baseURL+"/rechargeCombo/query",
				method:"GET",
				header:{
				    "authorization":uni.getStorageSync('token')
				},
				success:(res)=> {
					// console.log(res.data.data)
					this.combo = res.data.data
				}
			})
		}
	}
</script>

<style>
.my-header{
	background-image: linear-gradient(-60deg, #ff5858 0%, #f09819 100%);
	width: 100%;
	height: 300rpx;
	margin: auto;
	padding: auto;
	text-align: center;
}
.header-main{
	height: 400rpx;
	width: 100%;
	color: white;
	font-weight: bold;
	padding-top: 100rpx;
}
.title2{
	font-size: 54rpx;
}
.title{
	font-size: 30rpx;
}
.main{
	padding: 20rpx 20rpx;
}
.main-item{
	display: flex;
	justify-content: center;
	align-items: center;
}
.combo{
	width: 300rpx;
	height: 200rpx;
	text-align: center;
	line-height: 100rpx;
	padding: 30rpx 20rpx;
	margin: 20rpx 20rpx;
	border: 2rpx solid gainsboro;
}
.click{
	border: 2rpx solid coral;
}
.text-type{
	font-weight: bold;
	font-size: 60rpx;
}
.text-active{
	font-size: 32rpx;
}
.tip{
	text-align: center;
	font-size: 32rpx;
}
.btn{
	border-radius: 30rpx;
	background-color: #ca4545;
	color: white;
	line-height: 80rpx;
	padding: 0 30rpx;
	margin-top: 20rpx;
	text-align: center;
}
</style>

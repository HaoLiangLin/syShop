<template>
	<view class="body">
		
		<view class="my-config">
			<view class="config-item" style="line-height: 200rpx;">			
				<view class="config-header">
					<image :src="setIcon(userImg)" class="userImg"></image>
				</view>
			</view>
			
			<view class="config-item" @click="uploadImg">
				<view>上传头像</view>
				<view class="config-mark">></view>
			</view>
			
			<view class="config-item" @click="updateAva">
				<view>确定提交头像</view>
				<view class="config-mark">></view>
			</view>
		</view>
				

		
	</view>
</template>

<script>
	import { getImage } from '@/utils/resources.js'
	import { baseURL } from '@/api/setting.js'
	export default {
		data() {
			return {
				userImg:'',
			}
		},
		methods: {
			// 获取图片
			setIcon(icon) {
				if (icon) {
					const images = icon.split(',')
					return getImage(images[0])
				}
			},
			//上传头像
			uploadImg(){
				uni.chooseImage({
					count:1,
					sizeType: ['original', 'compressed'], //可以指定是原图还是压缩图，默认二者都有
					sourceType: ['album'], //从相册选择
					success:(res)=> {
						const tempFilePaths = res.tempFilePaths;//JSON.stringify(res.tempFilePaths)
						uni.uploadFile({
							url:baseURL+"/users/uploadIcon",
							filePath:tempFilePaths[0],
							name:'file',//后台接收字段名
							header:{
								"Content-Type": "multipart/form-data",
							    "authorization":uni.getStorageSync('token')
							},
							success:(res2)=> {
								console.log(res2)
								const data = JSON.parse(res2.data)
								console.log(data)
								this.userImg = data.data
							}
						})			
					}
				})
			},
			//修改头像
			updateAva(){			
				uni.request({
					url:baseURL+"/users/updateIcon",
					method:"PUT",
					header:{
						"authorization":uni.getStorageSync('token')
					},
					data:{
						icon:this.userImg
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
			let obj=uni.getStorageSync('userinfo');
			this.userImg=obj.icon
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
	width: 180rpx;
	height: 180rpx;
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

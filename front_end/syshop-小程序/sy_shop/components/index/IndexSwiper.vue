<template>
	<!-- 商城系统主页轮播图 -->
	<swiper :indicator-dots="true" :autoplay="true" :interval="3000" :duration="1000">
		<swiper-item  v-for="item in swiperList" :key="item.id">
			<view class="swiper-item">
				<image class="swiper-img" :src="setIcon(item.icon)" mode=""></image>
			</view>
		</swiper-item>
	</swiper>
</template>

<script>
	import { getImage } from '@/utils/resources.js'
	export default {
		data() {
			return{
				// 存放轮播图数据的列表
				swiperList:[{ id: null, name: null, icon: null}]
			}
		},
		created() {
			this.getSwiperList()
		},
		methods: {
			// 获取轮播图数据的方法
			getSwiperList(){
				uni.request({
					url: 'http://43.139.55.209:8081/api/events/query',
					method: 'GET',
					success: (res) =>{
						const data = res.data
						if(data.success){
							this.swiperList = []
							data.data.forEach(item => {
								this.swiperList.push({
									id: item.id,
									name: item.name,
									icon: item.icon
								})
							})
							// console.log(this.swiperList)
						}
					}
				})
			},
			// 获取图片
			setIcon(icon) {
				return getImage(icon)
			}
		}
	}
</script>

<style scoped>
	.search-result {
			padding-top: 10px;
			padding-bottom: 20px;
			text-align: center;
		}
	
		.search-result-text {
			text-align: center;
			font-size: 14px;
			color:#666;
		}
	
		.example-body {
			/* #ifndef APP-NVUE */
			display: block;
			/* #endif */
			padding: 0px;
		}
	
		.uni-mt-10 {
			margin-top: 10px;
		}
		swiper{
			width: 100%;
			height: 400rpx;
		}
		.swiper-img{
			width: 100%;
			height: 400rpx;
		}
</style>

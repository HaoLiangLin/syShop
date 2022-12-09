<template>
		
		<view class="index">
			
			<SearchTap @click="gotoSearch"></SearchTap>
			<IndexSwiper></IndexSwiper>
			<Icons></Icons>
			<Slogan></Slogan>
			<Event></Event>
			<Card cardTitle="精选推荐"></Card>
			<Recommend></Recommend>
			<Card cardTitle="每日爆品"></Card>
			<Shop></Shop>
			<Card cardTitle="猜你喜欢"></Card>
			<CommodityList></CommodityList>
			
		</view>
</template>

<script>
	import SearchTap from '@/components/common/SearchTap.vue'
	import IndexSwiper from '@/components/index/IndexSwiper.vue'
	import Icons from '@/components/index/Icons.vue'
	import Slogan from '@/components/index/Slogan.vue'
	import Event from '../../components/index/Event.vue'
	import Commodity from '@/components/common/Commodity.vue'
	import Recommend from '@/components/index/Recommend.vue'
	import Shop from '@/components/index/Shop.vue'
	import Card from '@/components/common/Card.vue'
	import CommodityList from '@/components/common/CommodityList.vue'
	
	export default {
		data() {
			return {
				title: '欢迎来到小程序！'
			}
		},
		components:{
			SearchTap,
			IndexSwiper,
			Icons,
			Slogan,
			Event,
			Commodity,
			Recommend,
			Shop,
			Card,
			CommodityList
		},
		onLoad() {

		},
		// 关闭页面下拉刷新效果的处理函数
		onPullDownRefresh() {
			uni.stopPullDownRefresh()
		},
		// 页面上拉触底事件的处理函数
		onReachBottom() {
			console.log('触发了上拉触底事件！')
			// 需要进行节流处理【当上一触发事件未完成时，不能进行继续触发】
			uni.showLoading({
				title: '加载中'
			})
			setTimeout(function () {
				uni.hideLoading();
			}, 300);
		},
		// 在uni-app中：
		// 标题栏按钮点击
		onNavigationBarButtonTap(e) {
			if(e.float=='left'){
				uni.navigateTo({
					url:'../search/search'
				})
			}
		},
		methods: {
			// 在微信小程序中：
			gotoSearch(){
				// 通过编程式导航 导航到非tabBar页面
				uni.navigateTo({
					url:'/pages/search/search'
				})
			}
		}
	}
</script>

<style scoped>
	
</style>

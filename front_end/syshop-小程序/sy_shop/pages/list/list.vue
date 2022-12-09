<template>
	<view>
		<SearchTap marginTop="0rpx" @click="gotoSearch"></SearchTap>
		<Lines></Lines>
		
		<view class="list">
			<!-- 左侧滑动 -->
			<scroll-view scroll-y="true" class="list-left" :style="'height:'+clentHeight+'px;'">
				<view v-for="item in classData" class="left-item" @tap="changeLeftTab(item)">
					<view class="left-name" :class='activeIndex===item ? ".left-name-active" : ""' @click="onCategory(item)">
					{{item.name}}
					</view>
				</view>
			</scroll-view>
			
			<!-- 右侧滑动 -->
			<scroll-view scroll-y="true" class="list-right" :style="'height:'+clentHeight+'px;'">
				<view class="right-list">
					<view class="right-content">
						<view class="right-item" v-for="category in listData" :key="category.id" @click="gotoShopCategory">
							<image class="right-img" :src="setIcon(category.icon)" mode=""></image>
							<view class="right-name">{{category.name}}</view>
						</view>
					</view>
				</view>
			</scroll-view>
			
		</view>
	</view>
</template>

<script>
	import SearchTap from '../../components/common/SearchTap.vue'
	import Lines from '../../components/common/Line.vue'
	import { getImage } from '../../utils/resources.js'
	export default {
		data() {
			return {
				clentHeight:0,
				activeIndex:1,
				classData: [{ id: null, name: null, icon: null }],
				listData:[]
			}
		},
		// 获取可视高度
		onReady() {
			uni.getSystemInfo({
				success: (res) => {
					this.clentHeight = res.windowHeight - this.getClientHeight();
				}
			})
		},
		components:{
			SearchTap,
			Lines
		},
		created() {
			this.getlist()
		},
		methods: {
			// 获取一级分类 初始化 
			getlist(){
				uni.request({
				    url: 'http://43.139.55.209:8081/api/goodsCategory/query/one', //请求的接口地址
					method:'GET',//请求的方式
					data:{		//发送到服务器的数据
					},
				    success: (res) => { //请求成功之后的回调函数
				        let data = res.data.data
						if (data) {
							this.classData = []
							// 定义第一个分类的id
							let firstCid = null
							data.forEach(item => {
								// 判断firstCid是否没有值
								if (!firstCid) {
									// 设置第一个分类的id
									firstCid = item.id
								}
								// 设置一级分类的数据
								this.classData.push({
									id: item.id,
									name: item.name,
									icon: item.icon
								})
							})
							// 根据第一个分类id设置子分类数据
							this.setlsitData(firstCid)
						}
				    }
				})
			},
			// 根据一级分类id，获取子分类数据
			setlsitData(cid) {
				uni.request({
					url: `http://43.139.55.209:8081/api/goodsCategory/query/child/${cid}`,
					method: 'GET',
					success: (res) =>{
						const data = res.data
						if(data.success){
							this.listData = []
							const data = res.data
							if (data.success) {
								data.data.forEach(item =>{
									this.listData.push({
										id: item.id,
										name: item.name,
										icon: item.icon
									})
								})
							}
						}
					}
				})
			},
			// 点击分类 获取到一级分类id
			onCategory(item) {
				this.setlsitData(item.id)
			},
			// 获取图片
			setIcon(icon) {
				return getImage(icon)
			},
			
			// 跳转到搜索页面
			gotoSearch(){
				uni.navigateTo({
					url:'/pages/search/search'
				})
			},
			// 跳转到商品分类页面
			gotoShopCategory(){
				uni.navigateTo({
					// url:''
				})
			},
			// 获取可视域高度【兼容】
			getClientHeight(){
				const res = uni.getSystemInfoSync();
				const system = res.platform;
				if( system ==='android' ){
					return 48+res.statusBarHeight;
				}else if( system ==='ios' ){
					return 44+res.statusBarHeight;	
				}else{
					return 0;
				}
			},
			// 左侧点击事件
			changeLeftTab(index){
				this.activeIndex = index;
			}
		},
		// input输入框点击事件
		onNavigationBarSearchInputClicked() {
			uni.navigateTo({
				url:'/pages/search/search'
			})
		},
		// 关闭页面下拉刷新效果的处理函数
		onPullDownRefresh() {
			uni.stopPullDownRefresh()
		},
	}
</script>

<style scoped>
.list{
	display: flex;
}
/* 左侧滑动样式设置 */
.list-left{
	width: 200rpx;
}
.left-item{
	border-bottom: 2rpx solid #FFFFFF;
	font-size: 35rpx;
	font-weight: bold;
	background-color: #F7F7F7;
}
.left-name{
	padding: 30rpx 6rpx;/* 调整上下边距为30rpx；左右边距为6rpx */
	text-align: center;
}
.left-name-active{
	border-left: 8rpx solid #ff5733;
	background-color: #FFFFFF;
}

/* 右侧滑动样式设置 */
.list-right{
	flex: 1;
	padding-left: 30rpx;
}
.right-content{
	display: flex;
	flex-wrap: wrap;
}
.right-list{
	padding-top: 30rpx;
}
.right-item{
	width: 150rpx;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 10rpx;
}
.right-img{
	width: 80rpx;
	height: 80rpx;
}
.right-name{
	padding: 14rpx;
}
</style>

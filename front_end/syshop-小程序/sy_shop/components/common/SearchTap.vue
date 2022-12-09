<template>
	<!-- 定义搜索表单 -->
	<view class="searchTap" :style="'margin-top:'+marginTop+';'" @click="searchTap">
		<view class="search">
			<uni-icons type="search" size="20"></uni-icons>
			<text class="placeholder">搜索</text>
		</view>
	</view>
</template>

<script>
	import uniIcons from '../uni/uni-icons/components/uni-icons/uni-icons.vue'
	import uniSearchBar from '../uni/uni-search-bar/components/uni-search-bar/uni-search-bar.vue'
	export default {
		data() {
			return {
				
			}
		},
		components:{
			uniIcons,
			uniSearchBar
		},
		methods: {
			searchTap(){
				this.$emit('click')
			}
		},
		props:{
			// 定义默认距离上面宽度
			marginTop:{
				type:String,
				default:"0rpx"
			}
		}
	}
</script>

<style scoped>
	.searchTap{
		height: 90rpx;
		background-color: #f8f8f8;
		display: flex;
		align-items: center;
		padding: 0 10rpx;
	}
	.search{
		height: 70rpx;
		background-color: #FFFFFF;
		border: 1rpx solid #c1bfbf;
		border-radius: 30rpx;
		width: 100%;
		display: flex;
		justify-content: center;
		align-items: center;
	}
	.placeholder{
		font-size: 35rpx;
		margin-left: 5rpx;
	}
</style>
<template>
	<view class="shop">
		<view class="shop-item">
			<!-- 定义滑块 -->
			<scroll-view scroll-x="true" class="scroll-content" show-scrollbar="true">

				<view class='scroll-item'>
					<Commodity :dataList='HotData' wrap="no-wrap" itemWidth="250rpx" imgHeight="220rpx" marketLeft="0rpx" nameSize="28rpx"></Commodity>
				</view>
				
			</scroll-view>
		</view>
	</view>
</template>

<script>
	import Commodity from '@/components/common/Commodity.vue'
	export default {
		data () {
			return {
				page: 1,
				HotData:[]
			}
		},
		components:{
			Commodity
		},
		created() {
			this.getHotList()
		},
		methods:{
			// 获取热门爆品数据的方法
			getHotList() {
				uni.request({
					url: `http://43.139.55.209:8081/api/goods/query/${this.page}/10`,
					method:'POST',
					data:{
						saleSort: 'Des'
					},
					success: (res) => {
						const data = res.data
						if(data.success){
							this.HotData = []
							data.data.records.forEach(item => {
								this.HotData.push(item)
							})
						}
					}
				})
			}
		}
	}
</script>

<style scoped>
	.scroll-content{
		width: 100%;
		white-space: nowrap;/* 不能进行换行 */
	}
	.scroll-item{
		display: inline-block;
		/* width: 300rpx;
		height: 300rpx; */
	}
</style>
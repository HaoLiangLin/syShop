<template>
	<view class="Recommend">
		<Commodity :dataList="RecommendData" itemWidth='250rpx' marketLeft="-5rpx" imgHeight="250rpx" nameSize="25rpx"></Commodity>
	</view>
</template>

<script>
	import Commodity from '@/components/common/Commodity.vue'
	export default {
		data() {
			return {
				page:1,
				size:6,
				RecommendData:[]
			}
		},
		components: {
			Commodity
		},
		created() {
			this.getRecommendList()
		},
		methods:{
			// 获取精品推荐数据的方法
			getRecommendList(){
				uni.request({
					url: `http://43.139.55.209:8081/api/goods/query/${this.page}/${this.size}`,
					method:'POST',
					data:{
						recommend:1
					},
					success: (res) =>{
						const data = res.data
						if(data.success){
							this.RecommendData = []
							data.data.records.forEach(item => {
								this.RecommendData.push(item)
							})
						}
					}
				})
			}
		}
	}
</script>

<style scoped>
	

</style>

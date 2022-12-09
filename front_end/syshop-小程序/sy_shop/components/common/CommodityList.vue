<template>
	<view class="CommodityList-list">
		
		<!-- 商品列表组件 -->
		<Commodity :dataList="LikeData"></Commodity>
		
	</view>
</template>

<script>
	import Commodity from './Commodity.vue'
	export default {
		data () {
			return{
				page:1,
				size:6,
				LikeData:[]
			}
		},
		components:{
			Commodity
		},
		created() {
			this.getLikeList()
		},
		methods:{
			// 获取猜你喜欢数据的方法
			getLikeList(){
				uni.request({
					url: `http://43.139.55.209:8081/api/goods/query/${this.page}/${this.size}`,
					method:'POST',
					success: (res) =>{
						const data = res.data
						if(data.success){
							this.LikeData = []
							data.data.records.forEach(item => {
								this.LikeData.push(item)
							})
						}
					}
				})
			}
		}
	}
</script>

<style>

</style>

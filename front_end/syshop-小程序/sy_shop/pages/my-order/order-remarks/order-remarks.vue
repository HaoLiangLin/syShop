<template>
	<view>
		
		<view class="order-main">
			<view v-for='(obj,objIndex) in list' :key="objIndex">
				<!-- 商品 -->
				<view class="order-goods">
					<view v-for="(item,index) in obj.orderItems">
						<orderList :item='item' :index='index'></orderList>
					</view>
					<!-- 评论 -->
					<!-- <view class="remarks-title">留下你的评论:</view> -->
					<view class="evaluate flex align-items-center">
						订单评价
						<template v-for="(val, index) in 5">
							<image
								@click="setStar(index + 1)"
								class="img"
								:src="star > index ? '../../../static/img/star.png' : '../../../static/img/nostar.png'"
								mode="aspectFill"
							></image>
						</template>					
						<text>{{ starName[star - 1] }}</text>
					</view>
					<!--  -->
					<view class="remarks-title">留下你的评价:</view>
					<view class="order-remarks">
						<input v-model="obj.remarks"/>
					</view>
					<!-- <view class="commit-btn">
						<button plain="true" size="mini" @click="commitRemarks(objIndex)">提交评论</button>
					</view>	 -->			
				</view>							
			</view>
		</view>
		
	</view>
</template>

<script>
	import orderList from '@/components/order/order-list.vue'
	import { baseURL } from '@/api/setting.js'
	export default {
		data() {
			return {
				list:[],
				i:-1,
				star: 5, //评价，默认5星
				starName: ['极差', '差点火候', '一般般', '还可以', '非常好']
			}
		},
		methods: {
			//设置评分
			setStar: function(star) {
				this.star = star;
			},
			//
			commitRemarks(objIndex){
				// let OrderData = this.list[objIndex].orderItems;
				// let Orderid = OrderData..orderItemId
				// console.log(Orderid)
				uni.request({
					url:baseURL+"/evaluation/save",
					method:"POST",
					header:{
					    "authorization":uni.getStorageSync('token')
					},
					data:{
						orderItemId:Orderid,
						content:this.list[objIndex].remarks,
						stars:this.star,
						images:""
					},
					success:(res)=> {
						console.log(res.data)
					}
				})
			}
		},
		components:{
			orderList,
		},
		onLoad() {		
			//查询完成的订单
			uni.request({
				url:baseURL+"/orders/query/completed",
				method:"GET",
				header:{
				    "authorization":uni.getStorageSync('token')
				},
				success:(res)=> {
					console.log(res.data),
					this.list = res.data.data
				}
			})
			//根据id查询订单			
		}
	}
</script>

<style>
.order-main{
	border: 2rpx solid lightgray;
	box-shadow: 1px 2px 5px 0 rgba(0, 0, 0, .5);
	padding: 20rpx 20rpx;
	border-radius: 30rpx;
}
.order-remarks{
	height: 100rpx;
	padding: 20rpx 20rpx;
	margin: 20rpx;
	border: 2rpx solid gainsboro;
	font-size: 24rpx;
}
.remarks-title{
	padding: 10rpx 20rpx;
	font-size: 24rpx;
	font-weight: bold;
}
.evaluate {
	font-size: 28rpx;
	font-weight: bold;
	color: #333333;
	height: 100rpx;
	border-top: 1rpx solid #e5e5e5;
	margin-top: 30rpx;
	line-height: 100rpx;
}
.evaluate text {
	font-size: 28rpx;
	color: #c4c4c4;
	margin-left: 30rpx;
}
.img {
	width: 43rpx;
	height: 43rpx;
	margin-left: 30rpx;
	margin-bottom: 10rpx;
}
.commit-btn{
	display: flex;
	padding: 20rpx 0;
}
.commit-btn button{
	flex: 1;
	text-align: center;
}
</style>

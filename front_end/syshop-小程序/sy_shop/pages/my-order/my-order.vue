<template>
	<view class="my-order">
		
		<view class="order-headr">
			<view class="header-item" v-for="(item,index) in tabList" :key="index"
			:class='tabIndex==index?"active":""' @click="changeTab(index)">{{item.name}}</view>
		</view>
		
		<block v-for="(tabItem,tabI) in tabList" :key="tabI">
			
			<view v-show='tabI===tabIndex'>
				<view class="order-main" v-if="tabItem.list.length>0">
					
					<view  v-for="(k,i) in tabItem.list" :key="i">
						<!-- 商品 -->
						<view class="order-goods">
							<view v-for='(item,index) in k.orderItems' @click.stop="goRemark">
								<orderList :item='item' :index='index'></orderList>
							</view>
						</view>
						
						<!-- 总价 -->
						<view class="total-price">
							合计:<text style="color: orangered;">￥{{k.price}}</text>
						</view>
						
						<!-- 支付 -->
						<view class="order-pay">
							<view class="pay-state">{{k.isPay}}</view>
							<view class="pay-btn" v-show="!k.isPay==='已付款'" @click.stop="confirmOrder">立即付款</view>
						</view>		
					</view>
						
					
				</view>
				
				<view class="no-order" v-else>
					<view>您还没有相关订单</view>
					<view class="no-order-btn">去首页逛逛吧</view>
				</view>
				
			</view>
			
		</block>
			
	</view>
		
</template>

<script>
	import orderList from '@/components/order/order-list.vue'
	import { baseURL } from '@/api/setting.js'
	import { getImage } from '@/utils/resources.js'
	export default {
		data() {
			return {
				tabIndex:0,
				tabList:[
					{
						name:"全部",
						list:[]
					},
					{
						name:"待付款",
						list:[]
					},
					{
						name:"待发货",
						list:[],
					},
					{
						name:"待收货",
						list:[]
					},
					{
						name:"待评价",
						list:[]
					}
				]
			}
		},
		onLoad() {
			//查询全部订单
			uni.request({
				url:baseURL+"/orders/query/all",
				method:"GET",
				header:{
				    "authorization":uni.getStorageSync('token')
				},
				success:(res)=> {
					console.log(res.data.data),
					this.tabList[0].list = [],
					this.tabList[0].list = res.data.data
				}
			})
			//查询待付款的订单
			uni.request({
				url:baseURL+"/orders/query/unpaid",
				method:"GET",
				header:{
				    "authorization":uni.getStorageSync('token')
				},
				success:(res)=> {
					console.log(res.data.data),
					this.tabList[1].list = res.data.data
				}
			})
			//查询待收货的订单
			uni.request({
				url:baseURL+"/orders/query/undelivered",
				method:"GET",
				header:{
				    "authorization":uni.getStorageSync('token')
				},
				success:(res)=> {
					console.log(res.data.data),
					this.tabList[3].list = res.data.data
				}
			})
			//查询完成的订单
			uni.request({
				url:baseURL+"/orders/query/completed",
				method:"GET",
				header:{
				    "authorization":uni.getStorageSync('token')
				},
				success:(res)=> {
					console.log(res.data.data),
					this.tabList[4].list = res.data.data
				}
			})
		},
		methods: {
			changeTab(index){
				this.tabIndex = index
			},
			confirmOrder(){
				uni.navigateTo({
					url:"/pages/orderDetails/orderDetails"
				})
			},
			goRemark(){			
				uni.navigateTo({
					url:"/pages/my-order/order-remarks/order-remarks"
				})
			},
			// 获取图片
			setIcon(icon) {
				if (icon) {
					const images = icon.split(',')
					return getImage(images[0])
				}
			},
		},
		components:{
			orderList
		}
	}
</script>

<style>
.my-order{
}
.no-order{
	display: flex;
	justify-content: center;
	align-items: center;
	flex-direction: column;
}
.no-order-btn{
	padding: 6rpx 60rpx;
	border: 2rpx solid orangered;
	color: orangered;
}
.order-main{
	border: 2rpx solid lightgray;
	box-shadow: 1px 2px 5px 0 rgba(0, 0, 0, .5);
	padding: 20rpx 20rpx;
	border-radius: 30rpx;
}
.order-headr{
	display: flex;
	justify-content: center;
	align-items: center;
	border-bottom: 2rpx solid #F7F7F7;
}
.header-item{
	text-align: center;
	flex: 1;
	line-height: 120rpx;
}
.active{
	color: red;
	border-bottom: 4rpx solid red;
	font-weight: bold;
}

.total-price{
	display: flex;
	justify-content: flex-end;
	padding: 20rpx;
	font-size: 32rpx;
}
.order-pay{
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 20rpx 40rpx;
	color: orangered;
}
.pay-btn{
	border-radius: 30rpx;
	border: 2rpx solid orangered;
	padding: 6rpx 40rpx;
	
}
</style>

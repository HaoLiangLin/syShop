<template>
	<!-- 商城系统主页分类组件 -->
	<view class="Icons">
		<!-- navigator声明式导航 通过open-type="navigate"【可省略】导航到非tabBar页面  open-type="switchTab"导航到tabBar页面-->
		<navigator class="Icons-item" v-for="item in categoryby3" :key="item.id" url="/components/index/ShopCategory" open-type="navigate">
			<image class="Icons-img" :src="setIcon(item.icon)" mode=""/>
			<text class="f-color">{{item.name}}</text>
		</navigator>
		<navigator class="Icons-item" url="/pages/my/my" open-type="switchTab">
			<image class="Icons-img" src="../../static/img/Integral.png" mode=""/>
			<text class="f-color">积分商城</text>
		</navigator>
		<navigator class="Icons-item" url="/pages/attendance/attendance">
			<image class="Icons-img" src="../../static/img/Sign.png" mode=""/>
			<text class="f-color">签到</text>
		</navigator>
	</view>                                                                                         
</template>

<script>
	import { getImage } from '../../utils/resources.js'
	export default {
		data() {
			return {
				categoryby3: []
			}
		},
		methods: {
			// 获取图片
			setIcon(icon) {
				return getImage(icon)
			}
		},
		created() {
			// 获取商品一级分类中的前3条数据
			uni.request({
			    url: 'http://43.139.55.209:8081/api/goodsCategory/query/one', //请求的接口地址
				method:'GET',//请求的方式
			    success: (res) => { //请求成功之后的回调函数
					const data = res.data
					if (data.success) {
						this.categoryby3 = []
						for(let i = 0; i<=2; i++) {
							// 设置一级分类的数据
							this.categoryby3.push(data.data[i])
						}
					}		
			    }
			})
		}
	}
</script>

<style scoped>
	.Icons{
		display: flex;
		flex-wrap: wrap;
		margin-top: 10rpx;
	}
	.Icons-item{
		width: 20%;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		padding-bottom: 10rpx;
	}
	.Icons-img{
		width: 110rpx;
		height: 110rpx;
	}
</style>
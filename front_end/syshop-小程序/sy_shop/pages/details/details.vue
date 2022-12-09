<template>
	<view class="details">
		
		<!-- 商品轮播图 -->
		<swiper :indicator-dots="true" :autoplay="true" :interval="3000" :duration="1000">
			<swiper-item v-for="(item,index) in swiperList" :key="index">
				<view class="swiper-item">
					<image class="swiper-img" :src="setIcon(item)" mode=""></image>
				</view>
			</swiper-item>
		</swiper>
		
		<!-- 商品名称、价格 -->
		<view class="details-goods">
			<view class="goods-name" style="">{{shopData.goods.name}}</view>
			<view class="goods-price">￥{{shopData.price}}</view>
			<view style="display: flex; margin-bottom: 2rpx;">
				<view class="goods-address">发货地：{{shopData.goods.city}}</view>
				<view class="goods-market">月销量：{{shopData.goods.sale}}</view>
			</view>
		</view>
		
		<!-- 商品信息 -->
		<view class="goods-content">
			<view class="shop-conta">
				<text class="shop-contd">水院商城独家</text>
				<text class="shop-conte">精选大牌 独家出品</text>
			</view>
			<view class="shop-contb">
				<view class="shop-contx">首单满38元包邮</view>
				<view class="shop-contx">7天无理由退货</view>
				<view class="shop-contx">上门退货</view>
			</view>
		</view>
		<!-- <Line></Line> -->
		<view class="shop-tex">
			<view class="shop-texa">
				<image src="../../static/img/go.png"></image>
				<view class="shop-texax">安心购 · 上门退货 极速退款 破损无忧</view>
			</view>
			<view class="shop-texb">
				<view style="margin: 4rpx;"><image style="width: 30rpx; height: 30rpx;" src="../../static/img/goto_icon.png"/>7天可换</view>
				<view style="margin: 4rpx;"><image style="width: 30rpx; height: 30rpx;" src="../../static/img/goto_icon.png"/>10天价保</view>
				<view style="margin: 4rpx;"><image style="width: 30rpx; height: 30rpx;" src="../../static/img/goto_icon.png"/>15天质量退</view>
				<view style="margin: 4rpx;"><image style="width: 30rpx; height: 30rpx;" src="../../static/img/goto_icon.png"/>正规发票</view>
			</view>
		</view>
		<Line></Line>
		
		<!-- 商品详情图 -->
		<Card cardTitle="商品详情"></Card>
		<view class="details-pic" v-for="item in swiperList">
			<image class="details-img" :src="setIcon(item)"></image>
		</view>
		
		<Line></Line><Line></Line>
		<!-- 商品列表 -->
		<Card cardTitle="猜你喜欢"></Card>
		<CommodityList :dataList="goodsDataList"></CommodityList>
		
		<!-- 底部 -->
		<view class="details-foot">
			<navigator class="iconfont icon-gouwuche" url="/pages/shopCart/shopCart" open-type="switchTab">
				<view class="fonta" @tap="goShopCart">购物车</view>
			</navigator>
			<view class="iconfont icon-shoucang1" @click="collect">
				<view class="fontb">收藏</view>
			</view>
			<view class="add-shopcart" @tap='showPopGoShopCart'>加入购物车</view>
			<view class="purchase" @tap='showPopGoPay'>立即购买</view>
		</view>
		
		<!-- 底部弹出层 @touchmove.stop.prevent表示：取消默认行为-->
		<view class="pop" v-show="isShow" @touchmove.stop.prevent=''>
			<!-- 蒙层 -->
			<view class="pop-mask" @tap='hidePop'></view>
			<!-- 内容块 -->
			<view class="pop-box" :animation="animationData">
				<view class="pop-cont">
					<image class="pop-img" :src="setIcon(icon)" mode=""></image>
					<view class="pop-cot">
						<view class="pop-price">￥{{defaultOption.price}}</view>
						<view class="pop-name">{{shopData.goods.name}}</view>
						<view class="pop-address">发货地：{{shopData.goods.city}}</view>
						<view class="pop-market">月销量：{{shopData.goods.sale}}</view>
					</view>
				</view>
				<view class="pop-color" v-if="colorAndIcon.length>0">
					<view style="font-weight: bold;">颜色</view>
					<view class="pop-col">
						<view class="pop-colx" 
							v-for="(item,index) in colorAndIcon" 
							:key="index" 
							:class="{isSelect: item.isOption, notSelect: !item.isOption, isSellOut: item.isSellOut}" 
							@click="onColor(item)">
							{{item.color}}</view>
					</view>
				</view>
				<view class="pop-size" v-if="sizes.length>0">
					<view style="font-weight: bold;">规格大小</view>
					<view class="pop-siz">
						<view class="pop-sizx" 
							v-for="(item,index) in sizes" 
							:key="index" 
							:class="{isSelect: item.isOption, notSelect: !item.isOption, isSellOut: item.isSellOut}"
							@click="onSize(item)">
							{{item.size}}</view>
					</view>
				</view>
				<view class="pop-size" v-if="combos.length>0">
					<view style="font-weight: bold;">套餐</view>
					<view class="pop-siz">
						<view class="pop-sizx" 
							v-for="(item,index) in combos" 
							:key="index" 
							:class="{isSelect: item.isOption, notSelect: !item.isOption, isSellOut: item.isSellOut}"
							@click="onCombo(item)">
							{{item.combo}}</view>
					</view>
				</view>
				<view class="pop-size" v-if="editions.length>0">
					<view style="font-weight: bold;">版本</view>
					<view class="pop-siz">
						<view class="pop-sizx" 
							v-for="(item,index) in editions" 
							:key="index" 
							:class="{isSelect: item.isOption, notSelect: !item.isOption, isSellOut: item.isSellOut}"
							@click="onEdition(item)">
							{{item.edition}}</view>
					</view>
				</view>
				<view class="pop-num">
					<view style="font-weight: bold;">购买数量</view>
					<UniNumberBox :value='num' @change="changeNumber"></UniNumberBox>
				</view>
				<view class="pop-sub" @tap="addCart">确定</view>
			</view>
		</view>
		
	</view>
</template>

<script>
	import { getImage } from '../../utils/resources.js'
	import Card from '@/components/common/Card.vue'
	import CommodityList from '@/components/common/CommodityList.vue'
	import Line from '@/components/common/Line.vue'
	import UniNumberBox from '@/components/uni/uni-number-box/components/uni-number-box/uni-number-box.vue'
	export default {
		data() {
			return {
				gid: null,
				shopData:null,
				GoodsData:[],
				isShow:false,
				animationData:{},
				num:1,
				swiperList:[],
				btnType:0,
				goodsImages:[],
				// 定义默认选择数据
				defaultOption:null,
				//颜色与图片选择
				colorAndIcon:[],
				// 大小选择
				sizes:[],
				// 套餐选择
				combos:[],
				// 版本选择
				editions:[],
				// 已选择的
				color:null,
				icon:null,
				size:null,
				combo:null,
				edition:null,
				// 购买数量
				quantity:1,
				// 定义库存最大值
				inventory:0
			}
		},
		components:{
			Card,
			CommodityList,
			Line,
			UniNumberBox
		},
		created() {
			
		},
		mounted() {
			this.getShopList(),
			this.getGoodsItem()
		},
		methods: {
			// 根据商品id获取到商品的数据
			getShopList(){
				uni.request({
					url: `http://43.139.55.209:8081/api/goods/query/${this.gid}`,
					method: 'GET',
					success: (res) =>{
						const data = res.data
						if(data.success){
							this.shopData = data.data
							console.log(this.shopData)
							const images = this.shopData.goods.images.split(',') 
							this.goodsImages = []
							images.forEach(image => {
								this.goodsImages.push(image)
							})
						}
					}
				})
			},
			// 根据商品id获取到商品属性的数据
			getGoodsItem(){
				uni.request({
					url: `http://43.139.55.209:8081/api/goodsItem/query/${this.gid}`,
					method: 'GET',
					success: (res) => {
						this.setItem(res)
					}
				})
			},
			// 获取图片
			setIcon(icon) {
				return getImage(icon)
			},
			// 定义选中的数据
			setItem(res){
				const data = res.data
				if(data.success){
					this.GoodsData = data.data
					// console.log(this.GoodsData)
					this.defaultOption = this.GoodsData.defaultOption
					this.colorAndIcon = []
					this.swiperList = []
					this.goodsImages.forEach(item => {
						this.swiperList.push(item)
					})
					this.GoodsData.colorAndIcon.forEach(item => {
						this.swiperList.push(item.icon)
						// 是否选择
						let isOption = false
						// 是否售罄
						let isSellOut = false
						if(item.isSellOut){
							isSellOut = true
						}
						if(item.color === this.defaultOption.color){
							this.color = item.color
							this.icon = item.icon
							isOption = true
						}
						this.colorAndIcon.push({
							color: item.color,
							icon: item.icon,
							isOption,
							isSellOut
						})
					})
					this.sizes = []
					this.GoodsData.size.forEach(item => {
						// 是否选择
						let isOption = false
						// 是否售罄
						let isSellOut = false
						if(item.isSellOut){
							isSellOut = true
						}
						if(item.size === this.defaultOption.size){
							this.size = item.size
							isOption = true
						}
						this.sizes.push({
							size: item.size,
							isOption,
							isSellOut
						})
					})
					this.combos = []
					this.GoodsData.combo.forEach(item => {
						// 是否选择
						let isOption = false
						// 是否售罄
						let isSellOut = false
						if(item.isSellOut){
							isSellOut = true
						}
						if(item.combo === this.defaultOption.combo){
							this.combo = item.combo
							isOption = true
						}
						this.combos.push({
							combo: item.combo,
							isOption,
							isSellOut
						})
					})
					this.editions = []
					this.GoodsData.edition.forEach(item => {
						// 是否选择
						let isOption = false
						// 是否售罄
						let isSellOut = false
						if(item.isSellOut){
							isSellOut = true
						}
						if(item.edition === this.defaultOption.edition){
							this.edition = item.edition
							isOption = true
						}
						this.editions.push({
							edition: item.edition,
							isOption,
							isSellOut
						})
					})
				}
				this.inventory = this.defaultOption.stock
			},
			// 点击选择颜色
			onColor(item){
				// 商品售罄时，不能选择改商品
				if(item.isSellOut){
					uni.showToast({
						title: '商品已售罄',
						icon: 'none',
						duration: 2000
					})
				}
				// 根据商品属性条件获取到商品属性的数据
				uni.request({
					url: "http://43.139.55.209:8081/api/goodsItem/query",
					method:'POST',
					data:{
						gid:this.gid,
						color:item.color
					},
					success: (res) => {
						this.setItem(res)
					}
				})
			},
			// 点击选择大小
			onSize(item){
				// 商品售罄时，不能选择改商品
				if(item.isSellOut){
					uni.showToast({
						title: '商品已售罄',
						icon: 'none',
						duration: 2000
					})
				}
				// 根据商品属性条件获取到商品属性的数据
				uni.request({
					url: "http://43.139.55.209:8081/api/goodsItem/query",
					method:'POST',
					data:{
						gid:this.gid,
						color: this.color,
						size: item.size
					},
					success: (res) => {
						this.setItem(res)
					}
				})
			},
			// 点击选择套餐
			onCombos(item){
				// 商品售罄时，不能选择改商品
				if(item.isSellOut){
					// return
					uni.showToast({
						title: '商品已售罄',
						icon: 'none',
						duration: 2000
					}) 
				}
				// 根据商品属性条件获取到商品属性的数据
				uni.request({
					url: "http://43.139.55.209:8081/api/goodsItem/query",
					method:'POST',
					data:{
						gid:this.gid,
						color: this.color,
						size: this.size,
						combo: item.combo
					},
					success: (res) => {
						this.setItem(res)
					}
				})
			},
			// 点击选择商品版本
			onEdition(item){
				// 商品售罄时，不能选择改商品
				if(item.isSellOut){
					// return 
					uni.showToast({
						title: '商品已售罄',
						icon: 'none',
						duration: 2000
					})
				}
				// 根据商品属性条件获取到商品属性的数据
				uni.request({
					url: "http://43.139.55.209:8081/api/goodsItem/query",
					method:'POST',
					data:{
						gid:this.gid,
						color: this.color,
						size: this.size,
						combo: this.combo,
						edition: item.edition
					},
					success: (res) => {
						this.setItem(res)
					}
				})
			},
			// 改变商品购买数量
			changeNumber(value){
				this.num = value;
			},
			// 加入收藏
			collect(){
				uni.showToast({
					title: '收藏成功',
					duration: 2000
				}) 
			},
			// 显示底部弹出层
			showPopGoShopCart(){//加入购物车
				this.btnType=0
				var animation = uni.createAnimation({
					duration:200
				})
				animation.translateY(600).step();
				this.animationData = animation.export();
				this.isShow = true;
				setTimeout(()=>{
					animation.translateY(0).step();
					this.animationData = animation.export();
				},200)
			},
			showPopGoPay(){//立即购买
				this.btnType=1
				var animation = uni.createAnimation({
					duration:200
				})
				animation.translateY(600).step();
				this.animationData = animation.export();
				this.isShow = true;
				setTimeout(()=>{
					animation.translateY(0).step();
					this.animationData = animation.export();
				},200)
			},
			// 隐藏底部弹出层
			hidePop(){
				var animation = uni.createAnimation({
					duration:200
				})
				animation.translateY(600).step();
				this.animationData = animation.export();
				this.isShow = true;
				setTimeout(()=>{
					animation.translateY(0).step();
					this.isShow = false;
				},200)
			},
			// 跳转到购物车页面
			goShopCart(){
				uni.switchTab({
					url:'../shopCart/shopCart'
				})
			},
			// 加入购物车
			addCart(){
				if (this.btnType === 0) {
					// 加入购物车
					console.log(0)
					// console.log(this.defaultOption.id,this.num)//将数据商品gid和商品数量传到添加购物车
					uni.request({
						url: 'http://43.139.55.209:8081/api/shoppingCart/save',
						method: 'POST',
						data:{
							gid: this.defaultOption.id,
							quantity: this.num
						},
						header:{
							"authorization":uni.getStorageSync('token')
						},
						success: (res) =>{
							const data = res.data
							console.log(data)
							if(data.success){
								uni.showToast({
									title: '已加入购物车',
									duration: 2000
								})
							}
						}
					})
				}else{
					// 立即购买
					console.log(1)
					const item = {name: this.shopData.goods.name, price: this.shopData.price, defaultOption: this.defaultOption, num: this.num}
					uni.navigateTo({
						url:'/pages/orderDetails/orderDetails?shopData=' + encodeURIComponent(JSON.stringify(item))
					})
				}
				let goods = this.goodsContent;
				console.log(goods);
			}
		},
		// 修改返回默认行为
		onBackPress() {
			if(this.isShow){
				this.hidePop();
				return true;
			}
		},
		// 获取id
		onLoad(value) {
			this.gid = value.id
		}
	}
</script>

<style scoped>
	.details{
		padding-bottom: 90rpx;
	}
	swiper{
		width: 100%;
		height: 700rpx;
	}
	.swiper-img{
		width: 100%;
		height: 700rpx;
	}
	.details-goods{
		padding: 10rpx 0;
	}
	.details-img{
		width: 100%;
		height: 700rpx;
	}
	.shop-conta{
		display: flex;
	}
	.shop-contd{
		font-size: 35rpx;
		font-weight: bold;
	}
	.shop-conte{
		font-size: 25rpx;
		margin: 15rpx;
		margin-top: 12rpx;
	}
	.shop-contb{
		display: flex;
	}
	.shop-contx{
		margin: 5rpx;
		padding: 5rpx 2rpx;
		font-size: 33rpx;
		color: #FFFFFF;
		border: 1rpx solid #efefef;
		border-radius: 5rpx;
		background-color: #66CC00;
	}
	.shop-texa{
		display: flex;
	}
	.shop-tex{
		margin-top:6rpx;
		margin-left: 6rpx;
	}
	.shop-texa image{
		width: 53rpx;
		height: 53rpx;
	}
	.shop-texax{
		color: #C60022;
		margin-top: 5rpx;
	}
	.shop-texb{
		display: flex;
		margin-bottom: 5rpx;
	}
	.goods-name{
		text-align: center;
		font-size: 33rpx;
		font-weight: bold;
	}
	.goods-price{
		text-align: center;
		font-size: 36rpx;
		color: #ff5733;
	}
	.goods-address{
		/* text-align: center; */
		margin-left: 50rpx;
		font-size: 30rpx;
		color: #aba9a9;
	}
	.goods-market{
		margin-left: 280rpx;
		font-size: 30rpx;
		color: #aba9a9;
	}
	.goods-content{
		width: 100%;
		height: 120rpx;
		margin-left: 6rpx;
	}
	.details-foot{
		position: fixed;/* 定义在下方 */
		left: 0;
		bottom: 0;
		width: 100%;
		height: 90rpx;
		display: flex;/* 弹性盒子 */
		/* align-items: center;/* 每一个都居中 */ 
		/* justify-content: center; */
		background-color: #FFFFFF;
	}
	.details-foot .iconfont{
		width: 60rpx;
		height: 60rpx;
		margin: 0rpx 26rpx;
		padding-top: 15rpx;
		font-size: 50rpx;
	}
	.fonta{
		font-size: 17rpx;
		margin-left: 5rpx;
	}
	.fontb{
		font-size: 17rpx;
		margin-left: 12rpx;
	}
	.add-shopcart{
		width: 230rpx;
		height: 90rpx;
		padding-top: 20rpx;
		padding-left: 50rpx;
		background-color: orange;
		color: #FFFFFF;
		font-weight: bold;
		font-size: 36rpx;
	}
	.purchase{
		width: 200rpx;
		height: 90rpx;
		padding-top: 20rpx;
		padding-left: 60rpx;
		background-color: red;
		color: #FFFFFF;
		font-weight: bold;
		font-size: 36rpx;
	}
	.pop{
		position: fixed;/* 定位 */
		left: 0;
		top: 0;
		width: 100%;
		height: 100%;
		z-index: 9999;/* 设置为最大值 */
	}
	.pop-mask{
		position: absolute;
		left: 0;
		top: 0;
		width: 100%;
		height: 100%;
	}
	.pop-box{
		position: absolute;
		left: 0;
		bottom: 0;
		width: 100%;
		/* height: 600rpx; */
		background-color: #FFFFFF;
	}
	.pop-cont{
		display: flex;
	}
	.pop-price{
		margin-left: 50rpx;
		margin-top: 40rpx;
		color: #ff5733;
	}
	.pop-name{
		width: 420rpx;
		margin-top: 10rpx;
		margin-left: 50rpx;
		font-weight: bold;
	}
	.pop-address{
		margin-top: 6rpx;
		margin-left: 50rpx;
		font-size: 30rpx;
		color: #aba9a9;
	}
	.pop-market{
		margin-top: 6rpx;
		margin-left: 50rpx;
		font-size: 30rpx;
	}
	.pop-img{
		width: 260rpx;
		height: 260rpx;
	}
	.pop-color{
		padding: 20rpx;
	}
	.pop-col{
		display: flex;
		padding: 5rpx;
	}
	.pop-colx{
		width: 200rpx;
		padding: 5rpx;
		margin-left: 10rpx;
		border: 1rpx solid black;
		background-color: #efefef;
		border-radius: 5rpx;
		text-align: center;
	}
	.pop-size{
		padding: 20rpx;
	}
	.pop-siz{
		display: flex;
		padding: 5rpx;
	}
	.pop-sizx{
		width: 200rpx;
		padding: 5rpx;
		margin-left: 10rpx;
		border: 1rpx solid black;
		background-color: #efefef;
		border-radius: 5rpx;
		text-align: center;
	}
	.pop-num{
		display: flex;
		padding: 20rpx;
		justify-content: space-between;
	}
	.pop-sub{
		text-align: center;
		line-height: 80rpx;
		background-color: red;
		color: #FFFFFF;
		font-weight: bold;
	}
	.isSelect{
		background-color: #fde7ea;
		color: #ee0a24;
		border: 1rpx solid #ee0a24;
	}
	.notSelect{
		background-color: #f5f5f5;
		border: 1rpx solid transparent;
	}
	.isSellOut{
		background-color: rgba(255, 255, 255, 0.7);
		color: #ccc;
		border: 1rpx solid #ccc;
	}
</style>

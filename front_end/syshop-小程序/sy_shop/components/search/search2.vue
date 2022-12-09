<template>
	<view class="search">
		<view class="search-box">
			<uniSearchBar @input="input" placeholder="请输入搜索内容!" :radius="100"></uniSearchBar>
		</view>
		<Lines></Lines>
		
	<!-- 	<view class="search-item">
			<view class="search-title">
				<view class="f-color">最近搜索</view>
				<view class="iconfont icon-laji_huabanfuben" @tap="clearHistory"></view>
			</view>
			<view v-if='searchData.length > 0'>
				<view  class="search-name f-color" v-for='(item,index) in searchData' :key='index'>{{item}}</view>
			</view>
			<view v-else class="search-end">暂无搜索记录</view>
		</view> -->
	</view>
</template>

<script>
	import uniSearchBar from '../../components/uni/uni-search-bar/components/uni-search-bar/uni-search-bar.vue'
	import Lines from '@/components/common/Line.vue'
	export default {
		data() {
			return {
				// 延时器的 timerId
				timer: null,
				// 搜索关键词
				kw: '',
				// 搜索结果列表
				searchResults: [],
				
				// 输入的关键词
				keyword:'',
				// 搜索过的词记录
				searchData:[]
			}
		},
		// 页面加载的时候
		onLoad() {
			uni.getStorage({
				key:"searchData",
				success: (res) => {
					this.searchData = JSON.parse(res.data);
				}
			})
		},
		// 监听input输入内容
		onNavigationBarSearchInputChanged(e) {
			this.keyword = e.text;
		},
		// 点击顶栏中的搜索按钮
		onNavigationBarButtonTap(e){
			this.search();
		},
		// 检查软键盘的搜索按钮
		onNavigationBarSearchInputConfirmed() {
			this.search();
		},
		components:{
			uniSearchBar,
			Lines
		},
		methods: {
			// 在微信小程序中：
			// input输入事件的处理函数
			input(e){
				// 清除 timer 对应的延时器
				clearTimeout(this.timer)
				// 重新启动一个延时器，并把 timerId 赋值给 this.timer
				this.timer = setTimeout(() => {
				// 如果 500 毫秒内，没有触发新的输入事件，则为搜索关键词赋值
				this.kw = e.value
				// console.log(this.kw)
				// 根据关键词，查询搜索建议列表
				  this.getSearchList()
			  }, 500)
			},
			// 根据搜索关键词，搜索商品建议列表
			async getSearchList(){
				// 判断搜索关键词是否为空
				if (this.kw === '') {
				    this.searchResults = []
				    return
				}
				// 发起请求，获取搜索建议列表
				// const { data: res } = await uni.$http.get('/api/public/v1/goods/qsearch', { query: this.kw })
				// if (res.meta.status !== 200) return uni.$showMsg()
				// this.searchResults = res.message
			},
			
			// 在APP时执行下面操作：
			// 判断关键词是否为空和跳转页面
			search(){
				if(this.keyword ===""){
					return uni.showToast({
						title:"关键词不能为空！",
						icon:"none"
					})
				}else{
					uni.navigateTo({
						url:"../search-list/search-list?keyword="+this.keyword+""
					})
				}
				uni.hideKeyboard();
				this.addSearch();
			},
			// 记录最近搜索词
			addSearch(){
				let idx = this.searchData.indexOf(this.keyword);
				if( idx < 0 ){
					this.searchData.unshift(this.keyword);
				}else{
					this.searchData.unshift(this.searchData.splice(idx,1)[0]);
					
				}
				// 保存本地存储记录
				uni.setStorage({
					key:"searchData",
					data: JSON.stringify(this.searchData) //JSON.stringify序列化对象
				})
			},
			// 清除搜索记录
			clearHistory(){
				uni.showModal({
					title:"提示",
					content:'是否要清楚搜索记录',
					cancelText:'取消',
					confirmText:'确定',
					success: (res) => {
						if(res.confirm){
							uni.removeStorage({
								key:"searchData"
							})
							this.searchData=[];
						}
					}
				})
			}
		}
	}
</script>

<style scoped>
	.search-box{
		margin-top: 20rpx;
		height: 120rpx;
		background-color: #FFFFFF;
	}
	.search-item{
		padding: 20rpx;
		margin-bottom: 10rpx;
	}
	.search-title{
		display: flex;
		justify-content: space-between;
		padding-bottom: 10rpx;
	}
	.search-name{
		padding: 4rpx 24rpx;
		background-color: #e1e1e1;
		display: inline-block;/* 行级块元素 */
		border-radius: 26rpx;/* 圆角边框 */
		margin: 10rpx;
	}
	.search-end{
		text-align: center;
		font-size: 28rpx;
	}

</style>

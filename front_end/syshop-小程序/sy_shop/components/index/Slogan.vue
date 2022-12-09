<template>
	<!-- 商城系统主页公告 -->
	<view class="Slogan">
		<uniSection title="公告" type="line">
			<uniNoticBar show-icon scrollable :text="`[${notices.title}] ${notices.content}`" />
		</uniSection>
	</view>
</template>

<script>
	import uniSection from '../uni/uni-section/components/uni-section/uni-section.vue'
	import uniNoticBar from '../uni/uni-notice-bar/components/uni-notice-bar/uni-notice-bar.vue'
	export default {
		components:{
			uniSection,
			uniNoticBar
		},
		data() {
			return {
				notices: null
			}
		},
		created() {
			// 获取id为1586647321033064450的公告信息
			uni.request({
				url:'http://43.139.55.209:8081/api/notices/query',
				method:'POST',
				data:{
					id: '1586647321033064450'
				},
				success:(res) =>{
					const data = res.data
					if(data.success){
						this.notices = data.data
					}
				}
			})
		}
	}
</script>

<style scoped>
	.container {
			/* #ifndef APP-NVUE */
			position: absolute;
			left: 0;
			right: 0;
			top: 0;
			bottom: 0;
			/* #endif */
		}
</style>
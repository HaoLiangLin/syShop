<template>
	<view class="my-add-path">
		<view class="path-item">
			<view>收 件 人</view>
			<input type="text" placeholder="收件人姓名" value="" v-model="pathObj.name"/>
		</view>
		<view class="path-item">
			<view>手 机 号</view>
			<input type="text" placeholder="11位手机号" value="" v-model="pathObj.phone"/>
		</view>
		<view class="path-item">
			<view>所在地区</view>
			<view @click="showMulLinkageThreePicker">{{pathObj.province}} {{pathObj.city}} {{pathObj.district}}</view>
			<mpvue-city-picker :themeColor="themeColor" ref="mpvueCityPicker" :pickerValueDefault="cityPickerValueDefault"
			         @onConfirm="onConfirm"></mpvue-city-picker><!-- @onCancel="onCancel" -->
		</view>
		<view class="path-item">
			<view>详细地址</view>
			<input type="text" placeholder="5到60个字符" value="" v-model="pathObj.address"/>
		</view>
		<view class="path-item">
			<view>设为默认地址</view>
			<radio-group @change="radioChange">
				<label class="radio">
					<radio value="" color="#FF3333" :checked="pathObj.isDefault"/><text></text>
				</label>
			</radio-group>
		</view>
		<view class="btn" @click="savePath">保存地址</view>
	</view>
</template>

<script>
	import mpvueCityPicker from '../../components/mpvue-citypicker/mpvueCityPicker.vue'
	import {mapActions} from 'vuex'
	export default {
		data() {
			return {
				cityPickerValueDefault: [0, 0, 1],
				themeColor: '#007AFF',
				pathObj:{
					id:"",
					uid:"",
					name:"",
					phone:"",
					province:'',
					city:'请选择>',
					district:'',
					address:'',
					isDefault:false,
				},
				i:-1,
				isStatus:false
			}
		},
		onLoad(e) {
			if(e.data){
				uni.setNavigationBarTitle({
					title:"修改地址"
				})
				let result=JSON.parse(e.data);
				this.pathObj = result.item;
				this.i=result.index;
				this.isStatus=true
			}
		},
		methods: {
			// 三级联动选择
			showMulLinkageThreePicker() {
			    this.$refs.mpvueCityPicker.show()
			},
			onConfirm(e) {
			    let res = e.label.split('-')
				this.pathObj.province = res[0],
				this.pathObj.city = res[1],
				this.pathObj.district = res[2]
			},
			//
			savePath(){
				if(this.isStatus){
					//修改地址
					uni.request({
						url:"http://43.139.55.209:8081/api/address/update",
						method:"PUT",
						header:{
						    "authorization":uni.getStorageSync('token')
						},
						data:{
							id:this.pathObj.id,
							uid:this.pathObj.uid,
							name:this.pathObj.name,
							phone:this.pathObj.phone,
							province:this.pathObj.province,
							city:this.pathObj.city,
							district:this.pathObj.district,
							address:this.pathObj.address,
							isDefault:this.pathObj.isDefault
						},
						success:(res)=> {
							console.log(res.data)
							uni.navigateBack({
								delta:1
							})
						}
					})
				}else{
					//判断是否添加为默认地址
					if(this.isDefault){
						this.pathObj.isDefault = 0;
					}else{
						this.pathObj.isDefault = 1;
					}
					//提交地址信息
					uni.request({
						url:"http://43.139.55.209:8081/api/address/save",
						method:"POST",
						header:{
						    "authorization":uni.getStorageSync('token')
						},
						data:{
							name:this.pathObj.name,
							phone:this.pathObj.phone,
							province:this.pathObj.province,
							city:this.pathObj.city,
							district:this.pathObj.district,
							address:this.pathObj.address,
							isDefault:this.pathObj.isDefault
						},
						success:(res)=> {
							console.log(res.data)
							uni.navigateBack({
								delta:1
							})
						}
					})
				}		
			},
			//选择是否为默认地址
			radioChange(){
				this.pathObj.isDefault = !this.pathObj.isDefault;
			},
		},
		components:{
			mpvueCityPicker
		},
		// 页面生命周期
		onNavigationBarButtonTap() {
			this.createPathFn(this.pathObj)
			
			uni.navigateBack({
				delta:1
			})
		}
	}
</script>

<style>
.my-add-path{
	padding-left: 20rpx;
}
.path-item{
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 16rpx 0;
	width: 100%;
	border-bottom: 2rpx solid #CCCCCC;
}
.path-item input{
	flex: 1;
	text-align: left;
	padding-left: 10rpx;
}
.btn{
	background-color:gainsboro;
	width: 100%;
	line-height: 100rpx;
	color: black;
	border-radius: 30rpx;
	text-align: center;
	font-weight: bold;
	margin-top: 20rpx;
}
</style>

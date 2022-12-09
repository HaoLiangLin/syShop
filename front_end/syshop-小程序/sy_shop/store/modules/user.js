export default {
	state:{
		//登录状态
		loginStatus:false,
		//token
		token:null,
		//用户信息
		userInfo:{}
	},
	getters:{
		
	},
	mutations:{
		// 登录后保存用户信息
		login(state,userInfo){
			state.userinfo = userInfo;
			state.loginStatus = true;
			state.token = userInfo.token;
		},
		//退出登录
		loginOut(state){
			state.loginStatus = false;
			state.userInfo = {};
			state.token = null;
			//删除本地储存的信息
			// uni.removeStorageSync('userinfo');
		}
	},
	actions:{
		
	}
}
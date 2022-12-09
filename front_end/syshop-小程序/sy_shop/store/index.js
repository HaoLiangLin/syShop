import Vue from 'vue'
import Vuex from 'vuex'
// 地址管理
import path from './modules/path.js'
//用户
import user from './modules/user.js'
//
import cart from './modules/cart.js'
Vue.use(Vuex)

export default new Vuex.Store({
  
  modules: {
		path,
		user,
		cart
  },
  
})
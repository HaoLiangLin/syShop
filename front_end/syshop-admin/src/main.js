import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import '@/elementui/index.js'
import * as echarts from 'echarts'

Vue.config.productionTip = false

new Vue({
  router,
  store,
  beforeCreate() {
    Vue.prototype.$bus = this
    Vue.prototype.echarts = echarts
  },
  render: h => h(App)
}).$mount('#app')

import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import '@/elementui/index.js'

Vue.config.productionTip = false

new Vue({
  router,
  store,
  beforeCreate() {
    Vue.prototype.$bus = this
  },
  render: h => h(App)
}).$mount('#app')

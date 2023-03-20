import Vue from 'vue'
import VueRouter from 'vue-router'
import { me } from '@/api/user.js'

Vue.use(VueRouter)

const routes = [
  { path: '/', redirect: '/home' },
  {
    name: 'home',
    path: '/home',
    component: () => import('@/views/home.vue'),
    redirect: '/home/home',
    children: [
      { path: 'home', component: () => import('@/components/home/home.vue') },
      { path: 'userList', component: () => import('@/components/user/userList.vue') },
      { path: 'saveUser', component: () => import('@/components/user/saveUser.vue') },
      { path: 'goodsCategory', component: () => import('@/components/goods/goodsCategory.vue') },
      { path: 'goodsList', component: () => import('@/components/goods/goodsList.vue') },
      { path: 'saveGoods', component: () => import('@/components/goods/saveGoods.vue') },
      { path: 'goodsItem/:id', component: () => import('@/components/goods/goodsItem.vue') },
      { path: 'orderList', component: () => import('@/components/order/orderList.vue') }
    ]
  },
  // 登录
  { name: 'login', path: '/login', component: () => import('@/views/login.vue') }
]

const router = new VueRouter({
  routes
})

router.beforeEach((to, from, next) => {
  if (to.name !== 'login') {
    const jwt = sessionStorage.getItem('authorization')
    if (jwt) {
      me().then(res => {
        const data = res.data
        if (data.success) {
          if (data.data.userType === 0) {
            next()
          }
        }
      })
    } else {
      sessionStorage.removeItem('authorization')
      next('/login')
    }
  }
  next()
})

export default router

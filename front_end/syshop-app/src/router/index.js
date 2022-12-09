import Vue from 'vue'
import VueRouter from 'vue-router'
import { me } from '@/api/user.js'

Vue.use(VueRouter)

const routes = [
  { path: '/', redirect: '/home' },
  { name: 'home', path: '/home', component: () => import('@/views/home.vue') },
  { name: 'category', path: '/category', component: () => import('@/views/category.vue') },
  { name: 'shoppingCart', path: '/shoppingCart', component: () => import('@/views/shoppingCart.vue') },
  { name: 'me', path: '/me', component: () => import('@/views/me/me.vue') },
  { name: 'setting', path: '/setting', component: () => import('@/views/me/setting.vue') },
  // 首页
  { name: 'search', path: '/search', component: () => import('@/components/home/search.vue') },
  // 商品
  { name: 'goodsList', path: '/goodsList', component: () => import('@/views/goods/goodsList.vue') },
  { name: 'goods', path: '/goods/:gid', component: () => import('@/views/goods/goods.vue'), meta: { isBack: false } },
  { name: 'goodsEvaluation', path: '/goodsEvaluation/:gid', component: () => import('@/components/goods/goodsEvaluation.vue') },
  // 我的收藏
  { name: 'collection', path: '/collection', component: () => import('@/views/me/collection.vue') },
  // 个人信息
  { name: 'userInfo', path: '/userInfo', component: () => import('@/components/setting/userInfo.vue') },
  { name: 'updateUsername', path: '/updateUsername/:username', component: () => import('@/components/userInfo/updateUsername.vue') },
  { name: 'updateNickname', path: '/updateNickname/:nickname', component: () => import('@/components/userInfo/updateNickname.vue') },
  { name: 'updateName', path: '/updateName/:name', component: () => import('@/components/userInfo/updateName.vue') },
  { name: 'updateEmail', path: '/updateEmail/:email', component: () => import('@/components/userInfo/updateEmail.vue') },
  { name: 'updateQQ', path: '/updateQQ/:qq', component: () => import('@/components/userInfo/updateQQ.vue') },
  // 收货地址
  { name: 'address', path: '/address', component: () => import('@/views/me/address.vue') },
  { name: 'saveAddress', path: '/saveAddress', component: () => import('@/components/address/saveAddress.vue') },
  { name: 'updateAddress', path: '/updateAddress/:id', component: () => import('@/components/address/updateAddress.vue') },
  // 账户与安全
  { name: 'accountSecurity', path: '/accountSecurity', component: () => import('@/views/me/accountSecurity.vue') },
  { name: 'updatePhone', path: '/updatePhone', component: () => import('@/components/accountSecurity/updatePhone.vue') },
  { name: 'updatePwd', path: '/updatePwd', component: () => import('@/components/accountSecurity/updatePassword.vue') },
  // 关于我们
  { name: 'aboutUs', path: '/aboutUs', component: () => import('@/views/me/aboutUs.vue') },
  // 我的钱包
  { name: 'purse', path: '/purse', component: () => import('@/views/me/purse.vue') },
  { name: 'bill', path: '/bill', component: () => import('@/components/furse/bill.vue') },
  // 订单
  { name: 'order', path: '/order', component: () => import('@/views/me/order.vue') },
  { name: 'orderInfo', path: '/orderInfo/:id', component: () => import('@/components/order/orderInfo.vue') },
  { name: 'evaluation', path: '/evaluation/:id', component: () => import('@/components/order/evaluation.vue') },
  // 登录注册
  { name: 'login', path: '/login', component: () => import('@/views/login/login.vue') },
  { name: 'register', path: '/register', component: () => import('@/views/login/register.vue') },
  {
    name: 'forgetPwd',
    path: '/forgetPwd',
    redirect: '/forgetPwd/checkPhone',
    component: () => import('@/views/login/forgetPwd.vue'),
    children: [
      { path: 'checkPhone', component: () => import('@/components/forgetPwd/checkPhone.vue') },
      { path: 'updatePwd/:phone', component: () => import('@/components/forgetPwd/updatePwd.vue') }
    ]
  }
]

const router = new VueRouter({
  routes
})

router.beforeEach((to, from, next) => {
  /**
   * 商品组件有条件进行缓存
   * 如果将要访问的路由时商品组件便进行判断
   */
  if (to.name === 'goods') {
    // 如果离开的路由的收货地址列表，则访问路由缓存生效，数据保持
    if (from.path === '/address') {
      to.meta.isBack = true
    // 反之，则访问路由缓存失效，数据刷新
    } else {
      to.meta.isBack = false
    }
  }
  if (to.name === 'userInfo' || to.name === 'address' || to.name === 'purse' || to.name === 'shoppingCart' || to.name === 'order') {
    const jwt = localStorage.getItem('authorization')
    if (!jwt) {
      next('/me')
    } else {
      me().then(res => {
        const data = res.data
        if (!data.success) {
          localStorage.removeItem('authorization')
          next('/me')
        }
      })
    }
  }
  next()
})

export default router

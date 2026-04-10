import Vue from 'vue'
import VueRouter from 'vue-router'
import store from '@/store'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    redirect: '/inventory',
    meta: { requiresAuth: true }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue')
  },
  {
    path: '/',
    component: () => import('@/views/layout/index.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '/inventory',
        name: 'Inventory',
        component: () => import('@/views/inventory/List.vue')
      },
      {
        path: '/inventory/form',
        name: 'InventoryForm',
        component: () => import('@/views/inventory/Form.vue')
      },
      {
        path: '/inventory/edit/:id',
        name: 'InventoryEdit',
        component: () => import('@/views/inventory/Form.vue')
      },
      {
        path: '/package',
        name: 'Package',
        component: () => import('@/views/package/List.vue')
      },
      {
        path: '/package/form',
        name: 'PackageForm',
        component: () => import('@/views/package/Form.vue')
      },
      {
        path: '/package/edit/:id',
        name: 'PackageEdit',
        component: () => import('@/views/package/Form.vue')
      }
    ]
  }
]

const router = new VueRouter({
  mode: 'hash',
  base: process.env.BASE_URL,
  routes
})

router.beforeEach((to, from, next) => {
  const isLoggedIn = store.getters['user/isLoggedIn']
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)

  if (requiresAuth && !isLoggedIn) {
    next('/login')
  } else if (to.path === '/login' && isLoggedIn) {
    next('/')
  } else {
    next()
  }
})

export default router

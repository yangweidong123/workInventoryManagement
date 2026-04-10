import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    redirect: '/inventory'
  },
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
    path: '/inventory/form/:id',
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
    path: '/package/form/:id',
    name: 'PackageEdit',
    component: () => import('@/views/package/Form.vue')
  }
]

const router = new VueRouter({
  mode: 'hash',
  base: process.env.BASE_URL,
  routes
})

export default router

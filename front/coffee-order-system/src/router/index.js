import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Index',
    component: () => import('@/views/HomeView.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/LoginView.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/RegisterView.vue')
  },
  {
    path: '/menu',
    name: 'Menu',
    component: () => import('@/views/MenuView.vue')
  },
  {
    path: '/cart',
    name: 'Cart',
    component: () => import('@/views/CartView.vue')
  },
  {
    path: '/orders',
    name: 'Orders',
    component: () => import('@/views/OrdersView.vue')
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/ProfileView.vue')
  },
  {
    path: '/staff',
    name: 'Staff',
    component: () => import('@/views/PendingOrdersView.vue')
  },
  {
    path: '/staff/all-orders',
    name: 'AllOrders',
    component: () => import('@/views/AllOrdersView.vue')
  },
  {
    path: '/admin/coffee',
    name: 'CoffeeManagement',
    component: () => import('@/views/CoffeeManagementView.vue')
  },
  {
    path: '/admin/statistics',
    name: 'Statistics',
    component: () => import('@/views/StatisticView.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
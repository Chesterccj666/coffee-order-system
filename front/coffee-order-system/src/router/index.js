import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Index',
    component: () => import('@/views/IndexView.vue')
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
    component: () => import('@/views/StaffView.vue')
  },
  {
    path: '/staff/all-orders',
    name: 'StaffAllOrders',
    component: () => import('@/views/StaffAllOrdersView.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
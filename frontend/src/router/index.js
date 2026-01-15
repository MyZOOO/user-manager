import { createRouter, createWebHashHistory } from 'vue-router'
import { isTokenExpired } from '@/utils/auth'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  // 管理员路由
  {
    path: '/admin',
    component: () => import('@/components/AdminLayout.vue'),
    redirect: '/admin/dashboard',
    meta: { requiresRole: 'admin' },
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/AdminDashboard.vue'),
        meta: { title: '管理员首页', requiresAuth: true, requiresRole: 'admin' }
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/UserManagement.vue'),
        meta: { title: '用户管理', requiresAuth: true, requiresRole: 'admin' }
      },
      {
        path: 'roles',
        name: 'AdminRoles',
        component: () => import('@/views/RoleManagement.vue'),
        meta: { title: '角色管理', requiresAuth: true, requiresRole: 'admin' }
      },
      {
        path: 'permissions',
        name: 'AdminPermissions',
        component: () => import('@/views/PermissionManagement.vue'),
        meta: { title: '权限管理', requiresAuth: true, requiresRole: 'admin' }
      },
      {
        path: 'tasks',
        name: 'AdminTasks',
        component: () => import('@/views/TaskManagement.vue'),
        meta: { title: '任务管理', requiresAuth: true, requiresRole: 'admin' }
      },
      {
        path: 'task-publish',
        name: 'AdminTaskPublish',
        component: () => import('@/views/AdminTaskPublish.vue'),
        meta: { title: '发布任务', requiresAuth: true, requiresRole: 'admin' }
      },
      {
        path: 'logs',
        name: 'AdminLogs',
        component: () => import('@/views/LogInfoManagement.vue'),
        meta: { title: '操作日志', requiresAuth: true, requiresRole: 'admin' }
      },
      {
        path: 'profile',
        name: 'AdminProfile',
        component: () => import('@/views/Profile.vue'),
        meta: { title: '管理员信息', requiresAuth: true, requiresRole: 'admin' }
      }
    ]
  },
  // 普通用户路由
  {
    path: '/user',
    component: () => import('@/components/UserLayout.vue'),
    redirect: '/user/dashboard',
    meta: { requiresRole: 'user' },
    children: [
      {
        path: 'dashboard',
        name: 'UserDashboard',
        component: () => import('@/views/UserDashboard.vue'),
        meta: { title: '我的首页', requiresAuth: true }
      },
      {
        path: 'tasks',
        name: 'UserTasks',
        component: () => import('@/views/UserTasks.vue'),
        meta: { title: '我的任务', requiresAuth: true }
      },
      {
        path: 'profile',
        name: 'UserProfile',
        component: () => import('@/views/Profile.vue'),
        meta: { title: '个人信息', requiresAuth: true }
      }
    ]
  },
  // 旧的通用路由，将其重定向
  {
    path: '/',
    redirect: () => {
      const user = localStorage.getItem('user')
      const token = localStorage.getItem('token')

      if (!user || !token || isTokenExpired(token)) {
        localStorage.clear()
        return '/login'
      }

      const userObj = JSON.parse(user)
      return userObj.role === 'admin'
          ? '/admin/dashboard'
          : '/user/dashboard'
    }
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const userStr = localStorage.getItem('user')
  const user = userStr ? JSON.parse(userStr) : null
  
  // 检查是否需要认证
  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (!token) {
      // 未登录，重定向到登录页
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
    } else if (to.meta.requiresRole && user && to.meta.requiresRole !== user.role) {
      // 角色不匹配，重定向到对应的首页
      next(user.role === 'admin' ? '/admin/dashboard' : '/user/dashboard')
    } else {
      next()
    }
  } else {
    // 不需要认证的路由
    if (to.path === '/login' && token) {
      // 已登录用户访问登录页，重定向到首页
      next(user.role === 'admin' ? '/admin/dashboard' : '/user/dashboard')
    } else {
      next()
    }
  }
})

export default router


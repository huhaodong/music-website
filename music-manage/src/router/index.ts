import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import { getToken } from '@/utils'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/Home',
    component: () => import('@/views/Home.vue'),
    meta: { title: '自述文件' },
    children: [
      {
        path: '/Info',
        component: () => import('@/views/InfoPage.vue'),
        meta: { title: 'Info' }
      },
      {
        path: '/song',
        component: () => import('@/views/SongPage.vue'),
        meta: { title: 'Song' }
      },
      {
        path: '/singer',
        component: () => import('@/views/SingerPage.vue'),
        meta: { title: 'Singer' }
      },
      {
        path: '/SongList',
        component: () => import('@/views/SongListPage.vue'),
        meta: { title: 'SongList' }
      },
      {
        path: '/ListSong',
        component: () => import('@/views/ListSongPage.vue'),
        meta: { title: 'ListSong' }
      },
      {
        path: '/Comment',
        component: () => import('@/views/CommentPage.vue'),
        meta: { title: 'Comment' }
      },
      {
        path: '/Consumer',
        component: () => import('@/views/ConsumerPage.vue'),
        meta: { title: 'Consumer' }
      },
      {
        path: '/Collect',
        component: () => import('@/views/CollectPage.vue'),
        meta: { title: 'Collect' }
      },
      // RBAC权限系统路由 - Home子路由
      {
        path: '/Home/user',
        component: () => import('@/views/UserManage.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: '/Home/org',
        component: () => import('@/views/OrgManage.vue'),
        meta: { title: '组织管理' }
      },
      {
        path: '/Home/role',
        component: () => import('@/views/RoleManage.vue'),
        meta: { title: '角色管理' }
      },
      {
        path: '/Home/permission',
        component: () => import('@/views/PermissionManage.vue'),
        meta: { title: '权限管理' }
      }
    ]
  },
  // 兼容旧路径重定向到新路径
  {
    path: '/system/user',
    redirect: '/Home/user'
  },
  {
    path: '/system/org',
    redirect: '/Home/org'
  },
  {
    path: '/system/role',
    redirect: '/Home/role'
  },
  {
    path: '/system/permission',
    redirect: '/Home/permission'
  },
  // 兼容 /user 路径重定向
  {
    path: '/user',
    redirect: '/Home/user'
  },
  {
    path: '/org',
    redirect: '/Home/org'
  },
  {
    path: '/role',
    redirect: '/Home/role'
  },
  {
    path: '/permission',
    redirect: '/Home/permission'
  },
  {
    path: '/',
    component: () => import('@/views/Login.vue')
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

router.beforeEach((to, from, next) => {
  const publicPages = ['/', '/Login'];
  const authRequired = !publicPages.includes(to.path);

  if (authRequired && !getToken()) {
    next('/');
  } else {
    next();
  }
});

export default router

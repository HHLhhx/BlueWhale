import { createRouter, createWebHashHistory } from "vue-router"

const router = createRouter({
    history: createWebHashHistory(),
    routes: [{
        path: '/',
        redirect: '/login',
    }, {
        path: '/login',
        component: () => import('../views/user/Login.vue'),
        meta: { title: '用户登录' }
    }, {
        path: '/register',
        component: () => import('../views/user/Register.vue'),
        meta: { title: '用户注册' }
    }, {
        path: '/home',
        redirect: '/allStore',
        component: () => import('../views/Home.vue'),
        children: [
            {
                path: '/dashboard',
                name: 'Dashboard',
                component: () => import('../views/user/Dashboard.vue'),
                meta: { title: '个人信息' }
            },
            {
                path: '/allStore',
                name: 'allStore',
                component: () => import('../views/store/AllStore.vue'),
                meta: { title: '商品列表界面/主页' }
            },
            {
                path: '/searchProduct',
                name: 'searchProduct',
                component: () => import('../views/product/SearchProduct.vue'),
                meta: { title: '商品列表界面/搜索' }
            },
            {
                path: '/createStore',
                name: 'createStore',
                component: () => import('../views/store/CreateStore.vue'),
                meta: {
                    title: '创建商店',
                    permission: ['MANAGER']
                }
            },
            {
                path: '/storeDetail/:storeId',
                name: 'storeDetail',
                component: () => import('../views/store/StoreDetail.vue'),
                meta: { title: '店铺详情' }
            },
            {
                path: '/createProduct/:storeId',
                name: 'createProduct',
                component: () => import('../views/product/CreateProduct.vue'),
                meta: {
                    title: '创建商品',
                    permission: ['STAFF']
                }
            },
            {
                path: '/productDetail/:productId',
                name: 'productDetail',
                component: () => import('../views/product/ProductDetail.vue'),
                meta: { title: '商品详情' }
            },

            {
                path: '/createOrder/:storeId',
                name: 'createOrder',
                component: () => import('../views/order/CreateOrder.vue'),
                meta: {
                    title: '创建订单',
                    permission: ['CUSTOMER']
                }
            },

            {
                path: '/allOrder',
                name: 'allOrder',
                component: () => import('../views/order/AllOrder.vue'),
                meta: { title: '商品列表界面/主页' }
            },

            {
                path: '/allCoupon',
                name: 'allCoupon',
                component: () => import('../views/coupon/AllCoupon.vue'),
                meta: { title: '商品列表界面/主页' }
            },

            {
                path: '/createCoupon',
                name: 'createCoupon',
                component: () => import('../views/coupon/CreateCoupon.vue'),
                meta: {
                    title: '创建订单',
                    permission: ['STAFF', 'CEO']
                }
            }
        ]
    }, {
        path: '/404',
        name: '404',
        component: () => import('../views/NotFound.vue'),
        meta: { title: '404' }
    }, {
        path: '/:catchAll(.*)',
        redirect: '/404'
    }]
})

router.beforeEach((to, _, next) => {
    const token: string | null = sessionStorage.getItem('token')
    const role: string | null = sessionStorage.getItem('role')
    const storeId: string | null = sessionStorage.getItem('storeId')

    if (to.meta.title) {
        document.title = to.meta.title
    }

    if (!token) {
        if (to.path === '/login' || to.path === '/register') {
            next()
        } else {
            next('/login')
        }
        return
    }

    if (to.meta.permission && !to.meta.permission.includes(role!)) {
        next('/404')
        return
    }

    if (to.name === 'createProduct' && to.params.storeId !== storeId) {
        next('/404')
        return
    }

    next()
})


export { router }

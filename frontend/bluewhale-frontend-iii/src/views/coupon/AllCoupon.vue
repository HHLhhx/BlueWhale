<script setup lang="ts">
import { ref } from "vue"
import { router } from '../../router'
import { getAllCoupon, getAllCouponSet } from "../../api/coupon.ts"
import CouponItem from "../../components/CouponItem.vue"

const role = sessionStorage.getItem("role")
const couponSetList = ref<number[]>([]);
const couponAllList = ref<number[]>([]);
const couponUsedList = ref<number[]>([]);
const couponUnusedList = ref<number[]>([]);
const couponExpiredList = ref<number[]>([]);
const isMy = ref(false)
init()

function init() {
    getAllCouponSet().then(res => {
        couponSetList.value = res.data.result
    })
    isMy.value = false
}

function toCreateCouponPage() {
    router.push("/createCoupon")
}

function getMyCoupon() {
    getAllCoupon("ALL").then(res => {
        couponAllList.value = []
        res.data.result.forEach((coupon: any) => {
            couponAllList.value.push(coupon.setId)
        })
    })
    getAllCoupon("USED").then(res => {
        couponUsedList.value = []
        res.data.result.forEach((coupon: any) => {
            couponUsedList.value.push(coupon.setId)
        })
    })
    getAllCoupon("UNUSED").then(res => {
        couponUnusedList.value = []
        res.data.result.forEach((coupon: any) => {
            couponUnusedList.value.push(coupon.setId)
        })
    })
    getAllCoupon("EXPIRED").then(res => {
        couponExpiredList.value = []
        res.data.result.forEach((coupon: any) => {
            couponExpiredList.value.push(coupon.setId)
        })
    })
    isMy.value = true
}
</script>


<template>
    <el-main>
        <div v-if="role === 'STAFF' || role === 'CEO'">
            <el-button class="add-coupon-button" type="primary" plain @click="toCreateCouponPage()">
                创建优惠券组
            </el-button>
        </div>
        <div v-if="role === 'CUSTOMER'">
            <el-button class="add-coupon-button" v-if="!isMy" type="primary" plain @click="getMyCoupon()">
                查看我领取的优惠券
            </el-button>
            <el-button class="add-coupon-button" v-if="isMy" type="primary" plain @click="init()">
                查看所有优惠券
            </el-button>
        </div>
        <div v-if="isMy">
            <el-tabs v-if="isMy" tabPosition="left">
                <el-tab-pane label="全部">
                    <el-scrollbar max-height="780px" always>
                        <div class="coupon-item-list">
                            <p v-if="couponAllList.length == 0">暂无优惠券</p>
                            <CouponItem v-for="couponId in couponAllList" :key="couponId" :couponId="couponId" :isMy="true"/>
                        </div>
                    </el-scrollbar>
                </el-tab-pane>
                <el-tab-pane label="可使用">
                    <el-scrollbar max-height="780px" always>
                        <div class="coupon-item-list">
                            <p v-if="couponUnusedList.length == 0">暂无可使用的优惠券</p>
                            <CouponItem v-for="couponId in couponUnusedList" :key="couponId" :couponId="couponId" :isMy="true"/>
                        </div>
                    </el-scrollbar>
                </el-tab-pane>
                <el-tab-pane label="已过期">
                    <el-scrollbar max-height="780px" always>
                        <div class="coupon-item-list">
                            <p v-if="couponExpiredList.length == 0">暂无已过期的优惠券</p>
                            <CouponItem v-for="couponId in couponExpiredList" :key="couponId" :couponId="couponId" :isMy="true"/>
                        </div>
                    </el-scrollbar>
                </el-tab-pane>
                <el-tab-pane label="已使用">
                    <el-scrollbar max-height="780px" always>
                        <div class="coupon-item-list">
                            <p v-if="couponUsedList.length == 0">暂无已使用的优惠券</p>
                            <CouponItem v-for="couponId in couponUsedList" :key="couponId" :couponId="couponId" :isMy="true"/>
                        </div>
                    </el-scrollbar>
                </el-tab-pane>
            </el-tabs>
        </div>
        <div v-if="!isMy">
            <el-scrollbar v-if="!isMy" max-height="780px" always>
                <div class="coupon-item-list">
                    <p v-if="couponSetList.length == 0">暂无优惠券</p>
                    <CouponItem v-for="couponId in couponSetList" :key="couponId" :couponId="couponId" />
                </div>
            </el-scrollbar>
        </div>
    </el-main>
</template>


<style scoped>
.add-coupon-button {
    margin-left: 30px;
    margin-bottom: 10px;
}

.coupon-item-list {
    display: flex;
    padding: 2px;
    flex-flow: wrap;
    justify-content: center;
    align-content: start;
}
</style>

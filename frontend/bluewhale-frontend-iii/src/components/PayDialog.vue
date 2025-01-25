<script setup lang="ts">
import { ref } from "vue"
import { getOrderById, getUsefulCoupon, pay, priceOfOrder } from "../api/order.ts"
import { parseCouponType, parseDeliveryMethod, parseTime } from "../utils"
import { getCouponSetById } from "../api/coupon.ts"
import { ElTable } from 'element-plus'
import { router } from "../router/index.ts"

interface Coupon {
    id: number,
    setId: number,
    uid: number,
    hasUsed: boolean
}
interface UpdatedCoupon {
    couponId: number,
    couponType: string,
    expireTime: string
}

const saveOrderId = ref(0)
const orderDialogVisible = ref(false)
const couponList = ref<Coupon[]>([])
const updatedCouponList = ref<UpdatedCoupon[]>([])

//通过父组件传来的orderId获取订单详情
function open(orderId: number) {
    saveOrderId.value = orderId
    initInfo(orderId)
    orderDialogVisible.value = true
}

defineExpose({
    open
})

const num = ref(0)
const totalPrice = ref(0)
const deliveryMethod = ref()

const couponTableVisible = ref(false)

async function initInfo(orderId: number) {
    couponTableVisible.value = false
    await getOrderById(orderId).then(res => {
        num.value = res.data.result.num
        totalPrice.value = res.data.result.totalPrice
        deliveryMethod.value = parseDeliveryMethod(res.data.result.deliveryMethod)
    })
    await getUsefulCoupon(orderId).then(res => {
        updatedCouponList.value = []
        couponList.value = res.data.result
        couponList.value.forEach((coupon) => {
            getCouponSetById(coupon.setId).then(res => {
                const updatedCoupon = <UpdatedCoupon>{
                    couponId: coupon.id,
                    couponType: parseCouponType(res.data.result.couponType),
                    expireTime: parseTime(res.data.result.expireTime)
                }
                updatedCouponList.value.push(updatedCoupon)
            })
        })
    })
}

const couponTableRef = ref<InstanceType<typeof ElTable>>()

function handleCouponVisible(val: boolean) {
    couponTableVisible.value = val;
    if (val == false) {
        selectedCouponId.value = []
        priceOfOrder(saveOrderId.value, selectedCouponId.value).then(res => {
            totalPrice.value = res.data.result
        })
    }
}

const selectedCouponId = ref<number[]>([])
const handleSelectionChange = (rows: UpdatedCoupon[]) => {
    selectedCouponId.value = []
    rows.forEach((coupon) => {
        selectedCouponId.value.push(coupon.couponId)
    })
    priceOfOrder(saveOrderId.value, selectedCouponId.value).then(res => {
        totalPrice.value = res.data.result
    })
}

const resHTML = ref('')
async function handlePayOrder() {
    const isDirectPay = ref(false)
    if (router.currentRoute.value.name === 'allOrder') {
        isDirectPay.value = false;
    } else {
        isDirectPay.value = true;
    }
    resHTML.value = (await pay(saveOrderId.value, selectedCouponId.value, isDirectPay.value)).data
    // 读取本地保存的html数据，使用当前窗口打开
    window.open('', '_self')?.document.write(resHTML.value)
    orderDialogVisible.value = false
}
</script>

<template>
    <el-dialog v-model="orderDialogVisible" width="500px" :close-on-click-modal="false" align-center>
        <div v-html="resHTML"></div>
        <div class="order-info">
            <el-descriptions class="margin-top" title="订单支付" :column="1" border
                :style="{ width: '400px', margin: '0 auto' }">
                <template #extra>
                    <el-button v-if="couponTableVisible == false" @click="handleCouponVisible(true)"
                        type="primary">查看可用优惠券</el-button>
                    <el-button v-else @click="handleCouponVisible(false)">不使用优惠券</el-button>
                </template>
                <el-descriptions-item label-align="center" align="center">
                    <template #label>
                        <div class="cell-item">
                            购买数量
                        </div>
                    </template>
                    {{ num }}
                </el-descriptions-item>
                <el-descriptions-item label-align="center" align="center">
                    <template #label>
                        <div class="cell-item">
                            提货方式
                        </div>
                    </template>
                    {{ deliveryMethod }}
                </el-descriptions-item>
                <el-descriptions-item label-align="center" align="center">
                    <template #label>
                        <div class="cell-item">
                            总价
                        </div>
                    </template>
                    {{ totalPrice }}
                </el-descriptions-item>
            </el-descriptions>
        </div>

        <div>
            <el-table v-if="couponTableVisible == true" :data="updatedCouponList"
                @selection-change="handleSelectionChange" :style="{ width: '400px', margin: '0 auto' }" border
                empty-text="暂无可用优惠券" max-height="190" :header-cell-style="{ 'text-align': 'center' }"
                :cell-style="{ 'text-align': 'center' }" ref="couponTableRef">
                <el-table-column type="selection">
                </el-table-column>
                <el-table-column type="index" label="序号" width="65">
                </el-table-column>
                <el-table-column prop="couponType" label="优惠券种类" width="125">
                </el-table-column>
                <el-table-column prop="expireTime" label="截止日期">
                </el-table-column>
            </el-table>
        </div>

        <div class="footer-button">
            <el-button @click="handlePayOrder" type="primary" plain>确认支付</el-button>
            <el-button @click="orderDialogVisible = false" plain>稍后支付</el-button>
        </div>
    </el-dialog>
</template>

<style scoped>
.pay-dialog-title {
    font-size: 30px;
    margin-bottom: 20px;
}

.footer-button {
    display: flex;
    justify-content: center;
    margin-top: 20px;
}
</style>
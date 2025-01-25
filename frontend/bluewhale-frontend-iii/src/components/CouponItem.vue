<script setup lang="ts">
    import { ref } from "vue"
    import { acquire, check, checkValid, getCouponSetById } from "../api/coupon.ts";
    import { parseCouponType, parseTime } from "../utils";
    import { getStoreById } from "../api/store.ts";

    // 使用props接收父界面传来的数据
    const props = defineProps({
        couponId: {
            type: Number,
            required: true
        },
        isMy: {
            type: Boolean,
            default: false
        }
    })

    const couponType = ref('')
    const createTime = ref('')
    const expireTime = ref('')
    const storeName = ref('')

    const fulfillPrice = ref(0)
    const reducePrice = ref(0)
    const totalNum = ref(0)
    const sentNum = ref(0)
    const storeId = ref(0)
    const isGlobal = ref(false)
    const isOwned = ref(false)
    const isValid = ref(false)

    const role = sessionStorage.getItem("role")

    initInfo(props.couponId);

    function initInfo(couponId: number) {
        getCouponSetById(couponId).then(res => {
            couponType.value = res.data.result.couponType
            createTime.value = res.data.result.createTime
            fulfillPrice.value = res.data.result.fulfillPrice
            expireTime.value = res.data.result.expireTime
            reducePrice.value = res.data.result.reducePrice
            totalNum.value = res.data.result.totalNum
            sentNum.value = res.data.result.sentNum
            isGlobal.value = res.data.result.isGlobal
            storeId.value = res.data.result.storeId
            if (storeId.value != null) {
                getStoreById(storeId.value).then(res => {
                    storeName.value = res.data.result.name
                })
            }
        })
        if (role === 'CUSTOMER') {
            check(couponId).then(res => {
                isOwned.value = res.data.result
            })
        }
        checkValid(couponId).then(res => {
            isValid.value = res.data.result
        })
    }
    function handleAcquire() {
        acquire(props.couponId).then(res => {
            if (res.data.code === '000') {
                ElMessage({
                    message: '领取成功！',
                    type: 'success',
                    center: true,
                })
                initInfo(props.couponId)
            } else {
                ElMessage({
                    message: res.data.msg,
                    type: 'error',
                    center: true,
                })
            }
        })
    }
</script>


<template>
    <el-card class="order-item-card" shadow="hover">

        <template #header>
            <div class="card-header">
                <div>
                    <span v-if="!isGlobal"> 可用商店： {{ storeName }}</span>
                    <span v-if="isGlobal"> 所有商店均可用 </span>
                </div>
                <div v-if="!isMy">
                    <el-tag v-if="isOwned && role === 'CUSTOMER'">
                        已领取！
                    </el-tag>
                    <el-tag v-else-if="totalNum == sentNum && role === 'CUSTOMER'">
                        已领光！下次早点来~
                    </el-tag>
                    <el-tag v-else-if="!isValid && role === 'CUSTOMER'">
                        已过期！
                    </el-tag>
                    <el-button @click="handleAcquire" v-else-if="!isOwned && isValid && role === 'CUSTOMER'"
                        class="status-change-button" size="small" type="primary">
                        领取
                    </el-button>
                </div>
            </div>
        </template>

        <el-descriptions :column="1">

            <el-descriptions-item style="font-size: 15px" label="类型">
                <el-tag> {{ parseCouponType(couponType) }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item style="font-size: 15px" v-if="couponType === 'FULL_REDUCTION'" label="优惠">
                满 {{ fulfillPrice.toString() }} 减 {{ reducePrice.toString() }}
            </el-descriptions-item>
            <el-descriptions-item style="font-size: 15px" label="发布时间">
                {{ parseTime(createTime) }}
            </el-descriptions-item>
            <el-descriptions-item style="font-size: 15px" label="过期时间">
                {{ parseTime(expireTime) }}
            </el-descriptions-item>
            <div v-if="!isMy">
                <el-descriptions-item style="font-size: 15px" label="总数量">
                    {{ totalNum }} 张
                </el-descriptions-item>
                <el-descriptions-item style="font-size: 15px" label="剩余">
                    {{ totalNum - sentNum }} 张
                </el-descriptions-item>
            </div>
        </el-descriptions>
    </el-card>
</template>


<style scoped>
    .order-item-card {
        margin: 20px;
        border-radius: 8px;
        min-width: max-content;
    }

    .card-header {
        display: flex;
        align-items: center;
        justify-content: space-between;
    }

    .status-change-button {
        margin-left: 10px;
    }
</style>

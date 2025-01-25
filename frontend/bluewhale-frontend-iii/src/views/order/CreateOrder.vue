<script setup lang="ts">
import { ref, computed } from "vue"
import { router } from '../../router'
import { Back } from "@element-plus/icons-vue"
import { createOrder } from "../../api/order.ts"
import { getProductById } from "../../api/product.ts";
import { parseCategory } from "../../utils/index.ts"

const productId = Number(sessionStorage.getItem("productId"))
const productVO = ref()
const name = ref('')
const category = ref()
const deliveryMethod = ref('')
const address = ref('')
const price = ref(0)
const num = ref(0)
const photoUrlList = ref([])
const stock = ref(10)
const storeId = ref(0)
const centerDialogVisible = ref(false)
const pendingNum = ref(0)
const orderId = ref(0)

// 支付弹窗实例
const dialogRef = ref()

const hasGetMethodInput = computed(() => deliveryMethod.value == "DELIVERY" && address.value != '' || deliveryMethod.value == "PICKUP")
const NumInputEnable = computed(() => num.value <= stock.value - pendingNum.value && num.value > 0)
const createOrderDisabled = computed(() => {
    return !(hasGetMethodInput.value && NumInputEnable.value);
})

getProductDetail()

function handleCreateOrder() {
    const payload = {
        storeId: storeId.value,
        productId: productId,
        name: name.value,
        orderState: "UNPAID",
        deliveryMethod: deliveryMethod.value,
        totalPrice: (price.value * num.value),
        num: num.value,
        address: address.value
    }

    createOrder(payload).then(res => {
        if (res.data.code === '000') {
            ElMessage({
                message: '创建订单成功！',
                type: 'success',
                center: true,
            })
            deliveryMethod.value = ''
            centerDialogVisible.value = true
            orderId.value = res.data.result

            dialogRef.value.open(orderId.value)
        } else if (res.data.code === '400') {
            ElMessage({
                message: res.data.msg,
                type: 'error',
                center: true,
            })
        }
    })
}

function getProductDetail() {
    getProductById(productId).then(res => {
        productVO.value = res.data.result
        storeId.value = productVO.value.storeId
        name.value = productVO.value.name
        photoUrlList.value = productVO.value.photoUrlList
        stock.value = productVO.value.stock
        category.value = parseCategory(productVO.value.category)
        price.value = productVO.value.price
        pendingNum.value = productVO.value.pendingNum
    })
}

function toBackPage() {
    router.push("/productDetail/" + productId)
}
</script>


<template>
    <el-main>
        <el-button @click="toBackPage" type="primary" circle plain>
            <el-icon>
                <Back />
            </el-icon>
        </el-button>

        <h1 class="create-order-title">新建订单</h1>

        <el-form label-position="left" label-width="90px" size="large" class="create-order-form">

            <p class="product-title">{{ name }}</p>

            <el-descriptions :column="1">

                <el-descriptions-item style="font-size: 10px" label="品类">
                    {{ category }}
                </el-descriptions-item>

                <el-descriptions-item style="font-size: 10px" label="价格">
                    {{ price }} 元
                </el-descriptions-item>

                <el-descriptions-item style="font-size: 10px" label="库存">
                    {{ stock - pendingNum }} 件
                </el-descriptions-item>
            </el-descriptions>
            <el-form-item label="提货方式">
                <el-select id="deliveryMethod" v-model="deliveryMethod" placeholder="请选择">
                    <el-option value="DELIVERY" label="快递送达" />
                    <el-option value="PICKUP" label="到店自提" />
                </el-select>

            </el-form-item>
            <el-form-item label="地址" v-if="deliveryMethod == 'DELIVERY'">
                <el-input id="num" v-model="address" required placeholder="请输入快递送货地址" />
            </el-form-item>
            <el-form-item label="商品数量">
                <el-input id="num" v-model="num" required placeholder="请输入商品数量，单位（件）" />
            </el-form-item>
            <el-form-item style="font-size: 10px" label="订单总价">
                {{ num * price }} 元
            </el-form-item>

            <el-form-item>
                <el-button @click.prevent="handleCreateOrder()" :disabled="createOrderDisabled" type="primary" plain>
                    创建订单
                </el-button>
            </el-form-item>
            
            <PayDialog ref="dialogRef" />
        </el-form>
    </el-main>
</template>


<style scoped>
.create-order-title {
    margin-left: 25%;
}

.create-order-form {
    margin-left: 25%;
    width: 50%;
}
</style>

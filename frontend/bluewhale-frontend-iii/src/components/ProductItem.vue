<script setup lang="ts">
import { ref } from "vue"
import { getProductById, getRatingInfoById } from "../api/product.ts"
import { parseCategory } from "../utils"

// 使用props接收父界面传来的数据
const props = defineProps({
    productId: {
        type: Number,
        required: true
    }
})

const storeId = ref(0)
const name = ref('')
const photoUrlList = ref([])
const stock = ref(0)
const pendingNum = ref(0)
const category = ref()
const price = ref()
const photoUrl = ref('')

const rating = ref(0)
const numRated = ref(0)

initInfo(props.productId);

function initInfo(productId: number) {
    getProductById(productId).then(res => {
        storeId.value = res.data.result.storeId
        name.value = res.data.result.name
        photoUrlList.value = res.data.result.photoUrlList
        stock.value = res.data.result.stock
        category.value = res.data.result.category
        price.value = res.data.result.price
        pendingNum.value = res.data.result.pendingNum

        photoUrl.value = photoUrlList.value[0]
    })
    getRatingInfoById(productId).then(res => {
        rating.value = res.data.result.rating
        numRated.value = res.data.result.numRated
    })
}
</script>


<template>
    <el-card class="product-item-card" shadow="hover">
        <div class="photo-div">
            <el-image class="photo-image" :src="photoUrl" />
        </div>
        <div style="padding: 14px">
            <h1> {{ name }} </h1>
            <el-descriptions :column="1">
                <el-descriptions-item style="font-size: 10px" label="品类">
                    {{ parseCategory(category) }}
                </el-descriptions-item>
                <el-descriptions-item style="font-size: 10px" label="价格">
                    {{ price }} 元
                </el-descriptions-item>
                <el-descriptions-item style="font-size: 10px" label="库存">
                    {{ stock - pendingNum }} 件
                </el-descriptions-item>
                <el-descriptions-item style="font-size: 10px" label="参与评分">
                    {{ numRated }} 人
                </el-descriptions-item>
            </el-descriptions>
            <el-rate v-model="rating" disabled />
        </div>
    </el-card>
</template>


<style scoped>
.product-item-card {
    margin: 8px;
    border-radius: 8px;
    min-width: 31%;
}

.photo-div {
    display: flex;
    justify-content: center;
}

.photo-image {
    height: 200px;
}
</style>

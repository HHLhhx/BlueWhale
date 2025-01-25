<script setup lang="ts">
import { ref, watch } from 'vue'
import { comment, getOrderById } from '../api/order';
import { getRatingInfoById } from '../api/product'

const props = defineProps({
    orderId: {
        type: Number,
        required: true
    },
    isShow: {
        type: Boolean,
        default: false
    }
})
const dialogVisible = ref(false)

const textarea = ref('')
const value = ref(0)
const numRated = ref(0)
const productId = ref(0)

const emits = defineEmits(['close'])
const closeCreateComment = () => {
    textarea.value = '',
        value.value = 0,
        emits('close', false)
}

async function initInfo() {
    await getOrderById(props.orderId).then(res => {
        productId.value = res.data.result.productId
    })
    await getRatingInfoById(productId.value).then(res => {
        numRated.value = res.data.result.numRated
    })
}

watch(() => props.isShow, (flag) => {
    dialogVisible.value = flag;
    if (flag == true) {
        initInfo();
    }

}, { immediate: true })

function handleConfirm() {
    const content = {
        text: textarea.value,
        rating: value.value
    }
    comment(props.orderId, content);
    closeCreateComment()
}


</script>

<template>
    <el-dialog v-model="dialogVisible" @close="closeCreateComment" title="评论" width="30%" align-center>
        <div class="input-class">
            <el-input v-model="textarea" :rows="7" type="textarea" align-center maxlength="120" show-word-limit
                placeholder="请您评价我们的商品" />
        </div>
        <div class="rate-class">
            <span class="demonstration">请您为该商品评分</span>
            <el-rate v-model="value" />
            <p style="font-size: small;">
                目前已有{{ " " + numRated + " " }}人参与评分
            </p>
        </div>
        <div class="dialog-footer">
            <el-button @click="closeCreateComment">退出</el-button>
            <el-button type="primary" @click="handleConfirm">
                确定
            </el-button>
        </div>
    </el-dialog>
</template>

<style scoped>
.rate-class {
    padding: 20px;
    text-align: center;
    box-sizing: border-box;
}

.rate-class .demonstration {
    display: block;
    font-size: 15px;
    margin-bottom: 10px;
}

.dialog-footer {
    text-align: center;
}
</style>

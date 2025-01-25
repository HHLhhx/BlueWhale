<script setup lang="ts">
    import { ref, computed } from "vue"
    import { router } from '../../router'
    import { Back } from "@element-plus/icons-vue"
    import { addStock, getCommentByProductId, getProductById } from "../../api/product.ts"
    import { parseCategory } from "../../utils"
    import CommentItem from "../../components/CommentItem.vue"

    const role = sessionStorage.getItem("role")

    const productId = Number(router.currentRoute.value.params.productId)
    const actualStoreId = Number(sessionStorage.getItem("storeId"))
    const productVO = ref()
    const storeId = ref(0)
    const name = ref('')
    const photoUrlList = ref([])
    const stock = ref(0)
    const category = ref('')
    const price = ref(0)
    const pendingNum = ref(0)

    const addStockNumber = ref()

    const hasAddStockInput = computed(() => addStockNumber.value != null)
    const addStockDisabled = computed(() => {
        return !(hasAddStockInput.value)
    })

    initInfo()

    function initInfo() {
        getProductDetail()
        getCommentList()
    }

    //获取商品信息
    function getProductDetail() {
        getProductById(productId).then(res => {
            productVO.value = res.data.result
            storeId.value = productVO.value.storeId
            name.value = productVO.value.name
            photoUrlList.value = productVO.value.photoUrlList
            stock.value = productVO.value.stock
            category.value = productVO.value.category
            price.value = productVO.value.price
            pendingNum.value = productVO.value.pendingNum

        })
    }

    // 添加库存按钮
    function AddStock() {
        addStock(productId, addStockNumber.value).then(res => {
            if (res.data.code === '000') {
                ElMessage({
                    message: '添加库存成功！',
                    type: 'success',
                    center: true,
                })
                addStockNumber.value = null
                getProductDetail()
            } else if (res.data.code === '400') {
                ElMessage({
                    message: res.data.msg,
                    type: 'error',
                    center: true,
                })
            }
        })
    }

    //页面跳转
    function toBackPage() {
        router.push("/storeDetail/" + storeId.value)
    }
    function toCreateOrderPage() {
        sessionStorage.setItem("productId", String(productId))
        router.push("/createOrder/" + productId)
    }

    //获取评论信息
    const commentList = ref(<any>[]);
    function getCommentList() {
        getCommentByProductId(productId).then(res => {
            commentList.value = res.data.result
        })
    }
</script>


<template>
    <el-container>
        <el-aside width="25%" class="page-aside">
            <div class="product-detail-aside">
                <el-button @click="toBackPage()" type="primary" circle plain class="back-button">
                    <el-icon>
                        <Back />
                    </el-icon>
                </el-button>

                <el-carousel trigger="click" arrow="always" indicator-position="outside">
                    <el-carousel-item v-for="item in photoUrlList" :key="item">
                        <el-image class="logo-image" :src="item" />
                    </el-carousel-item>
                </el-carousel>

                <span class="product-title">{{ name }}</span>

                <el-descriptions :column="1">
                    <el-descriptions-item style="font-size: 10px" label="品类">
                        {{ parseCategory(category) }}
                    </el-descriptions-item>

                    <el-descriptions-item style="font-size: 10px" label="价格">
                        {{ price }} 元
                    </el-descriptions-item>

                    <el-descriptions-item style="font-size: 10px" label="库存" v-if="role === 'CUSTOMER'">
                        {{ stock - pendingNum }} 件
                    </el-descriptions-item>
                    <div v-if="role === 'STAFF' && storeId === actualStoreId">
                        <el-descriptions-item style="font-size: 10px" label="实际库存">
                            {{ stock }} 件
                        </el-descriptions-item>
                        <el-descriptions-item style="font-size: 10px" label="已下单暂未发货">
                            {{ pendingNum }} 件
                        </el-descriptions-item>
                    </div>
                </el-descriptions>
            </div>
            <div v-if="role === 'CUSTOMER'">
                <div class="add-stock-main">
                    <el-button class="create-order-button" type="primary" plain @click="toCreateOrderPage()">
                        购买
                    </el-button>
                </div>
            </div>
            <div v-if="role === 'STAFF' && storeId === actualStoreId">
                <div class="add-stock-main">
                    <el-input class="add-stock-number-input" v-model="addStockNumber" placeholder="请输入添加库存数">
                        <template #append>件</template>
                    </el-input>
                    <br>
                    <el-button @click="AddStock" :disabled="addStockDisabled" class="add-stock-button" type="primary"
                        plain>
                        新增库存
                    </el-button>
                </div>
            </div>
        </el-aside>

        <el-main>
            <div>
                <div>
                    <span class="main-title">评论区</span>
                    <span>（点击评论展开详情）</span>
                </div>
                <el-scrollbar max-height="750px" always>
                    <div v-if="commentList.length == 0" style="text-align: center;">暂无评论</div>
                    <div class="comment-item-list">
                        <CommentItem v-for="commentVO in commentList" :commentId="commentVO.id" :key="commentVO.id" />
                    </div>
                </el-scrollbar>
            </div>
        </el-main>
    </el-container>
</template>


<style scoped>
    .store-item-list {
        padding: 10px;
    }

    .item-card {
        padding: 2px;
    }

    .page-aside {
        border-right: lightgrey solid 1px;
    }

    .back-button {
        margin-top: 20px;
        margin-bottom: 20px;
    }

    .product-title {
        font-size: 30px;
        margin-bottom: 20px;
    }

    .product-detail-aside {
        width: 80%;
        margin-left: 10%;
        display: flex;
        flex-direction: column;
        justify-content: start;
    }

    .logo-image {
        width: 100%;
    }

    .add-stock-number-input {
        width: 300px;
        margin-bottom: 20px;
    }

    .add-stock-button {
        margin-bottom: 40px;
    }

    .main-title {
        font-size: 30px;
        margin-top: 20px;
        margin-bottom: 20px;
        margin-left: 20px;
    }

    .add-stock-main {
        margin-top: 20px;
        margin-left: 30px;
    }

    .create-order-button {
        margin-left: 15px;
    }

    .comment-item-list {
        display: grid;
        grid-template-columns: 100%;
        width: 90%;
        padding: 2px;
        justify-content: center;
    }
</style>

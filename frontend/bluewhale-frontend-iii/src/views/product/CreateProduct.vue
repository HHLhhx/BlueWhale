<script setup lang="ts">
import { ref, computed } from "vue"
import { router } from '../../router'
import { Back, UploadFilled } from "@element-plus/icons-vue"
import { createProduct } from "../../api/product.ts"
import { uploadImage } from "../../api/tools.ts"

const storeId = Number(router.currentRoute.value.params.storeId)
const name = ref('')
const category = ref()
const price = ref()
const photoUrlList = ref<string[]>([])

const imageFileList = ref([])

const hasNameInput = computed(() => name.value != '')
const hasCategoryInput = computed(() => category.value != null)
const hasPriceInput = computed(() => price.value != '')
const hasImageFile = computed(() => imageFileList.value.length != 0)
const createProductDisabled = computed(() => {
    return !(hasNameInput.value && hasCategoryInput.value && hasPriceInput.value && hasImageFile.value);
})

function handleCreateProduct() {
    const payload = {
        storeId: storeId,
        name: name.value,
        category: category.value,
        price: price.value,
        photoUrlList: photoUrlList.value
    }
    createProduct(payload).then(res => {
        if (res.data.code === '000') {
            ElMessage({
                message: '添加商品成功！',
                type: 'success',
                center: true,
            })
            name.value = ''
            category.value = ''
            price.value = ''
            photoUrlList.value = []
            imageFileList.value = []
        } else if (res.data.code === '400') {
            ElMessage({
                message: res.data.msg,
                type: 'error',
                center: true,
            })
        }
    })
}

function handleChange(file: any, fileList: any) {
    imageFileList.value = fileList
    let formData = new FormData()
    formData.append('file', file.raw)
    uploadImage(formData).then(res => {
        photoUrlList.value.push(res.data.result)
    });
}

function uploadHttpRequest() {
    return new XMLHttpRequest()
}

function toBackPage() {
    router.push("/storeDetail/" + storeId)
}
</script>


<template>
    <el-main>
        <el-button @click="toBackPage()" type="primary" circle plain>
            <el-icon>
                <Back />
            </el-icon>
        </el-button>

        <h1 class="create-product-title">新建商品</h1>

        <el-form label-position="left" label-width="90px" size="large" class="create-product-form">

            <el-form-item label="商品名">
                <el-input id="name" v-model="name" required placeholder="请输入商品名" />
            </el-form-item>

            <el-form-item label="品类">
                <el-select id="category" v-model="category" placeholder="请选择">
                    <el-option value="FOOD" label="食品" />
                    <el-option value="CLOTHES" label="服饰" />
                    <el-option value="FURNITURE" label="家具" />
                    <el-option value="ELECTRONICS" label="电子产品" />
                    <el-option value="ENTERTAINMENT" label="娱乐" />
                    <el-option value="SPORTS" label="体育产品" />
                    <el-option value="LUXURY" label="奢侈品" />
                </el-select>
            </el-form-item>

            <el-form-item label="单价">
                <el-input id="price" v-model="price" required placeholder="请输入商品单价，单位（元）" />
            </el-form-item>

            <el-form-item label="商品照片">
                <el-upload v-model:file-list="imageFileList" :on-change="handleChange" :on-remove="handleChange"
                    class="upload-demo" list-type="picture" :http-request="uploadHttpRequest" drag>
                    <el-icon class="el-icon--upload">
                        <upload-filled />
                    </el-icon>
                    <div class="el-upload__text">
                        将文件拖到此处或单击此处上传。
                    </div>
                </el-upload>

            </el-form-item>

            <el-form-item>
                <el-button @click.prevent="handleCreateProduct()" :disabled="createProductDisabled" type="primary"
                    plain>
                    创建商品
                </el-button>
            </el-form-item>
        </el-form>
    </el-main>
</template>


<style scoped>
.create-product-title {
    margin-left: 25%;
}

.create-product-form {
    margin-left: 25%;
    width: 50%;
}
</style>

<script setup lang="ts">
import { ref } from "vue"
import { router } from '../../router'
import {searchProduct} from "../../api/product.ts"
import ProductItem from "../../components/ProductItem.vue"
import SearchComponent from "../../components/SearchComponent.vue";

const productList = ref()

function toProductDetailPage(productId: number) {
  router.push("/productDetail/" + productId)
}
const submitForm = (formData:any) => {
  searchProduct(formData).then(res => {
    productList.value = res.data.result
  });
}

</script>


<template>
  <el-container>
    <el-main>
      <SearchComponent @clickSearch="submitForm"></SearchComponent>
      <div>
        <span class="product-list-title">商品列表</span>
      </div>
      <div>
        <el-scrollbar max-height="750px" always>
          <div class="all-product-main">
            <ProductItem class="product-item-list" v-for="productVO in productList"
                         :productId="productVO.id" :key = "productVO.id"  @click="toProductDetailPage(productVO.id)" />
          </div>
        </el-scrollbar>
      </div>
    </el-main>
  </el-container>

</template>


<style scoped>
.page-aside {
  border-right: lightgrey solid 1px;
}

.back-button {
  margin-top: 20px;
  margin-bottom: 20px;
}

.product-list-title {
  font-size: 30px;
  margin-top: 20px;
  margin-bottom: 20px;
  margin-left: 20px;
}

.all-product-main {
  display: flex;
  flex-direction: row;
  padding: 20px;
  flex-flow: wrap;
  align-content: start;
  justify-content: start;
}
</style>

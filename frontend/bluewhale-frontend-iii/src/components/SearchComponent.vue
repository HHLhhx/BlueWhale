<script setup lang="ts">
import { ref, reactive } from 'vue'
import type { ElForm } from 'element-plus'

const storeName =ref('')
const  name =ref('')
const  minPrice =ref( '')
const  maxPrice =ref('')
const  category =ref('')

// 查找
const emit = defineEmits(['clickSearch'])
const submitForm=()=> {
  const productData = reactive({
    storeName:storeName.value,
    name: name.value,
    minPrice: 0,
    maxPrice: Number.MAX_VALUE,
    category: category.value,
  })
  if(maxPrice.value==''){
    productData.maxPrice = Number.MAX_VALUE
  }else {
    productData.maxPrice =parseInt(maxPrice.value, 10);
  }
  if(minPrice.value==''){
    productData.minPrice = 0
  }else {
    productData.minPrice =parseInt(minPrice.value, 10);
  }
  emit('clickSearch', productData)
}
// 重置
const submitReset = () => {
  storeName.value =('')
  name.value =('')
  minPrice.value =('')
  maxPrice.value =('')
  category.value =('')
  submitForm()
}
submitForm()

</script>

<template>
  <div class="mysearch">
    <el-form  label-width="80px">
      <el-row :gutter="24">
        <el-col :span="5">
          <el-form-item label="商品名称" prop="name">
            <el-input v-model="name"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="5">
          <el-form-item label="商店名称" prop="storeName">
            <el-input v-model="storeName"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="4">
          <el-form-item label="品类" prop="category">
            <el-select v-model="category" placeholder="请选择">
              <el-option value="FOOD" label="食品" />
              <el-option value="CLOTHES" label="服饰" />
              <el-option value="FURNITURE" label="家具" />
              <el-option value="ELECTRONICS" label="电子产品" />
              <el-option value="ENTERTAINMENT" label="娱乐" />
              <el-option value="SPORTS" label="体育产品" />
              <el-option value="LUXURY" label="奢侈品" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="3">
          <el-form-item label="最小价格">
            <el-input v-model="minPrice"  prop="minPrice"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="3">
        <el-form-item label="最大价格">
          <el-input v-model="maxPrice"  prop="maxPrice"></el-input>
        </el-form-item>
      </el-col>
        <el-col :span="2" >
          <el-button type="primary"  @click="submitForm">
            查 询
          </el-button>
        </el-col>
        <el-col :span="2" :offset="0">
          <el-button type="primary"  @click="submitReset">
            重 置
          </el-button>
        </el-col>
      </el-row>
      <el-row :gutter="20">

      </el-row>
    </el-form>
  </div>
</template>

<style scoped>
.mysearch {
  padding: 20px;
}
</style>

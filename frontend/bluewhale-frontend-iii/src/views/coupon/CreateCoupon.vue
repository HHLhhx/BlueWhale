<script setup lang="ts">
import {computed, ref} from 'vue'
import {router} from '../../router'
import {Back} from "@element-plus/icons-vue"
import {createCoupon} from "../../api/coupon.ts"

// 输入框值（需要前端阻拦不合法输入）
const couponType = ref('')
const fulfillPrice = ref(0)
const reducePrice = ref(0)
const totalNum = ref(0)
const expireTime = ref('')
const ratio = ref(100)
const createDisabled = computed(() => {
  return !(couponType.value != ''
      && (couponType.value == "SPECIAL"
          || (couponType.value == "FULL_REDUCTION" && reducePrice.value != 0 && fulfillPrice.value != 0)
          || (couponType.value == "VOUCHER" && reducePrice.value != 0)
          || (couponType.value == "DIRECT_DISCOUNT" && ratio.value < 100))
      && expireTime.value != ''
      && totalNum.value != 0)
})

function handleCreateCoupon() {
  const coupon = {
    couponType: couponType.value,
    reducePrice: reducePrice.value,
    fulfillPrice: fulfillPrice.value,
    expireTime: expireTime.value,
    totalNum: totalNum.value,
    ratio:ratio.value / 100
  };
  createCoupon(coupon).then(res => {
    if (res.data.code === '000') {
      ElMessage({
        message: '创建优惠券组成功！',
        type: 'success',
        center: true,
      })
      couponType.value = ''
      reducePrice.value = 0
      fulfillPrice.value = 0
      totalNum.value = 0
      toBackPage()
    } else if (res.data.code === '400') {
      ElMessage({
        message: res.data.msg,
        type: 'error',
        center: true,
      })
    }
  })
}


function toBackPage() {
  router.push("/allCoupon")
}
</script>


<template>
  <el-main>
    <el-button @click="toBackPage()" type="primary" circle plain>
      <el-icon>
        <Back/>
      </el-icon>
    </el-button>

    <h1 class="create-coupon-title">新建优惠券组</h1>

    <el-form label-position="left" label-width="90px" size="large" class="create-coupon-form">

      <el-form-item label="优惠券类型">
        <el-select id="couponType" v-model="couponType" placeholder="请选择">
          <el-option value="FULL_REDUCTION" label="满减券"/>
          <el-option value="SPECIAL" label="蓝鲸券"/>
          <el-option value="VOUCHER" label="代金券"/>
          <el-option value="DIRECT_DISCOUNT" label="普通打折券"/>
        </el-select>
      </el-form-item>
      <el-form-item v-if="couponType == 'FULL_REDUCTION'">
        满额
        <el-input id="fulfillPrice" v-model="fulfillPrice" required/>
        优惠
        <el-input id="reducePrice" v-model="reducePrice" required/>
      </el-form-item>
      <el-form-item v-if="couponType == 'VOUCHER'">
        优惠 ：
        <el-input id="reducePrice" v-model="reducePrice" required style="width: 20%"/>元
      </el-form-item>
      <el-form-item v-if="couponType == 'DIRECT_DISCOUNT'">
        打折为原价的 ：
        <el-input id="ratio" v-model="ratio" required style="width: 20%"/>%

      </el-form-item>
      <el-form-item label="优惠券数量">
        <el-input id="totalNum" v-model="totalNum" required/>
      </el-form-item>
      <el-form-item label="过期时间">
        <el-date-picker v-model="expireTime" type="date" placeholder="过期日期"/>
      </el-form-item>
      <el-form-item>
        <el-button @click.prevent="handleCreateCoupon()" :disabled="createDisabled" type="primary" plain>
          创建优惠券组
        </el-button>
      </el-form-item>
    </el-form>
  </el-main>
</template>


<style scoped>
.create-coupon-title {
  margin-left: 25%;
}

.create-coupon-form {
  margin-left: 25%;
  width: 50%;
}
</style>

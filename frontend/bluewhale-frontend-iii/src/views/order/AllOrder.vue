<script setup lang="ts">
import {ref} from "vue"
import {getAllOrder, getOrderReport} from "../../api/order.ts"
import OrderItem from "../../components/OrderItem.vue"
import {Download} from "@element-plus/icons-vue";
const orderList = ref(<any>[])
const role = sessionStorage.getItem("role")

getAllOrder().then(res => {
  orderList.value = res.data.result
})
if(role === 'CEO' || role === 'STAFF'){
  getReport();
}
// 获取报表信息
const orderReport = ref()
function getReport() {
  getOrderReport().then(res => {
    orderReport.value = res.data.result
  })
}
async function goToLink() {
  window.location.href = orderReport.value
}
</script>


<template>
  <el-main>
    <div class="download-button" v-if="role === 'CEO' || role === 'STAFF'">
      <el-tooltip content="下载订单报表" placement="top" effect="light">
        <el-button type="primary" :icon="Download" style="float: right" circle @click="goToLink" />
      </el-tooltip>
    </div>
    <div v-if="orderList.length == 0" style="text-align: center;">暂无订单</div>
    <div class="order-item-list">
      <OrderItem
          v-for="orderVO in orderList" :orderId="orderVO.id" :key="orderVO.id"/>
    </div>
  </el-main>
</template>


<style scoped>
.order-item-list {
  display: flex;
  padding: 2px;
  flex-flow: wrap;
  justify-content: center;
}
</style>

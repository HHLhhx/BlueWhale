<script setup lang="ts">
import { ref, watch } from "vue";
import { getStoreById } from "../api/store.ts";
import { parseDeliveryMethod, parseTime } from "../utils/index.ts";
import { ElDescriptions } from "element-plus";
import { getOrderById, receive, send } from "../api/order.ts";
import { getProductById } from "../api/product.ts";
import CreateCommentItem from "./CreateCommentItem.vue";

// 使用props接收父界面传来的数据
const props = defineProps({
  orderId: {
    type: Number,
    required: true,
  },
  isShow: {
    type: Boolean,
    default: false,
  },
});

const role = sessionStorage.getItem("role");
// 订单属性
const productId = ref(0);
const photoUrlList = ref([]);
const photoUrl = ref("");
const productName = ref("");
const storeId = ref(0);
const storeName = ref("");
const num = ref(0);
const totalPrice = ref(0);
const trueTotalPrice = ref(0);
const deliveryMethod = ref();
const createTime = ref("");
const userPhoneNum = ref();
const orderState = ref("");

async function updateOrder(id: number) {
  await getOrderById(id).then((res) => {
    productId.value = res.data.result.productId;
    storeId.value = res.data.result.storeId;
    num.value = res.data.result.num;
    totalPrice.value = res.data.result.totalPrice;
    trueTotalPrice.value = res.data.result.trueTotalPrice;
    deliveryMethod.value = parseDeliveryMethod(res.data.result.deliveryMethod);
    createTime.value = parseTime(res.data.result.createTime);
    userPhoneNum.value = res.data.result.userPhoneNum;
    orderState.value = res.data.result.orderState;
  });

  await getStoreById(storeId.value).then((res) => {
    storeName.value = res.data.result.name;
  });

  await getProductById(productId.value).then((res) => {
    productName.value = res.data.result.name;
    photoUrlList.value = res.data.result.photoUrlList;
    photoUrl.value = photoUrlList.value[0];
  });
}

const dialogVisible = ref(false);
watch(
  () => props.isShow,
  (flag) => {
    dialogVisible.value = flag;
    if (flag == true) {
      updateOrder(props.orderId);
    }
  },
  { immediate: true }
);

const emits = defineEmits(["close"]);
const closeOrderDetail = () => {
  emits("close", false);
};

// 处理和子组件CreateCommentItem的绑定
const isShowCreateComment = ref(false);
const openCreateComment = () => {
  isShowCreateComment.value = true;
};
const closeCreateComment = (flag: boolean) => {
  isShowCreateComment.value = flag;
};

// 支付弹窗实例
const dialogRef = ref();
function showPayDialog(orderId: number) {
  dialogRef.value.open(orderId);
}
</script>

<template>
  <div class="order-dialog">
    <el-dialog
      v-model="dialogVisible"
      @close="closeOrderDetail"
      title="订单详情"
      width="50%"
      align-center
    >
      <div class="order-descriptions">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="商品图片" label-align="center" :span="2">
            <div class="img-div">
              <el-image class="img-class" :src="photoUrl" />
            </div>
          </el-descriptions-item>
          <el-descriptions-item
            label="商品名称"
            label-align="center"
            align="center"
            width="15%"
          >
            {{ productName }}
          </el-descriptions-item>
          <el-descriptions-item
            label="所属商店"
            label-align="center"
            align="center"
            width="15%"
          >
            {{ storeName }}
          </el-descriptions-item>
          <el-descriptions-item label="购买数量" label-align="center" align="center">
            {{ num }}
          </el-descriptions-item>
          <el-descriptions-item label="实际支付" label-align="center" align="center">
            <div v-if="trueTotalPrice === null">
              {{ totalPrice }}
            </div>
            <div v-else>
              {{ trueTotalPrice }}
            </div>
          </el-descriptions-item>
          <el-descriptions-item label="提货方式" label-align="center" align="center">
            {{ deliveryMethod }}
          </el-descriptions-item>
          <el-descriptions-item label="创建时间" label-align="center" align="center">
            {{ createTime }}
          </el-descriptions-item>
          <el-descriptions-item label="用户联系方式" label-align="center" align="center">
            {{ userPhoneNum }}
          </el-descriptions-item>
          <el-descriptions-item label="订单状态" label-align="center" align="center">
            <el-tag effect="dark" type="warning" v-if="orderState === 'UNGET'"
              >待收货</el-tag
            >
            <el-button
              class="button"
              effect="dark"
              @click="receive(orderId)"
              v-if="orderState === 'UNGET' && role == 'CUSTOMER'"
              >收货</el-button
            >

            <el-tag effect="dark" type="danger" v-if="orderState === 'UNPAID'"
              >待支付</el-tag
            >
            <el-button
              class="button"
              effect="dark"
              @click="showPayDialog(orderId)"
              v-if="orderState === 'UNPAID' && role == 'CUSTOMER'"
              >支付</el-button
            >

            <el-tag effect="dark" type="warning" v-if="orderState === 'UNSEND'"
              >待发货</el-tag
            >
            <el-button
              class="button"
              effect="dark"
              @click="send(orderId)"
              v-if="orderState === 'UNSEND' && role == 'STAFF'"
              >发货</el-button
            >

            <el-tag effect="dark" type="warning" v-if="orderState === 'UNCOMMENT'"
              >待评价</el-tag
            >
            <el-button
              class="button"
              effect="dark"
              @click="openCreateComment()"
              v-if="orderState === 'UNCOMMENT' && role == 'CUSTOMER'"
              >评价</el-button
            >

            <el-tag effect="dark" type="success" v-if="orderState === 'DONE'"
              >已完成</el-tag
            >
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <div class="close-button">
        <el-button @click="closeOrderDetail">退出</el-button>
      </div>
      <div class="comment-dialog">
        <CreateCommentItem
          :orderId="orderId"
          :isShow="isShowCreateComment"
          @close="closeCreateComment"
        />
      </div>
      <div class="pay-dialog">
        <PayDialog ref="dialogRef" />
      </div>
    </el-dialog>
  </div>
</template>

<style scoped>
.img-div {
  display: flex;
  justify-content: center;
}

.img-class {
  width: 55%;
}

.close-button {
  text-align: center;
  padding-top: 25px;
}
</style>

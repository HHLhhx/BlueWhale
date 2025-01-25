<script setup lang="ts">
import {ref, computed} from "vue"
import PayDialog from "./PayDialog.vue"
import {deliverOrder, receive, getOrderById} from "../api/order.ts"
import {parseDeliveryMethod, parseTime} from "../utils"
import CreateCommentItem from "./CreateCommentItem.vue";
import OrderDetailItem from "./OrderDetailItem.vue";
import {getStoreById} from "../api/store.ts";
import {getProductById} from "../api/product.ts";

const props = defineProps({
  orderId: {
    type: Number,
    required: true
  }
})

const role = sessionStorage.getItem("role")
const isShowCreateComment = ref(false)
const isShowOrderDetail = ref(false)
//订单详细信息
const userId = ref(0)
const productId = ref(0)
const productName = ref('')   //商品名字
const storeName = ref('')   //商品名字
const price = ref(0)          //商品单价
const amount = ref(0)         //购买数量
const deliveryMethod = ref('')          //订单类型
const content = ref('')       //订单评论
const status = ref('')        //订单状态
const createTime = ref('')

const storeId = ref(0)
//订单总价（折扣前）
const totalPrice = ref(0)

//评论dialog
const dialogRef = ref()


getOrderDetail()

function getOrderDetail() {
  getOrderById(props.orderId).then(res => {
    userId.value = res.data.result.userId
    productId.value = res.data.result.productId
    amount.value = res.data.result.num
    price.value = res.data.result.price
    deliveryMethod.value = res.data.result.deliveryMethod
    content.value = res.data.result.content
    status.value = res.data.result.orderState
    createTime.value = res.data.result.createTime
    storeId.value = res.data.result.storeId
    totalPrice.value = res.data.result.totalPrice

    getStoreById(storeId.value).then((res) => {
      storeName.value = res.data.result.name;
    });
    getProductById(productId.value).then((res) => {
      productName.value = res.data.result.name;
    });
  })
}


function handlePay() {
  //触发支付订单弹窗，传入当前orderId
  dialogRef.value.open(props.orderId)
}


//注册回调，用于更新当前订单详情（如果不用这个，可能只能强制刷新整个界面了，用户体验不好）
function handleConfirmOrder(success: boolean) {
  if (success) {
    getOrderDetail()
  }
}

//发货
function handleDeliver() {
  deliverOrder(props.orderId).then(res => {
    if (res.data.code === '000') {
      ElMessage({
        message: '订单发货成功！',
        type: 'success',
        center: true,
      })
      getOrderDetail()
    } else if (res.data.code === '400') {
      ElMessage({
        message: res.data.msg,
        type: 'error',
        center: true,
      })
    }
  })
}


// 处理和子组件OrderDetailItem的绑定
const openOrderDetail = () => {
  isShowOrderDetail.value = true
}
const closeOrderDetail = (flag: boolean) => {
  isShowOrderDetail.value = flag
}
//打开评论dialog
function handleComment() {
  isShowCreateComment.value = true
}
const closeCreateComment = (flag: boolean) => {
  isShowCreateComment.value = flag
}

// 对于变化的内容使用计算属性返回响应式结果
const statusText = computed(() => {
  switch (status.value) {
    case 'UNPAID':
      return "待顾客支付"
    case 'UNSEND':
      return "待商家发货"
    case 'UNGET':
      return "待顾客收货"
    case 'UNCOMMENT':
      return "待顾客评价"
    case 'DONE':
      return "已完成"
    default:
      return "状态标签"
  }
})
</script>


<template>
  <el-card class="order-item-card" shadow="hover">

    <template #header>
      <div class="card-header">
        <div>
          <span> 订单号 {{ props.orderId }}</span>
          <el-tag style="margin-left: 8px" v-if="status!=='DONE'" type="info"> {{ statusText }}</el-tag>
          <el-tag style="margin-left: 8px" v-if="status==='DONE'" type="success"> {{ statusText }}</el-tag>
        </div>

        <el-button @click="handlePay" v-if="status==='UNPAID' && role==='CUSTOMER'"
                   class="status-change-button" size="small" type="primary">
          支付
        </el-button>
        <el-button @click="handleDeliver" v-if="status==='UNSEND' && role==='STAFF'"
                   class="status-change-button" size="small" type="primary">
          发货
        </el-button>
        <el-button @click="receive(props.orderId)" v-if="status==='UNGET' && role==='CUSTOMER'"
                   class="status-change-button" size="small" type="primary">
          收货
        </el-button>
        <el-button @click="handleComment" v-if="status==='UNCOMMENT'&& role==='CUSTOMER'"
                   class="status-change-button" size="small" type="primary">
          评价
        </el-button>
      </div>
    </template>

    <el-descriptions
        :column="1"
    >
      <el-descriptions-item style="font-size: 15px" label="商品">
        {{ productName }}
      </el-descriptions-item>
      <el-descriptions-item style="font-size: 15px" label="数量">
        {{ amount }} 件
      </el-descriptions-item>
      <el-descriptions-item style="font-size: 15px" label="总价">
        {{ totalPrice }} 元
      </el-descriptions-item>
      <el-descriptions-item style="font-size: 15px" label="取货类型">
        <el-tag> {{ parseDeliveryMethod(deliveryMethod) }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item style="font-size: 15px" label="创建时间">
        {{ parseTime(createTime) }}
      </el-descriptions-item>

    </el-descriptions>
    <el-tooltip effect="dark" content="点击查看或修改订单详情" placement="top">
      <el-button class="button" type="warning" @click="openOrderDetail()">查看</el-button>
    </el-tooltip>
    <div class="order-item-class">
      <OrderDetailItem :orderId="props.orderId" :isShow="isShowOrderDetail" @close="closeOrderDetail" />
    </div>
    <div class="create-comment-class">
      <CreateCommentItem :orderId="props.orderId" :isShow="isShowCreateComment" @close="closeCreateComment" />
    </div>
    <PayDialog ref="dialogRef" @operation-finish="handleConfirmOrder"/>

  </el-card>
</template>


<style scoped>
.order-item-card {
  margin: 20px;
  border-radius: 8px;
  min-width: max-content;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.status-change-button {
  margin-left: 10px;
}
</style>
import { axios } from '../utils/request'
import { ORDER_MODULE } from './_prefix'
import { router } from "../router";

// 做了修改，匹配后端orderVO
type OrderInfo = {
    productId: number,
    storeId: number,
    num: number,
    deliveryMethod: string,
    orderState: string,
    totalPrice: number
}

type OrderCommentInfo = {
    text: string,
    rating: number
}

// 创建订单
export const createOrder = (orderInfo: OrderInfo) => {
    return axios.post(`${ORDER_MODULE}/`, orderInfo,
        {headers: {'Content-Type': 'application/json'}})
        .then(res => {
            return res
        })
}

//支付
export const payOrder = (orderId: number, couponList: number[], isDirectPay: boolean) => {
    return axios.post(`${ORDER_MODULE}/${orderId}/pay?isDirectPay=${isDirectPay}`, couponList)
        .then(res => {
            return res
        })
}

// 获取全部订单
export const getAllOrder = () => {
    return axios.get(`${ORDER_MODULE}`)
        .then(res => {
            return res
        })
}

// 订单收货
export const getOrder = (orderId: number) => {
    return axios.post(`${ORDER_MODULE}/get/?orderId=${orderId}`)
        .then(res => {
            return res
        })
}

// 根据订单Id获取订单
export const getOrderById = (orderId: number) => {
    return axios.get(`${ORDER_MODULE}/${orderId}`)
        .then(res => {
            return res
        })
}

// 在订单上评论
export const commentOnOrder = (comment: OrderCommentInfo, orderId: number) => {
    return axios.post(`${ORDER_MODULE}/${orderId}/comment`, comment)
        .then(res => {
            return res
        })
}

// 发货
export const deliverOrder = (orderId: number) => {
    return axios.post(`${ORDER_MODULE}/${orderId}/delivery`)
        .then(res => {
            return res
        })
}

// 收货
export const receiveOrder = (orderId: number) => {
    return axios.post(`${ORDER_MODULE}/${orderId}/receive`)
        .then(res => {
            return res
        })
}

// 计算订单价格
export const priceOfOrder = (orderId: number, couponList: number[]) => {
    return axios.post(`${ORDER_MODULE}/${orderId}/price`, couponList)
        .then(res => {
            return res;
        })
}

// 获取订单可用优惠券
export const getUsefulCoupon = (orderId: number) => {
    return axios.post(`${ORDER_MODULE}/${orderId}/coupon`)
        .then(res => {
            return res;
        })
}

// 获取订单报表
export const getOrderReport = () => {
    return axios.get(`${ORDER_MODULE}/report`)
        .then(res => {
            return res
        })
}

function handleRes(res: any) {
    if (res.data.code === '000') {
        ElMessage({
            message: '成功！',
            type: 'success',
            center: true,
        })
        router.go(0)
    } else if (res.data.code === '400') {
        ElMessage({
            message: res.data.msg,
            type: 'error',
            center: true,
        })
    }
}

export function pay(id: number, couponList: number[], isDirectPay: boolean) {
    return payOrder(id, couponList, isDirectPay).then((res: any) => {
        return res
    })
}

export function receive(id: number) {
    receiveOrder(id).then((res: any) => {
        handleRes(res)
    })

}

export function send(id: number) {
    deliverOrder(id).then((res: any) => {
        handleRes(res)
    })
}

export function comment(id: number, content: OrderCommentInfo) {
    commentOnOrder(content, id).then((res: any) => {
        handleRes(res)
    })
}

import { axios } from '../utils/request'
import { COUPON_MODULE } from './_prefix'

type CouponInfo = {
    couponType: string,
    reducePrice: number,
    fulfillPrice: number,
    expireTime: string,
    totalNum: number,
    ratio: number
}

// 创建优惠券组
export const createCoupon = (couponInfo: CouponInfo) => {
    return axios.post(`${COUPON_MODULE}/`, couponInfo,
        { headers: { 'Content-Type': 'application/json' } })
        .then(res => {
            return res
        })
}

// 所有人查看相应优惠券组
export const getAllCouponSet = () => {
    return axios.get(`${COUPON_MODULE}/getSet`)
        .then(res => {
            return res
        })
}

// 顾客查看所有优惠券
export const getAllCoupon = (type: string) => {
    return axios.get(`${COUPON_MODULE}/coupon/${type}`)
        .then(res => {
            return res
        })
}

// 根据券组Id获取指定优惠券组
export const getCouponSetById = (couponId: number) => {
    return axios.get(`${COUPON_MODULE}/${couponId}/get`)
        .then(res => {
            return res
        })
}

// 根据优惠券组id判断优惠券是否过期
export const checkValid = (setId: number) => {
    return axios.get(`${COUPON_MODULE}/${setId}/valid`)
        .then(res => {
            return res
        })
}
export const acquire = (setId: number) => {
    return axios.get(`${COUPON_MODULE}/${setId}/acquire`)
        .then(res => {
            return res
        })
}
export const check = (setId: number) => {
    return axios.get(`${COUPON_MODULE}/${setId}/check`)
        .then(res => {
            return res
        })
}
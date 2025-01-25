import { axios } from '../utils/request'
import { PRODUCT_MODULE } from './_prefix'

type ProductInfo = {
    storeId: number,
    name: string,
    category: string,
    price: number,
    photoUrlList: string[]
}
type SearchInfo = {
    storeName:string
    name: string,
    minPrice: number,
    maxPrice: number,
    category: string,
}

// 创建商品
export const createProduct = (productInfo: ProductInfo) => {
    return axios.post(`${PRODUCT_MODULE}/`, productInfo,
        { headers: { 'Content-Type': 'application/json' } })
        .then(res => {
            return res
        })
}

// 获取商店下所有商品
export const getProductsByStoreId = (storeId: number) => {
    return axios.get(`${PRODUCT_MODULE}/?storeId=${storeId}`)
        .then(res => {
            return res
        })
}

// 根据商品Id获取商品信息
export const getProductById = (productId: number) => {
    return axios.get(`${PRODUCT_MODULE}/${productId}`)
        .then(res => {
            return res
        })
}

// 添加库存
export const addStock = (id: number, number: number) => {
    return axios.post(`${PRODUCT_MODULE}/${id}/stock?number=${number}`)
        .then(res => {
            return res
        })
}

// 根据商品Id获取商品评分信息
export const getRatingInfoById = (productId: number) => {
    return axios.get(`${PRODUCT_MODULE}/${productId}/rating` )
    .then(res => {
        return res
    })
}

// 根据商品Id获取评论
export const getCommentByProductId = (productId: number) => {
    return axios.get(`${PRODUCT_MODULE}/${productId}/comments` )
        .then(res => {
            return res
        })
}
// 根据商品Id获取评论
export const searchProduct = (searchInfo: SearchInfo) => {
    return axios.get(`${PRODUCT_MODULE}/search`, { params: searchInfo })
        .then(res => {
            return res;
        });
};

// Lab2新增
import { axios } from '../utils/request'
import { API_MODULE } from './_prefix'

// 上传图片文件
export const uploadImage = (payload: any) => {
    return axios.post(`${API_MODULE}/images`, payload, {
        headers: {
            'Content-Type': "multipart/form-data;"
        }
    })
        .then(res => {
            return res
        })
}

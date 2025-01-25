import { axios } from '../utils/request'
import { COMMENT_MODULE } from './_prefix'

//根据id获取评论
export const getCommentById = (commentId: number) => {
    return axios.get(`${COMMENT_MODULE}/${commentId}`)
        .then(res => {
            return res
        })
}

//在商品评论上评论
export const commentOn = (comment: string, commentId: number) => {
    const commentVO = {
        text: comment
    }
    return axios.post(`${COMMENT_MODULE}/commentOther/${commentId}`, commentVO)
        .then(res => {
            return res
        })
}

//获取评论上的评论
export const getCommentsOn = (commentId: number) => {
    return axios.post(`${COMMENT_MODULE}/${commentId}`)
        .then(res => {
            return res
        })
}

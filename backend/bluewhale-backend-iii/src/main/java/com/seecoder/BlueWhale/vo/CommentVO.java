package com.seecoder.BlueWhale.vo;

import java.util.Date;

import com.seecoder.BlueWhale.po.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentVO {
    private Integer id;
    private Integer productId;
    private Integer orderId;
    private Integer userId;
    private String userName;
    private Integer storeId;
    private Date createTime;
    private Integer commentOnId;
    private String text;
    private Double rating;

    public Comment toPO() {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setUserId(userId);
        comment.setOrderId(orderId);
        comment.setStoreId(storeId);
        comment.setProductId(productId);
        comment.setCommentOnId(commentOnId);
        comment.setRating(rating);
        comment.setCreateTime(createTime);
        comment.setText(text);
        return comment;
    }
}

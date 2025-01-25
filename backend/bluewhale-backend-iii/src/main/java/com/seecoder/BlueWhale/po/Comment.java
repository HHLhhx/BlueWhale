package com.seecoder.BlueWhale.po;

import java.util.Date;

import javax.persistence.*;

import com.seecoder.BlueWhale.vo.CommentVO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "`comment`")
public class Comment {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer id;

    @Basic
    @Column(name = "product_id")
    private Integer productId;

    @Basic
    @Column(name = "user_id")
    private Integer userId;

    @Basic
    @Column(name = "order_id")
    private Integer orderId;

    @Basic
    @Column(name = "store_id")
    private Integer storeId;

    @Basic
    @Column(name = "rating")
    private Double rating;

    @Basic
    @Column(name = "create_time")
    private Date createTime;

    @Basic
    @Column(name = "comment_on_id")
    private Integer commentOnId;

    @Basic
    @Column(name = "text")
    private String text;

    public CommentVO toVO() {
        CommentVO comment = new CommentVO();
        comment.setId(id);
        comment.setOrderId(orderId);
        comment.setProductId(productId);
        comment.setUserId(userId);
        comment.setStoreId(storeId);
        comment.setCommentOnId(commentOnId);
        comment.setCreateTime(createTime);
        comment.setRating(rating);
        comment.setText(text);
        return comment;
    }
}
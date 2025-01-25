package com.seecoder.BlueWhale.repository;

import com.seecoder.BlueWhale.po.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findAllByUserId(Integer userId);

    List<Comment> findAllByOrderId(Integer orderId);

    List<Comment> findAllByProductId(Integer productId);

    List<Comment> findAllByStoreId(Integer storeId);
    List<Comment> findAllByCommentOnId(Integer commentOnId);

    List<Double> findAllRatingByOrderId(Integer orderId);
    List<Double> findAllRatingByStoreId(Integer storeId);
    List<Double> findAllRatingByProductId(Integer storeId);

    Comment findByOrderId(Integer commentId);
}

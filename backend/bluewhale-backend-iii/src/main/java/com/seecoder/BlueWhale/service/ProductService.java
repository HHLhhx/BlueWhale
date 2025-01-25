package com.seecoder.BlueWhale.service;

import java.util.List;

import com.seecoder.BlueWhale.enums.CategoryEnum;
import com.seecoder.BlueWhale.po.Comment;
import com.seecoder.BlueWhale.po.Product;
import com.seecoder.BlueWhale.vo.ProductVO;
import com.seecoder.BlueWhale.vo.RatingVO;

public interface ProductService {
    Boolean create(ProductVO productVO);

    Boolean addStock(Integer id, Integer number);

    List<ProductVO> getAllProducts(Integer storeId);

    ProductVO getProduct(Integer id);

    RatingVO getRating(Integer id);

    List<Comment> getComments(Integer id);


    List<ProductVO> searchFor(String storeName, String name, Double minPrice, Double maxPrice, CategoryEnum category);
}

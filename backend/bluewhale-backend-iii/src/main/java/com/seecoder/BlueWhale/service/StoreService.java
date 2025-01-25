package com.seecoder.BlueWhale.service;

import java.util.List;

import com.seecoder.BlueWhale.enums.CategoryEnum;
import com.seecoder.BlueWhale.po.Product;
import com.seecoder.BlueWhale.vo.ProductVO;
import com.seecoder.BlueWhale.vo.RatingVO;
import com.seecoder.BlueWhale.vo.StoreVO;

public interface StoreService {
    Boolean create(StoreVO storeVO);

    StoreVO getStore(Integer id);

    List<StoreVO> getAllStores();

    RatingVO getRating(Integer id);

    List<ProductVO> searchProducts(Integer storeId, String name, Double minPrice, Double maxPrice, CategoryEnum category);
}

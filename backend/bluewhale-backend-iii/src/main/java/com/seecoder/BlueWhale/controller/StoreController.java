package com.seecoder.BlueWhale.controller;

import java.util.List;

import com.seecoder.BlueWhale.annotation.Access;
import com.seecoder.BlueWhale.enums.CategoryEnum;
import com.seecoder.BlueWhale.enums.RoleEnum;
import com.seecoder.BlueWhale.vo.ProductVO;
import com.seecoder.BlueWhale.vo.RatingVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.seecoder.BlueWhale.service.StoreService;
import com.seecoder.BlueWhale.vo.ResultVO;
import com.seecoder.BlueWhale.vo.StoreVO;

@RestController
@RequestMapping("/api/stores")
public class StoreController {

    @Autowired
    StoreService storeService;

    @PostMapping
    @Access(roles = RoleEnum.MANAGER)
    public ResultVO<Boolean> create(@RequestBody StoreVO storeVO) {
        return ResultVO.buildSuccess(storeService.create(storeVO));
    }

    @GetMapping("/{id}")
    public ResultVO<StoreVO> getStore(@PathVariable(value = "id") Integer id) {
        return ResultVO.buildSuccess(storeService.getStore(id));
    }

    @GetMapping("/all")
    public ResultVO<List<StoreVO>> getAllStores() {
        return ResultVO.buildSuccess(storeService.getAllStores());
    }

    @GetMapping("/{id}/rating")
    public ResultVO<RatingVO> getRating(@PathVariable(value = "id") Integer id) {
        return ResultVO.buildSuccess(storeService.getRating(id));
    }

    // NOTE: The name is the name of product.
    @GetMapping("/{id}/search")
    ResultVO<List<ProductVO>> searchProducts(@PathVariable(value = "id") Integer storeId,
                                             @RequestParam("name") String name,
                                             @RequestParam("minPrice") Double minPrice, @RequestParam("maxPrice") Double maxPrice,
                                             @RequestParam("category") CategoryEnum category) {
        return ResultVO.buildSuccess(storeService.searchProducts(storeId, name, minPrice, maxPrice, category));
    }

}

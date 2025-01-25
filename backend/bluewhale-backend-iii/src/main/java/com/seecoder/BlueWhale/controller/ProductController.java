package com.seecoder.BlueWhale.controller;

import com.seecoder.BlueWhale.annotation.Access;
import com.seecoder.BlueWhale.enums.CategoryEnum;
import com.seecoder.BlueWhale.enums.RoleEnum;
import com.seecoder.BlueWhale.po.Comment;
import com.seecoder.BlueWhale.service.ProductService;
import com.seecoder.BlueWhale.vo.ProductVO;
import com.seecoder.BlueWhale.vo.RatingVO;
import com.seecoder.BlueWhale.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    ProductService productService;

    @PostMapping
    @Access(roles = RoleEnum.STAFF)
    public ResultVO<Boolean> create(@RequestBody ProductVO productVO) {
        return ResultVO.buildSuccess(productService.create(productVO));
    }

    @PostMapping("/{id}/stock")
    @Access(roles = RoleEnum.STAFF)
    public ResultVO<Boolean> addStock(@PathVariable(value = "id") Integer id, @RequestParam("number") Integer number) {
        return ResultVO.buildSuccess(productService.addStock(id, number));
    }

    @GetMapping
    public ResultVO<List<ProductVO>> getAllProducts(@RequestParam("storeId") Integer storeId) {
        return ResultVO.buildSuccess(productService.getAllProducts(storeId));
    }

    @GetMapping("/{id}/rating")
    public ResultVO<RatingVO> getRating(@PathVariable(value = "id") Integer id) {
        return ResultVO.buildSuccess(productService.getRating(id));
    }

    @GetMapping("/{id}")
    public ResultVO<ProductVO> getProduct(@PathVariable(value = "id") Integer id) {
        return ResultVO.buildSuccess(productService.getProduct(id));
    }

    @GetMapping("/{id}/comments")
    public ResultVO<List<Comment>> getComments(@PathVariable(value = "id") Integer id) {
        return ResultVO.buildSuccess(productService.getComments(id));
    }

    @GetMapping("/search")
    public ResultVO<List<ProductVO>> searchFor(@RequestParam("storeName") String storeName, @RequestParam("name") String name,
                                               @RequestParam("minPrice") Double minPrice, @RequestParam("maxPrice") Double maxPrice,
                                               @RequestParam("category") CategoryEnum category) {
        return ResultVO.buildSuccess(productService.searchFor(storeName, name, minPrice, maxPrice, category));
    }
}

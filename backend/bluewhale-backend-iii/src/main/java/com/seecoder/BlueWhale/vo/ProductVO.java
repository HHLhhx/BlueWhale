package com.seecoder.BlueWhale.vo;

import com.seecoder.BlueWhale.enums.CategoryEnum;
import com.seecoder.BlueWhale.po.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ProductVO {
    private Integer id;
    private Integer storeId;
    private List<String> photoUrlList;
    private String name;
    private Integer salesAmount;
    private Integer stock;
    private Double price;
    private CategoryEnum category;
    private Integer pendingNum;

    public Product toPO() {
        Product product = new Product();
        product.setCategory(this.category);
        product.setId(this.id);
        product.setPrice(this.price);
        product.setName(this.name);
        product.setStock(this.stock);
        product.setStoreId(this.storeId);
        product.setSalesAmount(this.salesAmount);
        product.setPhotoUrlList(this.photoUrlList);
        product.setPendingNum(this.pendingNum);
        return product;
    }
}

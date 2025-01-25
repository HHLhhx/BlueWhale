package com.seecoder.BlueWhale.po;

import com.seecoder.BlueWhale.enums.CategoryEnum;
import com.seecoder.BlueWhale.vo.ProductVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Product {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer id;

    @Basic
    @Column(name = "store_id")
    private Integer storeId;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> photoUrlList;

    @Basic
    @Column(name = "name")
    private String name;

    @Basic
    @Column(name = "sales_amount")
    private Integer salesAmount;

    @Basic
    @Column(name = "stock")
    private Integer stock;

    @Basic
    @Column(name = "price")
    private Double price;

    @Basic
    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    private CategoryEnum category;

    @Basic
    @Column(name = "pending_num")
    private Integer pendingNum;

    public ProductVO toVO() {
        ProductVO productVO = new ProductVO();
        productVO.setCategory(this.category);
        productVO.setName(this.name);
        productVO.setId(this.id);
        productVO.setPrice(this.price);
        productVO.setStock(this.stock);
        productVO.setStoreId(this.storeId);
        productVO.setSalesAmount(this.salesAmount);
        productVO.setPhotoUrlList(this.photoUrlList);
        productVO.setPendingNum(this.pendingNum);
        return productVO;
    }
}

package com.seecoder.BlueWhale.po;

import java.util.Date;

import javax.persistence.*;

import com.seecoder.BlueWhale.enums.DeliveryEnum;
import com.seecoder.BlueWhale.enums.OrderStateEnum;
import com.seecoder.BlueWhale.util.BooleanToStringConverter;
import com.seecoder.BlueWhale.vo.OrderVO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "`order`")
public class Order {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer id;

    @Basic
    @Column(name = "product_id")
    private Integer productId;

    @Basic
    @Column(name = "store_id")
    private Integer storeId;

    @Basic
    @Column(name = "num")
    private Integer num;

    @Basic
    @Column(name = "user_id")
    private Integer userId;

    @Basic
    @Column(name = "delivery_method")
    @Enumerated(EnumType.STRING)
    private DeliveryEnum deliveryMethod;

    @Basic
    @Column(name = "order_state")
    @Enumerated(EnumType.STRING)
    private OrderStateEnum orderState;

    @Basic
    @Column(name = "create_time")
    private Date createTime;

    @Basic
    @Column(name = "total_price")
    private Double totalPrice;

    @Basic
    @Column(name = "true_total_price")
    private Double trueTotalPrice;

    @Basic
    @Column(name = "user_phone_num")
    private String userPhoneNum;

    @Basic
    @Column(name = "address")
    private String address;

    @Basic
    @Convert(converter = BooleanToStringConverter.class)
    @Column(name = "has_info")
    private Boolean hasInfo;

    public OrderVO toVO() {
        OrderVO order = new OrderVO();
        order.setId(id);
        order.setProductId(productId);
        // order.setProductName(productName);
        order.setStoreId(storeId);
        // order.setStoreName(storeName);
        order.setNum(num);
        order.setUserId(userId);
        order.setDeliveryMethod(deliveryMethod);
        order.setOrderState(orderState);
        order.setCreateTime(createTime);
        order.setTotalPrice(totalPrice);
        order.setUserPhoneNum(userPhoneNum);
        order.setAddress(address);
        order.setTrueTotalPrice(trueTotalPrice);
        order.setHasInfo(hasInfo);
        return order;
    }
}

package com.seecoder.BlueWhale.vo;

import java.util.Date;

import com.seecoder.BlueWhale.enums.DeliveryEnum;
import com.seecoder.BlueWhale.enums.OrderStateEnum;
import com.seecoder.BlueWhale.po.Order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderVO {
    private Integer id;
    private Integer productId;
    // private String productName;
    private Integer storeId;
    // private String storeName;
    private Integer num;
    private Integer userId;
    private DeliveryEnum deliveryMethod;
    private OrderStateEnum orderState;
    private Date createTime;
    private Double totalPrice;
    private Double trueTotalPrice;
    private String userPhoneNum;
    private String address;
    private Boolean hasInfo;

    public Order toPO() {
        Order order = new Order();
        order.setId(id);
        order.setProductId(productId);
        order.setStoreId(storeId);
        order.setNum(num);
        order.setUserId(userId);
        order.setDeliveryMethod(deliveryMethod);
        order.setOrderState(orderState);
        order.setCreateTime(createTime);
        order.setTotalPrice(totalPrice);
        order.setUserPhoneNum(userPhoneNum);
        ;
        order.setAddress(address);
        order.setTrueTotalPrice(trueTotalPrice);
        order.setHasInfo(hasInfo);
        return order;
    }
}

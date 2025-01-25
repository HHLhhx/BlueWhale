package com.seecoder.BlueWhale.vo;

import com.seecoder.BlueWhale.enums.RoleEnum;
import com.seecoder.BlueWhale.po.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class UserVO {

    private Integer id;
    private String name;
    private String phone;
    private String password;
    private Integer storeId;
    private String address;
    private RoleEnum role;
    private Date createTime;
    private String storeName;

    public User toPO() {
        User user = new User();
        user.setId(this.id);
        user.setAddress(this.address);
        user.setName(this.name);
        user.setPhone(this.phone);
        user.setRole(this.role);
        user.setStoreId(this.storeId);
        user.setPassword(this.password);
        user.setCreateTime(this.createTime);
        return user;
    }

    public SafeUserVO toSafeVO() {
        SafeUserVO safeVO = new SafeUserVO();
        safeVO.setId(id);
        safeVO.setName(name);
        safeVO.setRole(role);
        safeVO.setStoreName(storeName);
        return safeVO;
    }

    @Override
    public String toString() {
        return String.format("[id:%d name:%s phone:%s role:%s]", id, name, phone, role);
    }
}

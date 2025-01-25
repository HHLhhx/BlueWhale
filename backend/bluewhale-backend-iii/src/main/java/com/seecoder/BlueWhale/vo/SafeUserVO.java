package com.seecoder.BlueWhale.vo;

import com.seecoder.BlueWhale.enums.RoleEnum;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SafeUserVO {
    private Integer id;
    private String name;
    private RoleEnum role;
    private String storeName;
}

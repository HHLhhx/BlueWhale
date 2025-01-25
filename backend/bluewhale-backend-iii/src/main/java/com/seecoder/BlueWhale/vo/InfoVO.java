package com.seecoder.BlueWhale.vo;

import java.util.Date;

import com.seecoder.BlueWhale.enums.InfoEnum;
import com.seecoder.BlueWhale.po.Info;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InfoVO {
    private Integer id;
    private Integer uid;
    private InfoEnum type;
    private String message;
    private Date createTime;

    public Info toPO() {
        Info infoPO = new Info();
        infoPO.setId(id);
        infoPO.setUid(uid);
        infoPO.setType(type);
        infoPO.setMessage(message);
        infoPO.setCreateTime(createTime);
        return infoPO;
    }
}

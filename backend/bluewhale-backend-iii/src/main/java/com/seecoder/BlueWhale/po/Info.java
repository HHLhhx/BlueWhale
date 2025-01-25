package com.seecoder.BlueWhale.po;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.seecoder.BlueWhale.enums.InfoEnum;
import com.seecoder.BlueWhale.vo.InfoVO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Info {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer id;

    @Basic
    @Column(name = "uid")
    private Integer uid;

    @Basic
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private InfoEnum type;

    @Basic
    @Column(name = "message")
    private String message;

    @Basic
    @Column(name = "create_time")
    private Date createTime;

    public InfoVO toVO() {
        InfoVO infoVO = new InfoVO();
        infoVO.setId(id);
        infoVO.setUid(uid);
        infoVO.setType(type);
        infoVO.setMessage(message);
        infoVO.setCreateTime(createTime);
        return infoVO;
    }

    public Info (Integer uid, InfoEnum type, String message) {
        this.uid = uid;
        this.type = type;
        this.message = message;
        this.createTime = Date.from(LocalDateTime.now().atZone(ZoneId.of("Asia/Shanghai")).toInstant());
    }
}

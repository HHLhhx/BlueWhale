package com.seecoder.BlueWhale.po;

import javax.persistence.*;

import com.seecoder.BlueWhale.vo.StoreVO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Store {
    
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer id;

    @Basic
    @Column(name = "name")
    private String name;

    @Basic
    @Column(name = "logo_url")
    private String logoUrl;

    @Basic
    @Column(name = "location")
    private String location;

    public StoreVO toVO(){
        StoreVO storeVO=new StoreVO();
        storeVO.setId(this.id);
        storeVO.setLocation(this.location);
        storeVO.setLogoUrl(this.logoUrl);
        storeVO.setName(this.name);
        return storeVO;
    }
}

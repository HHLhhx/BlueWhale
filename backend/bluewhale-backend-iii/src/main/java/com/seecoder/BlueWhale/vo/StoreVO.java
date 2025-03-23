package com.seecoder.BlueWhale.vo;

import com.seecoder.BlueWhale.po.Store;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StoreVO {
    private Integer id;
    private String name;
    private String logoUrl;
    private String location;

    public Store toPO() {
        Store store = new Store();
        store.setId(this.id);
        store.setLocation(this.location);
        store.setLogoUrl(this.logoUrl);
        store.setName(this.name);
        return store;
    }
}

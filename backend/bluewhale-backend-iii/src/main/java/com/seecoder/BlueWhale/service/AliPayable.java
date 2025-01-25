package com.seecoder.BlueWhale.service;

import java.util.Map;

public interface AliPayable {
    public boolean payNotify(Map<String, String> params);
}

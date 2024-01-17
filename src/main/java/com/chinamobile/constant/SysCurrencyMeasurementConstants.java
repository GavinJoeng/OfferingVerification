package com.chinamobile.constant;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: 貨幣單位
 * @author: gavin yang
 * @email: gavinyang@hk.chinamobile.com
 * @date: 2024/1/16 14:34
 */

public class SysCurrencyMeasurementConstants {

    public static final Map<String, String> DATA_MAP;

    static {
        Map<String, String> map = new HashMap<>();
        map.put("600", "SDR");
        map.put("501", "Cent");
        map.put("500", "HKD");
        DATA_MAP = Collections.unmodifiableMap(map);
    }

}

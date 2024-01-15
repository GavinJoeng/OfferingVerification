package com.chinamobile.constant;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: some desc
 * @author: gavin yang
 * @email: gavinyang@hk.chinamobile.com
 * @date: 2024/1/15 14:41
 */
public class SystemMeasurementConstants {

    public static final Map<String, String> DATA_MAP;

    static {
        Map<String, String> map = new HashMap<>();
        map.put("902", "bit/s");
        map.put("901", "kbit/s");
        map.put("1106", "Bytes");
        map.put("1107", "KB");
        map.put("1108", "MB");
        map.put("1109", "GB");
        map.put("1003", "Seconds");
        map.put("1004", "Minutes");
        map.put("1005", "Hours");
        map.put("1122", "Entrys");
        map.put("10000", "Unit");
        map.put("1101", "Items");
        map.put("1121", "Pages");
        map.put("1120", "Points");
        map.put("2001", "Pulse");
        map.put("1006", "Times");
        DATA_MAP = Collections.unmodifiableMap(map);
    }
}

package com.chinamobile.test;

import com.chinamobile.constant.FreeUnitTypeConstants;
import com.chinamobile.constant.SysMeasurementConstants;

/**
 * @description: some desc
 * @author: gavin yang
 * @email: gavinyang@hk.chinamobile.com
 * @date: 2024/1/15 14:29
 */
public class Test {

    public static void main(String[] args) {
        String s = FreeUnitTypeConstants.DATA_MAP.get("3000047");
        System.out.println(s);

        String s1 = SysMeasurementConstants.DATA_MAP.get("901");
        System.out.println(s1);
    }

}

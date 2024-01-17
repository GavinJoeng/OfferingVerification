package com.chinamobile.entity;

import lombok.*;

import java.util.Map;

/**
 * @description: 用於存儲action動作的關鍵信息
 * @author: gavin yang
 * @email: gavinyang@hk.chinamobile.com
 * @date: 2024/1/16 11:50
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class Action {
    //關鍵信息
    //如果沒有動作：actionType0-0 = "-"，actionType2-4="charge-discount-action";
    private String actionType;
    //用於記錄月費信息，如果沒有則用"-"表示
    private String amount;
    //貨幣單位
    private String currencyUnit;

}

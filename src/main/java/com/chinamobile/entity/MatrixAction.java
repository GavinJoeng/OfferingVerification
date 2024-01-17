package com.chinamobile.entity;

import lombok.*;

/**
 * @description: 用於存儲矩陣集合關鍵信息
 * @author: gavin yang
 * @email: gavinyang@hk.chinamobile.com
 * @date: 2024/1/15 14:49
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class MatrixAction {

    //矩陣ID
    private String matrixId;

    //查詢SQL
    private String matrixQuerySQL;

    //<!-- data-element="C_CALLING_HOME_CC" -->
    private String dataElement;

    //ActionType free-unit-bonus-action
    private String actionType;

    //以下為矩陣文件中獲取的信息

    //Free Unit Type    Local GPRS Bucket C
    private String freeUnitType;

    //Bonus Amount  32212254720
    private String bonusAmount;

    //Measurement Unit  KB
    private String measurementUnit;




}

package com.chinamobile.entity.action.type.chargediscount;

import lombok.*;

/**
 * @description: some desc
 * @author: gavin yang
 * @email: gavinyang@hk.chinamobile.com
 * @date: 2024/1/5 15:44
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class ChargeDiscountAction {
    private DiscountCalc discountCalc;
    private String discountFeeFlag;
    private String discountProrateMethod;
}
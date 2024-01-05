package com.chinamobile.entity.logic;

import lombok.*;

/**
 * @description: some desc
 * @author: gavin yang
 * @email: gavinyang@hk.chinamobile.com
 * @date: 2024/1/5 15:38
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class LogicExpressionText {

    private String editorType;
    private boolean hasMain;
    //層級
    private int translatedCrlFunctionId;


}

package com.chinamobile.entity.logic;

import lombok.*;

/**
 * @description: some desc
 * @author: gavin yang
 * @email: gavinyang@hk.chinamobile.com
 * @date: 2024/1/5 15:37
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class LogicExpression {
    private Annotation annotation;
    private LogicExpressionText logicExpressionText;
}

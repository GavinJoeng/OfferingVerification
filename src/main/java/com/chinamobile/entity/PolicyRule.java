package com.chinamobile.entity;

import com.chinamobile.entity.condition.ConditionNode;
import lombok.*;

/**
 * @description: some desc
 * @author: gavin yang
 * @email: gavinyang@hk.chinamobile.com
 * @date: 2024/1/5 16:05
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class PolicyRule {

    private ConditionNode condition;
    private String defaultPath;


}

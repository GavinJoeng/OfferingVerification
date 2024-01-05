package com.chinamobile.entity.condition;


import com.chinamobile.entity.action.PatternActionUnion;
import com.chinamobile.entity.logic.LogicExpression;
import lombok.*;

import java.util.List;

/**
 * @description: some desc
 * @author: gavin yang
 * @email: gavinyang@hk.chinamobile.com
 * @date: 2024/1/5 15:42
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class ConditionNode {
    private PatternActionUnion actionUnion;
    private LogicExpression logicExpression;
    private String nodeName;
    private List<ConditionNode> subNodes;
    private ConditionNode subNode;


}

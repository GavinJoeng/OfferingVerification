package com.chinamobile.entity;

import lombok.*;

import java.util.List;
import java.util.Map;

/**
 * @description: 節點實體類
 * @author: gavin yang
 * @email: gavinyang@hk.chinamobile.com
 * @date: 2024/1/8 15:27
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class ConditionNode {

    //logicScript-用於存儲script
    private Map<String,String> logicScriptMap;
    //textBody-用於存儲"文字條件"
    private Map<String,String> textBodyMap;
    //nodeLevel-nodeLevel是所有的key命名的關鍵，根據是否有葉子節點來判斷這棵樹的層級
    //因為不止一個節點，需要用List/Map來存儲這個層級treeMapLevel()方法進行完善。
    private Integer nodeLevel;

    //關鍵信息
    //如果沒有動作：actionType0-0 = "-"，actionType2-4="charge-discount-action";
    private Map<String,String> actionTypeMap;
    //用於記錄月費信息，如果沒有則用"-"表示
    private Map<String,String> amountMap;

    //node分支數，層級數,[1,1,5]表示第一層有1個節點，第二層1個節點，第三個層級為5個節點
    List<Integer> conditionNodeListSize;

    //action
    private List<Map<String,String>> actionMapList;

    //根據節點不同獲取不同的MatrixAction
    private Map<String, MatrixAction> matrixActionMap;

    //根據節點不同獲取不同的action
    private Map<String, Action> actionMap;

    //把生成的matrixPathList傳入Node中，以便於刪除文件。
    private List<String> uselessPathList;

}

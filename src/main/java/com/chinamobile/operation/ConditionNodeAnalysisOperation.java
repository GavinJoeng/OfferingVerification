package com.chinamobile.operation;

import com.chinamobile.constant.SingleNodePathConstants;

import static com.chinamobile.tools.JsonParserUtil.extractJsonInfo;

/**
 * @description: 用於作條件節點進行分析
 * @author: gavin yang
 * @email: gavinyang@hk.chinamobile.com
 * @date: 2024/1/17 14:10
 */
public class ConditionNodeAnalysisOperation {




    public static void main(String[] args) {
        String jsonReadPath = SingleNodePathConstants.BASIC_PATH + SingleNodePathConstants.OFFERING_ID + SingleNodePathConstants.RULE_JSON_PATH;
        String isSuccess = extractJsonInfo(jsonReadPath);
        System.out.println(isSuccess);


    }

}

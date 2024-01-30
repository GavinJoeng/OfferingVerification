package com.chinamobile.operation;

import com.alibaba.fastjson.JSON;
import com.chinamobile.constant.FreeUnitTypeConstants;
import com.chinamobile.constant.MatrixPatternPath;
import com.chinamobile.constant.SysMeasurementConstants;

import com.chinamobile.entity.MatrixAction;
import com.jayway.jsonpath.JsonPath;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import static com.chinamobile.tools.XMLParser.convertStringToXml;
import static com.chinamobile.tools.XMLParser.readFileToString;
import static com.chinamobile.tools.XMLtoJSON.convertXML2Json;

/**
 * @description: 用於作矩陣模式進行分析
 * @author: gavin yang
 * @email: gavinyang@hk.chinamobile.com
 * @date: 2024/1/17 9:51
 */
public class MatrixAnalysisOperation {


    public static void main(String[] args) throws Exception {
        //讀取MATRIX_ID -> 應該是以List集合去存儲Matrix_id
//        for (String matrixId : MatrixPatternPath.MATRIX_ID_LIST) {
//            System.out.println(matrixId);
//        }



        //根據MATRIX_ID獲取文件
        String matrixTxtPath = MatrixPatternPath.BASIC_PATH + MatrixPatternPath.MATRIX_ID + "_MATRIX_ID.txt";
        String matrixXmlPath = MatrixPatternPath.BASIC_PATH + MatrixPatternPath.MATRIX_ID + "_MATRIX_ID.xml";
        String matrixJsonPath = MatrixPatternPath.BASIC_PATH + MatrixPatternPath.MATRIX_ID + "_MATRIX_ID.json";


        //將文件轉為XML、JSON格式
        String context = readFileToString(matrixTxtPath);

        String result = java.net.URLDecoder.decode(context, StandardCharsets.UTF_8.name());

        convertStringToXml(result, matrixXmlPath);

        convertXML2Json(matrixXmlPath, matrixJsonPath);


        //提取JSON關鍵信息,返回什麼呢？
        MatrixAction matrixAction = extractMatrixJsonInfo(matrixJsonPath);

        System.out.println(matrixAction);

        //已經用別的方法實現
        //讀取EXCEL表格，拼接關鍵信息
        //String filePath = MultiNodePathConstants.BASIC_PATH + MultiNodePathConstants.OFFERING_ID + "_PLAN_POLICY_RULE" + ".xlsx";
        //generateExcel(filePath, conditionNode);
        //輸出新的EXCEL表格



    }

    private static MatrixAction extractMatrixJsonInfo(String matrixJsonPath) {
        MatrixAction matrixAction = new MatrixAction();

        // 创建一个File类型的对象，指向传入的路径
        File inputFile = new File(matrixJsonPath);

        // 创建一个StringBuilder类型的对象，用于存储文件内容
        StringBuilder content = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            // 逐行读取文件内容，并将其追加到content中
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 将StringBuilder类型的对象转换为String类型，并赋值给fileContent变量
        String fileContent = content.toString();

        // 解析JSON格式的字符串，将其转换为Object类型的对象
        Object jsonObject = JSON.parse(fileContent);

        // 将Object类型的对象转换为JSON格式的字符串，并赋值给jsonString变量
        String jsonString = JSON.toJSONString(jsonObject);

        //獲取某分支的層次，0、1、2...，從0開始計數
        //Integer treeMapLevel = treeMapLevel(jsonString);
        //System.out.println("--------最高層數為：--------->" + treeMapLevel);

        //用List來存儲conditionNodeList每層的大小
        //如果treeMaplevel是2的話，因為是從0計算層數，則需要遍歷的次數應該為3次，故需要加1；
        //List<Integer> conditionNodeListSize = calConditionNodeListSize(jsonString, treeMapLevel, MultiNodePathConstants.BASIC_NODE_PATH_MID);
        matrixAction = getMatrixAction(jsonString, matrixAction);

        return matrixAction;
    }

    /**
     * TODO 後續會更該，根據ActionType的不同會修改對應的路經，需要把動作類型進行替換。
     * JSON["pattern-action-union"].action["free-unit-bonus-action"]["bonus-amount"].constant["constant-value"]["@attributes"]["display-value"]
     * JSON["pattern-action-union"].action["free-unit-bonus-action"]["display-measurement-id"].value
     * @param jsonString
     * @param matrixAction
     * @return
     */
    private static MatrixAction getMatrixAction(String jsonString, MatrixAction matrixAction) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append(MatrixPatternPath.FREE_UNIT_TYPE_ID_PATH);
        String freeUnitTypeID = JsonPath.read(jsonString, queryBuilder.toString()).toString();

        String freeUnitType = FreeUnitTypeConstants.DATA_MAP.get(freeUnitTypeID);

        queryBuilder.setLength(0);
        queryBuilder.append(MatrixPatternPath.MATRIX_BONUS_AMOUNT_PATH);
        String matrixBonusAmount = JsonPath.read(jsonString, queryBuilder.toString()).toString();


        queryBuilder.setLength(0);
        queryBuilder.append(MatrixPatternPath.MATRIX_MEASUREMENT_ID_PATH);
        String matrixMeasurementId = JsonPath.read(jsonString, queryBuilder.toString()).toString();
        String matrixMeasurement = SysMeasurementConstants.DATA_MAP.get(matrixMeasurementId);
        matrixAction.setFreeUnitType(freeUnitType);
        matrixAction.setBonusAmount(matrixBonusAmount);
        matrixAction.setMeasurementUnit(matrixMeasurement);

        return matrixAction;
    }



}

package com.chinamobile.tools;

import com.chinamobile.constant.SingleNodePathConstants;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinamobile.entity.ConditionNode;
import com.jayway.jsonpath.JsonPath;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import java.io.*;
import java.util.*;

/**
 * @description: 用於解析Json文件，用於封裝ConditionNode實體類，並輸出EXCEL表格
 * @author: gavin yang
 * @email: gavinyang@hk.chinamobile.com
 * @date: 2024/1/8 15:38
 */
public class JsonParserUtil {

    private static ConditionNode conditionNode;

    public static void main(String[] args) {

        String jsonReadPath = SingleNodePathConstants.BASIC_PATH + SingleNodePathConstants.OFFERING_ID + SingleNodePathConstants.RULE_JSON_PATH;
        String isSuccess = extractJsonInfo(jsonReadPath);
        System.out.println(isSuccess);
    }


    public static String extractJsonInfo(String path) {
        // 创建一个File类型的对象，指向传入的路径
        File inputFile = new File(path);

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

        //是否為葉子節點
        boolean isLeft = leftNodeJudgment(jsonString, SingleNodePathConstants.BASIC_NODE_PATH_START + "[0]" + SingleNodePathConstants.BASIC_NODE_PATH_MID + "[0]" + SingleNodePathConstants.IS_LEFT_NODE_PATH);
        System.out.println("----------------->" + isLeft);

        //conditionNodeList的獲取，並且獲取多少分支。
        List<String> conditionNodeList = getConditionNodeList(jsonString, SingleNodePathConstants.BASIC_NODE_PATH_START + "[0]" + SingleNodePathConstants.CONDITION_NODE_LIST_PATH);
        for (int i = 0; i < conditionNodeList.size(); i++) {
            String conditionNode = conditionNodeList.get(i);
            System.out.println("下標為：" + i + " 節點信息為：" + conditionNode);
        }

        System.out.println("conditionNodeList的大小為：" + conditionNodeList.size() + "  值為：" + conditionNodeList.get(0));

        //獲取某分支的層次，0、1、2...，從0開始計數
        Integer treeMapLevel = treeMapLevel(jsonString);
        System.out.println("--------最高層數為：--------->" + treeMapLevel);

        //根據treeMapLevel（以後會封裝為一個List/Map，因為不止一個分支，目前按照一個分支來先實現基本功能），先獲取logicScript
        Map<String, String> logicScriptMap = getLogicScriptMap(jsonString, treeMapLevel);

        System.out.println("logicScriptMaplogicScriptMaplogicScriptMap" + logicScriptMap);
        //再獲取textBody
        Map<String, String> textBodyMap = getTextBodyMap(jsonString, treeMapLevel);

        System.out.println("textBodyMaptextBodyMaptextBodyMap" + textBodyMap);

        //再獲取actionMap
        //用List來存儲conditionNodeList每層的大小
        List<Integer> conditionNodeListSize = new ArrayList();

        //如果treeMaplevel是2的話，因為是從0計算層數，則需要遍歷的次數應該為3次，故需要加1；
        conditionNodeListSize = calConditionNodeListSize(jsonString, treeMapLevel, SingleNodePathConstants.BASIC_NODE_PATH_MID);


        conditionNode = new ConditionNode();
        conditionNode.setLogicScriptMap(logicScriptMap);
        conditionNode.setConditionNodeListSize(conditionNodeListSize);
        conditionNode.setTextBodyMap(textBodyMap);
        conditionNode.setNodeLevel(treeMapLevel);
        List<Map<String, String>> actionMapList = getActionMapList(jsonString, treeMapLevel, conditionNodeListSize);

        //把信息裝入實體類中，傳送到前端進行展示，目前是以實現後端為主。
        conditionNode.setActionMapList(actionMapList);
        String filePath = SingleNodePathConstants.BASIC_PATH + SingleNodePathConstants.OFFERING_ID + "_PLAN_POLICY_RULE" + ".xlsx";
        generateExcel(filePath, conditionNode);
        System.out.println("gavinjoeng" + conditionNode);

        return "生成EXCEL文件成功！";
    }

    /**
     * 根據conditionNode生成EXCEL文件
     *
     * @param filePath
     * @param conditionNode
     */
    public static void generateExcel(String filePath, ConditionNode conditionNode) {
        Workbook workbook = new XSSFWorkbook();
        //sheet名稱
        Sheet sheet = workbook.createSheet("Condition Node");
        // 设置表头样式
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setFontName("宋體");
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        // 设置为文本格式
        DataFormat dataFormat = workbook.createDataFormat();
        headerStyle.setDataFormat(dataFormat.getFormat("@"));

        // 创建表头行
        Row headerRow = sheet.createRow(0);

        // 创建表头单元格并设置样式
        Cell headerCell0 = headerRow.createCell(0);
        headerCell0.setCellValue("層次");
        headerCell0.setCellStyle(headerStyle);

        Cell headerCell1 = headerRow.createCell(1);
        headerCell1.setCellValue("logicScript");
        headerCell1.setCellStyle(headerStyle);

        Cell headerCell2 = headerRow.createCell(2);
        headerCell2.setCellValue("條件");
        headerCell2.setCellStyle(headerStyle);

        Cell headerCell3 = headerRow.createCell(3);
        headerCell3.setCellValue("動作類型");
        headerCell3.setCellStyle(headerStyle);

        Cell headerCell4 = headerRow.createCell(4);
        headerCell4.setCellValue("費用");
        headerCell4.setCellStyle(headerStyle);


        // 调整列宽
        for (int i = 0; i < 5; i++) {
            if (i == 1 || i == 2) {
                sheet.setColumnWidth(i, 60 * 256);
            } else if (i == 3 || i == 4) {
                sheet.setColumnWidth(i, 30 * 256);
            } else {
                sheet.setColumnWidth(i, 15 * 256);
            }

        }


        List<Integer> nodeListSize = conditionNode.getConditionNodeListSize();
        int rowIndex = 1; // 数据行的起始索引

        // 填充数据并设置样式和边框
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setAlignment(HorizontalAlignment.CENTER);
        dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);
        Font dataFont = workbook.createFont();
        dataFont.setFontName("新細明體");
        dataStyle.setFont(dataFont);

        dataStyle.setDataFormat(dataFormat.getFormat("@")); // 设置为文本格式

        // 填充数据
        for (int i = 0; i < nodeListSize.size(); i++) {
            int branchSize = nodeListSize.get(i);

            for (int j = 0; j < branchSize; j++) {
                Row dataRow = sheet.createRow(rowIndex);

                // 获取对应的属性值
                String script = conditionNode.getLogicScriptMap().get("logicScript" + i + "-" + j);
                String textBody = conditionNode.getTextBodyMap().get("textBody" + i + "-" + j);
                String actionType = conditionNode.getActionTypeMap().get("actionType" + i + "-" + j);
                String amount = conditionNode.getAmountMap().get("amount" + i + "-" + j);

                // 填充单元格数据
                Cell cell0 = dataRow.createCell(0);
                cell0.setCellValue(i + "-" + j);
                cell0.setCellStyle(dataStyle);

                Cell cell1 = dataRow.createCell(1);
                cell1.setCellValue(script);
                cell1.setCellStyle(dataStyle);

                Cell cell2 = dataRow.createCell(2);
                cell2.setCellValue(textBody);
                cell2.setCellStyle(dataStyle);

                Cell cell3 = dataRow.createCell(3);
                cell3.setCellValue(actionType);
                cell3.setCellStyle(dataStyle);

                Cell cell4 = dataRow.createCell(4);
                cell4.setCellValue(amount);
                cell4.setCellStyle(dataStyle);

                rowIndex++;
            }
        }

        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            workbook.write(outputStream);
            System.out.println("Excel file exported successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 獲取節點Action動作信息，以什麼作為Key？
     * action 2-1/2-2
     * action類型-charge-discount-action 2-1/2-2
     * discount-fee-flag 2-1/2-2
     * discount-prorate-method 2-1/2-2
     * integer 2-1/2-2
     * exponent 2-1/2-2
     * currency-measurement 2-1/2-2
     *
     * @param jsonString
     * @param treeMapLevel
     * @param conditionNodeListSize
     * @return
     */
    private static List<Map<String, String>> getActionMapList(String jsonString, Integer treeMapLevel, List<Integer> conditionNodeListSize) {
        List<Map<String, String>> actionMapList = new ArrayList<>();
        //2.定義Map存儲actionMap
        //Map的名稱必須不同存儲於List中,Map的命名方式可以在循環中進行創建，然後存儲在List當中
        //命名格式：treeMapLevel=2，代表是3層的樹狀圖，最後一層才會有動作出現，actionMap的命名方式為：actionMap2-0,actionMap2-1,actionMap2-2...
        //Map<String, String> actionMap = new HashMap<>();

        //拼接中間路經
        List<String> actionLinkPathList = new ArrayList<>();

        StringBuilder actionBuilder = new StringBuilder(SingleNodePathConstants.BASIC_NODE_PATH_START);
        for (int i = 0; i <= treeMapLevel; i++) {

            Integer loopCount = conditionNodeListSize.get(i);
            if (loopCount == 1 && i == 0) {
                actionBuilder.append("[").append(0).append("]");
            } else if (loopCount == 1) {
                actionBuilder.append(SingleNodePathConstants.BASIC_NODE_PATH_MID);
                actionBuilder.append("[").append(0).append("]");
            } else {
                //TODO 需要解決循環[1,2,5]類問題。
                for (int j = 0; j < loopCount; j++) {
                    Integer startIndex = actionBuilder.length(); // 记录循环前的 pathBuilder 长度
                    actionBuilder.append(SingleNodePathConstants.BASIC_NODE_PATH_MID);
                    actionBuilder.append("[").append(j).append("]");
                    actionLinkPathList.add(actionBuilder.toString());
                    // 在下一次循环之前，删除新增的内容，恢复原来的 pathBuilder
                    if (j < loopCount - 1) {
                        actionBuilder.delete(startIndex, actionBuilder.length());
                    }
                }
            }
        }

        for (String actionLinkPath : actionLinkPathList) {
            System.out.println("actionLinkPathactionLinkPathactionLinkPath" + actionLinkPath);
        }
        Map<String, String> actionTypeMap = new HashMap<>();
        Map<String, String> amountMap = new HashMap<>();

        for (int i = 0; i < conditionNodeListSize.size(); i++) {
            Integer loopCount = conditionNodeListSize.get(i);

            if (loopCount == 1 && i == 0) {
                String actionTypeKey = "actionType0" + "-" + i;
                String amountKey = "amount0" + "-" + i;
                actionTypeMap.put(actionTypeKey, "-");
                amountMap.put(amountKey, "-");
            } else if (loopCount == 1) {
                String actionTypeKey = "actionType" + i + "-" + "0";
                String amountKey = "amount" + i + "-" + "0";
                actionTypeMap.put(actionTypeKey, "-");
                amountMap.put(amountKey, "-");
            } else {
                for (int j = 0; j < loopCount; j++) {
                    String actionTypeKey = "actionType" + i + "-" + j;
                    String amountKey = "amount" + i + "-" + j;
                    actionTypeMap.put(actionTypeKey, "-");
                    amountMap.put(amountKey, "-");
                }
            }
        }

        for (int i = 0; i < actionLinkPathList.size(); i++) {
            Map<String, String> actionMap = new HashMap<>();
            //<!-- "charge-discount-action" -->
            //獲取actionType
            String queryNodeListString = "";
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append(actionLinkPathList.get(i)).append(SingleNodePathConstants.ACTION_TYPE_PATH);
            //queryNodeListString = actionLinkPathList.get(i) + ACTION_TYPE_PATH;
            String actionType = JsonPath.read(jsonString, queryBuilder.toString()).toString();
            actionMap.put("actionType", actionType);

            //用於表示最後一層，actionType2-0;
            Integer actionCount = conditionNodeListSize.size() - 1;
            String actionTypeKey = "actionType" + actionCount + "-" + i;
            actionTypeMap.put(actionTypeKey, actionType);

            //<!-- discount-fee-flag = 1 -->
            queryBuilder.setLength(0);
            queryBuilder.append(actionLinkPathList.get(i)).append(SingleNodePathConstants.ACTION_BASIC_PATH).append("action[\"").append(actionType).append("\"]").append(SingleNodePathConstants.DISCOUNT_FEE_FLAG_PATH);
            String discountFeeFlag = JsonPath.read(jsonString, queryBuilder.toString()).toString();
            actionMap.put("discountFeeFlag", discountFeeFlag);

            //<!-- discount-prorate-method = 1 -->
            queryBuilder.setLength(0);
            queryBuilder.append(actionLinkPathList.get(i)).append(SingleNodePathConstants.ACTION_BASIC_PATH).append("action[\"").append(actionType).append("\"]").append(SingleNodePathConstants.DISCOUNT_PRORATE_METHOD_PATH);
            String discountProrateMethod = JsonPath.read(jsonString, queryBuilder.toString()).toString();
            actionMap.put("discountProrateMethod", discountProrateMethod);

            //<!-- integer = 0 -->
            queryBuilder.setLength(0);
            queryBuilder.append(actionLinkPathList.get(i)).append(SingleNodePathConstants.ACTION_BASIC_PATH).append("action[\"").append(actionType).append("\"]").append(SingleNodePathConstants.INTEGER_PATH);
            String integer = JsonPath.read(jsonString, queryBuilder.toString()).toString();
            actionMap.put("integer", integer);

            //用於表示最後一層，actionType2-0;
            String amountKey = "amount" + actionCount + "-" + i;
            amountMap.put(amountKey, integer);

            //<!-- exponent = 0 -->
            queryBuilder.setLength(0);
            queryBuilder.append(actionLinkPathList.get(i)).append(SingleNodePathConstants.ACTION_BASIC_PATH).append("action[\"").append(actionType).append("\"]").append(SingleNodePathConstants.EXPONENT_PATH);
            String exponent = JsonPath.read(jsonString, queryBuilder.toString()).toString();
            actionMap.put("exponent", exponent);

            //<!-- currency-measurement = 501 -->
            queryBuilder.setLength(0);
            queryBuilder.append(actionLinkPathList.get(i)).append(SingleNodePathConstants.ACTION_BASIC_PATH).append("action[\"").append(actionType).append("\"]").append(SingleNodePathConstants.CURRENCY_MEASUREMENT_PATH);
            String currencyMeasurement = JsonPath.read(jsonString, queryBuilder.toString()).toString();
            actionMap.put("currencyMeasurement", currencyMeasurement);


            actionMapList.add(actionMap);
        }


        Integer actionBottomNum = conditionNodeListSize.get(treeMapLevel);
        System.out.println("actionLevelactionLevelactionLevelactionLevelactionLevelactionLevel" + actionBottomNum);


        for (Map<String, String> stringStringMap : actionMapList) {
            System.out.println("stringStringMapstringStringMapstringStringMapstringStringMap" + stringStringMap);
        }


        conditionNode.setAmountMap(amountMap);
        conditionNode.setActionTypeMap(actionTypeMap);


        return actionMapList;
    }

    /**
     * 獲取TextBody信息
     * TODO 循環問題需要修改，等有例子後進行完善
     *   [1,1,5]，循環的次數是，7次。
     *   [1,2,5]，應該循環的次數是，13次。
     *   對於這種狀況我們需要重新修改循環代碼，以下是示例
     *          int[] conditionNodeListSize = {1, 2, 5};
     *          int cycleCount = 0;
     *          // 遍历 conditionNodeListSize 数组并累加列表值
     *          for (int i = 0; i < conditionNodeListSize.length; i++) {
     *              int multiplier = 1;
     *              for (int j = 0; j < i; j++) {
     *                  multiplier *= conditionNodeListSize[j];
     *              }
     *              cycleCount += multiplier * conditionNodeListSize[i];
     *          }
     *          System.out.println("循环的次数是：" + cycleCount);
     *
     * @param jsonString
     * @param treeMapLevel
     * @return
     */
    private static Map<String, String> getTextBodyMap(String jsonString, Integer treeMapLevel) {
        //1.拼接路經 TEXT_BODY_PATH
        String basicLogicScriptPath = SingleNodePathConstants.BASIC_NODE_PATH_START + "[0]";
        String getScriptPath = basicLogicScriptPath + SingleNodePathConstants.TEXT_BODY_PATH;
        String addPath = SingleNodePathConstants.BASIC_NODE_PATH_MID;

        /**
         * $.["pattern-action-union"].pattern["condition-selection-pattern"]["condition-node"][0]["logic-expression"].annotation["annotation-text"]["inner-text"]["single-language-text"]["text-body"]
         * $.["pattern-action-union"].pattern["condition-selection-pattern"]["condition-node"][0]["sub-pattern-and-action"]["pattern-action-union"].pattern["condition-selection-pattern"]["condition-node"][0]["logic-expression"].annotation["annotation-text"]["inner-text"]["single-language-text"]["text-body"]
         * $.["pattern-action-union"].pattern["condition-selection-pattern"]["condition-node"][0]["sub-pattern-and-action"]["pattern-action-union"].pattern["condition-selection-pattern"]["condition-node"][0]["sub-pattern-and-action"]["pattern-action-union"].pattern["condition-selection-pattern"]["condition-node"][0]["logic-expression"].annotation["annotation-text"]["inner-text"]["single-language-text"]["text-body"]
         * $.["pattern-action-union"].pattern["condition-selection-pattern"]["condition-node"][0]["sub-pattern-and-action"]["pattern-action-union"].pattern["condition-selection-pattern"]["condition-node"][0]["sub-pattern-and-action"]["pattern-action-union"].pattern["condition-selection-pattern"]["condition-node"][1]["logic-expression"].annotation["annotation-text"]["inner-text"]["single-language-text"]["text-body"]
         *
         */

        //2.定義Map存儲textBody
        Map<String, String> textBodyMap = new HashMap<>();
        String textBodyMapKey = "";
        String textBodyMapValue = "";

        //用List來存儲conditionNodeList每層的大小
        List<Integer> conditionNodeListSize = new ArrayList();

        //如果treeMaplevel是2的話，因為是從0計算層數，則需要遍歷的次數應該為3次，故需要加1；
        conditionNodeListSize = calConditionNodeListSize(jsonString, treeMapLevel, addPath);

        StringBuilder recordBuilder = new StringBuilder();

        for (int i = 0; i < treeMapLevel + 1; i++) {
            StringBuilder pathBuilder = new StringBuilder(SingleNodePathConstants.BASIC_NODE_PATH_START);

            /**
             * TODO 循環問題需要修改，等有例子後進行完善
             * [1,1,5]，循環的次數是，7次。
             * [1,2,5]，應該循環的次數是，13次。
             * 對於這種狀況我們需要重新修改循環代碼，以下是示例
             *         int[] conditionNodeListSize = {1, 2, 5};
             *         int cycleCount = 0;
             *
             *         // 遍历 conditionNodeListSize 数组并累加列表值
             *         for (int i = 0; i < conditionNodeListSize.length; i++) {
             *             int multiplier = 1;
             *             for (int j = 0; j < i; j++) {
             *                 multiplier *= conditionNodeListSize[j];
             *             }
             *             cycleCount += multiplier * conditionNodeListSize[i];
             *         }
             *
             *         System.out.println("循环的次数是：" + cycleCount);
             *
             */


            Integer loopCount = conditionNodeListSize.get(i);
            if (i == 0 && loopCount == 1) {
                //暫時定為[0]
                String queryNodeListString = pathBuilder.toString() + "[0]" + SingleNodePathConstants.TEXT_BODY_PATH;
                textBodyMapValue = JsonPath.read(jsonString, queryNodeListString).toString();
                System.out.println("textBodyValue为：" + textBodyMapValue);
                textBodyMapKey = String.format("textBody0-0");
                textBodyMap.put(textBodyMapKey, textBodyMapValue);
                recordBuilder.append(SingleNodePathConstants.BASIC_NODE_PATH_START + "[0]");
            } else {
                if (loopCount == 1) {
                    for (int j = 0; j < i; j++) {
                        pathBuilder.append("[0]").append(addPath).append("[0]");
                    }
                    String queryNodeListString = pathBuilder.toString() + SingleNodePathConstants.TEXT_BODY_PATH;
                    textBodyMapValue = JsonPath.read(jsonString, queryNodeListString).toString();
                    System.out.println("textBodyValue为：" + textBodyMapValue);
                    Integer nameCount = i - 1;
                    textBodyMapKey = String.format("textBody" + i + "-" + nameCount);
                    textBodyMap.put(textBodyMapKey, textBodyMapValue);
                    recordBuilder.setLength(0);
                    recordBuilder.append(pathBuilder);
                } else {
                    //conditionNodeListSize == 3
                    for (int j = 0; j < loopCount; j++) {

                        Integer startIndex = recordBuilder.length(); // 记录循环前的 pathBuilder 长度
                        recordBuilder.append(addPath).append("[").append(j).append("]");
                        String queryNodeListString = recordBuilder.toString() + SingleNodePathConstants.TEXT_BODY_PATH;
                        textBodyMapValue = JsonPath.read(jsonString, queryNodeListString).toString();
                        System.out.println("textBodyMapValue为：" + textBodyMapValue);
                        textBodyMapKey = String.format("textBody" + i + "-" + j);
                        textBodyMap.put(textBodyMapKey, textBodyMapValue);
                        //TODO 需要添加路經進行多層遍歷嘛？？？？如果節點是[1,2,5]怎麼遍歷？


                        // 在下一次循环之前，删除新增的内容，恢复原来的 pathBuilder
                        if (j < loopCount - 1) {
                            recordBuilder.delete(startIndex, recordBuilder.length());
                        }

                    }
                }
            }
        }

        for (Map.Entry<String, String> entry : textBodyMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            System.out.println("textBodyMapKey: Joeng " + key + ", textBodyMapValue: " + value + "gavin");
        }

        return textBodyMap;
    }


    /**
     * 根據treeMapLevel（以後會封裝為一個List/Map，因為不止一個分支，目前按照一個分支來先實現基本功能），獲取logicScript
     * 需要獲取conditionNode節點獲取數組長度來獲取對應的下標的script
     * ----------------TODO 如何去定義Map的key的命名方法？？？用於定位。 [1,1,5] 0-1,1-1,2-1,2-2,2-3..,2-5?
     *
     * @param jsonString
     * @param treeMapLevel
     * @return
     */
    private static Map<String, String> getLogicScriptMap(String jsonString, Integer treeMapLevel) {
        //1.拼接路經 LOGIC_SCRIPT_PATH
        String basicLogicScriptPath = SingleNodePathConstants.BASIC_NODE_PATH_START + "[0]";
        String getScriptPath = basicLogicScriptPath + SingleNodePathConstants.LOGIC_SCRIPT_PATH;
        String addPath = SingleNodePathConstants.BASIC_NODE_PATH_MID;
        //2.定義Map存儲Script
        Map<String, String> logicScriptMap = new HashMap<>();
        String logicScriptValue = "";
        String logicScriptKey = "";

        //CONDITION_NODE_LIST_PATH
        //JSON["pattern-action-union"].pattern["condition-selection-pattern"]["condition-node"][0]["sub-pattern-and-action"]["pattern-action-union"]
        // .pattern["condition-selection-pattern"]["condition-node"][0]["sub-pattern-and-action"]["pattern-action-union"].pattern["condition-selection-pattern"]["condition-node"]

        // List<String> conditionNodeList = getConditionNodeList(jsonString, BASIC_NODE_PATH_START + "[0]" + CONDITION_NODE_LIST_PATH);

        //用List來存儲conditionNodeList每層的大小
        List<Integer> conditionNodeListSize = new ArrayList();

        //如果treeMaplevel是2的話，因為是從0計算層數，則需要遍歷的次數應該為3次，故需要加1；
        conditionNodeListSize = calConditionNodeListSize(jsonString, treeMapLevel, addPath);

        //System.out.println("conditionNodeListSizeconditionNodeListSizeconditionNodeListSizeconditionNodeListSizeconditionNodeListSizeconditionNodeListSize" + conditionNodeListSize);


//        $.["pattern-action-union"].pattern["condition-selection-pattern"]["condition-node"][0]["logic-expression"].text["logic-script"].value
//        $.["pattern-action-union"].pattern["condition-selection-pattern"]["condition-node"][0]["sub-pattern-and-action"]["pattern-action-union"].pattern["condition-selection-pattern"]["condition-node"][0]["logic-expression"].text["logic-script"].value
//        $.["pattern-action-union"].pattern["condition-selection-pattern"]["condition-node"][0]["sub-pattern-and-action"]["pattern-action-union"].pattern["condition-selection-pattern"]["condition-node"][0]["sub-pattern-and-action"]["pattern-action-union"].pattern["condition-selection-pattern"]["condition-node"][0]["logic-expression"].text["logic-script"].value

        StringBuilder recordBuilder = new StringBuilder();
        //--------最高層數為：--------->2
        //需要獲取conditionNode的數組大小
        for (int i = 0; i < treeMapLevel + 1; i++) {
            StringBuilder pathBuilder = new StringBuilder(SingleNodePathConstants.BASIC_NODE_PATH_START);

            /**
             * TODO 循環問題需要修改，等有例子後進行完善
             * [1,1,5]，循環的次數是，7次。
             * [1,2,5]，應該循環的次數是，13次。
             * 對於這種狀況我們需要重新修改循環代碼，以下是示例
             *         int[] conditionNodeListSize = {1, 2, 5};
             *         int cycleCount = 0;
             *
             *         // 遍历 conditionNodeListSize 数组并累加列表值
             *         for (int i = 0; i < conditionNodeListSize.length; i++) {
             *             int multiplier = 1;
             *             for (int j = 0; j < i; j++) {
             *                 multiplier *= conditionNodeListSize[j];
             *             }
             *             cycleCount += multiplier * conditionNodeListSize[i];
             *         }
             *
             *         System.out.println("循环的次数是：" + cycleCount);
             *
             */


            Integer loopCount = conditionNodeListSize.get(i);
            if (i == 0 && loopCount == 1) {
                //暫時定為[0]
                String queryNodeListString = pathBuilder.toString() + "[0]" + SingleNodePathConstants.LOGIC_SCRIPT_PATH;
                logicScriptValue = JsonPath.read(jsonString, queryNodeListString).toString();
                System.out.println("logicScriptValue为：" + logicScriptValue);
                logicScriptKey = String.format("logicScript0-0");
                logicScriptMap.put(logicScriptKey, logicScriptValue);
                recordBuilder.append(SingleNodePathConstants.BASIC_NODE_PATH_START + "[0]");
            } else {
                if (loopCount == 1) {
                    for (int j = 0; j < i; j++) {
                        pathBuilder.append("[0]").append(addPath).append("[0]");
                    }
                    String queryNodeListString = pathBuilder.toString() + SingleNodePathConstants.LOGIC_SCRIPT_PATH;
                    logicScriptValue = JsonPath.read(jsonString, queryNodeListString).toString();
                    System.out.println("logicScriptValue为：" + logicScriptValue);
                    Integer nameCount = i - 1;
                    logicScriptKey = String.format("logicScript" + i + "-" + nameCount);
                    logicScriptMap.put(logicScriptKey, logicScriptValue);
                    recordBuilder.setLength(0);
                    recordBuilder.append(pathBuilder);
                } else {
                    //conditionNodeListSize == 3
                    for (int j = 0; j < loopCount; j++) {

                        Integer startIndex = recordBuilder.length(); // 记录循环前的 pathBuilder 长度
                        recordBuilder.append(addPath).append("[").append(j).append("]");
                        String queryNodeListString = recordBuilder.toString() + SingleNodePathConstants.LOGIC_SCRIPT_PATH;
                        logicScriptValue = JsonPath.read(jsonString, queryNodeListString).toString();
                        System.out.println("logicScriptValue为：" + logicScriptValue);
                        logicScriptKey = String.format("logicScript" + i + "-" + j);
                        logicScriptMap.put(logicScriptKey, logicScriptValue);
                        //TODO 需要添加路經進行多層遍歷嘛？？？？如果節點是[1,2,5]怎麼遍歷？


                        // 在下一次循环之前，删除新增的内容，恢复原来的 pathBuilder
                        if (j < loopCount - 1) {
                            recordBuilder.delete(startIndex, recordBuilder.length());
                        }
                        //

                    }
                }
            }
        }

        for (Map.Entry<String, String> entry : logicScriptMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            System.out.println("Key: Joeng " + key + ", Value: " + value + "gavin");
        }

        return logicScriptMap;
    }

    /**
     * 計算conditionNode每層有多少個節點，[1,2,5]代表第一層1個節點，第一層每個節點第二層2個節點，第二層每個節點有5個節點
     *
     * @param jsonString
     * @param treeMapLevel
     * @param addPath
     * @return
     */
    private static List<Integer> calConditionNodeListSize(String jsonString, Integer treeMapLevel, String addPath) {
        List<Integer> conditionNodeListSize = new ArrayList<>();
        Integer listSize = 0;
        for (int i = 0; i < treeMapLevel + 1; i++) {
            StringBuilder pathBuilder = new StringBuilder(SingleNodePathConstants.BASIC_NODE_PATH_START);
            if (i == 0) {
                String queryNodeListString = pathBuilder.toString();
                List<String> conditionNodeList = getConditionNodeList(jsonString, queryNodeListString);
                listSize = conditionNodeList.size();
                conditionNodeListSize.add(listSize);

            } else {

                if (listSize == 1) {
                    for (int j = 0; j < i; j++) {
                        pathBuilder.append("[0]").append(addPath);
                    }
                    String queryNodeListString = pathBuilder.toString();
                    List<String> conditionNodeList = getConditionNodeList(jsonString, queryNodeListString);
                    listSize = conditionNodeList.size();
                    conditionNodeListSize.add(listSize);
                } else {
                    //conditionNodeListSize == 3
                    for (int j = 0; j < conditionNodeListSize.size(); j++) {

                        //[1,1,5]
                        Integer loopCount = conditionNodeListSize.get(j);

                        for (int k = 0; k < loopCount; k++) {
                            Integer startIndex = pathBuilder.length(); // 记录循环前的 pathBuilder 长度
                            pathBuilder.append("[").append(k).append("]").append(addPath);
                            String queryNodeListString = pathBuilder.toString();
                            List<String> conditionNodeList = getConditionNodeList(jsonString, queryNodeListString);
                            listSize = conditionNodeList.size();
                            conditionNodeListSize.add(listSize);
                            // 在下一次循环之前，删除新增的内容，恢复原来的 pathBuilder
                            if (k < loopCount - 1) {
                                pathBuilder.delete(startIndex, pathBuilder.length());
                            }
                        }
                    }
                }
            }
        }

        return conditionNodeListSize;
    }

    /**
     * 判斷是否葉子節點,true葉子節點，false非葉子節點;節點不存在;
     *
     * @param nodePath
     * @return
     */
    private static boolean leftNodeJudgment(String jsonString, String nodePath) {

        try {
            Object branchNodes = JsonPath.read(jsonString, nodePath);
            if (branchNodes == null) {
                System.out.println("該節點不存在");
                return false;
            } else if (branchNodes.equals("pattern")) {
                System.out.println("該節點存在有子節點");
                return false;
            } else if (branchNodes.equals("action")) {
                System.out.println("該節點為葉子節點");
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("節點不存在" + e.getMessage());
            return false;
        }

    }


    /**
     * 校驗傳入Json路經是否存在JsonString中
     * true:該節點存在
     * false:該節點不存在
     *
     * @param jsonString
     * @param verifyPath
     * @return
     */
    private static boolean isContainNode(String jsonString, String verifyPath) {
        try {
            String nodeValue = JsonPath.read(jsonString, verifyPath).toString();
            if (nodeValue == null || nodeValue.isEmpty()) {
                System.out.println("路經不存在");
                return false;
            } else if (nodeValue != null && !nodeValue.isEmpty()) {
                System.out.println("該節點存在");
                return true;
            } else if (nodeValue != null && nodeValue.isEmpty()) {
                System.out.println("該路經存在,但值為空");
            } else {
                System.out.println("該路經不存在");
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("路經不存在 " + e.getMessage());
            return false;
        }
        return false;
    }


    /**
     * 獲取最高層數
     * 以後參數會傳入一個數組進行計算哪個路經下的最高層級
     *
     * @return
     */
    private static Integer treeMapLevel(String jsonString) {
        //1.拼接路經
        String basicLevelPath = SingleNodePathConstants.BASIC_NODE_PATH_START + "[0]";
        String verifyPath = basicLevelPath + SingleNodePathConstants.LEVEL_PATH;
        String addPath = SingleNodePathConstants.BASIC_NODE_PATH_MID + "[0]";
//        verifyPath = BASIC_NODE_PATH_START +"[0]" + BASIC_NODE_PATH_MID + "[0]" + LEVEL_PATH;
//        verifyPath = BASIC_NODE_PATH_START +"[0]" + BASIC_NODE_PATH_MID + "[0]" + BASIC_NODE_PATH_MID + "[0]" + LEVEL_PATH;
//        verifyPath = BASIC_NODE_PATH_START +"[0]" + BASIC_NODE_PATH_MID + "[0]" + BASIC_NODE_PATH_MID + "[0]" + BASIC_NODE_PATH_MID + "[0]" + LEVEL_PATH;

        //2.定義一個計數器
        Integer midPathCount = 0;
        String levelNum = "";
        //1.先判斷是否存在該層級

        boolean isContains = isContainNode(jsonString, verifyPath);

        while (isContains) {
            midPathCount++;
            String jointStr = basicLevelPath;
            for (int i = 0; i < midPathCount; i++) {
                jointStr += addPath;
            }
            verifyPath = "";
            verifyPath = jointStr + SingleNodePathConstants.LEVEL_PATH;
            isContains = isContainNode(jsonString, verifyPath);
        }

        String jointStr = basicLevelPath;
        midPathCount--;
        for (int i = 0; i < midPathCount; i++) {
            jointStr += addPath;
        }
        verifyPath = "";
        verifyPath = jointStr + SingleNodePathConstants.LEVEL_PATH;
        levelNum = JsonPath.read(jsonString, verifyPath).toString();
        return Integer.parseInt(levelNum);
    }


    /**
     * 用於獲取conditionNodeList
     *
     * @param jsonString
     * @param conditionNodeListPath
     * @return
     */
    private static List<String> getConditionNodeList(String jsonString, String conditionNodeListPath) {

        List<String> conditionsNodeList = new ArrayList<>();
        try {
            String conditionsNodeStringList = JsonPath.read(jsonString, conditionNodeListPath).toString();
            conditionsNodeList = JSON.parseArray(conditionsNodeStringList, String.class);
            System.out.println(conditionsNodeList.size());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println();
            System.out.println(e.getMessage());
        }


        return conditionsNodeList;
    }


}

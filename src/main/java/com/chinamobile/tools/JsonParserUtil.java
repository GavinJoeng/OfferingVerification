package com.chinamobile.tools;

import com.chinamobile.constant.*;
import com.alibaba.fastjson.JSON;
import com.chinamobile.entity.Action;
import com.chinamobile.entity.ConditionNode;
import com.chinamobile.entity.MatrixAction;
import com.jayway.jsonpath.JsonPath;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.chinamobile.tools.XMLParser.*;
import static com.chinamobile.tools.XMLtoJSON.convertXML2Json;

/**
 * @description: 用於解析Json文件，用於封裝ConditionNode實體類，並輸出EXCEL表格
 * @author: gavin yang
 * @email: gavinyang@hk.chinamobile.com
 * @date: 2024/1/8 15:38
 */
public class JsonParserUtil {

    private static ConditionNode conditionNode;



    public static void main(String[] args) throws IOException {

        String RULE_TEXT_FILE = SingleNodePathConstants.BASIC_PATH + SingleNodePathConstants.OFFERING_ID + SingleNodePathConstants.RULE_TXT_FILE;
        String RULE_XML_FILE = SingleNodePathConstants.BASIC_PATH + SingleNodePathConstants.OFFERING_ID + SingleNodePathConstants.RULE_XML_FILE;
        convertTxt2XML(RULE_TEXT_FILE, RULE_XML_FILE);

        String xmlFilePath = SingleNodePathConstants.BASIC_PATH + SingleNodePathConstants.OFFERING_ID + SingleNodePathConstants.RULE_XML_FILE;
        String jsonFilePath = SingleNodePathConstants.BASIC_PATH + SingleNodePathConstants.OFFERING_ID + SingleNodePathConstants.RULE_JSON_PATH;
        convertXML2Json(xmlFilePath,jsonFilePath);

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

        //判斷是否為矩陣類型
        ConditionNode matrixNodeAndAction = getMatrixNodeAndAction(jsonString, conditionNodeListSize, conditionNode);
        System.out.println("matrixNodeAndActionmatrixNodeAndAction" + matrixNodeAndAction);
        //普通動作
        //List<Map<String, String>> actionMapList = getActionMapList(jsonString, conditionNodeListSize);

        //把信息裝入實體類中，傳送到前端進行展示，目前是以實現後端為主。
        //conditionNode.setActionMapList(actionMapList);
        String filePath = SingleNodePathConstants.BASIC_PATH + SingleNodePathConstants.OFFERING_ID + "_PLAN_POLICY_RULE" + ".xlsx";
        generateExcel(filePath, conditionNode);
        System.out.println("gavinjoeng" + conditionNode);

        return "生成EXCEL文件成功！";
    }


    /**
     * 判斷是否為矩陣類型，由於只有葉子節點才可能為矩陣類型
     *
     * @param jsonString
     * @param conditionNodeListSize
     * @param conditionNode
     * @return
     */
    private static ConditionNode getMatrixNodeAndAction(String jsonString, List<Integer> conditionNodeListSize, ConditionNode conditionNode) {

        Integer loopCount = conditionNodeListSize.size();
        String matrixPathLink = "";
        //存儲matrixActionMap
        Map<String, MatrixAction> matrixActionMap = new HashMap<>();
        //存儲actionMap
        Map<String, Action> actionMap = new HashMap<>();

        //拼接中間路經
        List<String> matrixLinkPathList = new ArrayList<>();
        //記錄葉子節點的層次,定為從0開始計數，故所以減一
        Integer treeLevel = conditionNodeListSize.size() - 1;

        StringBuilder matrixPathLinkBuilder = new StringBuilder(MatrixPatternPath.BASIC_NODE_PATH_START);
        //普通動作節點和矩陣節點走的是一樣的拼接邏輯。
        //把所有的葉子節點的中間路經都加入到matrixLinkPathList。
        for (int i = 0; i < loopCount; i++) {
            Integer listSize = conditionNodeListSize.get(i);
            if (i == 0 && listSize == 1) {
                matrixPathLinkBuilder.append("[").append(0).append("]");

            } else if (listSize == 1) {
                matrixPathLinkBuilder.append(MatrixPatternPath.BASIC_NODE_PATH_MID);
                matrixPathLinkBuilder.append("[").append(0).append("]");
                if (i == loopCount - 1) {
                    matrixLinkPathList.add(matrixPathLinkBuilder.toString());
                }
            } else {
                for (int j = 0; j < listSize; j++) {
                    //TODO 多節點循環問題
                    Integer startIndex = matrixPathLinkBuilder.length(); // 记录循环前的 pathBuilder 长度
                    matrixPathLinkBuilder.append(SingleNodePathConstants.BASIC_NODE_PATH_MID);
                    matrixPathLinkBuilder.append("[").append(j).append("]");
                    matrixLinkPathList.add(matrixPathLinkBuilder.toString());
                    // 在下一次循环之前，删除新增的内容，恢复原来的 pathBuilder
                    if (j < listSize - 1) {
                        matrixPathLinkBuilder.delete(startIndex, matrixPathLinkBuilder.length());
                    }
                }
            }
        }

        for (String matrixLinkPath : matrixLinkPathList) {
            System.out.println("matrixLinkPathmatrixLinkPathmatrixLinkPathmatrixLinkPath" + matrixLinkPath);
        }

        for (int i = 0; i < matrixLinkPathList.size(); i++) {

            String linkPath = matrixLinkPathList.get(i);

            //進行判斷是普通動作還是矩陣動作
            boolean matrixFlag = isMatrixPattern(jsonString, linkPath + MatrixPatternPath.IS_MATRIX_PATTERN_PATH);

            if (matrixFlag) {
                //獲取matrixFlag關鍵信息
                MatrixAction matrixAction = getMatrixAction(jsonString, linkPath);
                String matrixId = matrixAction.getMatrixId();
                String matrixQuerySQL = matrixAction.getMatrixQuerySQL();
                String filePath = MatrixPatternPath.BASIC_PATH + matrixId + MatrixPatternPath.MATRIX_TXT_FILE;

                String content = matrixQuerySQL;

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                    writer.write(content);
                    System.out.println("文本已成功写入到文件：" + filePath);
                } catch (IOException e) {
                    System.out.println("写入文件时出现错误：" + e.getMessage());
                }

                // 1.程序掛起，需要打開輸出的文件，裏面有SQL語句。
                // 2.需要在華為數據庫裏進行查詢對應Matrix文件，之後複製到輸出的文件中
                // 3.选择你複製之後的文件
                String matrixFilePath = chooseFile();

                // 读取文件内容
                if (matrixFilePath != null) {
                    matrixAction = matrixTxtToJson(matrixFilePath, matrixAction);
                }

                String matrixActionKey = "matrixActionMap" + treeLevel + "-" + i;
                //存儲到Map，如何命名key？
                matrixActionMap.put(matrixActionKey, matrixAction);
                //TODO 由於xml文件只有一些matrix_id和動作類型的信息，對於資費需要再查詢數據庫進行獲取金額等信息

            } else {
                //獲取action關鍵信息
                Action action = getAction(jsonString, linkPath);
                //存儲到Map，如何命名key？
                String actionKey = "actionMap" + treeLevel + "-" + i;
                ;
                actionMap.put(actionKey, action);
            }

        }
        conditionNode.setMatrixActionMap(matrixActionMap);
        conditionNode.setActionMap(actionMap);
        return conditionNode;
    }



    private static MatrixAction matrixTxtToJson(String matrixFilePath, MatrixAction matrixAction) {
        String matrixId = matrixAction.getMatrixId();
        //根據MATRIX_ID獲取文件
        String matrixXmlPath = MatrixPatternPath.BASIC_PATH + matrixId + MatrixPatternPath.MATRIX_XML_FILE;
        String matrixJsonPath = MatrixPatternPath.BASIC_PATH + matrixId + MatrixPatternPath.MATRIX_JSON_PATH;

        try{
            //將文件轉為XML、JSON格式
            String context = readFileToString(matrixFilePath);

            String result = java.net.URLDecoder.decode(context, StandardCharsets.UTF_8.name());

            convertStringToXml(result, matrixXmlPath);

            convertXML2Json(matrixXmlPath, matrixJsonPath);

            //提取JSON關鍵信息,返回什麼呢？
            matrixAction = extractMatrixJsonInfo(matrixJsonPath, matrixAction);

        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        return matrixAction;
    }


    private static MatrixAction extractMatrixJsonInfo(String matrixJsonPath, MatrixAction matrixAction) {
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
        //List<Integer> conditionNodeListSize = calConditionNodeListSize(jsonString, treeMapLevel, SingleNodePathConstants.BASIC_NODE_PATH_MID);
        matrixAction = getMatrixAction(jsonString, matrixAction);

        return matrixAction;
    }

    /**
     * TODO 後續會更改，根據ActionType的不同會修改對應的路經，需要把動作類型進行替換。
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

    /**
     * 從華為數據庫中拿到Matrix的文件，需要在生成的文件中粘帖上去。
     * @return
     */
    private static String chooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("TXT Files", "txt");
        fileChooser.setFileFilter(filter);

        // 设置初始目录
        String initialDirectory = MatrixPatternPath.BASIC_PATH;
        fileChooser.setCurrentDirectory(new File(initialDirectory));

        while (true) {
            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                System.out.println("选择的文件路径：" + filePath);
                return filePath;
            } else if (result == JFileChooser.CANCEL_OPTION) {
                System.out.println("未选择文件。");
                System.exit(0); // 关闭程序
                return null;
            }
        }
    }

    /**
     * 讀取Matrix的TXT的文件
     * @param filePath
     */
    private static void readFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Action getAction(String jsonString, String linkPath) {
        Action action = new Action();

        //獲取actionType
        //<!-- "charge-discount-action" -->
        //獲取actionType
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append(linkPath).append(SingleNodePathConstants.ACTION_TYPE_PATH);
        String actionType = JsonPath.read(jsonString, queryBuilder.toString()).toString();
        action.setActionType(actionType);

        //<!-- integer = 0 -->
        queryBuilder.setLength(0);
        queryBuilder.append(linkPath).append(SingleNodePathConstants.ACTION_BASIC_PATH).append("action[\"").append(actionType).append("\"]").append(SingleNodePathConstants.INTEGER_PATH);
        String amount = JsonPath.read(jsonString, queryBuilder.toString()).toString();
        action.setAmount(amount);

        //<!-- currency-measurement = 501 -->
        queryBuilder.setLength(0);
        queryBuilder.append(linkPath).append(SingleNodePathConstants.ACTION_BASIC_PATH).append("action[\"").append(actionType).append("\"]").append(SingleNodePathConstants.CURRENCY_MEASUREMENT_PATH);
        String currencyMeasurement = JsonPath.read(jsonString, queryBuilder.toString()).toString();
        //直接轉換為對應貨幣
        String currencyUnit = SysCurrencyMeasurementConstants.DATA_MAP.get(currencyMeasurement);
        action.setCurrencyUnit(currencyUnit);

        return action;
    }

    private static MatrixAction getMatrixAction(String jsonString, String linkPath) {

        MatrixAction matrixAction = new MatrixAction();


        //<!-- "matrix-id"="696989206252108387" -->
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append(linkPath).append(MatrixPatternPath.MATRIX_ID_PATH);
        String matrixId = JsonPath.read(jsonString, queryBuilder.toString()).toString();
        matrixAction.setMatrixId(matrixId);

        StringBuilder matrixQuerySQLBuilder = new StringBuilder();
        matrixQuerySQLBuilder.append("SELECT SUB_PATTERN_ACTION FROM PE_MATRIX_DATA  WHERE MATRIX_ID =").append(matrixId);
        matrixAction.setMatrixQuerySQL(matrixQuerySQLBuilder.toString());

        //ActionType free-unit-bonus-action
        queryBuilder.setLength(0);
        queryBuilder.append(linkPath).append(MatrixPatternPath.MATRIX_ACTION_TYPE_PATH);
        String actionType = JsonPath.read(jsonString, queryBuilder.toString()).toString();
        matrixAction.setActionType(actionType);

        //dataElement <!-- data-element="C_CALLING_HOME_CC" -->
        queryBuilder.setLength(0);
        queryBuilder.append(linkPath).append(MatrixPatternPath.MATRIX_DATA_ELEMENT);
        String dataElement = JsonPath.read(jsonString, queryBuilder.toString()).toString();
        matrixAction.setDataElement(dataElement);

        return matrixAction;
    }

    /**
     * 判斷是否為矩陣節點
     *
     * @param jsonString
     * @param matrixLinkPath
     * @return
     */
    private static boolean isMatrixPattern(String jsonString, String matrixLinkPath) {

        try{
            String chosenElement = JsonPath.read(jsonString, matrixLinkPath).toString();
            if (chosenElement.equals("matrix-selection-pattern")) {
                return true;
            } else if (chosenElement.equals("condition-selection-pattern")) {
                return false;
            } else {
                return false;
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            System.out.println("路經不存在！");
            return false;
        }

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
        headerCell3.setCellValue("矩陣");
        headerCell3.setCellStyle(headerStyle);

        Cell headerCell4 = headerRow.createCell(4);
        headerCell4.setCellValue("(矩陣)動作類型");
        headerCell4.setCellStyle(headerStyle);

        Cell headerCell5 = headerRow.createCell(5);
        headerCell5.setCellValue("費用/(矩陣)Bonus Amount");
        headerCell5.setCellStyle(headerStyle);

        Cell headerCell6 = headerRow.createCell(6);
        headerCell6.setCellValue("FreeUnitType");
        headerCell6.setCellStyle(headerStyle);

        Cell headerCell7 = headerRow.createCell(7);
        headerCell7.setCellValue("MATRIX_ID");
        headerCell7.setCellStyle(headerStyle);

        Cell headerCell8 = headerRow.createCell(8);
        headerCell8.setCellValue("DATA_ELEMENT");
        headerCell8.setCellStyle(headerStyle);

        Cell headerCell9 = headerRow.createCell(9);
        headerCell9.setCellValue("矩陣查詢SQL");
        headerCell9.setCellStyle(headerStyle);


        // 调整列宽
        for (int i = 0; i <= 9; i++) {
            if (i == 2) {
                sheet.setColumnWidth(i, 40 * 256);
            } else if (i == 0 || i == 3) {
                sheet.setColumnWidth(i, 7 * 256);
            } else if (i == 4 || i == 5 ||i == 6 || i == 7 || i == 8) {
                sheet.setColumnWidth(i, 30 * 256);
            } else if (i == 9) {
                sheet.setColumnWidth(i, 120 * 256);
            } else {
                sheet.setColumnWidth(i, 20 * 256);
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

        Integer treeLevel = nodeListSize.size() - 1;
        // 填充数据
        for (int i = 0; i < nodeListSize.size(); i++) {
            int branchSize = nodeListSize.get(i);

            for (int j = 0; j < branchSize; j++) {
                Row dataRow = sheet.createRow(rowIndex);

                // 获取对应的属性值
                String script = conditionNode.getLogicScriptMap().get("logicScript" + i + "-" + j);
                String textBody = conditionNode.getTextBodyMap().get("textBody" + i + "-" + j);
                //String actionType = conditionNode.getActionTypeMap().get("actionType" + i + "-" + j);
                //String amount = conditionNode.getAmountMap().get("amount" + i + "-" + j);
                Map<String, Action> actionMap = conditionNode.getActionMap();
                Map<String, MatrixAction> matrixActionMap = conditionNode.getMatrixActionMap();


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


                if (i == nodeListSize.size() - 1) {
                    String actionMapKey = "actionMap" + treeLevel + "-" + j;
                    Optional<Action> optionalAction = Optional.ofNullable(actionMap.get(actionMapKey));
                    // 处理对象为空的情况
                    String matrixActionMapKey = "matrixActionMap" + treeLevel + "-" + j;
                    Optional<MatrixAction> optionalMatrixAction = Optional.ofNullable(matrixActionMap.get(matrixActionMapKey));


                    if (optionalAction.isPresent()) {
                        Action action = optionalAction.get();
                        String actionType = action.getActionType();
                        String amount = action.getAmount();
                        String currencyUnit = action.getCurrencyUnit();
                        String amountCurrency = amount + " " + currencyUnit;

                        Cell cell3 = dataRow.createCell(3);
                        cell3.setCellValue("否");
                        cell3.setCellStyle(dataStyle);

                        Cell cell4 = dataRow.createCell(4);
                        cell4.setCellValue(actionType);
                        cell4.setCellStyle(dataStyle);

                        Cell cell5 = dataRow.createCell(5);
                        cell5.setCellValue(amountCurrency);
                        cell5.setCellStyle(dataStyle);

                        Cell cell6 = dataRow.createCell(6);
                        cell6.setCellValue("-");
                        cell6.setCellStyle(dataStyle);

                        Cell cell7 = dataRow.createCell(7);
                        cell7.setCellValue("-");
                        cell7.setCellStyle(dataStyle);

                        Cell cell8 = dataRow.createCell(8);
                        cell8.setCellValue("-");
                        cell8.setCellStyle(dataStyle);

                        Cell cell9 = dataRow.createCell(9);
                        cell9.setCellValue("-");
                        cell9.setCellStyle(dataStyle);

                    } else if (optionalMatrixAction.isPresent()) {
                        MatrixAction matrixAction = optionalMatrixAction.get();
                        //動作類型
                        String actionType = matrixAction.getActionType();

                        //display屬性 + 計量單位
                        String amount = matrixAction.getBonusAmount();
                        String measurementUnit = matrixAction.getMeasurementUnit();
                        String bonusAmount = amount + "  " + measurementUnit;
                        //freeUnitType
                        String freeUnitType = matrixAction.getFreeUnitType();

                        String matrixId = matrixAction.getMatrixId();
                        String dataElement = matrixAction.getDataElement();
                        String matrixQuerySQL = matrixAction.getMatrixQuerySQL();

                        Cell cell3 = dataRow.createCell(3);
                        cell3.setCellValue("是");
                        cell3.setCellStyle(dataStyle);

                        Cell cell4 = dataRow.createCell(4);
                        cell4.setCellValue(actionType);
                        cell4.setCellStyle(dataStyle);

                        Cell cell5 = dataRow.createCell(5);
                        cell5.setCellValue(bonusAmount);
                        cell5.setCellStyle(dataStyle);

                        Cell cell6 = dataRow.createCell(6);
                        cell6.setCellValue(freeUnitType);
                        cell6.setCellStyle(dataStyle);

                        Cell cell7 = dataRow.createCell(7);
                        cell7.setCellValue(matrixId);
                        cell7.setCellStyle(dataStyle);

                        Cell cell8 = dataRow.createCell(8);
                        cell8.setCellValue(dataElement);
                        cell8.setCellStyle(dataStyle);

                        Cell cell9 = dataRow.createCell(9);
                        cell9.setCellValue(matrixQuerySQL);
                        cell9.setCellStyle(dataStyle);

                    } else {


                        Cell cell3 = dataRow.createCell(3);
                        cell3.setCellValue("是");
                        cell3.setCellStyle(dataStyle);

                        Cell cell4 = dataRow.createCell(4);
                        cell4.setCellValue("-");
                        cell4.setCellStyle(dataStyle);

                        Cell cell5 = dataRow.createCell(5);
                        cell5.setCellValue("-");
                        cell5.setCellStyle(dataStyle);

                        Cell cell6 = dataRow.createCell(6);
                        cell6.setCellValue("-");
                        cell6.setCellStyle(dataStyle);

                        Cell cell7 = dataRow.createCell(7);
                        cell7.setCellValue("-");
                        cell7.setCellStyle(dataStyle);

                        Cell cell8 = dataRow.createCell(8);
                        cell8.setCellValue("-");
                        cell8.setCellStyle(dataStyle);

                        Cell cell9 = dataRow.createCell(9);
                        cell9.setCellValue("-");
                        cell9.setCellStyle(dataStyle);
                    }


                } else {
                    Cell cell3 = dataRow.createCell(3);
                    cell3.setCellValue("否");
                    cell3.setCellStyle(dataStyle);

                    Cell cell4 = dataRow.createCell(4);
                    cell4.setCellValue("-");
                    cell4.setCellStyle(dataStyle);

                    Cell cell5 = dataRow.createCell(5);
                    cell5.setCellValue("-");
                    cell5.setCellStyle(dataStyle);

                    Cell cell6 = dataRow.createCell(6);
                    cell6.setCellValue("-");
                    cell6.setCellStyle(dataStyle);

                    Cell cell7 = dataRow.createCell(7);
                    cell7.setCellValue("-");
                    cell7.setCellStyle(dataStyle);

                    Cell cell8 = dataRow.createCell(8);
                    cell8.setCellValue("-");
                    cell8.setCellStyle(dataStyle);

                    Cell cell9 = dataRow.createCell(9);
                    cell9.setCellValue("-");
                    cell9.setCellStyle(dataStyle);
                }


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
     * @param conditionNodeListSize
     * @return
     */
    private static List<Map<String, String>> getActionMapList(String jsonString, List<Integer> conditionNodeListSize) {
        List<Map<String, String>> actionMapList = new ArrayList<>();
        //2.定義Map存儲actionMap
        //Map的名稱必須不同存儲於List中,Map的命名方式可以在循環中進行創建，然後存儲在List當中
        //命名格式：treeMapLevel=2，代表是3層的樹狀圖，最後一層才會有動作出現，actionMap的命名方式為：actionMap2-0,actionMap2-1,actionMap2-2...
        //Map<String, String> actionMap = new HashMap<>();

        //拼接中間路經
        List<String> actionLinkPathList = new ArrayList<>();
        Integer loopCount = conditionNodeListSize.size();

        StringBuilder actionBuilder = new StringBuilder(SingleNodePathConstants.BASIC_NODE_PATH_START);
        for (int i = 0; i <= loopCount; i++) {

            Integer listSize = conditionNodeListSize.get(i);
            if (listSize == 1 && i == 0) {
                actionBuilder.append("[").append(0).append("]");
            } else if (listSize == 1) {
                actionBuilder.append(SingleNodePathConstants.BASIC_NODE_PATH_MID);
                actionBuilder.append("[").append(0).append("]");
            } else {
                //TODO 需要解決循環[1,2,5]類問題。
                for (int j = 0; j < listSize; j++) {
                    Integer startIndex = actionBuilder.length(); // 记录循环前的 pathBuilder 长度
                    actionBuilder.append(SingleNodePathConstants.BASIC_NODE_PATH_MID);
                    actionBuilder.append("[").append(j).append("]");
                    actionLinkPathList.add(actionBuilder.toString());
                    // 在下一次循环之前，删除新增的内容，恢复原来的 pathBuilder
                    if (j < listSize - 1) {
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

        for (int i = 0; i < loopCount; i++) {
            Integer listSize = conditionNodeListSize.get(i);

            if (listSize == 1 && i == 0) {
                String actionTypeKey = "actionType0" + "-" + i;
                String amountKey = "amount0" + "-" + i;
                actionTypeMap.put(actionTypeKey, "-");
                amountMap.put(amountKey, "-");
            } else if (listSize == 1) {
                String actionTypeKey = "actionType" + i + "-" + "0";
                String amountKey = "amount" + i + "-" + "0";
                actionTypeMap.put(actionTypeKey, "-");
                amountMap.put(amountKey, "-");
            } else {
                for (int j = 0; j < listSize; j++) {
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


        Integer actionBottomNum = conditionNodeListSize.get(loopCount);
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
    public static List<Integer> calConditionNodeListSize(String jsonString, Integer treeMapLevel, String addPath) {
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
    public static Integer treeMapLevel(String jsonString) {
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

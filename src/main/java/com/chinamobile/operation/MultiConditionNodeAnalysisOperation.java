package com.chinamobile.operation;

import com.alibaba.fastjson.JSON;
import com.chinamobile.constant.*;
import com.chinamobile.entity.Action;
import com.chinamobile.entity.ConditionNode;
import com.chinamobile.entity.MatrixAction;
import com.chinamobile.tools.JDBCUtils;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.chinamobile.tools.XMLParser.convertStringToXml;
import static com.chinamobile.tools.XMLParser.readFileToString;
import static com.chinamobile.tools.XMLtoJSON.convertXML2Json;


/**
 * @description: 多節點分析
 * @author: gavin yang
 * @email: gavinyang@hk.chinamobile.com
 * @date: 2024/1/19 9:23
 */
public class MultiConditionNodeAnalysisOperation {
    public static void main(String[] args) throws IOException {


        // 记录程序开始执行的时间
        long startTime = System.currentTimeMillis();

        String jsonReadPath = MultiNodePathConstants.BASIC_PATH + MultiNodePathConstants.OFFERING_ID + MultiNodePathConstants.RULE_JSON_PATH;
        String txtReadPath = MultiNodePathConstants.BASIC_PATH + MultiNodePathConstants.OFFERING_ID + MultiNodePathConstants.RULE_TXT_FILE;
        String xmlReadPath = MultiNodePathConstants.BASIC_PATH + MultiNodePathConstants.OFFERING_ID + MultiNodePathConstants.RULE_XML_FILE;

        try {
            //將文件轉為XML、JSON格式
            String context = readFileToString(txtReadPath);

            String result = java.net.URLDecoder.decode(context, StandardCharsets.UTF_8.name());

            convertStringToXml(result, xmlReadPath);

            convertXML2Json(xmlReadPath, jsonReadPath);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        //獲取JSON文件
        String jsonString = fileToJsonString(jsonReadPath);
        //獲取ConditionNode節點
        List<String> conditionNodeList = getConditionNodeList(jsonString);
        //獲取logicScript
        Map<String, String> logicScriptMap = getLogicScriptMap(jsonString, conditionNodeList);
        //獲取textBody
        Map<String, String> textBodyMap = getTextBodyMap(jsonString, conditionNodeList);
        //獲取Action和MatrixAction
        ConditionNode matrixNodeAndAction = getMatrixNodeAndAction(jsonString, conditionNodeList);
        //獲取logicScript、textBody補充到ConditionNode中
        matrixNodeAndAction.setLogicScriptMap(logicScriptMap);
        matrixNodeAndAction.setTextBodyMap(textBodyMap);
        System.out.println("matrixNodeAndAction" + matrixNodeAndAction);


        String filePath = MultiNodePathConstants.BASIC_PATH + MultiNodePathConstants.OFFERING_ID + "_PLAN_POLICY_RULE" + ".xlsx";
        generateExcel(filePath, matrixNodeAndAction, conditionNodeList);


//        matrixNodeAndAction.getMatrixActionMap().get();
        List<String> delFileList = matrixNodeAndAction.getUselessPathList();


        //刪除多餘的文件
        //deleteUselessFile(delFileList);

        // 记录程序结束执行的时间
        long endTime = System.currentTimeMillis();

        // 计算程序运行的时间，单位为毫秒
        long timeElapsed = endTime - startTime;
        // 打印程序运行的时间
        System.out.println("程序运行的时间：" + timeElapsed + "毫秒");

    }

    /**
     * 刪除多餘文件
     *
     * @return
     */
    private static void deleteUselessFile(List<String> uselessPathList) {

        uselessPathList.add(MultiNodePathConstants.BASIC_PATH + MultiNodePathConstants.OFFERING_ID + "verifiedPaths.txt");
        uselessPathList.add(MultiNodePathConstants.BASIC_PATH + MultiNodePathConstants.OFFERING_ID + MultiNodePathConstants.RULE_XML_FILE);
        uselessPathList.add(MultiNodePathConstants.BASIC_PATH + MultiNodePathConstants.OFFERING_ID + MultiNodePathConstants.RULE_JSON_PATH);

        for (String delFile : uselessPathList) {
            Path path = Paths.get(delFile);
            try {
                Files.delete(path);
                System.out.println("文件删除成功！");
            } catch (IOException e) {
                System.out.println(delFile + "文件删除失败");
            }
        }
    }


    /**
     * 根據conditionNode生成EXCEL文件
     *
     * @param filePath
     * @param conditionNode
     * @param conditionNodeList
     */
    public static void generateExcel(String filePath, ConditionNode conditionNode, List<String> conditionNodeList) {
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

//        Cell headerCell9 = headerRow.createCell(9);
//        headerCell9.setCellValue("矩陣查詢SQL");
//        headerCell9.setCellStyle(headerStyle);


        // 调整列宽
        for (int i = 0; i <= 9; i++) {
            if (i == 2) {
                sheet.setColumnWidth(i, 40 * 256);
            } else if (i == 0 || i == 3) {
                sheet.setColumnWidth(i, 7 * 256);
            } else if (i == 4 || i == 5 || i == 6 || i == 7 || i == 8) {
                sheet.setColumnWidth(i, 30 * 256);
            } else if (i == 9) {
                sheet.setColumnWidth(i, 120 * 256);
            } else {
                sheet.setColumnWidth(i, 20 * 256);
            }
        }


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

        List<String> subScriptList = new ArrayList<>();

        Map<String, String> logicScriptMap = new HashMap<>();
        Map<String, String> textBodyMap = new HashMap<>();
        Map<String, Action> actionMap = new HashMap<>();
        Map<String, MatrixAction> matrixActionMap = new HashMap<>();


        //excel數據填充
        for (String conditionNodePath : conditionNodeList) {


            StringBuilder logicScriptKeyBuilder = new StringBuilder("logicScript");
            StringBuilder textBodyKeyBuilder = new StringBuilder("textBody");
            StringBuilder actionKeyBuilder = new StringBuilder("action");
            StringBuilder matrixKeyBuilder = new StringBuilder("matrixAction");
            StringBuilder subScriptBuilder = new StringBuilder("");
            //獲取Key值
            String subScript = processMapKey(conditionNodePath, subScriptBuilder);
            String logicScriptKey = processMapKey(conditionNodePath, logicScriptKeyBuilder);
            String textBodyKey = processMapKey(conditionNodePath, textBodyKeyBuilder);
            String actionKey = processMapKey(conditionNodePath, actionKeyBuilder);
            String matrixKey = processMapKey(conditionNodePath, matrixKeyBuilder);


            //獲取數據
            String logicScript = conditionNode.getLogicScriptMap().get(logicScriptKey);
            String textBody = conditionNode.getTextBodyMap().get(textBodyKey);
            Action action = conditionNode.getActionMap().get(actionKey);
            MatrixAction matrixAction = conditionNode.getMatrixActionMap().get(matrixKey);

            //裝入坐標值，用於定為各種Map中的key
            subScriptList.add(subScript);

            //數據裝入Map中，方便用subScript進行獲取。
            logicScriptMap.put(logicScriptKey, logicScript);
            textBodyMap.put(textBodyKey, textBody);
            actionMap.put(actionKey, action);
            matrixActionMap.put(matrixKey, matrixAction);

        }
        //把生成的坐標進行排序
        subScriptList.sort(Comparator.comparing(MultiConditionNodeAnalysisOperation::customSort));

        for (int i = 0; i < conditionNodeList.size(); i++) {
            Row dataRow = sheet.createRow(rowIndex);

            String subScript = subScriptList.get(i);

            //獲取數據
            String logicScript = logicScriptMap.get("logicScript" + subScript);
            String textBody = textBodyMap.get("textBody" + subScript);

            // 填充单元格数据
            Cell cell0 = dataRow.createCell(0);
            cell0.setCellValue(subScript);
            cell0.setCellStyle(dataStyle);

            Cell cell1 = dataRow.createCell(1);
            cell1.setCellValue(logicScript);
            cell1.setCellStyle(dataStyle);

            Cell cell2 = dataRow.createCell(2);
            cell2.setCellValue(textBody);
            cell2.setCellStyle(dataStyle);

            Action action = actionMap.get("action" + subScript);
            MatrixAction matrixAction = matrixActionMap.get("matrixAction" + subScript);
            //判斷對象是否為空
            Optional<Action> optionalAction = Optional.ofNullable(action);
            Optional<MatrixAction> optionalMatrixAction = Optional.ofNullable(matrixAction);

            if (optionalMatrixAction.isPresent() || optionalAction.isPresent()) {

                optionalAction.ifPresent(actionElement -> {
                    if (actionElement.getActionType() != null || actionElement.getAmount() != null || actionElement.getCurrencyUnit() != null) {

                        String actionType = actionElement.getActionType();
                        String amount = actionElement.getAmount();
                        String currencyUnit = actionElement.getCurrencyUnit();
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

//                        Cell cell9 = dataRow.createCell(9);
//                        cell9.setCellValue("-");
//                        cell9.setCellStyle(dataStyle);
                    } else {
                        setValueToCell(dataRow, dataStyle);
                    }

                });

                optionalMatrixAction.ifPresent(matrixActionElement -> {
                    // 检查actionType、amount和currencyUnit是否为null
                    if (matrixActionElement.getActionType() != null || matrixActionElement.getBonusAmount() != null || matrixActionElement.getMeasurementUnit() != null && matrixActionElement.getFreeUnitType() != null || matrixActionElement.getMatrixId() != null || matrixActionElement.getDataElement() != null || matrixActionElement.getMatrixQuerySQL() != null) {
                        // 如果条件满足，执行相应的操作
                        //動作類型
                        String actionType = matrixActionElement.getActionType();

                        //display屬性 + 計量單位
                        String amount = matrixActionElement.getBonusAmount();
                        String measurementUnit = matrixActionElement.getMeasurementUnit();
                        String bonusAmount = amount + "  " + measurementUnit;
                        //freeUnitType
                        String freeUnitType = matrixActionElement.getFreeUnitType();

                        String matrixId = matrixActionElement.getMatrixId();
                        String dataElement = matrixActionElement.getDataElement();
                        //String matrixQuerySQL = matrixActionElement.getMatrixQuerySQL();

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

//                        Cell cell9 = dataRow.createCell(9);
//                        cell9.setCellValue(matrixQuerySQL);
//                        cell9.setCellStyle(dataStyle);

                        // 进行其他操作
                    } else {
                        setValueToCell(dataRow, dataStyle);
                    }
                });
            } else {
                setValueToCell(dataRow, dataStyle);
            }
            rowIndex++;

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
     * 不滿足矩陣和Action條件單元格填充默認數據
     *
     * @param dataRow
     * @param dataStyle
     */
    private static void setValueToCell(Row dataRow, CellStyle dataStyle) {
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

//        Cell cell9 = dataRow.createCell(9);
//        cell9.setCellValue("-");
//        cell9.setCellStyle(dataStyle);
    }


    /**
     * 獲取矩陣和節點關鍵信息
     *
     * @param jsonString
     * @param conditionNodePathList
     * @return
     */
    private static ConditionNode getMatrixNodeAndAction(String jsonString, List<String> conditionNodePathList) {
        ConditionNode conditionNode = new ConditionNode();
        //存儲matrixActionMap
        Map<String, MatrixAction> matrixActionMap = new HashMap<>();
        //存儲actionMap
        Map<String, Action> actionMap = new HashMap<>();
        //存儲沒必要的文件路經
        List<String> uselessFilePathList = new ArrayList<>();
        //uselessFilePathList.add(MultiNodePathConstants.BASIC_PATH + MultiNodePathConstants.OFFERING_ID + MultiNodePathConstants.RULE_TXT_FILE);
        //uselessFilePathList.add(MultiNodePathConstants.BASIC_PATH + MultiNodePathConstants.OFFERING_ID + MultiNodePathConstants.RULE_XML_FILE);
        //uselessFilePathList.add(MultiNodePathConstants.BASIC_PATH + MultiNodePathConstants.OFFERING_ID + MultiNodePathConstants.RULE_XML_FILER_FILE);
        //uselessFilePathList.add(MultiNodePathConstants.BASIC_PATH + MultiNodePathConstants.OFFERING_ID + MultiNodePathConstants.RULE_JSON_PATH);
        for (String conditionNodePath : conditionNodePathList) {
            //進行判斷是普通動作還是矩陣動作
            boolean matrixFlag = isMatrixPattern(jsonString, conditionNodePath + MatrixPatternPath.IS_MATRIX_PATTERN_PATH);
            if (matrixFlag) {
                MatrixAction matrixAction = getMatrixActionInfo(jsonString, conditionNodePath);
                String matrixId = matrixAction.getMatrixId();
                String matrixQuerySQL = matrixAction.getMatrixQuerySQL();


                Connection connection = null;
                String matrixTxtFilePath = MatrixPatternPath.BASIC_PATH + matrixId + MatrixPatternPath.MATRIX_TXT_FILE;

                try {
                    connection = JDBCUtils.getConnection();
                    List<String> matrixInsideInfoList = JDBCUtils.queryClobAsList(connection, matrixQuerySQL);
                    for (String matrixInsideInfo : matrixInsideInfoList) {
                        String2TxtFile(matrixInsideInfo, matrixTxtFilePath);
                        matrixAction = matrixTxtToJson(matrixTxtFilePath, matrixAction);
                    }


                    uselessFilePathList.add(MatrixPatternPath.BASIC_PATH + matrixId + MatrixPatternPath.MATRIX_TXT_FILE);
                    uselessFilePathList.add(MatrixPatternPath.BASIC_PATH + matrixId + MatrixPatternPath.MATRIX_XML_FILE);
                    uselessFilePathList.add(MatrixPatternPath.BASIC_PATH + matrixId + MatrixPatternPath.MATRIX_JSON_PATH);
                    conditionNode.setUselessPathList(uselessFilePathList);


                } catch (SQLException exception) {
                    System.out.println(exception.getMessage());
                } finally {
                    JDBCUtils.closeResource(connection);
                }

                String matrixActionKey = "";
                StringBuilder matrixKeyBuilder = new StringBuilder("matrixAction");
                matrixActionKey = processMapKey(conditionNodePath, matrixKeyBuilder);
                //存儲到Map
                matrixActionMap.put(matrixActionKey, matrixAction);

            } else {
                //判斷是否包含節點信息
                //獲取action關鍵信息
                Action action = getAction(jsonString, conditionNodePath);
                //存儲到Map
                String actionKey = "";
                StringBuilder matrixKeyBuilder = new StringBuilder("action");
                actionKey = processMapKey(conditionNodePath, matrixKeyBuilder);
                actionMap.put(actionKey, action);

            }
        }
        conditionNode.setActionMap(actionMap);
        conditionNode.setMatrixActionMap(matrixActionMap);
        return conditionNode;
    }


    /**
     * 獲取Action信息
     *
     * @param jsonString
     * @param conditionNode
     * @return
     */
    private static Action getAction(String jsonString, String conditionNode) {
        Action action = new Action();
        String actionType = "";
        //獲取actionType
        //<!-- "charge-discount-action" -->
        //獲取actionType
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append(conditionNode).append(MultiNodePathConstants.ACTION_TYPE_PATH);
        boolean contains = isContains(jsonString, queryBuilder.toString());
        if (contains) {
            actionType = JsonPath.read(jsonString, queryBuilder.toString()).toString();
            action.setActionType(actionType);
        }


        //<!-- integer = 0 -->
        queryBuilder.setLength(0);
        queryBuilder.append(conditionNode).append(MultiNodePathConstants.ACTION_BASIC_PATH).append("action[\"").append(actionType).append("\"]").append(MultiNodePathConstants.DISCOUNT_INTEGER_PATH);
        contains = isContains(jsonString, queryBuilder.toString());
        if (contains) {
            String amount = JsonPath.read(jsonString, queryBuilder.toString()).toString();
            action.setAmount(amount);
        }


        //<!-- currency-measurement = 501 -->
        queryBuilder.setLength(0);
        queryBuilder.append(conditionNode).append(MultiNodePathConstants.ACTION_BASIC_PATH).append("action[\"").append(actionType).append("\"]").append(MultiNodePathConstants.DISCOUNT_CURRENCY_MEASUREMENT_PATH);
        contains = isContains(jsonString, queryBuilder.toString());
        if (contains) {
            String currencyMeasurement = JsonPath.read(jsonString, queryBuilder.toString()).toString();
            //直接轉換為對應貨幣
            String currencyUnit = SysCurrencyMeasurementConstants.DATA_MAP.get(currencyMeasurement);
            action.setCurrencyUnit(currencyUnit);
        }


        return action;
    }

    /**
     * 從Offering的XMl裏獲取矩陣信息
     *
     * @param jsonString
     * @param conditionNode
     * @return
     */
    private static MatrixAction getMatrixActionInfo(String jsonString, String conditionNode) {

        MatrixAction matrixAction = new MatrixAction();


        //<!-- "matrix-id"="696989206252108387" -->
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append(conditionNode).append(MatrixPatternPath.MATRIX_ID_PATH);
        String matrixId = JsonPath.read(jsonString, queryBuilder.toString()).toString();
        matrixAction.setMatrixId(matrixId);

        StringBuilder matrixQueryBuilder = new StringBuilder();
        matrixQueryBuilder.append("SELECT SUB_PATTERN_ACTION FROM PE_MATRIX_DATA  WHERE MATRIX_ID =").append(matrixId);
        matrixAction.setMatrixQuerySQL(matrixQueryBuilder.toString());

        //ActionType free-unit-bonus-action
        queryBuilder.setLength(0);
        queryBuilder.append(conditionNode).append(MatrixPatternPath.MATRIX_ACTION_TYPE_PATH);
        String actionType = JsonPath.read(jsonString, queryBuilder.toString()).toString();
        matrixAction.setActionType(actionType);

        //dataElement <!-- data-element="C_CALLING_HOME_CC" -->
        queryBuilder.setLength(0);
        queryBuilder.append(conditionNode).append(MatrixPatternPath.MATRIX_DATA_ELEMENT);
        String dataElement = JsonPath.read(jsonString, queryBuilder.toString()).toString();
        matrixAction.setDataElement(dataElement);

        return matrixAction;
    }


    /**
     * matrixInfo內部信息
     * String字符串轉為Txt文件
     *
     * @param matrixInfo
     * @param matrixTxtFilePath
     */
    private static void String2TxtFile(String matrixInfo, String matrixTxtFilePath) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(matrixTxtFilePath))) {
            writer.write(matrixInfo);
            System.out.println("字符串已成功写入到文件。");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * 從華為數據庫中拿到Matrix的文件，需要在生成的文件中粘帖上去。
     *
     * @return
     */
    private static String chooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("TXT Files", "txt");
        fileChooser.setFileFilter(filter);

        // 设置初始目录，這是默認打開文件夾的位置，在SingleNodePathConstants裏面更改。
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
     * txt文檔轉為Json格式
     *
     * @param matrixFilePath
     * @param matrixAction
     * @return
     */
    private static MatrixAction matrixTxtToJson(String matrixFilePath, MatrixAction matrixAction) {
        String matrixId = matrixAction.getMatrixId();
        //根據MATRIX_ID獲取文件
        String matrixXmlPath = MatrixPatternPath.BASIC_PATH + matrixId + MatrixPatternPath.MATRIX_XML_FILE;
        String matrixJsonPath = MatrixPatternPath.BASIC_PATH + matrixId + MatrixPatternPath.MATRIX_JSON_PATH;

        try {
            //將文件轉為XML、JSON格式
            String context = readFileToString(matrixFilePath);

            String result = java.net.URLDecoder.decode(context, StandardCharsets.UTF_8.name());

            convertStringToXml(result, matrixXmlPath);

            convertXML2Json(matrixXmlPath, matrixJsonPath);

            //提取JSON關鍵信息
            matrixAction = extractMatrixJsonInfo(matrixJsonPath, matrixAction);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return matrixAction;
    }

    /**
     * 提取矩陣Json文件裏面的信息
     *
     * @param matrixJsonPath
     * @param matrixAction
     * @return
     */
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

        matrixAction = getMatrixActionInsideInfo(jsonString, matrixAction);

        return matrixAction;
    }


    /**
     * 提取數據庫查詢的Matrix信息
     * JSON["pattern-action-union"].action["free-unit-bonus-action"]["bonus-amount"].constant["constant-value"]["@attributes"]["display-value"]
     * JSON["pattern-action-union"].action["free-unit-bonus-action"]["display-measurement-id"].value
     *
     * @param jsonString
     * @param matrixAction
     * @return
     */
    private static MatrixAction getMatrixActionInsideInfo(String jsonString, MatrixAction matrixAction) {
        StringBuilder queryBuilder = new StringBuilder();
        String actionType = matrixAction.getActionType();
        queryBuilder.append(MatrixPatternPath.MATRIX_ACTION_BASIC_PATH).append("action[\"").append(actionType).append("\"]").append(MatrixPatternPath.FREE_UNIT_TYPE_ID_PATH);
        String freeUnitTypeID = JsonPath.read(jsonString, queryBuilder.toString()).toString();

        String freeUnitType = FreeUnitTypeConstants.DATA_MAP.get(freeUnitTypeID);

        queryBuilder.setLength(0);
        queryBuilder.append(MatrixPatternPath.MATRIX_ACTION_BASIC_PATH).append("action[\"").append(actionType).append("\"]").append(MatrixPatternPath.MATRIX_BONUS_AMOUNT_PATH);
        String matrixBonusAmount = JsonPath.read(jsonString, queryBuilder.toString()).toString();


        queryBuilder.setLength(0);
        queryBuilder.append(MatrixPatternPath.MATRIX_ACTION_BASIC_PATH).append("action[\"").append(actionType).append("\"]").append(MatrixPatternPath.MATRIX_MEASUREMENT_ID_PATH);
        String matrixMeasurementId = JsonPath.read(jsonString, queryBuilder.toString()).toString();
        String matrixMeasurement = SysMeasurementConstants.DATA_MAP.get(matrixMeasurementId);
        matrixAction.setFreeUnitType(freeUnitType);
        matrixAction.setBonusAmount(matrixBonusAmount);
        matrixAction.setMeasurementUnit(matrixMeasurement);

        return matrixAction;
    }

    /**
     * 判斷是否為矩陣節點
     *
     * @param jsonString
     * @param matrixJudgePath
     * @return
     */
    private static boolean isMatrixPattern(String jsonString, String matrixJudgePath) {

        try {
            Configuration configuration = Configuration.defaultConfiguration()
                    .addOptions(Option.SUPPRESS_EXCEPTIONS);
            Object document = configuration.jsonProvider().parse(jsonString);
            String chosenElement = JsonPath.using(configuration).parse(document).read(matrixJudgePath).toString();

//            String chosenElement = JsonPath.read(jsonString, matrixLinkPath).toString();

            if (chosenElement != null) {
                if (chosenElement.equals("matrix-selection-pattern")) {
                    return true;
                } else if (chosenElement.equals("condition-selection-pattern")) {
                    return false;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("路經不存在！");
            return false;
        }

    }

    private static Map<String, String> getTextBodyMap(String jsonString, List<String> conditionNodeList) {
        //1.定義Map存儲Script
        Map<String, String> textBodyMap = new HashMap<>();
        StringBuilder verifyPathBuilder = new StringBuilder();

        for (String conditionNode : conditionNodeList) {
            verifyPathBuilder.setLength(0);
            StringBuilder logicScriptPath = verifyPathBuilder.append(conditionNode).append(MultiNodePathConstants.TEXT_BODY_PATH);
            boolean contains = isContains(jsonString, logicScriptPath.toString());
            if (contains) {
                String queryPath = logicScriptPath.toString();
                String logicScript = JsonPath.read(jsonString, queryPath).toString();
                //获取坐标值：
                StringBuilder logicScriptKeyBuilder = new StringBuilder("textBody");
                String logicScriptKey = processMapKey(queryPath, logicScriptKeyBuilder);
                String logicScriptValue = logicScript;
                textBodyMap.put(logicScriptKey, logicScriptValue);

            }
        }

        return textBodyMap;
    }

    /**
     * 獲取LogicScript
     * 應該怎麼命名key?
     * logicScript0-0
     * logicScript0-0-1
     * logicScript0-1-1-2
     * logicScript0-0-1-0
     *
     * @param jsonString
     * @param conditionNodeList
     * @return
     */
    private static Map<String, String> getLogicScriptMap(String jsonString, List<String> conditionNodeList) {
        //1.定義Map存儲Script
        Map<String, String> logicScriptMap = new HashMap<>();
        StringBuilder verifyPathBuilder = new StringBuilder();

        for (String conditionNode : conditionNodeList) {
            verifyPathBuilder.setLength(0);
            StringBuilder logicScriptPath = verifyPathBuilder.append(conditionNode).append(MultiNodePathConstants.LOGIC_SCRIPT_PATH);
            boolean contains = isContains(jsonString, logicScriptPath.toString());
            if (contains) {
                String queryPath = logicScriptPath.toString();
                String logicScript = JsonPath.read(jsonString, queryPath).toString();
                //获取坐标值：
                StringBuilder logicScriptKeyBuilder = new StringBuilder("logicScript");
                String logicScriptKey = processMapKey(queryPath, logicScriptKeyBuilder);
                String logicScriptValue = logicScript;
                logicScriptMap.put(logicScriptKey, logicScriptValue);

            }
        }

        return logicScriptMap;
    }


    /**
     * 根據查詢路經去獲取key名稱
     *
     * @param queryPath
     * @param keyBuilder
     * @return
     */
    private static String processMapKey(String queryPath, StringBuilder keyBuilder) {
        List<Integer> indicesList = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\[(\\d+)\\]");
        Matcher matcher = pattern.matcher(queryPath);

        while (matcher.find()) {
            int index = Integer.parseInt(matcher.group(1));
            indicesList.add(index);
        }
        if (indicesList != null && !indicesList.isEmpty()) {
            String indicesString = String.join("-", indicesList.stream().map(String::valueOf).toArray(String[]::new));
            keyBuilder.append(indicesString);
        } else {
            keyBuilder.append("0");
        }

        return keyBuilder.toString();
    }

    /**
     * 根據指定順序排序
     *
     * @param str
     * @return
     */
    public static String customSort(String str) {
        // 按照指定规则进行排序
        String[] parts = str.split("-");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            sb.append(String.format("%03d", Integer.parseInt(part)));
        }
        return sb.toString();
    }


    /**
     * 獲取路經坐標值，用於命名。
     *
     * @param input
     * @return
     */
    private static List<Integer> extractIndices(String input) {
        List<Integer> indices = new ArrayList<>();

        Pattern pattern = Pattern.compile("\\[(\\d+)\\]");
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            int index = Integer.parseInt(matcher.group(1));
            indices.add(index);
        }

        return indices;
    }

    /**
     * 通過生成路經來獲取conditionNodeList路徑
     *
     * @param jsonString
     * @return
     */
    private static List<String> getConditionNodeList(String jsonString) {
        List<String> conditionNodeList = new ArrayList<>();
        int[][][][] array = new int[4][4][4][4];
        List<String> nodeVerifyPathList = generateVerifiedPaths(MultiNodesConstants.RECURSIVE_ARRAY);

        conditionNodeList = verifyPathToGetNodeList(jsonString, nodeVerifyPathList);


        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(MultiNodePathConstants.BASIC_PATH + MultiNodePathConstants.OFFERING_ID + "verifiedPaths.txt"));
            for (String conditionNode : nodeVerifyPathList) {
                writer.write(conditionNode + "\n");
            }
            writer.close();
            System.out.println("已成功将字符串输出到txt文件。");
        } catch (IOException e) {
            System.out.println("写入文件时发生错误：" + e.getMessage());
        }
        System.out.println(conditionNodeList);

        return conditionNodeList;
    }

    /**
     * 驗證生成的路經是否包含json樹中，如果包含則加入到nodeList中。
     *
     * @param jsonString
     * @param nodeVerifyPathList
     * @return
     */
    private static List<String> verifyPathToGetNodeList(String jsonString, List<String> nodeVerifyPathList) {
        List<String> conditionNodeList = new ArrayList<>();
        for (String verifiedPath : nodeVerifyPathList) {
            boolean contains = isContains(jsonString, verifiedPath);
            if (contains) {
                conditionNodeList.add(verifiedPath);
            }
        }

        return conditionNodeList;
    }


    /**
     * 傳入四維數組，
     * int[][][][] recursiveArray = new int[2][3][4][0]; // 四维数组，第一维长度为2，第二维长度为3，第三维长度为4，第四維长度为0
     * 0代表沒有分支，必須傳入0不可空格，會拋出異常
     * 按照這樣的進行表達分支數目
     *
     * @param recursiveArray
     * @return
     */
    private static List<String> generateVerifiedPaths(int[][][][] recursiveArray) {


        //["pattern-action-union"][0]
        List<String> verifiedTotalPathsList = new ArrayList<>();
        List<String> verifiedPathsList = new ArrayList<>();
        List<String> verifiedPathsList1 = new ArrayList<>();
        List<String> verifiedPathsList2 = new ArrayList<>();
        List<String> verifiedPathsList3 = new ArrayList<>();
        List<String> verifiedPathsList4 = new ArrayList<>();

        int dim1Length = MultiNodesConstants.RECURSIVE_ARRAY.length;  // 第一维度的长度
        int dim2Length = MultiNodesConstants.RECURSIVE_ARRAY[0].length;  // 第二维度的长度
        int dim3Length = MultiNodesConstants.RECURSIVE_ARRAY[0][0].length;  // 第三维度的长度
        int dim4Length = MultiNodesConstants.RECURSIVE_ARRAY[0][0][0].length;  // 第四维度的长度

        String addPath = MultiNodePathConstants.BASIC_NODE_PATH_MID;
        StringBuilder verifyPathBuilder = new StringBuilder();
        for (int i = 0; i < dim1Length; i++) {
            Integer startIndex = verifyPathBuilder.length(); // 记录循环前的 pathBuilder 长度
            String verifyPath = verifyPathBuilder.append(MultiNodePathConstants.BASIC_NODE_PATH_START).append("[").append(i).append("]").toString();
            verifiedPathsList.add(verifyPath);
            if (i < dim1Length - 1) {
                verifyPathBuilder.delete(startIndex, verifyPathBuilder.length());
            }
        }


        for (int i = 0; i < verifiedPathsList.size(); i++) {

            String frontList = verifiedPathsList.get(i);
            for (int j = 0; j < dim2Length; j++) {
                verifyPathBuilder.setLength(0);
                String verifyPath = verifyPathBuilder.append(frontList).append(addPath).append("[").append(j).append("]").toString();
                verifiedPathsList1.add(verifyPath);
            }
        }

        for (int i = 0; i < verifiedPathsList1.size(); i++) {

            String frontList = verifiedPathsList1.get(i);

            for (int j = 0; j < dim3Length; j++) {
                verifyPathBuilder.setLength(0);
                String addPathString = verifyPathBuilder.append(frontList).append(addPath).append("[").append(j).append("]").toString();
                verifiedPathsList2.add(addPathString);
            }
        }


        for (int i = 0; i < verifiedPathsList2.size(); i++) {

            String frontList = verifiedPathsList2.get(i);
            for (int j = 0; j < dim4Length; j++) {
                verifyPathBuilder.setLength(0);
                String addPathString = verifyPathBuilder.append(frontList).append(addPath).append("[").append(j).append("]").toString();
                verifiedPathsList3.add(addPathString);
            }
        }

        int size = verifiedPathsList.size();
        System.out.println(size);
        verifiedTotalPathsList.addAll(verifiedPathsList);
        verifiedTotalPathsList.addAll(verifiedPathsList1);
        verifiedTotalPathsList.addAll(verifiedPathsList2);
        verifiedTotalPathsList.addAll(verifiedPathsList3);
        verifiedTotalPathsList.addAll(verifiedPathsList4);
        return verifiedTotalPathsList;
    }


    /**
     * 判斷是否包含路經
     *
     * @param jsonString
     * @param verifyPath
     * @return
     */
    private static boolean isContains(String jsonString, String verifyPath) {
        try {
            Configuration configuration = Configuration.defaultConfiguration()
                    .addOptions(Option.SUPPRESS_EXCEPTIONS);
            Object document = configuration.jsonProvider().parse(jsonString);
            Object result = JsonPath.using(configuration).parse(document).read(verifyPath);

            if (result != null) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 把文件轉為Json字符串
     *
     * @param jsonReadPath
     * @return
     */
    public static String fileToJsonString(String jsonReadPath) {
        // 创建一个File类型的对象，指向传入的路径
        File inputFile = new File(jsonReadPath);

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

        return jsonString;
    }

}

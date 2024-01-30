package com.chinamobile.test;

import com.alibaba.fastjson.JSON;
import com.chinamobile.constant.MultiNodePathConstants;
import com.chinamobile.constant.MultiNodesConstants;
import com.chinamobile.tools.JDBCUtils;
import org.json.JSONObject;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


import static com.chinamobile.tools.XMLtoJSON.convertElementToJson;

public class JSONExample {


    @Test
    public void selectMatrixById() throws IOException {

        String fileName = "/sql/singleOfferingQuery.sql";
        //String filePath = Test.class.getResource("/sql/singleOfferingQuery.sql").getFile();
        InputStream in = this.getClass().getResourceAsStream(fileName);
        String querySQL = getFileContent(in);

        String queryOfferingSQL = String.format(querySQL, Integer.parseInt(MultiNodePathConstants.OFFERING_ID));

        List<String> queryCLOBList = queryCLOB(queryOfferingSQL);
        if(queryCLOBList.size() > 1 || queryCLOBList.size() ==0){
            queryOfferingSQL = queryOfferingSQL + "\n AND b.plan_name LIKE 'Rental%'";
            queryCLOBList = queryCLOB(queryOfferingSQL);
        }

        String queryOfferingString = queryCLOBList.get(0);
        String jsonString = transferToJsonString(queryOfferingString);
        System.out.println(jsonString);
    }


    @Test
    public void generatePathsTest(){
        //.append(MultiNodesConstants.NODES_PATH)
        generatePaths();
    }

    private static List<String> generateVerifiedPaths(int[][][][] recursiveArray) {


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

        //["sub-pattern-and-action"]["pattern-action-union"]
        String addPath = MultiNodesConstants.BASIC_NODE_PATH_MID;
        StringBuilder verifyPathBuilder = new StringBuilder();

        //$.["pattern-action-union"][0]
        // .pattern["condition-selection-pattern"]["condition-node"][0]
        // ["sub-pattern-and-action"]["pattern-action-union"][0]

        for (int i = 0; i < dim1Length; i++) {
            Integer startIndex = verifyPathBuilder.length(); // 记录循环前的 pathBuilder 长度
            String verifyPath = verifyPathBuilder.append(MultiNodesConstants.BASIC_NODE_PATH_START).append("[").append(i).append("]").append(MultiNodesConstants.NODES_PATH).toString();
            verifiedPathsList.add(verifyPath);
            if (i < dim1Length - 1) {
                verifyPathBuilder.delete(startIndex, verifyPathBuilder.length());
            }
        }

        for (int i = 0; i < verifiedPathsList.size(); i++) {
            String frontList = verifiedPathsList.get(i);
            for (int j = 0; j < dim1Length; j++) {
                verifyPathBuilder.setLength(0);
                String verifyPath = verifyPathBuilder.append(frontList).append("[").append(j).append("]").toString();
                verifiedPathsList1.add(verifyPath);
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

    private static List<String> generatePaths() {

        List<String> verifiedTotalPathsList = new ArrayList<>();
        //patter[0]
        List<String> verifiedSPList = new ArrayList<>();
        List<String> verifiedMPList = new ArrayList<>();
        List<String> verifiedNList = new ArrayList<>();

        StringBuilder verifyPathBuilder = new StringBuilder();

        for (int i = 0; i < 3; i++) {
            Integer startIndex = verifyPathBuilder.length(); // 记录循环前的 pathBuilder 长度
            String verifyPath = verifyPathBuilder.append(MultiNodesConstants.BASIC_NODE_PATH_START).append("[").append(i).append("]").toString();
            verifiedSPList.add(verifyPath);
            if (i < 3 - 1) {
                verifyPathBuilder.delete(startIndex, verifyPathBuilder.length());
            }
        }

        verifyPathBuilder.setLength(0);
        for (int i = 0; i < 3; i++) {
            Integer startIndex = verifyPathBuilder.length(); // 记录循环前的 pathBuilder 长度
            String verifyPath = verifyPathBuilder.append(MultiNodesConstants.BASIC_NODE_PATH_MID).append("[").append(i).append("]").toString();
            verifiedMPList.add(verifyPath);
            if (i < 3 - 1) {
                verifyPathBuilder.delete(startIndex, verifyPathBuilder.length());
            }
        }

        verifyPathBuilder.setLength(0);
        for (int i = 0; i < 4; i++) {
            Integer startIndex = verifyPathBuilder.length(); // 记录循环前的 pathBuilder 长度
            String verifyPath = verifyPathBuilder.append(MultiNodesConstants.NODES_PATH).append("[").append(i).append("]").toString();
            verifiedNList.add(verifyPath);
            if (i < 4 - 1) {
                verifyPathBuilder.delete(startIndex, verifyPathBuilder.length());
            }
        }

        //開頭路經
        List<String> verifiedPathsList = new ArrayList<>();

        //中間路經
        List<String> verifiedPathsList1 = new ArrayList<>();


        //拼接
        List<String> verifiedPathsList2 = new ArrayList<>();

        //拼接1
        List<String> verifiedPathsList3 = new ArrayList<>();

        //拼接....
        //List<String> verifiedPathsList4 = new ArrayList<>();


        for (int i = 0; i <verifiedSPList.size(); i++) {
            String startPath = verifiedSPList.get(i);
            for (int j = 0; j < verifiedNList.size(); j++) {
                String mpPath = verifiedNList.get(j);
                verifiedPathsList.add(startPath + mpPath);
            }
        }


        for (int i = 0; i <verifiedMPList.size(); i++) {
            String midPath = verifiedMPList.get(i);
            for (int j = 0; j < verifiedNList.size(); j++) {
                String mpPath = verifiedNList.get(j);
                verifiedPathsList1.add(midPath + mpPath);
            }
        }

        for (String start : verifiedPathsList) {
            for (String mid : verifiedPathsList1) {
                verifiedPathsList2.add(start + mid);
            }

        }

        for (String start : verifiedPathsList2) {
            for (String mid : verifiedPathsList1) {
                verifiedPathsList3.add(start + mid);
            }
        }



        verifiedTotalPathsList.addAll(verifiedPathsList);
        verifiedTotalPathsList.addAll(verifiedPathsList2);
        verifiedTotalPathsList.addAll(verifiedPathsList3);


        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(MultiNodePathConstants.BASIC_PATH + MultiNodePathConstants.OFFERING_ID + "verifiedPaths.txt"));
            for (String conditionNode : verifiedTotalPathsList) {
                writer.write(conditionNode + "\n");
            }
            writer.close();
            System.out.println("已成功将字符串输出到txt文件。");
        } catch (IOException e) {
            System.out.println("写入文件时发生错误：" + e.getMessage());
        }

        return verifiedTotalPathsList;
    }

    /**
     * 根据文件路径读取文件内容
     *
     * @param fileInPath
     * @return
     * @throws IOException
     */
    public static String getFileContent(Object fileInPath) throws IOException {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder(); // 使用 StringBuilder 连接每行内容
        if (fileInPath == null) {
            return null;
        }
        if (fileInPath instanceof String) {
            br = new BufferedReader(new FileReader(new File((String) fileInPath)));
        } else if (fileInPath instanceof InputStream) {
            br = new BufferedReader(new InputStreamReader((InputStream) fileInPath));
        }
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n"); // 将每行内容连接到 StringBuilder 中
        }
        br.close();
        return sb.toString(); // 返回完整的字符串内容
    }


    @Test
    public void transTest(){
        //String json = transferTest();
        //System.out.println(json);
    }

    @Test
    public void transferTest(){
        String jsonString = "";
        String queryOfferingSQL = "\tSELECT\n" +
                "\ta.OFFERING_ID ,\n" +
                "\te.PLAN_POLICY_RULE,\n" +
                "\tb.PLAN_ID pe_plan_id,\n" +
                "\tb.PLAN_NAME ,\n" +
                "\tc.PLAN_ID PE_PLAN_VERSION_plan_id,\n" +
                "\tc.EXP_DATE ,\n" +
                "\tc.PLAN_VERSION_ID PE_PLAN_VERSION_ID,\n" +
                "\td.PLAN_VERSION_ID Pe_Plan_Policy_PLAN_VERSION_ID,\n" +
                "\td.PLAN_POLICY_ID Pe_Plan_Policy_id,\n" +
                "\te.PLAN_POLICY_ID pe_pp_rule_Policy_id\n" +
                "FROM\n" +
                "\tpm_plan_relation a,\n" +
                "\tpe_plan b,\n" +
                "\tPE_PLAN_VERSION c,\n" +
                "\tPe_Plan_Policy d,\n" +
                "\tpe_pp_rule e\n" +
                "WHERE\n" +
                "\ta.offering_id = " + MultiNodePathConstants.OFFERING_ID +
                "\tAND a.plan_type = 'P'\n" +
                "\tAND a.plan_id = b.plan_id\n" +
                "\tAND b.plan_id = c.plan_id\n" +
                "\tAND c.exp_date > SYSDATE\n" +
                "\tAND c.plan_version_id = d.plan_version_id\n" +
                "\tAND d.plan_policy_id = e.plan_policy_id\n" +
                "\tAND b.plan_name LIKE 'Rental%'";
        List<String> queryCLOBList = queryCLOB(queryOfferingSQL);
        if(queryCLOBList.size() == 0){
            queryOfferingSQL = "\tSELECT\n" +
                    "\ta.OFFERING_ID ,\n" +
                    "\te.PLAN_POLICY_RULE,\n" +
                    "\tb.PLAN_ID pe_plan_id,\n" +
                    "\tb.PLAN_NAME ,\n" +
                    "\tc.PLAN_ID PE_PLAN_VERSION_plan_id,\n" +
                    "\tc.EXP_DATE ,\n" +
                    "\tc.PLAN_VERSION_ID PE_PLAN_VERSION_ID,\n" +
                    "\td.PLAN_VERSION_ID Pe_Plan_Policy_PLAN_VERSION_ID,\n" +
                    "\td.PLAN_POLICY_ID Pe_Plan_Policy_id,\n" +
                    "\te.PLAN_POLICY_ID pe_pp_rule_Policy_id\n" +
                    "FROM\n" +
                    "\tpm_plan_relation a,\n" +
                    "\tpe_plan b,\n" +
                    "\tPE_PLAN_VERSION c,\n" +
                    "\tPe_Plan_Policy d,\n" +
                    "\tpe_pp_rule e\n" +
                    "WHERE\n" +
                    "\ta.offering_id = " + MultiNodePathConstants.OFFERING_ID +
                    "\tAND a.plan_type = 'P'\n" +
                    "\tAND a.plan_id = b.plan_id\n" +
                    "\tAND b.plan_id = c.plan_id\n" +
                    "\tAND c.exp_date > SYSDATE\n" +
                    "\tAND c.plan_version_id = d.plan_version_id\n" +
                    "\tAND d.plan_policy_id = e.plan_policy_id";
            queryCLOBList = queryCLOB(queryOfferingSQL);
        }

        if(queryCLOBList != null && queryCLOBList.size() != 0){
            String queryOfferingString = queryCLOBList.get(0);
            jsonString = transferToJsonString(queryOfferingString);
            System.out.println(jsonString);
        }
        System.out.println(jsonString);
    }



    public static List<String> queryCLOB(String querySQL){

        Connection connection = null;
        List<String> clobList = new ArrayList<>();
        try{
            connection = JDBCUtils.getConnection();
            clobList = JDBCUtils.queryClobAsList(connection, querySQL);

        }catch (SQLException exception){
            exception.getMessage();
        }

        return clobList;
    }

    public static String transferToJsonString(String xmlString){
        try{
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // 解析XML字符串
            Document document = builder.parse(new InputSource(new StringReader(xmlString)));

            // 将XML文件进行美化格式
            document.getDocumentElement().normalize();
            //String xmlPrettyString = prettyPrint(document.getDocumentElement());
            Element rootElement = document.getDocumentElement();
            JSONObject json = new JSONObject();
            convertElementToJson(rootElement, json);
            System.out.println(json.toString(4));
            //json.toString(4);

            // 将Object类型的对象转换为JSON格式的字符串，并赋值给jsonString变量
            String jsonString = JSON.toJSONString(json);
            return json.toString(4);

        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        return "文件未能解析，請聯繫管理員！";
    }



//    private static List<Integer> extractIndices(String input) {
//        List<Integer> indices = new ArrayList<>();
//
//        Pattern pattern = Pattern.compile("\\[(\\d+)\\]");
//        Matcher matcher = pattern.matcher(input);
//
//        while (matcher.find()) {
//            int index = Integer.parseInt(matcher.group(1));
//            indices.add(index);
//        }
//
//        return indices;
//    }
//
//
//    private static List<String> generatePaths(int[][][][] recursiveArray){
//
//        List<String> verifiedTotalPathsList = new ArrayList<>();
//        List<String> verifiedPathsList = new ArrayList<>();
//        List<String> verifiedPathsList1 = new ArrayList<>();
//        List<String> verifiedPathsList2 = new ArrayList<>();
//        List<String> verifiedPathsList3 = new ArrayList<>();
//        List<String> verifiedPathsList4 = new ArrayList<>();
//
//        String addPath = MultiNodePathConstants.BASIC_NODE_PATH_MID;
//        StringBuilder verifyPathBuilder = new StringBuilder();
//        for (int i = 0; i < recursiveArray.length; i++) {
//            Integer startIndex = verifyPathBuilder.length(); // 记录循环前的 pathBuilder 长度
//            String verifyPath = verifyPathBuilder.append(MultiNodePathConstants.BASIC_NODE_PATH_START).append("[").append(i).append("]").toString();
//            verifiedPathsList.add(verifyPath);
//            if (i < recursiveArray.length - 1) {
//                verifyPathBuilder.delete(startIndex, verifyPathBuilder.length());
//            }
//        }
//        for (int i = 0; i < recursiveArray.length; i++) {
//            String frontList = verifiedPathsList.get(i);
//            verifyPathBuilder.setLength(0);
//            for (int j = 0; j < recursiveArray[i].length; j++) {
//                Integer startIndex = verifyPathBuilder.length(); // 记录循环前的 pathBuilder 长度
//                String addPathString = verifyPathBuilder.append(frontList).append(addPath).append("[").append(j).append("]").toString();
//                verifiedPathsList1.add(addPathString);
//                if (i < recursiveArray[i].length - 1) {
//                    verifyPathBuilder.delete(startIndex, verifyPathBuilder.length());
//                }
//
//            }
//        }
//        for (int i = 0; i < recursiveArray.length; i++) {
//            for (int j = 0; j < recursiveArray[i].length; j++) {
//                String frontList = verifiedPathsList1.get(j);
//                verifyPathBuilder.setLength(0);
//                for (int k = 0; k < recursiveArray[i][j].length; k++) {
//                    Integer startIndex = verifyPathBuilder.length(); // 记录循环前的 pathBuilder 长度
//                    String addPathString = verifyPathBuilder.append(frontList).append(addPath).append("[").append(k).append("]").toString();
//                    verifiedPathsList2.add(addPathString);
//                    if (i < recursiveArray[i][j].length - 1) {
//                        verifyPathBuilder.delete(startIndex, verifyPathBuilder.length());
//                    }
//                    System.out.println("[" + i + "][" + j + "][" + k + "]");
//                }
//            }
//        }
//
//        for (int i = 0; i < recursiveArray.length; i++) {
//            for (int j = 0; j < recursiveArray[i].length; j++) {
//                for (int k = 0; k < recursiveArray[i][j].length; k++) {
//                    String frontList = verifiedPathsList2.get(k);
//                    verifyPathBuilder.setLength(0);
//                    for (int l = 0; l < recursiveArray[i][j][k].length; l++) {
//                        Integer startIndex = verifyPathBuilder.length(); // 记录循环前的 pathBuilder 长度
//                        String addPathString = verifyPathBuilder.append(frontList).append(addPath).append("[").append(l).append("]").toString();
//                        verifiedPathsList3.add(addPathString);
//                        if (i < recursiveArray[i][j][k].length - 1) {
//                            verifyPathBuilder.delete(startIndex, verifyPathBuilder.length());
//                        }
//                        System.out.println("[" + i + "][" + j + "][" + k + "][" + l + "]");
//
//                    }
//                }
//            }
//        }
//
//        for (int i = 0; i < recursiveArray.length; i++) {
//            for (int j = 0; j < recursiveArray[i].length; j++) {
//                for (int k = 0; k < recursiveArray[i][j].length; k++) {
//                    String frontList = verifiedPathsList2.get(k);
//                    verifyPathBuilder.setLength(0);
//                    for (int l = 0; l < recursiveArray[i][j][k].length; l++) {
//                        verifiedPathsList3.get(l);
//                        for (int m = 0; m < recursiveArray[i][j][k][l]; m++) {
//                            Integer startIndex = verifyPathBuilder.length(); // 记录循环前的 pathBuilder 长度
//                            String addPathString = verifyPathBuilder.append(frontList).append(addPath).append("[").append(m).append("]").toString();
//                            verifiedPathsList.add(addPathString);
//                            verifiedPathsList4.add(addPathString);
//                            if (i < recursiveArray[i][j][k][l] - 1) {
//                                verifyPathBuilder.delete(startIndex, verifyPathBuilder.length());
//                            }
//                            System.out.println("[" + i + "][" + j + "][" + k + "][" + l + "][" + m + "]");
//                        }
//                    }
//                }
//            }
//        }
//
//        int size = verifiedPathsList.size();
//        System.out.println(size);
//        verifiedTotalPathsList.addAll(verifiedPathsList);
//        verifiedTotalPathsList.addAll(verifiedPathsList1);
//        verifiedTotalPathsList.addAll(verifiedPathsList2);
//        verifiedTotalPathsList.addAll(verifiedPathsList3);
//        verifiedTotalPathsList.addAll(verifiedPathsList4);
//        return verifiedTotalPathsList;
//    }


}

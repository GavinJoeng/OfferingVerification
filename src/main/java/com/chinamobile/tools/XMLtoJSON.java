package com.chinamobile.tools;

import com.chinamobile.constant.MultiNodePathConstants;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;

/**
 * @description: 用於將XML文件轉為JSON文件
 * @author: gavin yang
 * @email: gavinyang@hk.chinamobile.com
 * @date: 2024/1/12 17:28
 */
public class XMLtoJSON {




    public static void main(String[] args) {

        String xmlFilePath = MultiNodePathConstants.BASIC_PATH + MultiNodePathConstants.OFFERING_ID + MultiNodePathConstants.RULE_XML_FILE;
        String jsonFilePath = MultiNodePathConstants.BASIC_PATH + MultiNodePathConstants.OFFERING_ID + MultiNodePathConstants.RULE_JSON_PATH;
        convertXML2Json(xmlFilePath,jsonFilePath);

    }


    /**
     * 將XML文件讀取調用convertElementToJson來遍歷XML文件，並且輸出JSON文件
     * @param xmlFilePath
     * @param jsonFilePath
     */
    public static void convertXML2Json(String xmlFilePath, String jsonFilePath){
        try {


            File inputFile = new File(xmlFilePath);

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            Element rootElement = doc.getDocumentElement();
            JSONObject json = new JSONObject();
            convertElementToJson(rootElement, json);

            System.out.println(json.toString(4));
            FileWriter fileWriter = new FileWriter(jsonFilePath);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write(json.toString(4));

            bufferedWriter.close();
            fileWriter.close();

            System.out.println("JSON object has been written to output.json");


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * 將XML的Element元素進行遍歷轉為JSON格式
     * @param element
     * @param json
     */
    public static void convertElementToJson(Element element, JSONObject json) {

        NamedNodeMap attributes = element.getAttributes();
        if (attributes.getLength() > 0) {
            JSONObject attributeJson = new JSONObject();
            for (int i = 0; i < attributes.getLength(); i++) {
                Node attribute = attributes.item(i);
                attributeJson.put(attribute.getNodeName(), attribute.getNodeValue());
            }
            json.put("@attributes", attributeJson);
        }

        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) node;
                String tagName = childElement.getTagName();
                if (tagName.equals("code")) {
                    String codeValue = childElement.getTextContent();
                    if (!codeValue.isEmpty()) {
                        //String processedCode = processCode(codeValue);
                        //json.put(tagName, processedCode);
                        System.out.println("code標籤進行忽略！");
                    }
                } else if (tagName.equals("text-body")) {
                    String textContent = node.getFirstChild().getTextContent();
                    if (!textContent.isEmpty()) {
                        json.put(tagName, textContent);
                    }
                }else {
                    JSONObject childJson = new JSONObject();
                    if (tagName.equals("condition-node")) {
                        // 将同级的 <condition-node> 标签转换为数组
                        JSONArray conditionNodesArray = json.optJSONArray(tagName);
                        if (conditionNodesArray == null) {
                            conditionNodesArray = new JSONArray();
                            json.put(tagName, conditionNodesArray);
                        }
                        convertElementToJson(childElement, childJson);
                        conditionNodesArray.put(childJson);
                    } else if (tagName.equals("pattern-action-union")) {
                        // 将同级的 <pattern-action-union> 标签转换为数组
                        JSONArray conditionNodesArray = json.optJSONArray(tagName);
                        if (conditionNodesArray == null) {
                            conditionNodesArray = new JSONArray();
                            json.put(tagName, conditionNodesArray);
                        }
                        convertElementToJson(childElement, childJson);
                        conditionNodesArray.put(childJson);
                    } else {
                        convertElementToJson(childElement, childJson);
                        json.put(tagName, childJson);
                    }
                }
            } else if (node.getNodeType() == Node.TEXT_NODE) {
                String textContent = node.getNodeValue().trim();
                if (!textContent.isEmpty()) {
                    json.put("value", textContent);
                }
            } else {
                String textContent = node.getNodeValue().trim();
                if (!textContent.isEmpty()) {
                    json.put("value", textContent);
                }
            }
        }
    }

}

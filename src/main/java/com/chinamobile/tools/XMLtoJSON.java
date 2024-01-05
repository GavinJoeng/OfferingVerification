package com.chinamobile.tools;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;

public class XMLtoJSON {


    private static String basicPath = "C:\\Users\\P7587\\Desktop\\testFile\\";
    private static String readXMLFile = "";

    private static String outputJsonFile = "";

    public static void main(String[] args) {
        try {

            readXMLFile = "51001800_PLAN_POLICY_RULE.xml";
            outputJsonFile = "51001800_PLAN_POLICY_RULE.json";
            File inputFile = new File(basicPath + readXMLFile);

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            Element rootElement = doc.getDocumentElement();
            JSONObject json = new JSONObject();
            convertElementToJson(rootElement, json);

            System.out.println(json.toString(4));
            FileWriter fileWriter = new FileWriter(basicPath + outputJsonFile);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write(json.toString(4));

            bufferedWriter.close();
            fileWriter.close();

            System.out.println("JSON object has been written to output.json");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void convertElementToJson(Element element, JSONObject json) {
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
                        String processedCode = processCode(codeValue);
                        //json.put(tagName, processedCode);
                    }
                } else if (tagName.equals("text-body")) {
                    String textContent = getTextContent(childElement);
                    if (!textContent.isEmpty()) {
                        json.put(tagName, textContent);
                    }
                } else {
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

    private static String getTextContent(Element element) {
        NodeList childNodes = element.getChildNodes();
        StringBuilder textContent = new StringBuilder();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node.getNodeType() == Node.TEXT_NODE) {
                textContent.append(node.getNodeValue());
            }
        }
        return textContent.toString().trim();
    }

    private static String processCode(String code) {
        // 进行代码处理，并返回处理后的结果
        return code;
    }
}

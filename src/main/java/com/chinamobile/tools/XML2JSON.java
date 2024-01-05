package com.chinamobile.tools;

import org.json.JSONObject;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class XML2JSON {

    public static void main(String[] args) {
        try {
            File inputFile = new File("C:\\Users\\P7587\\Desktop\\testFile\\51001800_PLAN_POLICY_RULE.txt");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            Element rootElement = doc.getDocumentElement();
            JSONObject json = new JSONObject();
            convertElementToJson(rootElement, json);

            System.out.println(json.toString(4));
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
            json.put("attributes", attributeJson);
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
                    String textContent = childElement.getTextContent();
                    if (!textContent.isEmpty()) {
                        json.put(tagName, textContent);
                    }
                } else {
                    JSONObject childJson = new JSONObject();
                    convertElementToJson(childElement, childJson);
                    json.put(tagName, childJson);
                }
            } else if (node.getNodeType() == Node.TEXT_NODE) {
                String textContent = node.getNodeValue().trim();
                if (!textContent.isEmpty()) {
                    json.put("value", textContent);
                }
            }
        }
    }

    private static String processCode(String code) {
        // 进行代码处理，并返回处理后的结果
        return code;
    }
}

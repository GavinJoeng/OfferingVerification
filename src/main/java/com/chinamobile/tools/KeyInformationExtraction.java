package com.chinamobile.tools;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @description: 用於提取xml關鍵信息，並且轉為表格
 * @author: gavin yang
 * @email: gavinyang@hk.chinamobile.com
 * @date: 2024/1/4 10:37
 */
public class KeyInformationExtraction {

    private static String originFile;

    public static void main(String[] args) {
        try {
            // 创建DOM解析器工厂
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            originFile = "C:\\Users\\P7587\\Desktop\\testFile\\51001800_PLAN_POLICY_RULE_FILTER.xml";
            // 解析XML文件
            File file = new File(originFile);
            Document document = builder.parse(file);

            // 获取根元素
            Element rootElement = document.getDocumentElement();

            // 调用递归方法进行解析和输出
            parseElement(rootElement, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void parseElement(Element element, int level) {
        // 打印当前层级的标签名称
        String tagName = element.getTagName();
        System.out.println("第 " + level + " 层：" + tagName);

        // 打印当前标签的属性
        NamedNodeMap attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attribute = attributes.item(i);
            String attributeName = attribute.getNodeName();
            String attributeValue = attribute.getNodeValue();
            System.out.println("属性：" + attributeName + "，值：" + attributeValue);
        }

        // 如果是<code>标签，则忽略
        if (tagName.equals("code")) {
            return;
        }

        // 递归处理子元素
        NodeList childNodes = element.getChildNodes();
        List<Element> childElements = new ArrayList<>();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) childNode;
                childElements.add(childElement);
            }
        }
        childElements.sort(Comparator.comparing(Element::getTagName)); // 按照标签名称排序

        for (Element childElement : childElements) {
            parseElement(childElement, level + 1);
        }

        // 输出文字信息
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            if (childNode.getNodeType() == Node.TEXT_NODE) {
                String nodeValue = childNode.getNodeValue().trim();
                if (!nodeValue.isEmpty()) {
                    System.out.println("文字信息：" + nodeValue);
                }
            }
        }


        // 递归处理子元素
        /*NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) childNode;
                // 忽略<code>标签
                if (childElement.getTagName().equals("code")) {
                    continue;
                }

                parseElement(childElement, level + 1);
            }else if (childNode.getNodeType() == Node.TEXT_NODE) {
                String nodeValue = childNode.getNodeValue().trim();
                if (!nodeValue.isEmpty()) {
                    System.out.println("文字信息：" + nodeValue);
                }
            } else {
                String nodeValue = childNode.getNodeValue().trim();
                if (!nodeValue.isEmpty()) {
                    System.out.println("文字信息：" + nodeValue);
                }
            }
        }*/

    }


    private static List<Element> sortElements(NodeList nodeList) {
        List<Element> elements = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                elements.add((Element) node);
            }
        }
        elements.sort(Comparator.comparing(Element::getTagName));
        return elements;
    }

}

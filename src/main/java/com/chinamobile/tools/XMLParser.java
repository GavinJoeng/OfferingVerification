package com.chinamobile.tools;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

/**
 * @description: XMLParser
 * @author: gavin yang
 * @email: gavinyang@hk.chinamobile.com
 * @date: 2023/12/29 17:57
 */

public class XMLParser {
    public static void main(String[] args) {
        try {
            // 创建一个DocumentBuilderFactory对象
            //DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            // 使用工厂对象创建一个DocumentBuilder对象
            //DocumentBuilder builder = factory.newDocumentBuilder();

            String xmlPath = "C:\\Users\\P7587\\Desktop\\";
            String xmlName = "PLAN_POLICY_RULE xml";

            String saveXmlPath = "C:\\Users\\P7587\\Desktop\\";
            String saveXmlName = "PLAN_POLICY_RULE filter";

            // 加载原始XML文件
            File inputFile = new File(xmlPath + xmlName + "");

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputFile);

            // 创建新的XML文档
            Document newDocument = builder.newDocument();

            // 复制原始XML到新文档，并删除 <code> 标签内的内容
            Element rootElement = document.getDocumentElement();
            Element newRootElement = copyElement(rootElement, newDocument);

            // 将新文档保存为格式美化后的XML文件
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(newRootElement);

            StreamResult result = new StreamResult(new File(saveXmlPath + saveXmlName + ""));
            transformer.transform(source, result);

            System.out.println("New XML file created successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 递归方法，复制元素到新文档，并删除 <code> 标签内的内容
     * @param element
     * @param newDocument
     * @return
     */
    private static Element copyElement(Element element, Document newDocument) {
        Element newElement = newDocument.createElement(element.getTagName());

        // 复制元素的属性
        NamedNodeMap attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attribute = attributes.item(i);
            newElement.setAttribute(attribute.getNodeName(), attribute.getNodeValue());
        }

        // 遍历元素的子节点
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);

            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) childNode;

                // 如果子节点是 <code> 标签，跳过处理
                if (childElement.getTagName().equals("code")) {
                    continue;
                }

                // 递归复制子节点
                Element newChildElement = copyElement(childElement, newDocument);

                // 将复制的子节点添加到新元素中
                newElement.appendChild(newChildElement);
            } else if (childNode.getNodeType() == Node.TEXT_NODE) {
                // 复制文本节点
                Text textNode = (Text) childNode;
                Text newText = newDocument.createTextNode(textNode.getNodeValue());
                newElement.appendChild(newText);
            }
        }

        return newElement;
    }



    /**
     * 递归方法遍历XML元素并提取内容
     * @param element
     * @param extractedDocument
     * @return Element
     */
    private static Element extractContent(Element element, Document extractedDocument) {
        // 创建一个新的元素节点，用于保存提取后的内容
        Element extractedElement = extractedDocument.createElement(element.getNodeName());

        // 获取当前节点的所有子节点
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node childNode = children.item(i);

            // 如果子节点是元素节点
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) childNode;

                // 检查子节点是否为 <code> 标签
                if (childElement.getNodeName().equalsIgnoreCase("code")) {
                    // 如果是 <code> 标签，则跳过该子节点的处理
                    continue;
                } else {
                    // 创建一个新的子节点，并将其添加到提取后的元素中
                    Element extractedChild = extractedDocument.createElement(childElement.getNodeName());
                    extractedElement.appendChild(extractedChild);

                    // 递归处理子节点的子节点
                    Element extractedGrandChild = extractContent(childElement, extractedDocument);
                    if (extractedGrandChild.hasChildNodes()) {
                        extractedChild.appendChild(extractedGrandChild);
                    }
                }
            }
        }

        return extractedElement;
    }


    // 检查元素节点是否包含 <code> 标签
    private static boolean containsCodeTag(Element element) {
        // 获取当前节点的所有子节点
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node childNode = children.item(i);

            // 如果子节点是元素节点，并且标签名是 <code>，则返回 true
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) childNode;
                if (childElement.getNodeName().equalsIgnoreCase("code")) {
                    return true;
                }
            }
        }

        return false;
    }
}

package com.chinamobile.tools;


import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * @description: some desc
 * @author: gavin yang
 * @email: gavinyang@hk.chinamobile.com
 * @date: 2024/1/4 15:40
 */
public class Test {
    private static Node getNodeById(Graph graph, String nodeId) {
        return graph.getNode(nodeId);
    }

    private static void parseElement(Element element, Graph graph, String parentNodeId) {
        // 获取当前标签名称
        String tagName = element.getTagName();

        // 检查节点是否已经存在
        Node currentNode = getNodeById(graph, tagName);
        if (currentNode == null) {
            // 创建当前节点
            currentNode = graph.addNode(tagName);
            currentNode.setAttribute("ui.label", tagName);

            // 如果有父节点，则创建父子关系边
            if (parentNodeId != null) {
                graph.addEdge(parentNodeId + "-" + tagName, parentNodeId, tagName);
            }
        }

        // 处理子元素
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            org.w3c.dom.Node childNode = childNodes.item(i);
            if (childNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                Element childElement = (Element) childNode;
                parseElement(childElement, graph, tagName);
            } else if (childNode.getNodeType() == org.w3c.dom.Node.TEXT_NODE) {
                String nodeValue = childNode.getNodeValue().trim();
                if (!nodeValue.isEmpty()) {
                    // 创建文字节点
                    Node textNode = getNodeById(graph, tagName + "-text");
                    if (textNode == null) {
                        textNode = graph.addNode(tagName + "-text");
                        textNode.setAttribute("ui.label", nodeValue);

                        // 创建父子关系边
                        graph.addEdge(tagName + "-" + tagName + "-text", tagName, tagName + "-text");
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        // 创建DocumentBuilder
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        // 从文件中读取XML文档
        String filePath = "C:\\Users\\P7587\\Desktop\\testFile\\51001800_PLAN_POLICY_RULE_FILTER.xml";
        Document document = builder.parse(filePath);

        // 获取根元素
        Element rootElement = document.getDocumentElement();

        // 创建图
        Graph graph = new SingleGraph("XML Graph");

        // 解析XML并生成可视化节点和边
        parseElement(rootElement, graph, null);

        // 显示图形界面
        graph.display();
    }

}

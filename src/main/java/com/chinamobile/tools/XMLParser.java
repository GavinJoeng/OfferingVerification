package com.chinamobile.tools;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;
import org.w3c.dom.*;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;


/**
 * @description: XMLParser 用於txt文件轉為xml文件並且轉譯code標籤內容
 * @author: gavin yang
 * @email: gavinyang@hk.chinamobile.com
 * @date: 2023/12/29 17:57
 */

public class XMLParser extends Application {

    // 将 filePath 变量声明为 static 类型
    private static String readTxtFile;
    private static String originFile;
    private static String outputFile;

    public static void main(String[] args) {

        try {

            readTxtFile = "C:\\Users\\P7587\\Desktop\\testFile\\51001800_PLAN_POLICY_RULE.txt";
            originFile = "C:\\Users\\P7587\\Desktop\\testFile\\51001800_PLAN_POLICY_RULE.xml";
            outputFile = "C:\\Users\\P7587\\Desktop\\testFile\\51001800_PLAN_POLICY_RULE_FILTER.xml";

            String context = readFileToString(readTxtFile + "");

            String result = java.net.URLDecoder.decode(context, StandardCharsets.UTF_8.name());

            convertStringToXml(result, originFile + "");

            extractXmlInfo(originFile, outputFile);
            //launch(args);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 提取xml文件信息
     *
     * @param originFile
     * @param outputFile
     */
    public static void extractXmlInfo(String originFile, String outputFile) {
        try {
            // 加载原始XML文件
            File inputFile = new File(originFile);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputFile);

            // 创建新的 Document 对象
            Document newDocument = builder.newDocument();

            // 复制源 XML 的根元素
            Element rootElement = document.getDocumentElement();
            Element newRootElement = copyPurifyElement(rootElement, newDocument);
            newDocument.appendChild(newRootElement);

            // 将新的 Document 对象保存为 XML 文件
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource source = new DOMSource(newDocument);
            StreamResult result = new StreamResult(new File(outputFile));
            transformer.transform(source, result);

            System.out.println("XML 文件复制成功！");

            System.out.println("New XML file created successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 利用遞歸的方法去複製並美化xml文件元素標籤等內容
     *
     * @param originalElement
     * @param newDocument
     * @return
     */
    private static Element copyPurifyElement(Element originalElement, Document newDocument) {
        Element newElement = newDocument.createElement(originalElement.getTagName());

        // 复制元素的属性
        if (originalElement.hasAttributes()) {
            NamedNodeMap attributes = originalElement.getAttributes();
            for (int i = 0; i < attributes.getLength(); i++) {
                Node attribute = attributes.item(i);
                Attr newAttribute = newDocument.createAttribute(attribute.getNodeName());
                newAttribute.setValue(attribute.getNodeValue());
                newElement.setAttributeNode(newAttribute);
            }
        }

        // 判断当前元素是否为 code 标签
        if (originalElement.getTagName().equals("code")) {
            String codeValue = originalElement.getTextContent();
            if (!codeValue.isEmpty() && codeValue != null) {
                // 在这里进行代码美化和转义处理
                String processedCode = processCode(codeValue);
                //newElement.setTextContent(processedCode);

                CDATASection cdata = newDocument.createCDATASection(processedCode);
                newElement.appendChild(cdata);
            }
        } else {
            // 处理普通标签，复制子元素并进行美化和转换处理
            NodeList childNodes = originalElement.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node node = childNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element originalChildElement = (Element) node;
                    Element newChildElement = copyPurifyElement(originalChildElement, newDocument);
                    if (originalChildElement.getTagName().equals("code")) {
                        String codeValue = originalChildElement.getTextContent();
                        if (!codeValue.isEmpty() && codeValue != null) {
                            String processedCode = processCode(codeValue);
                            CDATASection cdata = newDocument.createCDATASection(processedCode);
                            newChildElement.appendChild(cdata);
                            //newChildElement.setTextContent(processedCode);
                        }
                    }
                    newElement.appendChild(newChildElement);
                } else if (node.getNodeType() == Node.TEXT_NODE) {
                    Text textNode = newDocument.createTextNode(node.getNodeValue());
                    newElement.appendChild(textNode);
                } else {
                    // 判断当前节点是否为 text-body
                    if (originalElement.getTagName().equals("text-body")) {
                        CDATASection cdata = newDocument.createCDATASection(node.getNodeValue());
                        newElement.appendChild(cdata);
                    } else {
                        Text textNode = newDocument.createTextNode(node.getNodeValue());
                        newElement.appendChild(textNode);
                    }
                }
            }
        }


        //NodeList childNodes = originalElement.getChildNodes();

        /* for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            Node node = childNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element originalChildElement = (Element) node;
                // 判断当前元素是否为 code 标签
                if (originalChildElement.getTagName().equals("code")) {
                    String codeValue = originalChildElement.getTextContent();
                    if (!codeValue.isEmpty() && codeValue != null) {
                        // 在这里进行代码美化和转义处理
                        String processedCode = processCode(codeValue);
                        CDATASection cdata = newDocument.createCDATASection(processedCode);
                        newElement.appendChild(cdata);
                    }
                } else {
                    // 递归处理子元素
                    Element newChildElement = copyPurifyElement(originalChildElement, newDocument);
                    newElement.appendChild(newChildElement);
                }
            } else if (childNode.getNodeType() == Node.TEXT_NODE) {
                // 判断当前节点是否为 text-body
                if (originalElement.getTagName().equals("text-body")) {
                    CDATASection cdata = newDocument.createCDATASection(childNode.getNodeValue());
                    newElement.appendChild(cdata);
                } else {
                    Text textNode = newDocument.createTextNode(childNode.getNodeValue());
                    newElement.appendChild(textNode);
                }
            }else {
                // 判断当前节点是否为 text-body
                if (originalElement.getTagName().equals("text-body")) {
                    CDATASection cdata = newDocument.createCDATASection(childNode.getNodeValue());
                    newElement.appendChild(cdata);
                }else {
                    Text textNode = newDocument.createTextNode(childNode.getNodeValue());
                    newElement.appendChild(textNode);
                }
            }
        }

*/


        return newElement;
    }


    /**
     * 文件讀取轉為String
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public static String readFileToString(String filePath) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append(System.lineSeparator());
            }
        }
        return sb.toString();
    }


    /**
     * 美化轉譯code標籤
     *
     * @param codeValue
     * @return
     */
    private static String processCode(String codeValue) {
        try {
            // 将十六进制字符串转换为BigInteger
            BigInteger bigInteger = new BigInteger(codeValue, 16);

            // 将BigInteger转换为字节数组
            byte[] byteArray = bigInteger.toByteArray();

            // 将字节数组转换为普通文本
            String text = new String(byteArray, StandardCharsets.UTF_8);

            String removeText = text.replaceAll("[^\\p{Print}]", "");

            String filterText = "";
            // 去除void main()前面的数字信息
            filterText = removeText.replaceAll("\\d+`\\d+\\s*(?=void main\\(\\))", "");

            // 在每个void main()之前进行换行
            filterText = filterText.replaceAll("(?i)(?<=void main\\(\\))(.*?)", "\n$1");

            // 在};之后进行换行
            filterText = filterText.replaceAll("(?<=\\};)(.*?)", "\n$1");

            // 在}}之后进行换行
            filterText = filterText.replaceAll("\\}\\}(.*?)", "\n$1");
            // 在//之前添加制表符
            filterText = filterText.replaceAll("(?<=\\\\b)(.*?)(?=//)", "\t$1");

            System.out.println("Java 代码部分：\n" + filterText);

            codeValue = filterText;

        } catch (NumberFormatException e) {
            System.out.println("无法将16进制字符串转换为整数。");
            e.printStackTrace();
        }
        return codeValue;
    }


    /**
     * String轉換為xml格式
     *
     * @param xmlString
     * @param outputFilePath
     */
    public static void convertStringToXml(String xmlString, String outputFilePath) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // 解析XML字符串
            Document document = builder.parse(new InputSource(new StringReader(xmlString)));

            // 将XML文件进行美化格式
            document.getDocumentElement().normalize();

            // 创建输出文件
            File outputFile = new File(outputFilePath);

            // 将XML内容写入输出文件
            FileWriter writer = new FileWriter(outputFile);
            writer.write(prettyPrint(document.getDocumentElement()));
            writer.close();

            System.out.println("XML文件已成功生成：" + outputFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 美化格式输出XML内容
     *
     * @param element
     * @return
     */
    public static String prettyPrint(Element element) {
        try {
            // 创建Transformer对象并设置缩进和换行
            javax.xml.transform.TransformerFactory transformerFactory =
                    javax.xml.transform.TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", 2);
            javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");

            // 将XML内容转换为字符串
            java.io.StringWriter stringWriter = new java.io.StringWriter();
            javax.xml.transform.stream.StreamResult streamResult =
                    new javax.xml.transform.stream.StreamResult(stringWriter);
            transformer.transform(new javax.xml.transform.dom.DOMSource(element), streamResult);

            // 返回美化格式的XML字符串
            return stringWriter.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * XML文件轉為TreeView格式
     *
     * @param file
     * @param parent
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    private static void parseXMLToTree(File file, TreeItem<String> parent) throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        parser.parse(file, new DefaultHandler() {
            private TreeItem<String> currentItem = parent;

            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                // 创建新的 TreeItem 并添加到父节点中
                TreeItem<String> item = new TreeItem<>(qName);
                currentItem.getChildren().add(item);
                currentItem = item;

                // 将 XML 元素的属性也添加到 TreeItem 中
                for (int i = 0; i < attributes.getLength(); i++) {
                    String attributeName = attributes.getQName(i);
                    String attributeValue = attributes.getValue(i);
                    currentItem.getChildren().add(new TreeItem<>(attributeName + ": " + attributeValue));
                }
            }

            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {
                // 切换到父节点
                currentItem = currentItem.getParent();
            }

            @Override
            public void characters(char[] ch, int start, int length) throws SAXException {
                // 将 XML 元素的文本内容添加到 TreeItem 中
                String text = new String(ch, start, length).trim();
                if (!text.isEmpty()) {
                    currentItem.getChildren().add(new TreeItem<>(text));
                }
            }
        });
    }


    /**
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set. The primary stage will be embedded in
     *                     the browser if the application was launched as an applet.
     *                     Applications may create other stages, if needed, but they will not be
     *                     primary stages and will not be embedded in the browser.
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        TreeItem<String> rootItem = new TreeItem<>("Root");
        rootItem.setExpanded(true);

        // 解析 XML 文件并生成 TreeView
        parseXMLToTree(new File(outputFile), rootItem);

        TreeView<String> treeView = new TreeView<>(rootItem);
        Scene scene = new Scene(treeView, 500, 900);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}

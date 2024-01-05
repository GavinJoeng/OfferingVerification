package com.chinamobile.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * @description: XMLParser
 * @author: gavin yang
 * @email: gavinyang@hk.chinamobile.com
 * @date: 2023/12/29 17:57
 */
public class StringToXmlConverter {

    private static void convertStringToXml(String xmlString, String outputFilePath) {
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

    // 美化格式输出XML内容
    private static String prettyPrint(Element element) {
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


}

package com.chinamobile.tools;


import org.xml.sax.InputSource;
import org.w3c.dom.Document;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @description: XMLParser
 * @author: gavin yang
 * @email: gavinyang@hk.chinamobile.com
 * @date: 2023/12/29 17:57
 */

public class URLDecoderTest {
    /**
     * 文件讀取轉為String
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
     * String轉為xml文件
     * @param xmlString
     * @param filePath
     * @throws IOException
     * @throws TransformerException
     */
    public static void writeStringToXml(String xmlString, String filePath) throws IOException, TransformerException {
        try {
            // 创建XML文档对象
            Document document = javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(new InputSource(new StringReader(xmlString)).getByteStream());

            // 创建Transformer对象，并设置美化格式
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            ((Transformer) transformer).setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            // 将XML文档对象写入文件
            FileWriter writer = new FileWriter(filePath);
            transformer.transform(new DOMSource(document), new StreamResult(writer));
            writer.close();

            System.out.println("XML文件已成功保存。");
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, TransformerException {
//        String readFilePath = "C:\\Users\\P7587\\Desktop\\Offering verification Requirements\\PLAN_POLICY_RULE xml\\";
//        String readFileName = "11831 PLAN_POLICY_RULE.txt";
//        String outputPath = "C:\\Users\\P7587\\Desktop\\Offering verification Requirements\\";
//        String outputFileName = "PLAN_POLICY_RULE xml";
        String readFilePath = "C:\\Users\\P7587\\Desktop\\";
        String readFileName = "11831 PLAN_POLICY_RULE.txt";
        String outputPath = "C:\\Users\\P7587\\Desktop\\";
        String outputFileName = "PLAN_POLICY_RULE xml";
        String context = readFileToString(readFilePath + readFileName + "");
        String result = java.net.URLDecoder.
                decode(context, StandardCharsets.UTF_8.name());
        StringToXmlConverter stringToXmlConverter = new StringToXmlConverter();
        stringToXmlConverter.convertStringToXml(result, outputPath + outputFileName + "");

    }

}

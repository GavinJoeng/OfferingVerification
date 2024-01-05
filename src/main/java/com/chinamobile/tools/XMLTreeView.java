package com.chinamobile.tools;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;

public class XMLTreeView extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        TreeItem<String> rootItem = new TreeItem<>("Root");
        rootItem.setExpanded(true);
        String readTxtFile = "C:\\Users\\P7587\\Desktop\\testFile\\11831 PLAN_POLICY_RULE.txt";
        String originFile = "C:\\Users\\P7587\\Desktop\\testFile\\PLAN_POLICY_RULE.xml";
        String outputFile = "C:\\Users\\P7587\\Desktop\\testFile\\PLAN_POLICY_RULE filter.xml";
        // 解析 XML 文件并生成 TreeView
        parseXMLToTree(new File(outputFile), rootItem);

        TreeView<String> treeView = new TreeView<>(rootItem);
        Scene scene = new Scene(treeView, 300, 400);
        stage.setScene(scene);
        stage.show();
    }

    private void parseXMLToTree(File file, TreeItem<String> parent) throws ParserConfigurationException, SAXException, IOException {
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

    public static void main(String[] args) {
        launch(args);
    }
}

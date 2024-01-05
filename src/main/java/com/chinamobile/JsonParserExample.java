package com.chinamobile;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;

public class JsonParserExample {

    private static String basicPath = "C:\\Users\\P7587\\Desktop\\testFile\\";
    private static String readJsonFile;

    public static void main(String[] args) {
        try {
            readJsonFile = "51001800_PLAN_POLICY_RULE.json";
            // 读取JSON文件
            FileReader reader = new FileReader(basicPath+readJsonFile);

            // 使用JsonParser解析JSON文件
            JsonParser jsonParser = new JsonParser();
            JsonElement rootElement = jsonParser.parse(reader);

            // 递归处理JSON数据，并指定层级的condition-node
            processJson(rootElement,  "condition-node");

            // 关闭文件读取器
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void processJson(JsonElement element, String targetNodeName) {
        if (element.isJsonObject()) { // 处理JsonObject类型的数据
            JsonObject jsonObject = element.getAsJsonObject();
            for (String key : jsonObject.keySet()) {
                JsonElement childElement = jsonObject.get(key);
                processJson(childElement, targetNodeName); // 递归处理子元素
            }
        } else if (element.isJsonArray()) { // 处理JsonArray类型的数据
            JsonArray jsonArray = element.getAsJsonArray();
            for (JsonElement childElement : jsonArray) {
                processJson(childElement, targetNodeName); // 递归处理子元素
            }
        } else if (element.isJsonPrimitive() && targetNodeName.equals(element.getAsString())) {
            System.out.println(element); // 打印最后一层级的condition-node信息
        }
    }
}

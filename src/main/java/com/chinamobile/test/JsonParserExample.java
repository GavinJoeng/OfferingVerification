package com.chinamobile.test;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class JsonParserExample {
    private static String basicPath = "C:\\Users\\P7587\\Desktop\\testFile\\";
    private static String readJsonFile = "51001800_PLAN_POLICY_RULE.json";;
    // FileReader reader = new FileReader(basicPath+readJsonFile);

    public static void main(String[] args) {
        try {
            // 读取JSON文件
            FileReader reader = new FileReader(basicPath+readJsonFile);

            // 使用JsonParser解析JSON文件
            JsonParser jsonParser = new JsonParser();
            JsonElement rootElement = jsonParser.parse(reader);

            // 提取关键信息
            List<Info> extractedInfo = extractInformation(rootElement, "inner-text", "logic-script.value");

            // 打印提取的信息
            for (Info info : extractedInfo) {
                System.out.println(info);
            }

            // 关闭文件读取器
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<Info> extractInformation(JsonElement element, String targetField1, String targetField2) {
        List<Info> result = new ArrayList<>();

        extractInformationRecursive(element, targetField1, targetField2, 0, result);

        return result;
    }

    private static void extractInformationRecursive(JsonElement element, String targetField1, String targetField2,
                                                    int level, List<Info> result) {
        if (element.isJsonObject()) {
            JsonObject jsonObject = element.getAsJsonObject();

            // 获取level
            int currentLevel = level;
            if (jsonObject.has("logic-script")) {
                JsonObject logicScriptObject = jsonObject.getAsJsonObject("logic-script");
                if (logicScriptObject.has("@attributes")) {
                    JsonObject attributesObject = logicScriptObject.getAsJsonObject("@attributes");
                    if (attributesObject.has("translated-crl-function-id") &&
                            attributesObject.get("translated-crl-function-id").getAsString().equals("2")) {
                        currentLevel = 2;
                    }
                }
            }

            for (String key : jsonObject.keySet()) {
                JsonElement childElement = jsonObject.get(key);
                if (key.equals(targetField1)) {
                    String innerText = extractInnerText(childElement);
                    result.add(new Info(currentLevel, innerText));
                } else if (key.equals(targetField2)) {
                    String value = extractValue(childElement);
                    result.get(result.size() - 1).setValue(value);
                } else {
                    extractInformationRecursive(childElement, targetField1, targetField2, currentLevel, result);
                }
            }
        } else if (element.isJsonArray()) {
            JsonArray jsonArray = element.getAsJsonArray();
            for (JsonElement childElement : jsonArray) {
                extractInformationRecursive(childElement, targetField1, targetField2, level, result);
            }
        }
    }

    private static String extractInnerText(JsonElement element) {
        if (element.isJsonObject()) {
            JsonObject jsonObject = element.getAsJsonObject();
            if (jsonObject.has("single-language-text")) {
                JsonObject singleLanguageTextObject = jsonObject.getAsJsonObject("single-language-text");
                if (singleLanguageTextObject.has("text-language")) {
                    JsonObject textLanguageObject = singleLanguageTextObject.getAsJsonObject("text-language");
                    if (textLanguageObject.has("value")) {
                        JsonElement valueElement = textLanguageObject.get("value");
                        if (valueElement.isJsonPrimitive()) {
                            return valueElement.getAsJsonPrimitive().getAsString();
                        }
                    }
                }
            }
        }
        return "";
    }

    private static String extractValue(JsonElement element) {
        if (element.isJsonPrimitive()) {
            return element.getAsJsonPrimitive().getAsString();
        }
        return "";
    }

    private static class Info {
        private int level;
        private String innerText;
        private String value;

        public Info(int level, String innerText) {
            this.level = level;
            this.innerText = innerText;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "Level: " + level + "\nInner Text: " + innerText + "\nValue: " + value + "\n";
        }
    }
}

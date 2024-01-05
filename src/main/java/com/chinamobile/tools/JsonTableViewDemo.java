package com.chinamobile.tools;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonTableViewDemo extends Application {

    public static class Car {
        private String brand;
        private String model;

        public Car(String brand, String model) {
            this.brand = brand;
            this.model = model;
        }

        public String getBrand() {
            return brand;
        }

        public String getModel() {
            return model;
        }
    }

    private static String basicPath = "C:\\Users\\P7587\\Desktop\\testFile\\";
    private static String readJsonFile = "cars.json";

    @Override
    public void start(Stage stage) throws Exception {
        // 读取JSON文件内容并转换为JSONObject对象
        String jsonString = readJsonFromFile(basicPath + readJsonFile);
        JSONObject jsonObject = new JSONObject(jsonString);

        // 获取cars数组并创建表格列
        JSONArray carsArray = jsonObject.getJSONArray("cars");
        TableColumn<Car, String> brandCol = new TableColumn<>("Brand");
        brandCol.setCellValueFactory(new PropertyValueFactory<>("brand"));
        TableColumn<Car, String> modelCol = new TableColumn<>("Model");
        modelCol.setCellValueFactory(new PropertyValueFactory<>("model"));

        // 创建表格视图并添加列
        TableView<Car> tableView = new TableView<>();
        tableView.getItems().addAll(getCarList(carsArray));
        tableView.getColumns().addAll(brandCol, modelCol);

        // 创建场景并显示表格视图
        Scene scene = new Scene(new StackPane(tableView), 400, 300);
        stage.setScene(scene);
        stage.show();
    }

    private List<Car> getCarList(JSONArray carsArray) {
        List<Car> carList = new ArrayList<>();
        for (int i = 0; i < carsArray.length(); i++) {
            JSONObject carObject = carsArray.getJSONObject(i);
            String brand = carObject.getString("brand");
            String model = carObject.getString("model");
            carList.add(new Car(brand, model));
        }
        return carList;
    }

    private String readJsonFromFile(String filename) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

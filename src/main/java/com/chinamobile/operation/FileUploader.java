package com.chinamobile.operation;

import com.chinamobile.constant.MatrixPatternPath;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileUploader {
    public static void main(String[] args) {
        // 选择文件
        String filePath = chooseFile();

        // 读取文件内容
        if (filePath != null) {
            readFile(filePath);
        }
    }

    private static String chooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("TXT Files", "txt");
        fileChooser.setFileFilter(filter);

        // 设置初始目录
        String initialDirectory = MatrixPatternPath.BASIC_PATH;
        fileChooser.setCurrentDirectory(new File(initialDirectory));

        while (true) {
            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                System.out.println("选择的文件路径：" + filePath);
                return filePath;
            } else if (result == JFileChooser.CANCEL_OPTION) {
                System.out.println("未选择文件。");
                System.exit(0); // 关闭程序
                return null;
            }
        }
    }

    private static void readFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

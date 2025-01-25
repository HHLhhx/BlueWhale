package com.seecoder.BlueWhale.util;

import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class HtmlOpenUtil {
    private static final Object lock = new Object();

    public static boolean openHtml(String htmlContent) {
        // 创建临时文件
        File tempFile = null;
        try {
            tempFile = File.createTempFile("tempHtml", ".html");
            try (FileWriter writer = new FileWriter(tempFile)) {
                writer.write(htmlContent);
            }

            // 打开默认浏览器
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    File finalTempFile = tempFile;
                    new Thread(() -> {
                        try {
                            desktop.browse(finalTempFile.toURI());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();
                } else {
                    System.out.println("BROWSE action is not supported on your desktop.");
                }
            } else {
                System.out.println("Desktop is not supported on your platform.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //删除临时文件 (可选)
            if (tempFile != null && tempFile.exists()) {
                tempFile.deleteOnExit();
            }
        }
        System.out.print("如果成功支付, 请输入y, 否则输入n:");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            // 读取单个字符
            char ch = (char) reader.read();
            // 清空缓冲区（读取剩余的输入）
            reader.readLine();
            if (ch == 'y')
                return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}

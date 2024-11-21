package network;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class Ping {

    private static Process pingProcess; // 存储 ping 进程
    public boolean isPinging = false; // 判断是否正在 ping

    public void startPingProcess(String ipAddress, JTextArea outputArea) {
        isPinging = true;
        // 使用 -t 选项，持续 Ping
        // 存储 ping 线程
        Thread pingThread = new Thread(() -> {
            try {
                // 使用 -t 选项，持续 Ping
                String command = System.getProperty("os.name").toLowerCase().contains("win") ? "ping -t " + ipAddress : "ping " + ipAddress;

                String charset = System.getProperty("os.name").toLowerCase().contains("win") ? "GBK" : "UTF-8";
                pingProcess = Runtime.getRuntime().exec(command);
                BufferedReader reader = new BufferedReader(new InputStreamReader(pingProcess.getInputStream(), Charset.forName(charset)));

                String line;
                while ((line = reader.readLine()) != null && isPinging) {
                    final String outputLine = line;
                    SwingUtilities.invokeLater(() -> outputArea.append(outputLine + "\n"));
                    SwingUtilities.invokeLater(() -> outputArea.setCaretPosition(outputArea.getDocument().getLength()));
                }
                reader.close();
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> outputArea.append("Ping 失败: " + ex.getMessage() + "\n"));
            }
        });

        pingThread.start();
    }

    // 停止 Ping 进程
    public void stopPing(JTextArea outputArea) {
        isPinging = false;

        if (pingProcess != null) {
            pingProcess.destroy(); // 终止进程
            outputArea.append("Ping 已停止。\n");
        }
    }
}

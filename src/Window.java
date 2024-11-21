import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.Authenticator;
import java.net.PasswordAuthentication;

import network.*;

public class Window {

    public static void run() {
        Window window = new Window();
        JFrame myWindow = window.init();
        window.setTab(myWindow);
        myWindow.setVisible(true);
    }

    public JFrame init() {
        JFrame frame = new JFrame();
        frame.setBounds(300, 300, 600, 450);
        frame.setPreferredSize(new Dimension(200, 300));
        frame.setTitle(ProgramInfo.PROGRAM_TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        return frame;
    }

    public void setTab(JFrame myWindow) {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Ping", setPingPanel(myWindow));
        tabbedPane.addTab("Whois", setWhoisPanel(myWindow));
        tabbedPane.addTab("Proxy", setProxyPanel(myWindow));
        myWindow.add(tabbedPane);
    }

    public JPanel setPingPanel(JFrame myWindow) {
        JPanel pingPanel = new JPanel();
        pingPanel.setLayout(new BorderLayout());
        
        JPanel inputPanel = new JPanel();
        JTextField ipAddressField = new JTextField(15);
        JButton pingButton = new JButton("Ping");
        inputPanel.add(new JLabel("IP or Domain:"));
        inputPanel.add(ipAddressField);
        inputPanel.add(pingButton);

        // 添加显示结果的文本区域
        JTextArea outputArea = new JTextArea();
        outputArea.setEditable(false); // 禁止编辑
        JScrollPane scrollPane = new JScrollPane(outputArea);

        // 将输入面板和输出面板添加到 Ping 面板
        pingPanel.add(inputPanel, BorderLayout.NORTH);
        pingPanel.add(scrollPane, BorderLayout.CENTER);

        Ping ping = new Ping();
        pingButton.addActionListener(e -> {
            String ipAddress = ipAddressField.getText().trim();
            if (ipAddress.isEmpty()) {
                JOptionPane.showMessageDialog(myWindow, "请输入有效的 IP 地址或域名。", "输入错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!ping.isPinging) {
                pingButton.setText("Stop");
                outputArea.setText(""); // 清空之前的输出
                ping.startPingProcess(ipAddress, outputArea);
            } else {
                pingButton.setText("Ping");
                ping.stopPing(outputArea);
            }
        });
        return pingPanel;
    }

    public JPanel setWhoisPanel(JFrame myWindow) {
        JPanel whoisPanel = new JPanel();
        whoisPanel.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        JLabel domainLabel = new JLabel("域名：");
        JTextField domainTextField = new JTextField(20);
        JButton queryButton = new JButton("查询");
        JTextArea resultTextArea = new JTextArea();
        resultTextArea.setEditable(false);  // 禁止编辑查询结果
        JScrollPane scrollPane = new JScrollPane(resultTextArea);

        JComboBox<String> whoisServerComboBox = getStringJComboBox();

        inputPanel.add(new JLabel("服务器:"));
        inputPanel.add(whoisServerComboBox);
        inputPanel.add(domainLabel);
        inputPanel.add(domainTextField);
        inputPanel.add(queryButton);

        Whois whois = new Whois();
        queryButton.addActionListener(e -> {
            String domain = domainTextField.getText().trim();
            if (domain.isEmpty()) {
                JOptionPane.showMessageDialog(myWindow, "请输入有效的域名", "输入错误", JOptionPane.ERROR_MESSAGE);
            } else {// 获取用户选择的 Whois 服务器
                String selectedServer = (String) whoisServerComboBox.getSelectedItem();// 执行 Whois 查询并显示结果
                String result = whois.query(domain, selectedServer);
                resultTextArea.setText(result);
            }

        });

        whoisPanel.add(inputPanel, BorderLayout.NORTH);
        whoisPanel.add(scrollPane, BorderLayout.CENTER);

        return whoisPanel;
    }

    private static JComboBox<String> getStringJComboBox() {
        String[] whoisServers = {
                "whois.apnic.net",
                "whois.arin.net",
                "whois.ripe.net",
                "whois.lacnic.net",
                "whois.afrinic.net",
                "whois.internic.net",
                "whois.krnic.net",
                "whois.twnic.net",
                "whois.cnnic.cn",
                "whois.edu.cn",
                "whois.gov.cn",
                "whois.nic.ad.jp",
        };

        // 创建下拉框供用户选择
        JComboBox<String> whoisServerComboBox = new JComboBox<>(whoisServers);
        whoisServerComboBox.setSelectedIndex(0);  // 默认选择第一个服务器
        return whoisServerComboBox;
    }

    public JPanel setProxyPanel(JFrame myWindow) {
        JPanel proxyPanel = new JPanel();
        proxyPanel.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();

        // 代理地址输入
        JLabel hostLabel = new JLabel("Proxy Host:");
        JTextField hostField = new JTextField(20);
        hostField.setBorder(new EmptyBorder(10, 20, 10, 20));

        // 代理端口输入
        JLabel portLabel = new JLabel("Proxy Port:");
        JTextField portField = new JTextField(5);

        // 是否启用认证
        JCheckBox authCheckBox = new JCheckBox("Enable Authentication");
        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField(10);
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField(10);

        // 初始状态禁用认证输入框
        userLabel.setEnabled(false);
        userField.setEnabled(false);
        passLabel.setEnabled(false);
        passField.setEnabled(false);

        hostField.setPreferredSize(new Dimension(150, 25)); // 宽150像素，高25像素
        portField.setPreferredSize(new Dimension(50, 25)); // 宽50像素，高25像素
        userField.setPreferredSize(new Dimension(150, 25));
        passField.setPreferredSize(new Dimension(150, 25));

        // 根据复选框状态控制输入框可用性
        authCheckBox.addActionListener(e -> {
            boolean isAuthEnabled = authCheckBox.isSelected();
            userLabel.setEnabled(isAuthEnabled);
            userField.setEnabled(isAuthEnabled);
            passLabel.setEnabled(isAuthEnabled);
            passField.setEnabled(isAuthEnabled);
        });
        JButton applyButton = new JButton("Apply Proxy");
        applyButton.addActionListener(e -> {
            String host = hostField.getText();
            String port = portField.getText();
            boolean useAuth = authCheckBox.isSelected();
            String user = userField.getText();
            String password = new String(passField.getPassword());

            System.setProperty("socksProxyHost", host);
            System.setProperty("socksProxyPort", port);

            if (useAuth) {
                // 配置认证信息
                Authenticator.setDefault(new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user, password.toCharArray());
                    }
                });
            } else {
                // 清除认证
                Authenticator.setDefault(null);
            }
            JOptionPane.showMessageDialog(proxyPanel, "Proxy settings applied successfully!");
        });


        inputPanel.add(hostLabel);
        inputPanel.add(hostField);
        inputPanel.add(portLabel);
        inputPanel.add(portField);
        inputPanel.add(authCheckBox);
        inputPanel.add(userLabel);
        inputPanel.add(userField);
        inputPanel.add(passLabel);
        inputPanel.add(passField);
        inputPanel.add(applyButton);

        proxyPanel.add(inputPanel, BorderLayout.CENTER);


        return proxyPanel;
    }

}
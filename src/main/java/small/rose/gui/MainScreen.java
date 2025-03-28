package small.rose.gui;

import small.rose.listener.KeepActiveListener;
import small.rose.utils.AppSettings;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ItemEvent;

/**
 * @Project: keepOn
 * @Author: 张小菜
 * @Description: [ AntiLockScreen ] 说明： 无
 * @Function: 功能描述： 无
 * @Date: 2025/3/25 025 23:22
 * @Version: v1.0
 */
public class MainScreen extends JFrame {
    private JLabel statusLabel;
    private JLabel infoLabel;

    public MainScreen() {
        // 初始化GUI界面
        setTitle("防锁屏程序");
        setSize(330, 150);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());
        // 居中显示
        setLocationRelativeTo(null);
        // 创建字体对象
        Font mainFont = new Font("微软雅黑", Font.PLAIN, 18);
        Font labelFont = new Font("微软雅黑", Font.BOLD, 12);
        // 创建字体对象
        Font titleFont = new Font("微软雅黑", Font.PLAIN, 16);
        setFont(mainFont);
        // 创建一个面板并设置其背景颜色
        JPanel panel = new JPanel();
        //panel.setBackground(Color.GREEN);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        // 设置背景色
        // getContentPane().setBackground(Color.lightGray);

        // 添加控件
        JLabel infoButton = new JLabel("运行中，可最小化到系统托盘");
        statusLabel = new JLabel("状态: 已启动", JLabel.CENTER);
        statusLabel.setBackground(new Color(192,235,215));
        statusLabel.setFont(titleFont);
        statusLabel.setAlignmentX(10);
        statusLabel.setAlignmentY(10);
        panel.add(statusLabel);

        JCheckBox checkBoxButton = new JCheckBox("日志打开开关");
        panel.add(checkBoxButton);
        checkBoxButton.addItemListener(e -> {
            // 获取状态变化
            int state = e.getStateChange();
            if (state == ItemEvent.SELECTED) {
                AppSettings.getInstance().setLog(true);
            } else {
                AppSettings.getInstance().setLog(false);
            }
        });
        add(checkBoxButton, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);

        //JButton infoButton1 = new JButton("运行中，请勿关闭窗口");
        //JButton infoButton2 = new JButton("运行中，请勿关闭窗口");
        infoLabel = new JLabel("", JLabel.LEFT);
        infoLabel.setBorder(new BevelBorder(1));
        infoLabel.setFont(labelFont);
        add(infoLabel, BorderLayout.SOUTH);

        // 修改JFrame初始化代码
        //setUndecorated(true); // 隐藏窗口
        //setType(Window.Type.UTILITY); // 不在任务栏显示

        new KeepActiveListener(statusLabel, infoLabel).keepOn();
        addWindowListener(new WindowAdapter(this));

    }



}

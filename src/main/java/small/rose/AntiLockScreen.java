package small.rose;

import javax.swing.*;
import java.awt.*;

/**
 * @Project: keepOn
 * @Author: 张小菜
 * @Description: [ AntiLockScreen ] 说明： 无
 * @Function: 功能描述： 无
 * @Date: 2025/3/25 025 23:22
 * @Version: v1.0
 */
public class AntiLockScreen extends JFrame {
    private JLabel statusLabel;
    private JLabel infoLabel;

    public AntiLockScreen() {
        // 初始化GUI界面
        setTitle("防锁屏程序");
        setSize(300, 150);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null); // 居中显示

        // 添加控件
        JButton infoButton = new JButton("运行中，可最小化到系统托盘");
        statusLabel = new JLabel("状态: 已启动", JLabel.CENTER);
        add(infoButton, BorderLayout.NORTH);
        add(statusLabel, BorderLayout.CENTER);

        //JButton infoButton1 = new JButton("运行中，请勿关闭窗口");
        //JButton infoButton2 = new JButton("运行中，请勿关闭窗口");
        infoLabel = new JLabel("状态: ", JLabel.LEFT);
        add(infoLabel, BorderLayout.SOUTH);


        // 修改JFrame初始化代码
        //setUndecorated(true); // 隐藏窗口
        //setType(Window.Type.UTILITY); // 不在任务栏显示

        new KeepActiveListener(statusLabel, infoLabel).keepOn();
        addWindowListener(new WindowAdapter(this));

    }



}

package small.rose.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * @Project : keepOn
 * @Author : zhangxiaocai
 * @Description : [ KeepTrayUI ] 说明：无
 * @Function :  功能说明：无
 * @Date ：2025/3/27 16:27
 * @Version ： 1.0
 **/
public class KeepTrayUI{

    private static Map<String, TrayIcon> map = new HashMap<>();
    private static final String KEEP_TRAY_NAME = "KEEP_TRAY_NAME";

    public static final KeepTrayUI Instance = new KeepTrayUI();
    private KeepTrayUI() {
    }



    public TrayIcon createTrayIcon(JFrame jFrame) {

        TrayIcon trayIcon1 = map.get(KEEP_TRAY_NAME);
        if (trayIcon1!=null){
            return trayIcon1;
        }
        // 创建默认系统托盘图标
        //Image defaultImage = SystemTray.getSystemTray().getTrayIcons()[0].getImage();

        // 创建纯色图标（16x16像素）
        BufferedImage dynamicIcon = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = dynamicIcon.createGraphics();
        g2d.setColor(Color.BLUE); // 背景色
        g2d.fillRect(0, 0, 16, 16);
        g2d.setColor(Color.WHITE); // 文字色
        g2d.drawString("KP", 2, 12);
        g2d.dispose();

        TrayIcon trayIcon = new TrayIcon(dynamicIcon, "KeepOn");
        // 加载托盘图标（推荐使用16x16或32x32像素）
        //Image image = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/tray_icon.png")); // 从资源路径加载[4](@ref)

        // 创建托盘图标实例
        //TrayIcon trayIcon = new TrayIcon(image, "双击打开主界面");
        trayIcon.setImageAutoSize(true); // 自动调整图标尺寸[6](@ref)

        // 添加双击监听事件
        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // 双击触发
                    jFrame.setVisible(true); // 显示主界面[6](@ref)
                    jFrame.setExtendedState(JFrame.NORMAL); // 恢复窗口状态[4](@ref)
                }
            }
        });

        // 添加右键弹出菜单
        PopupMenu popup = new PopupMenu();
        MenuItem settingItem = new MenuItem("settings");
        settingItem.addActionListener(e -> jFrame.setVisible(true)); // 点击菜单打开[5](@ref)
        popup.add(settingItem);
        popup.addSeparator();
        MenuItem openItem = new MenuItem("open");
        openItem.addActionListener(e -> jFrame.setVisible(true)); // 点击菜单打开[5](@ref)
        MenuItem exitItem = new MenuItem("exit");
        exitItem.addActionListener(e -> System.exit(0)); // 退出程序[1](@ref)
        popup.add(openItem);
        popup.addSeparator();
        popup.add(exitItem);
        trayIcon.setPopupMenu(popup);
        map.put(KEEP_TRAY_NAME, trayIcon);
        return trayIcon ;
    }


    public TrayIcon addTrayIcon(JFrame jFrame) {
        // 检查系统支持性[1](@ref)
        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();

            TrayIcon trayIcon = createTrayIcon(jFrame);

            try {
                // 将图标加入系统托盘[1](@ref)
                tray.add(trayIcon);
            } catch (AWTException ex) {
                ex.printStackTrace();
            }
            return trayIcon;
        }
        return null ;
    }
}

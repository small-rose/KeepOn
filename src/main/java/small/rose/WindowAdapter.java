package small.rose;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;

/**
 * @Project: keepOn
 * @Author: 张小菜
 * @Description: [ WindowAdapter ] 说明： 无
 * @Function: 功能描述： 无
 * @Date: 2025/3/26 026 1:25
 * @Version: v1.0
 */
public class WindowAdapter implements WindowListener {

    private AntiLockScreen antiLockScreen ;

    public WindowAdapter(){
    }

    public WindowAdapter(AntiLockScreen antiLockScreen){
        this.antiLockScreen = antiLockScreen;
    }

    @Override
    public void windowOpened(WindowEvent e){

    }

    @Override
    public void windowClosing(WindowEvent e){
        minimizeToTray(); // 自定义最小化到托盘方法[4](@ref)
    }

    @Override
    public void windowClosed(WindowEvent e){

    }

    @Override
    public void windowIconified(WindowEvent e){

    }

    @Override
    public void windowDeiconified(WindowEvent e){

    }

    @Override
    public void windowActivated(WindowEvent e){

    }

    @Override
    public void windowDeactivated(WindowEvent e){

    }

    private TrayIcon createTrayIcon() {
        if (SystemTray.isSupported()) { // 检查系统支持性[1](@ref)
            SystemTray tray = SystemTray.getSystemTray();

            // 创建默认系统托盘图标
            //Image defaultImage = SystemTray.getSystemTray().getTrayIcons()[0].getImage();

            // 创建纯色图标（16x16像素）
            BufferedImage dynamicIcon = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = dynamicIcon.createGraphics();
            g2d.setColor(Color.BLUE); // 背景色
            g2d.fillRect(0, 0, 16, 16);
            g2d.setColor(Color.WHITE); // 文字色
            g2d.drawString("APP", 2, 12);
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
                        antiLockScreen.setVisible(true); // 显示主界面[6](@ref)
                        antiLockScreen.setExtendedState(JFrame.NORMAL); // 恢复窗口状态[4](@ref)
                    }
                }
            });

            // 添加右键弹出菜单
            PopupMenu popup = new PopupMenu();
            MenuItem openItem = new MenuItem("open");
            openItem.addActionListener(e -> antiLockScreen.setVisible(true)); // 点击菜单打开[5](@ref)
            MenuItem exitItem = new MenuItem("exit");
            exitItem.addActionListener(e -> System.exit(0)); // 退出程序[1](@ref)
            popup.add(openItem);
            popup.addSeparator();
            popup.add(exitItem);
            trayIcon.setPopupMenu(popup);

            try {
                tray.add(trayIcon); // 将图标加入系统托盘[1](@ref)
            } catch (AWTException ex) {
                ex.printStackTrace();
            }
            return trayIcon;
        }
        return null ;
    }


    private void minimizeToTray() {
        antiLockScreen.setVisible(false); // 隐藏窗口而非关闭[4](@ref)
        if (SystemTray.isSupported()) {
            // 显示托盘通知（可选）
            TrayIcon trayIcon = createTrayIcon(); // 获取已创建的托盘实例
            if(trayIcon!= null){
                trayIcon.displayMessage("程序已最小化", "点击托盘图标恢复界面", TrayIcon.MessageType.INFO); // 气泡提示[6](@ref)
            }
        }
    }

}

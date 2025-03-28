package small.rose.gui;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * @Project: keepOn
 * @Author: 张小菜
 * @Description: [ WindowAdapter ] 说明： 无
 * @Function: 功能描述： 无
 * @Date: 2025/3/26 026 1:25
 * @Version: v1.0
 */
public class WindowAdapter implements WindowListener {

    private MainScreen mainScreen;

    public WindowAdapter(){
    }

    public WindowAdapter(MainScreen mainScreen){
        this.mainScreen = mainScreen;
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



    private void minimizeToTray() {
        // 隐藏窗口而非关闭[4](@ref)
        mainScreen.setVisible(false);
        if (SystemTray.isSupported()) {
            // 显示托盘通知（可选）
            // 获取已创建的托盘实例
            TrayIcon trayIcon = KeepTrayUI.Instance.addTrayIcon(mainScreen);
            if(trayIcon!= null){
                // 气泡提示[6](@ref)
                trayIcon.displayMessage("程序已最小化", "点击托盘图标恢复界面", TrayIcon.MessageType.INFO);
            }
        }
    }

}

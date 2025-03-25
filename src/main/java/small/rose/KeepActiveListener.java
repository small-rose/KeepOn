package small.rose;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Project: keepOn
 * @Author: 张小菜
 * @Description: [ KeepActiveListener ] 说明： 无
 * @Function: 功能描述： 无
 * @Date: 2025/3/25 025 23:30
 * @Version: v1.0
 */
public class KeepActiveListener {

    private JLabel statusLabel ;
    private JLabel infoLabel ;
    // 状态检测间隔
    private static final int CHECK_INTERVAL = 1 ;

    // 鼠标静止判断阈值 秒
    private static final int INACTIVITY_THRESHOLD = 10 ;

    // 抖动间隔 (小于系统休眠阈值)
    private static final int JITTER_INTERVAL = 1 ;

    // 人为移动阈值判定 ( 3 - 10 )
    private static final int MOVE_THRESHOLD = 10 ;

    // 模拟键盘按键间隔
    private static final int KEY_EVENT_INTERVAL = 10 ;

    //
    private static final AtomicLong lastActiveTime = new AtomicLong(System.currentTimeMillis());
    private static final AtomicBoolean isJittering = new AtomicBoolean(false);
    private static Point lastPosition = new Point(0,0);

    public KeepActiveListener(){
    }
    public KeepActiveListener(JLabel statusLabel, JLabel infoLabel){
        this.statusLabel = statusLabel;
        this.infoLabel = infoLabel;
    }

    public void keepOn(){
        try{
            final ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(2);
            final Robot robot = new Robot();
            robot.setAutoDelay(10);
            // 状态检测任务
            scheduled.scheduleAtFixedRate(() -> {
                try{
                    Point currentPosition = MouseInfo.getPointerInfo().getLocation();
                    checkUserActivity(currentPosition);
                    updateJitterState(robot);
                    System.out.println(nowTime() + "用户状态检测任务执行完成");
                    infoLabel.setText(nowTime() + "用户状态检测任务执行完成");
                }catch(Exception e){
                    System.out.println("状态检测异常" + e.getMessage());
                }
            }, 0, CHECK_INTERVAL, TimeUnit.SECONDS);

            // 抖动执行任务检测
            scheduled.scheduleAtFixedRate(() -> {
                if(shouldJitter()){
                    performJitter(robot);
                }
                System.out.println(nowTime() + "抖动执行任务检测执行完成");
                infoLabel.setText(nowTime() + "抖动执行任务检测执行完成");
            }, 0, JITTER_INTERVAL, TimeUnit.SECONDS);

            // 启动防锁屏线程
            //startAntiLockThread(robot);
            scheduled.scheduleAtFixedRate(() -> {
                try{

                    // 模拟SCROLL LOCK键按下/释放（不影响正常使用）
                    robot.keyPress(KeyEvent.VK_SCROLL_LOCK);
                    robot.setAutoDelay(10);
                    robot.keyRelease(KeyEvent.VK_SCROLL_LOCK);

                    System.out.println(nowTime() + "I am live");
                    infoLabel.setText(nowTime() + "模拟按键任务执行完成");
                }catch(Exception e){
                    statusLabel.setText("模拟按键防锁任务错误: " + e.getMessage());
                }
            }, 0, KEY_EVENT_INTERVAL, TimeUnit.SECONDS);
            // 启动防锁屏线程-2 鼠标抖动
            //updateMouseThread(robot);
        }catch(Exception e){
            statusLabel.setText("错误: " + e.getMessage());
        }

    }

    // 执行抖动  同步防止重复触发
    private synchronized void performJitter(Robot robot){
        try{
            isJittering.set(false);
            Point originalPos = MouseInfo.getPointerInfo().getLocation();
            // 产生随机偏移（1-3像素）
            int dx = (int) ((Math.random() * 3 ) +1);
            int dy = (int) ((Math.random() * 3 ) +1);

            // 四个方向微移动
            robot.mouseMove(originalPos.x +dx, originalPos.y +dy);
            robot.delay(50);
            robot.mouseMove(originalPos.x, originalPos.y);
            robot.delay(50);
            robot.mouseMove(originalPos.x -dx, originalPos.y -dy);
            robot.delay(50);
            robot.mouseMove(originalPos.x , originalPos.y );
            System.out.println(nowTime() + "执行防锁屏抖动");
        }catch(Exception e){
            System.out.println("执行防锁屏抖动异常" + e.getMessage());
        }finally{
            isJittering.set(false);
        }
    }

    // 抖动条件判断
    private boolean shouldJitter(){
        // 鼠标静止超过 INACTIVITY_THRESHOLD 秒 并且抖动条件成立
        System.out.println(nowTime() +"抖动条件判断1 " + ((System.currentTimeMillis() - lastActiveTime.get()) > INACTIVITY_THRESHOLD * 1000L ));
        System.out.println(nowTime() +"抖动条件判断2 " + !isJittering.get());
        return (System.currentTimeMillis() - lastActiveTime.get()) > INACTIVITY_THRESHOLD * 1000L && !isJittering.get() ;
    }

    // 更新鼠标抖动状态
    private static void updateJitterState(Robot robot){
        long inactiveTime = (System.currentTimeMillis() - lastActiveTime.get() )/1000 ;
        System.out.printf((nowTime() +"系统静默： %02d秒 | 抖动状态: %-4s "),inactiveTime, isJittering.get() ? "ON" : "OFF");
        // 自动恢复机制： 如果抖动期间用户手动移动鼠标
        if(isJittering.get() && inactiveTime < INACTIVITY_THRESHOLD - 5 ){
            isJittering.set(false);
        }
    }

    // 用户活动检测
    private static synchronized void checkUserActivity(Point currentPosition){
        if(lastPosition.distance(currentPosition) > MOVE_THRESHOLD){
            lastActiveTime.set(System.currentTimeMillis());
            if(isJittering.get()){
                System.out.println("检测到用户活动，停止抖动");
                isJittering.set(false);
            }
        }
        lastPosition = currentPosition ;
        System.out.println(nowTime() +"用户活动检测 lastPosition = " +lastPosition.x +", "+lastPosition.y);
    }

    private void updateMouseThread(Robot robot) {
        new Thread(() -> {
            while (true) {
                // 隐藏鼠标移动
                int x = (int) (Math.random() * 2);
                int y = (int) (Math.random() * 2);

                robot.mouseMove(x, y);
                robot.delay(30);
                robot.mouseMove(-x, -y);
            }
        }).start();

    }



    private static String nowTime(){
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS "));
    }
}

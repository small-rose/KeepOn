package small.rose.listener;

import small.rose.utils.AppSettings;
import small.rose.utils.LogUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
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

    /**
     * 模拟键盘按键间隔
     */
    private static final int KEY_EVENT_INTERVAL = 10 ;

    /**
     * 上次激活时间
     */
    private static final AtomicLong LAST_ACTIVE_TIME = new AtomicLong(System.currentTimeMillis());
    /*
     * 是否正常抖动
     */
    private static final AtomicBoolean IS_JITTERING = new AtomicBoolean(false);
    /**
     * 初始化鼠标位置
     */
    private static Point lastPosition = new Point(0,0);

    private static boolean showLog = Boolean.FALSE ;

    public KeepActiveListener(){
    }
    public KeepActiveListener(JLabel statusLabel, JLabel infoLabel){
        this.statusLabel = statusLabel;
        this.infoLabel = infoLabel;
    }

    public void keepOn(){
        try{
            final ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(3);
            final Robot robot = new Robot();
            robot.setAutoDelay(10);
            // 状态检测任务
            scheduled.scheduleAtFixedRate(() -> {
                showLog = AppSettings.getInstance().isLog();
                try{
                    Point currentPosition = MouseInfo.getPointerInfo().getLocation();
                    checkUserActivity(currentPosition);
                    updateJitterState(robot);
                    infoLabel.setText(LogUtil.nowTime() + "用户状态检测...");
                }catch(Exception e){
                    LogUtil.log(true, "状态检测异常" + e.getMessage());
                }
            }, 0, CHECK_INTERVAL, TimeUnit.SECONDS);

            // 抖动执行任务检测
            scheduled.scheduleAtFixedRate(() -> {
                if(shouldJitter()){
                    performJitter(robot);
                }
                infoLabel.setText(LogUtil.nowTime() + "抖动执行任务检测...");
            }, 0, JITTER_INTERVAL, TimeUnit.SECONDS);

            // 启动防锁屏线程
            //startAntiLockThread(robot);
            scheduled.scheduleAtFixedRate(() -> {
                try{

                    // 模拟SCROLL LOCK键按下/释放（不影响正常使用）
                    robot.keyPress(KeyEvent.VK_SCROLL_LOCK);
                    robot.setAutoDelay(10);
                    robot.keyRelease(KeyEvent.VK_SCROLL_LOCK);

                    LogUtil.log(showLog, "I am live");
                    infoLabel.setText(LogUtil.nowTime() + "模拟按键任务执行...");
                }catch(Exception e){
                    statusLabel.setText("模拟按键防锁任务错误: " + e.getMessage());
                }
            }, 0, KEY_EVENT_INTERVAL, TimeUnit.SECONDS);
            // 启动防锁屏线程-2 鼠标抖动
            //updateMouseThread(robot);
        }catch(Exception e){
            statusLabel.setText("错误: " + e.getMessage());
            LogUtil.log(true,"程序出错："+e.getMessage());
        }

    }

    // 执行抖动  同步防止重复触发
    private synchronized void performJitter(Robot robot){
        try{
            IS_JITTERING.set(false);
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
            //LogUtil.log(showLog, "执行防锁屏抖动");
        }catch(Exception e){
            LogUtil.log(true, "执行防锁屏抖动异常" + e.getMessage());
        }finally{
            IS_JITTERING.set(false);
        }
    }

    // 抖动条件判断
    private boolean shouldJitter(){
        // 鼠标静止超过 INACTIVITY_THRESHOLD 秒 并且抖动条件成立
        LogUtil.log(showLog, "抖动条件判断1 " + ((System.currentTimeMillis() - LAST_ACTIVE_TIME.get()) > INACTIVITY_THRESHOLD * 1000L ));
        LogUtil.log(showLog, "抖动条件判断2 " + !IS_JITTERING.get());
        return (System.currentTimeMillis() - LAST_ACTIVE_TIME.get()) > INACTIVITY_THRESHOLD * 1000L && !IS_JITTERING.get() ;
    }

    // 更新鼠标抖动状态
    private static void updateJitterState(Robot robot){
        long inactiveTime = (System.currentTimeMillis() - LAST_ACTIVE_TIME.get() )/1000 ;
        LogUtil.log(showLog,  "系统鼠标静默： "+inactiveTime+"秒 | 抖动状态: "+(IS_JITTERING.get() ? "ON" : "OFF"));
        // 自动恢复机制： 如果抖动期间用户手动移动鼠标
        if(IS_JITTERING.get() && inactiveTime < INACTIVITY_THRESHOLD - 5 ){
            IS_JITTERING.set(false);
        }
    }

    // 用户活动检测
    private static synchronized void checkUserActivity(Point currentPosition){
        if(lastPosition.distance(currentPosition) > MOVE_THRESHOLD){
            LAST_ACTIVE_TIME.set(System.currentTimeMillis());
            if(IS_JITTERING.get()){
                LogUtil.log(showLog, "检测到用户活动，停止抖动");
                IS_JITTERING.set(false);
            }
        }
        lastPosition = currentPosition ;
        LogUtil.log(showLog,  "用户活动检测 lastPosition = " +lastPosition.x +", "+lastPosition.y);
    }

}

package small.rose.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Project : keepOn
 * @Author : zhangxiaocai
 * @Description : [ LogUtil ] 说明：无
 * @Function :  功能说明：无
 * @Date ：2025/3/27 19:56
 * @Version ： 1.0
 **/
public class LogUtil {

    public static void log(boolean log,  String content){
        if (log) {
            System.out.println(nowTime() + " " +content);
        }
    }
    public static String nowTime(){
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS "));
    }
}

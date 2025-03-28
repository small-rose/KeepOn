package small.rose.utils;

/**
 * @Project : keepOn
 * @Author : zhangxiaocai
 * @Description : [ AppSettings ] 说明：无
 * @Function :  功能说明：无
 * @Date ：2025/3/27 19:52
 * @Version ： 1.0
 **/
public class AppSettings {

    private static AppSettings instance = new AppSettings();
    private boolean log = Boolean.FALSE;

    private AppSettings() {}  // 禁止外部实例化

    public static AppSettings getInstance() {
        return instance;
    }


    public boolean isLog() {
        return log;
    }

    public void setLog(boolean log) {
        this.log = log;
    }
}

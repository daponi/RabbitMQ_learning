package com.atguigu.www.util;

/**
 * 睡眠工具类
 */
public class SleepUtils {

    public static void sleep(int second){
        try {
            //睡眠多少秒
            Thread.sleep(second*1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

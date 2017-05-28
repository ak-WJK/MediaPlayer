package com.atguigu.mediaplayer.app;

import android.app.Application;
import android.content.Context;

import com.atguigu.mediaplayer.utils.LogUtils;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import org.xutils.BuildConfig;
import org.xutils.x;

/**
 * Created by Administrator on 2017/5/22.
 */

public class MyApplication extends Application {

    public static Context mContext;
    public static LogUtils.Builder IBuilder;

    @Override
    public void onCreate() {
        super.onCreate();

//        x.Ext.init(this);
//        x.Ext.setDebug(BuildConfig.DEBUG); // 是否输出debug日志, 开启debug会影响性能.

        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);


        // 将“12345678”替换成您申请的 APPID，申请地址： http://www.xfyun.cn
// 请勿在“ =”与 appid 之间添加任务空字符或者转义符
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=59292f3f");



        mContext = this;

        IBuilder = new LogUtils.Builder()
                .setLogSwitch(true)
                .setGlobalTag("TAG")// 设置log全局标签，默认为空
                // 当全局标签不为空时，我们输出的log全部为该tag，
                // 为空时，如果传入的tag为空那就显示类名，否则显示tag
                .setLog2FileSwitch(false)// 打印log时是否存到文件的开关，默认关
                .setBorderSwitch(true)// 输出日志是否带边框开关，默认开
                .setLogFilter(LogUtils.V);// log过滤器，和logcat过滤器同理，默认Verbose

    }
}

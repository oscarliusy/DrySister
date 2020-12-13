package com.spl.drysister;

import android.app.Application;
import android.content.Context;

import com.spl.drysister.utils.CrashHandler;

/**
 * author: Oscar Liu
 * Date: 2020/12/11  12:23
 * Application类
 */
public class DrySisterApp extends Application {
  private static Context context;

  @Override
  public void onCreate() {
    super.onCreate();
    context = this;
    CrashHandler.getInstance().init(this); //崩溃日志采集类初始化
  }

  public static DrySisterApp getContext(){
    return (DrySisterApp) context;
  }
}

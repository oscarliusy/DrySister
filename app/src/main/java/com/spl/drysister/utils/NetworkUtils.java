package com.spl.drysister.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * author: Oscar Liu
 * Date: 2020/12/10  20:24
 * 网络相关工具类
 */
public class NetworkUtils {
  //获取网络信息
  private static NetworkInfo getActiveNetworkInfo(Context context){
    /**
     * ConnectivityManager:负责查询网络连接状态以及在连接状态有变化的时候发出通知
     * 1、  监视网络状态，包括（Wi-Fi、GPRS、UMTS等）
     * 2、  当网络状态发生变化时发送广播通知
     * 3、  当网络连接失败后会尝试连接其他网络
     * 4、  为App提供API，用于获取网络状态信息等
     * getActiveNetworkInfo():返回当前可用的网络对象
     */
    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    return cm.getActiveNetworkInfo();
  }

  //判断网络是否可用
  public static boolean isAvailable(Context context){
    NetworkInfo info = getActiveNetworkInfo(context);
    return info != null && info.isAvailable();
  }
}

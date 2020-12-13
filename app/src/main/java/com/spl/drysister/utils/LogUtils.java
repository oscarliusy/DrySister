package com.spl.drysister.utils;

import android.util.Log;

import com.spl.drysister.BuildConfig;

/**
 * author: Oscar Liu
 * Date: 2020/12/12  21:01
 * Log工具类
 * 替代自带的Log类
 * build时自动屏蔽
 */
public class LogUtils {
  private final static String TAG="DrySister";

  public static void v(String msg){ if (BuildConfig.DEBUG) Log.v(TAG,msg);}

  public static void v(String tag,String msg){ if (BuildConfig.DEBUG) Log.v(tag,msg);}

  public static void d(String msg){ if (BuildConfig.DEBUG) Log.v(TAG,msg);}

  public static void d(String tag,String msg){ if (BuildConfig.DEBUG) Log.v(tag,msg);}

  public static void i(String msg){ if (BuildConfig.DEBUG) Log.v(TAG,msg);}

  public static void i(String tag,String msg){ if (BuildConfig.DEBUG) Log.v(tag,msg);}

  public static void w(String msg){ if (BuildConfig.DEBUG) Log.v(TAG,msg);}

  public static void w(String tag,String msg){ if (BuildConfig.DEBUG) Log.v(tag,msg);}

  public static void e(String msg){ if (BuildConfig.DEBUG) Log.v(TAG,msg);}

  public static void e(String tag,String msg){ if (BuildConfig.DEBUG) Log.v(tag,msg);}
}

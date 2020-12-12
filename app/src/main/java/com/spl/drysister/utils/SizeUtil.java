package com.spl.drysister.utils;

import android.content.Context;

/**
 * author: Oscar Liu
 * Date: 2020/12/10  18:44
 * 尺寸转换工具类
 */
public class SizeUtil {

  //dp转px
  public static int dp2px(Context context, float dpValue){
    final float scale = context.getResources().getDisplayMetrics().density;
    return (int)(dpValue * scale + 0.5f);
  }

  //px转dp
  public static int px2dp(Context context, float pxValue){
    final float scale = context.getResources().getDisplayMetrics().density;
    return (int)(pxValue / scale + 0.5f);
  }
}

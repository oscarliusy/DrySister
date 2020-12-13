package com.spl.drysister.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.spl.drysister.controller.activity.MainActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * author: Oscar Liu
 * Date: 2020/12/12  21:11
 * 崩溃日志采集类
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

  private static final String TAG = "CrashHandler";

  private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

  private Map<String,String> infos = new HashMap<>();// 用于存储设备信息和异常信息

  private static CrashHandler instance;

  private Thread.UncaughtExceptionHandler mDefaultHandler; //系统默认UncaughtExceptionHandler

  private Context mContext;

  private CrashHandler(){}

  /** 单例模式 */
  public static CrashHandler getInstance(){
    if(instance == null){
      instance = new CrashHandler();
    }
    return instance;
  }

  /** 初始化 */
  public void init(Context context){
    mContext = context;
    mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    Thread.setDefaultUncaughtExceptionHandler(this);
  }

  /** 将字符串写入日志文件，返回文件名
   *  ！！
   *  要创建文件夹和写入文件，必须动态申请权限
   *  参见MainActivity中的initPermission（）
   */
  private String writeFile(String sb) throws Exception{
    String time = formatter.format(new Date());
    //文件名
    String fileName = "crash-" + time + ".log";
    //判断存储卡是否可用
    if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
      String path = getGlobalPath();
      File dir = new File(path);
      //判断crash目录是否存在
      //mkdir:已知存在父级路径时用 ， mkdirs：如果没有父级路径，会一路创建出来
      if(!dir.exists()) dir.mkdirs();
      //流写入
      FileOutputStream fos = new FileOutputStream(path + fileName,true);
      fos.write(sb.getBytes());
      fos.flush();//刷新
      fos.close();
    }
    return fileName;
  }

  /**获取Crash文件夹的存储路径*/
  private String getGlobalPath() {
    return Environment.getExternalStorageDirectory().getAbsolutePath()
        + File.separator + "Crash" + File.separator;
    //return Environment.getExternalStorageDirectory().getAbsolutePath();
  }

  /** 采集应用版本与设备信息 */
  private void getDeviceInfo(Context context){
    //获取APP版本
    try {
      PackageManager pm = context.getPackageManager();
      PackageInfo info = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
      if(info != null){
        infos.put("VersionName",info.versionName);
        infos.put("VersionCode",info.versionCode + "");
      }
    } catch (PackageManager.NameNotFoundException e) {
      LogUtils.e(TAG,"an error occured when collect package info");
    }

  }

  /** 把错误信息写入文件中，返回文件名称*/
  private String saveCrashInfoToFile(Throwable throwable) throws Exception{
    //异步字符串操作类
    StringBuilder sb = new StringBuilder();
    try{
      //拼接时间，构建时间格式，将时间传入
      SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      String date = sDateFormat.format(new java.util.Date());//需要研究一下时间操作
      sb.append("\r\n").append(date).append("\n");

      //拼接版本信息与设备信息，将infos中所有k-v都加入sb
      //Map.Entry代表一组<k,v>对，可以用来遍历HashMap.并取出其中的k，v值
      for(Map.Entry<String,String> entry : infos.entrySet()){
        String key = entry.getKey();
        String value = entry.getValue();
        sb.append(key).append("=").append(value).append("\n");
      }

      //获取崩溃日志信息
      Writer writer = new StringWriter();
      //创建不带自动刷新的PrintWriter， 是字符类型的打印输出流
      PrintWriter printWriter = new PrintWriter(writer);
      //打印异常？
      throwable.printStackTrace(printWriter);
      printWriter.flush();
      printWriter.close();
      String result = writer.toString();
      //拼接日志奔溃信息
      sb.append(result);
      //写入文件
      return writeFile(sb.toString());
    }catch (Exception e){
      //异常处理
      Log.e(TAG,"an error occured while writing file...",e);
      sb.append("an error occured while writing file...\r\n");
      writeFile(sb.toString());
    }
    return null;
  }

  /** 自定义错误处理，错误信息采集，日志文件保存，如果处理了返回True，否则返回false*/
  private boolean handleException(Throwable throwable){
    if(throwable == null) return false;
    try{
      new Thread(){
        @Override
        public void run() {
          Looper.prepare();
          Toast.makeText(mContext, "程序出现异常，即将重启", Toast.LENGTH_SHORT).show();
          Looper.loop();
        }
      }.start();
      //获取设备信息，写入infos
      getDeviceInfo(mContext);

      //将异常写入日志文件
      saveCrashInfoToFile(throwable);
      SystemClock.sleep(1000);
    }catch(Exception e){
      e.printStackTrace();
    }
    return true;
  }

  @Override
  public void uncaughtException(@NonNull Thread thread, @NonNull Throwable et) {
    //首先handleException处理异常，正常捕获返回fileName
    if(!handleException(et) && mDefaultHandler != null){
      mDefaultHandler.uncaughtException(thread, et);
    } else {
      //异常被正常捕获后，重启app
      Intent intent = null;
      //intent = mContext.getPackageManager().getLaunchIntentForPackage(mContext.getPackageName());
      intent = new Intent(mContext, MainActivity.class);
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      intent.putExtra("crash",true);
      mContext.startActivity(intent);
//      PendingIntent restartIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);
//
//      AlarmManager mgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
//      mgr.set(AlarmManager.RTC,System.currentTimeMillis() + 1000,restartIntent); //1秒后重启应用

      //杀死当前进程
      android.os.Process.killProcess(android.os.Process.myPid());
      System.exit(0);
      //5.0以下版本建议回收资源，5.0以上无效
      if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
        System.gc();
      }

    }
  }
}























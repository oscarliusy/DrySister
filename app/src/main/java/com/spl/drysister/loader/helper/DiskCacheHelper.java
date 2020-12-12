package com.spl.drysister.loader.helper;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.StatFs;
import android.util.Log;

import com.jakewharton.disklrucache.DiskLruCache;
import com.spl.drysister.loader.SisterCompress;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * author: Oscar Liu
 * Date: 2020/12/10  14:34
 * 磁盘缓存相关
 */
public class DiskCacheHelper {

  private static final String TAG = "DiskCacheHelper";
  private static final long DISK_CACHE_SIZE = 1024 * 1024 * 50;//设置磁盘缓存区的大小为：50MB
  private static final int DISK_CACHE_INDEX = 0;

  private Context mContext;
  private DiskLruCache mDiskLruCache;
  private SisterCompress mCompress;
  private boolean mIsDiskLruCacheCreated = false;

  public DiskCacheHelper(Context mContext){
    this.mContext = mContext;
    mCompress = new SisterCompress();
    File diskCacheDir = getDiskCacheDir(mContext,"diskCache");
    if(!diskCacheDir.exists()){
      diskCacheDir.mkdir();
    }
    if(getUsableSpace(diskCacheDir) > DISK_CACHE_SIZE){
      try {
        mDiskLruCache = DiskLruCache.open(diskCacheDir,1,1,DISK_CACHE_SIZE);
        mIsDiskLruCacheCreated = true;
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  //获得磁盘缓存的目录
  public File getDiskCacheDir(Context context, String dirName){
    //判断机身存储是否可用
    boolean externalStorageAvailable = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    final String cachePath;
    if(externalStorageAvailable){
      cachePath = context.getExternalCacheDir().getPath();
    }else{
      cachePath = context.getCacheDir().getPath();
    }
    Log.v(TAG,"diskCachePath = " + cachePath);
    return new File(cachePath + File.separator + dirName);
  }

  //查询可用空间的大小（兼容2.3以下版本)
  @TargetApi(Build.VERSION_CODES.GINGERBREAD)
  private long getUsableSpace(File path){
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD){
      return path.getUsableSpace();
    }
    //StatFs:检索有关文件系统空间的总体信息
    final StatFs stats = new StatFs(path.getPath());
    /**
     * stats.getBlockSizeLong():文件系统上块的大小（以字节为单位）
     * stats.getAvailableBlocksLong():文件系统上空闲且可供应用程序使用的块数
     */
    return stats.getBlockSizeLong()*stats.getAvailableBlocksLong();
  }

  /**
   *根据key加载磁盘缓存中的图片
   * Looper:此类包含基于MessageQueue设置和管理事件循环所需的代码.
   *        在循环器looper上定义了线程的准备，循环和退出
   */
  public Bitmap loadBitmapFromDiskCache(String key, int reqWidth, int reqHeight) throws IOException{
    Log.v(TAG,"加载磁盘缓存中的图片");
    //判断是否在主线程里操作
    if(Looper.myLooper() == Looper.getMainLooper()){
      throw new RuntimeException("不能在UI线程中加载图片");
    }
    if(mDiskLruCache == null){
      return null;
    }
    Bitmap bitmap = null;
    //获取磁盘缓存中的图片，添加到内存缓存中
    DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);//获取文件快照
    //如果存在文件快照，就获取文件描述符（FD），取出位图
    if(snapshot != null){
      FileInputStream fileInputStream = (FileInputStream) snapshot.getInputStream(DISK_CACHE_INDEX);
      FileDescriptor fileDescriptor = fileInputStream.getFD();
      bitmap = mCompress.decodeBitmapFromFileDescriptor(fileDescriptor,reqWidth,reqHeight);
    }
    return bitmap;
  }

  //将图片字节流缓存到磁盘，并返回一个Bitmap用于显示
  public Bitmap saveImgByte(String key, int reqWidth, int reqHeight, byte[] bytes){
    //判断是否在主线程中操作
    if(Looper.myLooper() == Looper.getMainLooper()){
      throw new RuntimeException("不能在UI线程里做网络操作");
    }
    if(mDiskLruCache == null){
      return null;
    }
    try {
      DiskLruCache.Editor editor = mDiskLruCache.edit(key);
      if(editor != null){
        OutputStream output = editor.newOutputStream(DISK_CACHE_INDEX);
        output.write(bytes);
        output.flush();
        editor.commit();
        output.close();
        return loadBitmapFromDiskCache(key,reqWidth,reqHeight);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public DiskLruCache getDiskLruCache(){
    return mDiskLruCache;
  }

  public boolean getIsDiskCacheCreate(){
    return mIsDiskLruCacheCreated;
  }

  public void setIsDistCacheCreate(boolean status){
    this.mIsDiskLruCacheCreated = status;
  }

}
























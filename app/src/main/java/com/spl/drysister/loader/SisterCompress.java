package com.spl.drysister.loader;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.FileDescriptor;

/**
 * author: Oscar Liu
 * Date: 2020/12/10  12:27
 * 图片压缩类
 * 1.压缩Resource图片
 * 2.压缩FileDescriptor图片
 */
public class SisterCompress {

  private static final String TAG = "ImageCompress";

  public SisterCompress() {
  }

  //将保存在drawable中资源图片压缩后返回
  public Bitmap decodeBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight){
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;//仅获取图片资源的宽高，不获取图片，不分配内存
    BitmapFactory.decodeResource(res, resId,options);//解码资源
    //计算缩放比例
    options.inSampleSize = computeSimpleSize(options,reqWidth,reqHeight);
    options.inJustDecodeBounds = false;
    //返回压缩后的位图
    return BitmapFactory.decodeResource(res,resId,options);
  }

  //根据文件描述符获取磁盘中的图片，压缩后返回
  public Bitmap decodeBitmapFromFileDescriptor(FileDescriptor descriptor, int reqWidth, int reqHeight){
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    //从文件获取图片的宽高
    BitmapFactory.decodeFileDescriptor(descriptor,null,options);
    //计算并设置压缩比例
    options.inSampleSize = computeSimpleSize(options,reqWidth,reqHeight);
    options.inJustDecodeBounds = false;
    //返回压缩后的位图
    return BitmapFactory.decodeFileDescriptor(descriptor,null,options);
  }

  //计算缩放比例，比较原图和需求，每次压缩一半
  private int computeSimpleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
    if(reqWidth == 0 || reqHeight == 0){
      return 1;
    }
    //压缩比例
    int inSampleSize = 1;
    //原图的高宽
    final int height = options.outHeight;
    final int width = options.outWidth;
    Log.v(TAG,"原图大小为：" + width + "x" +height);
    //如果原图比需求的高宽的大，则进行压缩
    if(height > reqHeight || width >reqWidth){
      final int halfHeight = height / 2;
      final int halfWidth = width / 2;
      //每次压缩一半，直到小于需求的高宽
      while((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth){
        inSampleSize *= 2;
      }
    }
    Log.v(TAG,"inSampleSize = " + inSampleSize);
    return inSampleSize;
  }
}
























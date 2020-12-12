package com.spl.drysister.loader;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * author: Oscar Liu
 * Date: 2020/12/10  18:52
 * 加载结果类
 */
public class LoaderResult {
  public ImageView img;
  public String uri;
  public Bitmap bitmap;
  public int reqWidth;
  public int reqHeight;

  public LoaderResult(ImageView img, String uri, Bitmap bitmap, int reqWidth, int reqHeight){
    this.img = img;
    this.uri = uri;
    this.bitmap = bitmap;
    this.reqWidth = reqWidth;
    this.reqHeight = reqHeight;
  }
}

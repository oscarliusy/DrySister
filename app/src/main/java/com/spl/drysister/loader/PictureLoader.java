package com.spl.drysister.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * auther: Oscar Liu
 * Date: 2020/12/9  16:01
 * 请求网络图片url，并向控件加载图片
 */
public class PictureLoader {

  private ImageView loadImg;
  private String imgUrl;
  private byte[] picByte;

  Handler handler = new Handler(){
    @Override
    public void handleMessage(@NonNull Message msg) {
      super.handleMessage(msg);
      if(msg.what == 0x123){
        if(picByte != null){
          //byte[] -> Bitmap
          Bitmap bitmap = BitmapFactory.decodeByteArray(picByte,0,picByte.length);
          loadImg.setImageBitmap(bitmap);
        }
      }
    }
  };

  Runnable runnable = new Runnable() {
    @Override
    public void run() {
      try {
        URL url = new URL(imgUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        if(conn.getResponseCode() == 200){
          InputStream in = conn.getInputStream();
          ByteArrayOutputStream out = new ByteArrayOutputStream();
          byte[] bytes = new byte[1024];
          int length = -1;
          while((length = in.read(bytes)) != -1){
            out.write(bytes,0,length);
          }
          //网络Bitmap->byte[]
          picByte = out.toByteArray();
          in.close();
          out.close();
          handler.sendEmptyMessage(0x123);
        }

      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  };

  public PictureLoader() {
  }

  /**
   * 加载图片工具
   * 1.如果loadImg上已经加载了图片，则将其释放，回收内存
   * 2.启动线程，向网络请求图片，获取字节流，将所有数据保存在picByte中，发送handler
   * 3.handler接收到信号，将字节list转换为Bitmap，在控件上加载图片
   * @param loadImg 加载图片的控件
   * @param imgUrl  图片的地址
   */
  public void load(ImageView loadImg,String imgUrl){
    this.loadImg = loadImg;
    this.imgUrl = imgUrl;
    Drawable drawable = loadImg.getDrawable();
    if(drawable != null && drawable instanceof BitmapDrawable){
      Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
      //释放Bitmap的内存
      if(bitmap != null && !bitmap.isRecycled()){
        bitmap.recycle();
      }
    }
    new Thread(runnable).start();
  }
}

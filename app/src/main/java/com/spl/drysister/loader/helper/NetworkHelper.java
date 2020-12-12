package com.spl.drysister.loader.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * author: Oscar Liu
 * Date: 2020/12/10  13:18
 * 加载网络图片相关
 */
public class NetworkHelper {

  private static final String TAG = "NetworkHelper";

  private static final int IO_BUFFER_SIZE = 8 * 1024;

  //根据URL下载图片，返回Bitmap
  public static Bitmap downloadBitmapFromUrl(String picUrl){
    Bitmap bitmap = null;
    HttpURLConnection urlConnection = null;
    BufferedInputStream in = null;

    try {
      //发起请求
      final URL url = new URL(picUrl);
      urlConnection = (HttpURLConnection)url.openConnection();
      //按流获取数据
      in = new BufferedInputStream(urlConnection.getInputStream(),IO_BUFFER_SIZE);
      //将流数据解码为bitmap
      bitmap = BitmapFactory.decodeStream(in);
    } catch (IOException e) {
      e.printStackTrace();
      Log.e(TAG,"下载图片出错： " + e);
    }finally {
      //断开链接
      if(urlConnection != null){
        urlConnection.disconnect();
      }
      try {
        //关闭流
        in.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return bitmap;
  }

  //根据URL下载图片，返回byte数组
  public static byte[] downloadUrlToStream(String picUrl){
    InputStream in = null;
    ByteArrayOutputStream out = null;
    try{
      URL url = new URL(picUrl);
      HttpURLConnection conn = null;
      conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");
      conn.setReadTimeout(10000);
      if(conn.getResponseCode() == 200){
        in = conn.getInputStream();
        out = new ByteArrayOutputStream();
        byte[] bytes = new byte[1024];
        int length;
        while((length = in.read(bytes)) != -1){
          out.write(bytes,0,length);
        }
        return out.toByteArray();
      }
    }catch (Exception e){
      e.printStackTrace();
    }finally {
      try{
        if(in != null && out != null){
          in.close();
          out.close();
        }
      }catch (IOException e){
        e.printStackTrace();
      }
    }
    return null;
  }

  //URL转MD5
  public static String hashKeyFromUrl(String url){
    String cacheKey;
    try {
      final MessageDigest mDigest = MessageDigest.getInstance("MD5");
      mDigest.update(url.getBytes());
      cacheKey = bytesToHexString(mDigest.digest());
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
      cacheKey = String.valueOf(url.hashCode());
    }
    return cacheKey;
  }

  //字节数转MD5
  public static String bytesToHexString(byte[] bytes){
    StringBuilder sb = new StringBuilder();
    for(int i = 0; i < bytes.length; i++){
      String hex = Integer.toHexString(0xFF & bytes[i]);
      if(hex.length() == 1){
        sb.append(0);
      }
      sb.append(hex);
    }
    return sb.toString();
  }
}
















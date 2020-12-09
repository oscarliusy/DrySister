package com.spl.drysister.network;

import android.util.Log;

import com.spl.drysister.model.bean.Sister;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * author: Oscar Liu
 * Date: 2020/12/9  18:31
 */
public class SisterApi {

  private static final String TAG = "Network";
  private static final String BASE_URL = "https://gank.io/api/data/福利/";

  /**
   * 网络请求数据信息
   */
  public ArrayList<Sister> fetchSister(int count,int page){
    String fetchUrl = BASE_URL + count + "/" + page;
    ArrayList<Sister> sisters = new ArrayList<>();

    try {
      URL url = new URL(fetchUrl);
      HttpURLConnection conn = (HttpURLConnection)url.openConnection();
      conn.setInstanceFollowRedirects(true);
      conn.setConnectTimeout(5000);
      conn.setRequestMethod("GET");
      int code = conn.getResponseCode();
      Log.d(TAG,"Server response:" + code);
      if(code == 200){
        InputStream in = conn.getInputStream();
        byte[] data = readFromStream(in);
        String result = new String(data,"UTF-8");
        sisters = parseSister(result);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    Log.d(TAG,"sisters length:"+sisters.size());
    return sisters;
  }

  /**
   * 读取流中数据
   */
  private byte[] readFromStream(InputStream inputStream) throws Exception{
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    byte[] buffer = new byte[1024];
    int len;
    while(((len = inputStream.read(buffer)) != -1)){
      outputStream.write(buffer,0,len);
    }
    inputStream.close();
    return outputStream.toByteArray();
  }

  /**
   *解析返回的Json数据
   * 1.将字符串转为JSONArray
   * 2.将每一个arrayItem的数据取出来构造Sister对象
   * 3.构造sisters并返回
   */
  private ArrayList<Sister> parseSister(String content) throws Exception{
    ArrayList<Sister> sisters = new ArrayList<>();
    JSONObject object = new JSONObject(content);
    JSONArray array = object.getJSONArray("results");
    for(int i = 0; i < array.length(); i++){
      JSONObject results = (JSONObject)array.get(i);
      Sister sister = new Sister();

      sister.set_id(results.getString("_id"));
      sister.setCreatedAt(results.getString("createdAt"));
      sister.setDesc(results.getString("desc"));
      sister.setPublishedAt(results.getString("publishedAt"));
      sister.setSource(results.getString("source"));
      sister.setType(results.getString("type"));
      sister.setUrl(results.getString("url"));
      sister.setUsed(results.getBoolean("used"));
      sister.setWho(results.getString("who"));

      sisters.add(sister);

    }
    return sisters;
  }
}



























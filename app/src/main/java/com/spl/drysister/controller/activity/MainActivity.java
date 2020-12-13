package com.spl.drysister.controller.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.spl.drysister.R;
import com.spl.drysister.db.SisterDBHelper;
import com.spl.drysister.loader.PictureLoader;
import com.spl.drysister.loader.SisterLoader;
import com.spl.drysister.model.bean.Sister;
import com.spl.drysister.network.SisterApi;
import com.spl.drysister.utils.NetworkUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

  private Button previousBtn;
  private Button nextBtn;
  private ImageView showImg;

  private ArrayList<Sister> data;
  private int curPos = 0; //当前显示的是哪一张
  private int page = 1;   //当前页数
  //private PictureLoader loader;//是没用到
  private SisterApi sisterApi;
  private SisterTask sisterTask;
  private SisterLoader mLoader;
  private SisterDBHelper mDbHelper;

  String[] mPermissionList = new String[]{
      Manifest.permission.WRITE_EXTERNAL_STORAGE,
      Manifest.permission.READ_EXTERNAL_STORAGE,
      Manifest.permission.MOUNT_FORMAT_FILESYSTEMS
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    sisterApi = new SisterApi();
   // loader = new PictureLoader();
    mLoader = SisterLoader.getInstance(MainActivity.this);
    mDbHelper = SisterDBHelper.getInstance();

    //initData();

    initUI();

    //申请权限，SD卡文件读写，创建文件夹和文件
    initPermission();
  }

  private void initPermission() {
    ActivityCompat.requestPermissions(MainActivity.this,mPermissionList,100);
  }


  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    switch (requestCode){
      case 100:
        boolean writeExternalStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;//true
        boolean readExternalStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;//true
        //grantResults:[0,0], true true
        if(grantResults.length > 0 && writeExternalStorage && readExternalStorage){
          initData();
        }else{
          Toast.makeText(this, "请设置权限", Toast.LENGTH_SHORT).show();
        }
    }
  }

  private void initData() {
    data = new ArrayList<>();
    sisterTask = new SisterTask();
    sisterTask.execute();
  }

  private void initUI() {
    previousBtn = findViewById(R.id.btn_previous);
    nextBtn = findViewById(R.id.btn_next);
    showImg = findViewById(R.id.img_show);

    previousBtn.setOnClickListener(this);
    nextBtn.setOnClickListener(this);

  }

  @Override
  public void onClick(View v) {
    switch (v.getId()){
      case R.id.btn_previous:
        --curPos;
        if(curPos == 0){
          previousBtn.setVisibility(View.INVISIBLE);
        }
        if(curPos == data.size() - 1){
          sisterTask = new SisterTask();
          sisterTask.execute();
        }else if(curPos < data.size()){
          mLoader.bindBitmap(data.get(curPos).getUrl(),showImg,400,400);
        }
        break;
      case R.id.btn_next:
        //测试崩溃日志和app重启
        //int a = 1/0;
        previousBtn.setVisibility(View.VISIBLE);
        if(curPos < data.size()){
          ++curPos;
        }
        if(curPos > data.size() - 1){
          sisterTask = new SisterTask();
          sisterTask.execute();
        }else if(curPos < data.size()){
          mLoader.bindBitmap(data.get(curPos).getUrl(),showImg,400,400);
        }
        break;
      default:
        break;
    }
  }

  private class SisterTask extends AsyncTask<Void,Void,ArrayList<Sister>>{

    public SisterTask(){}

    @Override
    protected ArrayList<Sister> doInBackground(Void... voids) {
      ArrayList<Sister> result = new ArrayList<>();
      if(page < (curPos + 1) / 10 + 1){
        ++page;
      }
      //判断是否有网络
      if(NetworkUtils.isAvailable(getApplicationContext())){
        result = sisterApi.fetchSister(10,page);
        //查询数据库里有多少妹子，避免重复插入
        if(mDbHelper.getSistersCount() / 10 < page){
          mDbHelper.insertSisters(result);
        }
      }else{
        result.clear();
        result.addAll(mDbHelper.getSistersLimit(page - 1, 10));
      }
      return result;
    }

    @Override
    protected void onPostExecute(ArrayList<Sister> sisters) {
      super.onPostExecute(sisters);
      data.addAll(sisters);
      if(data.size() > 0 && curPos + 1 < data.size()){
        mLoader.bindBitmap(data.get(curPos).getUrl(),showImg,400,400);
      }
    }

    @Override
    protected void onCancelled() {
      super.onCancelled();
      sisterTask = null;
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if(sisterTask != null){
      sisterTask.cancel(true);
    }
  }
}
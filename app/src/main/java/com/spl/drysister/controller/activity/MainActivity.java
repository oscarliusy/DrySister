package com.spl.drysister.controller.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

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

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    sisterApi = new SisterApi();
   // loader = new PictureLoader();
    mLoader = SisterLoader.getInstance(MainActivity.this);
    mDbHelper = SisterDBHelper.getInstance();

    initData();

    initUI();
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
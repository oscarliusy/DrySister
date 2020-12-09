package com.spl.drysister.controller.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.spl.drysister.R;
import com.spl.drysister.loader.PictureLoader;
import com.spl.drysister.model.bean.Sister;
import com.spl.drysister.network.SisterApi;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

  private Button refreshBtn;
  private Button showBtn;
  private ImageView showImg;

  private PictureLoader loader;

  private ArrayList<Sister> data;
  private int curPos = 0;
  private int page = 3;
  private SisterApi sisterApi;
  private SisterTask sisterTask;
  private static final String TAG = "Network";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    sisterApi = new SisterApi();
    loader = new PictureLoader();

    initData();

    initUI();
  }

  private void initData() {
    data = new ArrayList<>();
  }

  private void initUI() {
    showBtn = findViewById(R.id.btn_show);
    showImg = findViewById(R.id.img_show);
    refreshBtn = findViewById(R.id.btn_refresh);
    showBtn.setOnClickListener(this);
    refreshBtn.setOnClickListener(this);

  }

  @Override
  public void onClick(View v) {
    switch (v.getId()){
      case R.id.btn_show:
        if(data != null && !data.isEmpty()){
          if(curPos > 9){
            curPos = 0;
          }
          Log.d(TAG,data.get(curPos).getUrl());
          loader.load(showImg,data.get(curPos).getUrl());
          curPos++;
        }
        break;
      case R.id.btn_refresh:
        page++;
        new SisterTask(page).execute();
        curPos = 0;
        break;
    }
  }

  private class SisterTask extends AsyncTask<Void,Void,ArrayList<Sister>>{

    private int page;

    public SisterTask(int page){
      this.page = page;
    }

    @Override
    protected ArrayList<Sister> doInBackground(Void... voids) {
      return sisterApi.fetchSister(10,page);
    }

    @Override
    protected void onPostExecute(ArrayList<Sister> sisters) {
      super.onPostExecute(sisters);
      data.clear();
      data.addAll(sisters);
      page++;
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
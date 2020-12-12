package com.spl.drysister.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.android.material.tabs.TabLayout;
import com.spl.drysister.DrySisterApp;
import com.spl.drysister.model.bean.Sister;

import java.util.ArrayList;
import java.util.List;

/**
 * author: Oscar Liu
 * Date: 2020/12/11  11:31
 * 数据库操作类
 */
public class SisterDBHelper {

  private static final String TAG = "SisterDBHelper";

  private static SisterDBHelper dbHelper;
  private SisterOpenHelper sqlHelper;
  private SQLiteDatabase db;

  private SisterDBHelper(){
    sqlHelper = new SisterOpenHelper(DrySisterApp.getContext());
  }

  /** 单例 */
  public static SisterDBHelper getInstance(){
    if(dbHelper == null ){
      //同步锁，会阻塞其他访问该对象的线程，按时间顺序执行
      synchronized (SisterDBHelper.class){
        if(dbHelper == null){
          dbHelper = new SisterDBHelper();
        }
      }
    }
    return dbHelper;
  }

  /** 构建一个妹子db操作对象 */
  private ContentValues buildSister(Sister sister){
    ContentValues contentValues = new ContentValues();
    contentValues.put(TableDefine.COLUMN_FULI_ID,sister.get_id());
    contentValues.put(TableDefine.COLUMN_FULI_CREATEAT,sister.getCreatedAt());
    contentValues.put(TableDefine.COLUMN_FULI_DESC,sister.getDesc());
    contentValues.put(TableDefine.COLUMN_FULI_PUBLISHEDAT,sister.getPublishedAt());
    contentValues.put(TableDefine.COLUMN_FULI_SOURCE,sister.getSource());
    contentValues.put(TableDefine.COLUMN_FULI_TYPE,sister.getType());
    contentValues.put(TableDefine.COLUMN_FULI_URL,sister.getUrl());
    contentValues.put(TableDefine.COLUMN_FULI_USED,sister.getUsed());
    contentValues.put(TableDefine.COLUMN_FULI_WHO,sister.getWho());
    return contentValues;
  }

  /** 插入一个妹子 */
  public void insertSister(Sister sister){
    db = getWriteableDB();
    ContentValues contentValues = null;
    contentValues = buildSister(sister);
    db.insert(TableDefine.TABLE_FULI,null,contentValues);
    closeIO(null);
  }

  /** 插入一群妹子（使用事务） */
  public void insertSisters(ArrayList<Sister> sisters){
    db = getWriteableDB();
    db.beginTransaction();//用来对事物打包处理，如果没有成功标记，将会被整体回滚
    try{
      for(Sister sister:sisters){
        ContentValues contentValues = null;
        contentValues = buildSister(sister);
        db.insert(TableDefine.TABLE_FULI,null,contentValues);
      }
      db.setTransactionSuccessful();//成功则整体提交，失败则全部回滚
    }finally {
      if(db != null && db.isOpen()){
        db.endTransaction();
        closeIO(null);
      }
    }
  }

  /** 删除妹子（根据id） */
  public void deleteSister(String _id){
    db = getWriteableDB();
    db.delete(TableDefine.TABLE_FULI,"_id =?",new String[]{_id});
    closeIO(null);
  }

  /** 删除所有妹子 */
  public void deleteAllSisters(){
    db = getWriteableDB();
    db.delete(TableDefine.TABLE_FULI,null,null);
    closeIO(null);
  }

  /** 更新妹子信息（根据_id) */
  public void updateSister(String _id,Sister sister){
    db = getWriteableDB();
    ContentValues contentValues = buildSister(sister);
    db.update(TableDefine.TABLE_FULI,contentValues,"_id =?",new String[]{_id});
    closeIO(null);
  }

  /** 查询当前表中有多少妹子 */
  public int getSistersCount(){
    db = getReadableDB();
    Cursor cursor = db.rawQuery("SELECT COUNT (*) FROM " + TableDefine.TABLE_FULI,null);
    cursor.moveToFirst();
    int count = cursor.getInt(0);
    Log.v(TAG,"count: " + count);
    closeIO(cursor);
    return count;
  }

  /** 分页查询妹子，参数为当前页和每页数量，页数从0开始算
   * @param limit startPos + "," + limit: 从第startPos行开始，向下查询limit行数据
   *  */
  public List<Sister> getSistersLimit(int curPage,int limit){
    db = getReadableDB();
    List<Sister> sisters = new ArrayList<>();
    String startPos = String.valueOf(curPage * limit);//数据开始位置
    if( db != null){
      Cursor cursor = db.query(TableDefine.TABLE_FULI,new String[]{
          TableDefine.COLUMN_FULI_ID, TableDefine.COLUMN_FULI_CREATEAT,
          TableDefine.COLUMN_FULI_DESC, TableDefine.COLUMN_FULI_PUBLISHEDAT,
          TableDefine.COLUMN_FULI_SOURCE, TableDefine.COLUMN_FULI_TYPE,
          TableDefine.COLUMN_FULI_URL, TableDefine.COLUMN_FULI_USED,
          TableDefine.COLUMN_FULI_WHO,
      },null,null,null,null,TableDefine.COLUMN_ID,startPos + "," + limit);
      while(cursor.moveToNext()){
        Sister sister = new Sister();
        sister.set_id(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_ID)));
        sister.setCreatedAt(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_CREATEAT)));
        sister.setDesc(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_DESC)));
        sister.setPublishedAt(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_PUBLISHEDAT)));
        sister.setSource(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_SOURCE)));
        sister.setType(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_TYPE)));
        sister.setUrl(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_URL)));
        sister.setUsed(cursor.getInt(cursor.getColumnIndex(TableDefine.COLUMN_FULI_USED)));
        sisters.add(sister);
      }
      closeIO(cursor);
    }
    return sisters;
  }

  /** 查询所有妹子 */
  public List<Sister> getAllSisters(){
    db = getReadableDB();
    List<Sister> sisters = new ArrayList<>();
    Cursor cursor = db.rawQuery("SELECT * FROM " + TableDefine.TABLE_FULI,null);
    cursor.moveToFirst();
    while(cursor.moveToNext()){
      Sister sister = new Sister();
      sister.set_id(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_ID)));
      sister.setCreatedAt(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_CREATEAT)));
      sister.setDesc(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_DESC)));
      sister.setPublishedAt(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_PUBLISHEDAT)));
      sister.setSource(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_SOURCE)));
      sister.setType(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_TYPE)));
      sister.setUrl(cursor.getString(cursor.getColumnIndex(TableDefine.COLUMN_FULI_URL)));
      sister.setUsed(cursor.getInt(cursor.getColumnIndex(TableDefine.COLUMN_FULI_USED)));
      sisters.add(sister);
    }
    closeIO(cursor);
    return sisters;
  }

  /** 获得可写数据库的方法 */
  private SQLiteDatabase getWriteableDB(){
    return sqlHelper.getWritableDatabase();
  }

  /** 获得可读数据库的方法 */
  private SQLiteDatabase getReadableDB(){
    return sqlHelper.getReadableDatabase();
  }

  /** 关闭cursor和数据库的方法 */
  private void closeIO(Cursor cursor){
    if(cursor != null){
      cursor.close();
    }
    if(db != null){
      db.close();
    }
  }
}

























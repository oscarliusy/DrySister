package com.spl.drysister.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
 * author: Oscar Liu
 * Date: 2020/12/11  11:02
 */
public class SisterOpenHelper extends SQLiteOpenHelper {
  private static final String DB_NAME = "sister.db"; //数据库名
  private static final int DB_VERSION = 1; //数据库版本号
  public SisterOpenHelper(Context context) {
    super(context, DB_NAME, null, DB_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    String createTableSql = "CREATE TABLE IF NOT EXISTS " + TableDefine.TABLE_FULI + " ("
        + TableDefine.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
        + TableDefine.COLUMN_FULI_ID + " TEXT, "
        + TableDefine.COLUMN_FULI_CREATEAT + " TEXT, "
        + TableDefine.COLUMN_FULI_DESC + " TEXT, "
        + TableDefine.COLUMN_FULI_PUBLISHEDAT + " TEXT, "
        + TableDefine.COLUMN_FULI_SOURCE + " TEXT, "
        + TableDefine.COLUMN_FULI_TYPE + " TEXT, "
        + TableDefine.COLUMN_FULI_URL + " TEXT, "
        + TableDefine.COLUMN_FULI_USED + " BOOLEAN, "
        + TableDefine.COLUMN_FULI_WHO + " TEXT"
        + ")";
    db.execSQL(createTableSql);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

  }
}

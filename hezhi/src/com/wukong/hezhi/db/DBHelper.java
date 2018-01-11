package com.wukong.hezhi.db;

import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.ScreenUtil;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	public static String TABLE_NAME = "Unity";// 表名
	public static String PIC_ID = "picid";// 唯一的id
	public static String TYPE = "type";// 类型
	public static String CONTENT = "json";// 内容

	// 定义一个创建表Book的SQLite语句
	private static final String CREATE_MEDIA = "create table " + TABLE_NAME
			+ "(" + PIC_ID + " text," + TYPE + " text," + CONTENT + " text)";

	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_MEDIA);
		LogUtil.d("数据表创建成功");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists Unity"); // 如果存在表Unity，则删除该表
		onCreate(db); // 重新调用onCreate()，创建两张表
	}
}

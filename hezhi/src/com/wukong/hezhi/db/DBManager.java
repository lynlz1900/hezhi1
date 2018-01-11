package com.wukong.hezhi.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wukong.hezhi.constants.HezhiConfig;
import com.wukong.hezhi.utils.ContextUtil;

public class DBManager {
	private DBHelper dbHelper;
	private SQLiteDatabase db;
	private static DBManager instance;
	public static String UNITY = "10000";
	public static String JGPUSH = "10001";

	private DBManager() {
		dbHelper = new DBHelper(ContextUtil.getContext(), "hezhi.db", null, HezhiConfig.DB_VERSION);
		dbHelper.getWritableDatabase();
		db = dbHelper.getWritableDatabase(); // 获取返回的SQLiteDatabase对象
	}

	public static DBManager getInstance() {
		if (instance == null) {
			instance = new DBManager();
		}
		return instance;
	}

	/** 插入数据 */
	public void insert(String id, String type, String json) {
		ContentValues contentValues = new ContentValues();
		// 组装数据
		contentValues.put(DBHelper.PIC_ID, id);
		contentValues.put(DBHelper.TYPE, type);
		contentValues.put(DBHelper.CONTENT, json);
		db.insert(DBHelper.TABLE_NAME, null, contentValues); // 向表Unity中插入一条数据
	}

	/** 更新数据 */
	public void update(String id, String type, String json) {
		ContentValues contentValues = new ContentValues();
		// 组装数据
		contentValues.put(DBHelper.PIC_ID, id);
		contentValues.put(DBHelper.TYPE, type);
		contentValues.put(DBHelper.CONTENT, json);
		db.update(DBHelper.TABLE_NAME, contentValues, "picid=?",
				new String[] { id }); // 向表中更新一条数据
	}

	/** 通过类型查找数据库中的json集合 */
	public List<String> qurreyJsonsByTpye(String type) {
		List<String> list = new ArrayList<String>();

		Cursor cursor = db.rawQuery("select * from Unity where type=?",
				new String[] { type });

		// Cursor cursor = db.query(DBHelper.TABLE_NAME, null,
		// null, null, null, null, null);// 第一个是表名，其他六个参数填null，表示查询表中全部数据

		// 查询表中所有数据
		if (cursor.moveToFirst()) {
			// 遍历Cursor对象，取出数据
			do {
				String json = cursor.getString(cursor
						.getColumnIndex(DBHelper.CONTENT));
				list.add(json);
			} while (cursor.moveToNext());
			cursor.close();
		}
		Collections.reverse(list); // 倒序排列
		return list;
	}

	/** 查找数据库中的picId */
	public List<String> qurreyPicIds() {
		List<String> list = new ArrayList<String>();
		Cursor cursor = db.query(DBHelper.TABLE_NAME, null, null, null, null,
				null, null);// 第一个是表名，其他六个参数填null，表示查询表中全部数据
		// 查询表中所有数据
		if (cursor.moveToFirst()) {
			// 遍历Cursor对象，取出数据
			do {
				String picid = cursor.getString(cursor
						.getColumnIndex(DBHelper.PIC_ID));
				list.add(picid);
			} while (cursor.moveToNext());
			cursor.close();
		}
		return list;
	}

	/** 通过picId删除这条记录 */
	public void deleteById(String id) {
		db.delete(DBHelper.TABLE_NAME, "picid = ?", new String[] { id });// 参数：第一个是表名，第二个和第三个是限定条件，确定删除哪些行数据。
	}

	/** 通过picd查找这条记录 */
	public String qurreyById(String id) {
		String json = "";
		Cursor cursor = db.rawQuery("select * from Unity where picid=?",
				new String[] { String.valueOf(id) });
		if (cursor.moveToFirst()) {
			// 遍历Cursor对象，取出数据
			do {
				json = cursor
						.getString(cursor.getColumnIndex(DBHelper.CONTENT));
			} while (cursor.moveToNext());
			cursor.close();
		}

		return json;
	}

	/** 删除表 */
	public void deleteTable() {
		db.delete(DBHelper.TABLE_NAME, null, null);
	}
}

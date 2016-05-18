package com.entertera.pushbeginslib;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class UserDb {
	
	private static UserDb m_instance = new UserDb();
	private boolean bHasContext = false;
	//
	Context context;
	UserDbHelper mHelper;
	String strSql;
	SQLiteDatabase db;
	ContentValues row;
	//
	String strDebug;
	static String tag = "pushbegins_db";
	
	////
	private UserDb() { }
	
	public static UserDb getInstance() {
		return m_instance;
	}
	
	public void setContext(Context ctx) {
		if ( bHasContext ) 
			return;
		
		context = ctx;
		strSql = null;
		mHelper = new UserDbHelper(context);
		
		bHasContext = true;
	}
	////
	
	private UserDb(Context aContext) {
		context = aContext;
		strSql = null;
		mHelper = new UserDbHelper(context);
	}
	
	/////////////////////////////////////////////////////////////
	//
	public boolean setKeyValue(String strKey, String strValue) {
		boolean bRes = false;
		String dbValue = getValue(strKey);
		if ( strValue.equals(dbValue) ) {
			//strDebug = String.format("Already has same key/value [%s]/[%s]", strKey, strValue);
			//Log.i(tag, strDebug);
			return true;
		}
		if ( dbValue != null ) {
			deleteKey(strKey);
		}
		
		strSql = String.format(
				" INSERT INTO %s  ( mkey, mvalue ) " +
				" VALUES ( '%s', '%s' ); ", 
				DbDef.CONFIG_TABLE_NAME,  strKey, strValue);
		
		db = mHelper.getWritableDatabase();
		try {
			db.execSQL(strSql);
			bRes = true;
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			mHelper.close();
		}
		
		return bRes;
	}
	
	private boolean hasKey(String strKey) {
		strSql = String.format(
				"SELECT COUNT(mkey) FROM %s WHERE mkey = '%s'; ", 
				DbDef.CONFIG_TABLE_NAME, strKey );

		db = mHelper.getReadableDatabase();
		Cursor cursor;
		cursor = db.rawQuery(strSql, null);
		
		int nRes = 0;
		while ( cursor.moveToNext() ) {
			nRes = cursor.getInt(0);
		}
		cursor.close();
		mHelper.close();
		
		if ( nRes == 0 )
			return false;
		else
			return true;
	}
	
	public String getValue(String strKey) {
		if ( !hasKey(strKey) ) {
			//Log.i(tag, "no availabel key -> " + strKey);
			return null;
		}

		String strResult = null;
		strSql = String.format(
				"SELECT mvalue FROM %s WHERE mkey = '%s'; ", 
				DbDef.CONFIG_TABLE_NAME, strKey);
		
		db = mHelper.getReadableDatabase();
		Cursor cursor;
		cursor = db.rawQuery(strSql, null);
		
		while ( cursor.moveToNext() ) {
			strResult = cursor.getString(0);
		}
		cursor.close();
		mHelper.close();
		
		return strResult;
	}
	
	public void deleteKey(String strKey) {
		if ( !hasKey(strKey) ) {
			return;
		}
		
		strSql = String.format(
				"DELETE FROM %s WHERE mkey = '%s'; ", 
				DbDef.CONFIG_TABLE_NAME, strKey);
		
		db = mHelper.getWritableDatabase();
		try {
			db.execSQL(strSql);
			//Log.i(tag, "Delete key success -> " + strKey);
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			mHelper.close();
		}
	}
	
	public void deleteKeyValue(String strKey, String strValue) {
		if ( !hasKey(strKey) ) {
			//Log.i(tag, "no key to delete");
			return;
		}
		
		strSql = String.format(
				"DELETE FROM %s WHERE mkey = '%s' AND mvalue = '%s'; ", 
				DbDef.CONFIG_TABLE_NAME, strKey, strValue);
		
		db = mHelper.getWritableDatabase();
		try {
			db.execSQL(strSql);
			//Log.i(tag, "Delete key/value success -> " + strKey + "/" + strValue);
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			mHelper.close();
		}
	}
	
	////
	public boolean hasMessageId(long m_id) {
		strSql = String.format(
				"SELECT COUNT(m_id) FROM " + DbDef.MSG_CNT_TABLE_NAME +
				" WHERE m_id = " + m_id); 

		db = mHelper.getReadableDatabase();
		Cursor cursor;
		cursor = db.rawQuery(strSql, null);
		
		int nRes = 0;
		while ( cursor.moveToNext() ) {
			nRes = cursor.getInt(0);
		}
		cursor.close();
		mHelper.close();
		
		if ( nRes == 0 )
			return false;
		else
			return true;
	}
	
	//
	public boolean putMessageId(long m_id) {
		if ( hasMessageId(m_id) ) {
			return false;
		}
		
		boolean bResult = false; 
		strSql = String.format(
				" INSERT INTO " + DbDef.MSG_CNT_TABLE_NAME + 
				"  ( m_id ) VALUES (" + m_id +") "); 
		
		db = mHelper.getWritableDatabase();
		try {
			db.execSQL(strSql);
			bResult = true;
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			mHelper.close();
		}
				
		return bResult;
	}
	
	//
	public void upgradeDb() {

	}
	
	
	////////////////////////////////////////////////////////////////////
	// PUSH_MSG_TABLE_NAME
	public boolean putPushMessage(long m_id, String alert, String user_data) {
		boolean bResult = false;
		
		strSql = String.format(
				" INSERT INTO %s " +
				" ( m_id, alert, user_data ) VALUES " +
				" ( %d, '%s', '%s' );",
				DbDef.PUSH_MSG_TABLE_NAME, 
				m_id, alert, user_data);
		
		Log.i(tag, strSql);
		db = mHelper.getWritableDatabase();
		try {
			db.execSQL(strSql);
			bResult = true;
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			mHelper.close();
		}
		
		return bResult;
	}
	
	//
	public boolean deletePushMessage(long m_id) {
		boolean bResult = false;
		
		strSql = String.format(
				" DELETE FROM %s WHERE m_id = %d ",
				DbDef.PUSH_MSG_TABLE_NAME, m_id); 
		
		db = mHelper.getWritableDatabase();
		try {
			db.execSQL(strSql);
			bResult = true;
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			mHelper.close();
		}
		
		return bResult;
	}
	
	//
	public boolean deleteAllPushMessages() {
		boolean bResult = false;
		
		strSql = String.format(
				" DELETE FROM %s ",
				DbDef.PUSH_MSG_TABLE_NAME); 
		
		db = mHelper.getWritableDatabase();
		try {
			db.execSQL(strSql);
			bResult = true;
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			mHelper.close();
		}
		
		return bResult;
	}
	
	//
	public List<PushMsgInfo> getAllPushMessageLists() {
		List<PushMsgInfo> ltResult = new ArrayList<PushMsgInfo>();
		//
		strSql = String.format(
				" SELECT m_id, msg_time, alert, user_data " +
				"   FROM %s " +
				"   ORDER BY m_id DESC ",
				DbDef.PUSH_MSG_TABLE_NAME);
		//Log.i(tag, strSql);
		
		db = mHelper.getReadableDatabase();
		Cursor cursor;
		cursor = db.rawQuery(strSql, null);
		
		while ( cursor.moveToNext() ) {
			PushMsgInfo info = new PushMsgInfo();
			// 0 for index seq
			info.m_id = cursor.getLong(0);
			info.msg_time = cursor.getString(1);
			info.alert = cursor.getString(2);
			info.user_data = cursor.getString(3);
			//
			ltResult.add(info);
		}
		cursor.close();
		mHelper.close();
		
		return ltResult;
	}
	
	public List<PushMsgInfo> getPushMessageList(int nLimit, int nOffset) {
		List<PushMsgInfo> ltResult = new ArrayList<PushMsgInfo>();
		//
		strSql = String.format(
				" SELECT m_id, msg_time, alert, user_data " +
				"   FROM %s " +
				"   ORDER BY m_id DESC " +
				"   LIMIT %d OFFSET %d ",
				DbDef.PUSH_MSG_TABLE_NAME, nLimit, nOffset);
		//Log.i(tag, strSql);
		
		db = mHelper.getReadableDatabase();
		Cursor cursor;
		cursor = db.rawQuery(strSql, null);
		
		while ( cursor.moveToNext() ) {
			PushMsgInfo info = new PushMsgInfo();
			// 0 for index seq
			info.m_id = cursor.getLong(0);
			info.msg_time = cursor.getString(1);
			info.alert = cursor.getString(2);
			info.user_data = cursor.getString(3);
			//
			ltResult.add(info);
		}
		cursor.close();
		mHelper.close();
		
		return ltResult;
	}
	
	//
	public PushMsgInfo getPushMessage(long m_id) {
		PushMsgInfo info = new PushMsgInfo();
		//
		strSql = String.format(
				" SELECT m_id, msg_time, alert, user_data " +
				"   FROM %s " +
				"   WHERE m_id = %d ",
				DbDef.PUSH_MSG_TABLE_NAME, m_id);
		
		db = mHelper.getReadableDatabase();
		Cursor cursor;
		cursor = db.rawQuery(strSql, null);
		
		while ( cursor.moveToNext() ) {
			// 0 for index seq
			info.m_id = cursor.getLong(0);
			info.msg_time = cursor.getString(1);
			info.alert = cursor.getString(2);
			info.user_data = cursor.getString(3);
			//
		}
		cursor.close();
		mHelper.close();
		
		return info;
	}

	////////
}

//////////////////////////////////////////////////////////////
// Database class
class UserDbHelper extends SQLiteOpenHelper {
	String strSql;
	static String tag = "db_helper";
	public UserDbHelper(Context context) {
		//      context  name  factory  version
		super(context, DbDef.DB_FILE_NAME, null, 1);
	}

	public void onCreate(SQLiteDatabase db) {
		//Log.i(tag, "Db -> onCreate");
		strSql = String.format(
				" CREATE TABLE %s " +
				" ( mkey TEXT NOT NULL PRIMARY KEY, " +
				" mvalue TEXT NOT NULL );",
				DbDef.CONFIG_TABLE_NAME );
		db.execSQL(strSql);
		// SQLite 에서는 long 타입도 INTEGER 로 표현한다.
		strSql = String.format(
				" CREATE TABLE %s " +
				" ( m_id  INTEGER NOT NULL PRIMARY KEY )",
				DbDef.MSG_CNT_TABLE_NAME );
		db.execSQL(strSql);
		//
		
		/////////////////////////////////////////////////////////////////////////////
		// user specific database for pushbegins demo app
		// tb_push_message
		strSql = String.format(
				" CREATE TABLE %s " +
				" ( m_id  INTEGER NOT NULL PRIMARY KEY, " +
				"	alert TEXT NOT NULL, " +
				"	user_data TEXT," +
				"	msg_time DATETIME DEFAULT CURRENT_TIMESTAMP )",
				DbDef.PUSH_MSG_TABLE_NAME );
		db.execSQL(strSql);
		
		// create before update and after insert triggers
		strSql = String.format(
				" CREATE TRIGGER UPDATE_%s BEFORE UPDATE ON %s" +
				" BEGIN " +
				"     UPDATE %s SET msg_time = datetime('now', 'localtime') " +
				"     WHERE rowid = new.rowid; " +
				" END ",
				DbDef.PUSH_MSG_TABLE_NAME, DbDef.PUSH_MSG_TABLE_NAME, DbDef.PUSH_MSG_TABLE_NAME );
		db.execSQL(strSql);

		strSql = String.format(
				" CREATE TRIGGER INSERT_%s AFTER INSERT ON %s" +
				" BEGIN " +
				"     UPDATE %s SET msg_time = datetime('now', 'localtime') " +
				"     WHERE rowid = new.rowid; " +
				" END ",
				DbDef.PUSH_MSG_TABLE_NAME, DbDef.PUSH_MSG_TABLE_NAME, DbDef.PUSH_MSG_TABLE_NAME );
		db.execSQL(strSql);
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//Log.i(tag, "onUpgrade");
		strSql = String.format("DROP TABLE IF EXISTS %s ", DbDef.CONFIG_TABLE_NAME );
		db.execSQL(strSql);
		//
		strSql = String.format("DROP TABLE IF EXISTS %s ", DbDef.MSG_CNT_TABLE_NAME );
		db.execSQL(strSql);
		//
		strSql = String.format("DROP TABLE IF EXISTS %s ", DbDef.PUSH_MSG_TABLE_NAME );
		db.execSQL(strSql);
	}
}
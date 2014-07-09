package com.example.stocktrader;

import java.sql.SQLException;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {
	
	//Database columns
	static final String KEY_ROWID = "_id";
	static final String KEY_NAME = "name";
	static final String KEY_SYMBOL = "symbol";
	
	//Database properties
	static final String TAG = "DBAdapter";
	static final String DATABASE_NAME = "StockTraderDB";
	static final String DATABASE_TABLE = "stockinfo";
	static final int DATABASE_VERSION = 1;
	
	//Database creation command
	static final String DATABASE_CREATE = "create table stockinfo (_id integer primary key autoincrement, name text not null, symbol text not null);";
	
	final Context context;
	
	static DatabaseHelper DBHelper;
	static SQLiteDatabase db;
	
	public DBAdapter (Context ctx){
		this.context = ctx;
		DBHelper = new DatabaseHelper (context);
	}
	
	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper (Context context){
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			
			db.execSQL(DATABASE_CREATE);
			
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG,"Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all data");
			db.execSQL("DROP TABLE IF EXISTS stockinfo");
			onCreate(db);
		}
		
		//Open DB
		public DatabaseHelper open() throws SQLException {
			db = DBHelper.getWritableDatabase();
			return this;
		}
		
		public void close(){
			DBHelper.close();
		}
		
		//Inserts a stock into the database
		public long insertStock (String name, String symbol){
			
			ContentValues initialValues = new ContentValues ();
			initialValues.put(KEY_NAME, name);
			initialValues.put(KEY_SYMBOL, symbol);
			return db.insert(DATABASE_NAME, null, initialValues);
			
		}
		
		//delets a particular stock
		public int deleteStock (long rowId){
			return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null);
		}
		
		//Gets all the stocks
		public Cursor getAllStocks(){
			return  db.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_SYMBOL}, 
					null,null,null,null,null);
		}
		
		//Gets particular 
		@SuppressLint("NewApi")
		public Cursor getStock (long rowId) throws SQLException{
			Cursor mCursor = db.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,KEY_NAME,KEY_SYMBOL},
					KEY_ROWID + "=" + rowId, null, null, null, null, null, null);
			if (mCursor != null){
				mCursor.moveToFirst();
			}
			return mCursor;
		}
		
		//Edit a stock
		public boolean updateStock(long rowId, String name, String symbol){
			ContentValues args = new ContentValues();
			args.put(KEY_NAME, name);
			args.put(KEY_SYMBOL, symbol);
			return db.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
		}

	}
	
}

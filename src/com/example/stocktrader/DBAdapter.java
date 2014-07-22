package com.example.stocktrader;

import java.sql.SQLException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {

	//Database columns
	private static final String KEY_ROWID = "_id";
	private static final String KEY_NAME = "name";
	private static final String KEY_SYMBOL = "symbol";
	private static final String KEY_EXCHANGE = "exchange";
	private static final String KEY_LAST_TRADE_PRICE_ONLY = "lasttradepriceonly";
	private static final String KEY_CHANGE = "change";
	private static final String KEY_DAYS_HIGH = "dayshigh";
	private static final String KEY_DAYS_LOW = "dayslow";
	private static final String KEY_YEAR_HIGH = "yearhigh";
	private static final String KEY_YEAR_LOW = "yearlow";
	private static final String KEY_VOLUME = "volume";

	//Database properties
	private static final String TAG = "DBAdapter";
	private static final String DATABASE_NAME = "StockTrader.db";
	private static final String DATABASE_TABLE = "stockinfo";
	private static final int DATABASE_VERSION = 8;

	//Database creation command
	private static final String DATABASE_CREATE = "create table stockinfo ("+ KEY_ROWID + " integer primary key autoincrement, " + 
																	KEY_NAME + " text not null, "+ 
																	KEY_SYMBOL + " text not null, " +
																	KEY_CHANGE + " text not null, "+ 
																	KEY_EXCHANGE + " text not null, "+ 
																	KEY_LAST_TRADE_PRICE_ONLY + " text not null, "+ 
																	KEY_DAYS_HIGH + " text not null, "+ 
																	KEY_DAYS_LOW + " text not null, "+ 
																	KEY_YEAR_HIGH + " text not null, "+ 
																	KEY_YEAR_LOW + " text not null," + 
																	KEY_VOLUME + " text not null);";
	
	final Context context;

	DatabaseHelper DBHelper;
	SQLiteDatabase db;

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
	}

	//Open DB
	public DBAdapter open() throws SQLException {
		Log.w(TAG, "OPENNING DB...");
		db = DBHelper.getWritableDatabase();
		return this;
	}

	public void close(){
		Log.w(TAG, "CLOSING DB...");
		DBHelper.close();
	}

	//Inserts a stock into the database
	public long insertStock (String name, String symbol, String change, String exchange, String lasttradepriceonly, String dayshigh, String dayslow, String yearhigh, String yearlow, String volume){
		Log.w(TAG, "INSERTING STOCK WITH NAME:"+name+", SYMBOL:" + symbol + "..." + ", CHANGE:" + change + ", EXCHANGE:" + exchange + ", LAST TRADE PRICE ONLY:" + lasttradepriceonly + ", DAYS HIGH:" + dayshigh + 
				", DAYS LOW:" + dayslow + ", YEAR HIGH" + yearhigh + ", YEAR LOW" + yearlow + ", VOLUME" + volume);
		ContentValues initialValues = new ContentValues ();
		initialValues.put(KEY_NAME, name);
		initialValues.put(KEY_SYMBOL, symbol);
		initialValues.put(KEY_CHANGE, change);
		initialValues.put(KEY_EXCHANGE, exchange);
		initialValues.put(KEY_LAST_TRADE_PRICE_ONLY, lasttradepriceonly);
		initialValues.put(KEY_DAYS_HIGH, dayshigh);
		initialValues.put(KEY_DAYS_LOW, dayslow);
		initialValues.put(KEY_YEAR_HIGH, yearhigh);
		initialValues.put(KEY_YEAR_LOW, yearlow);
		initialValues.put(KEY_VOLUME, volume);
		
		return db.insert(DATABASE_TABLE, null, initialValues);

	}

	//deletes a particular stock
	public boolean deleteStock (long rowId){
		Log.w(TAG, "DELETING STOCK WITH ID: " +  rowId + "...");
		return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	}

	//Gets all the stocks
	public Cursor getAllStocks(){
		Log.w(TAG, "GETTING ALL STOCKS...");
		return  db.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_SYMBOL, KEY_CHANGE, KEY_EXCHANGE, KEY_LAST_TRADE_PRICE_ONLY, KEY_DAYS_HIGH, KEY_DAYS_LOW, KEY_YEAR_HIGH, KEY_YEAR_LOW, KEY_VOLUME}, 
				null,null,null,null,null);
	}

	//Gets particular stock
	public Cursor getStock (long rowId) throws SQLException{
		Log.w(TAG, "GETTING SINGLE STOCK WITH ID: " +  rowId +"...");
		Cursor mCursor = db.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_SYMBOL, KEY_CHANGE, KEY_EXCHANGE, KEY_LAST_TRADE_PRICE_ONLY, KEY_DAYS_HIGH, KEY_DAYS_LOW, KEY_YEAR_HIGH, KEY_YEAR_LOW, KEY_VOLUME},
				KEY_ROWID + "=" + rowId, null, null, null, null, null);
		if (mCursor != null){
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	//Edit a stock
	public boolean updateStock(long rowId, String name, String symbol, String change, String exchange, String lasttradepriceonly, String dayshigh, String dayslow, String yearhigh, String yearlow, String volume){
		Log.w(TAG, "UPGRADING DATABASE...");
		ContentValues args = new ContentValues();
		args.put(KEY_NAME, name);
		args.put(KEY_SYMBOL, symbol);
		args.put(KEY_CHANGE, change);
		args.put(KEY_EXCHANGE, exchange);
		args.put(KEY_LAST_TRADE_PRICE_ONLY, lasttradepriceonly);
		args.put(KEY_DAYS_HIGH, dayshigh);
		args.put(KEY_DAYS_LOW, dayslow);
		args.put(KEY_YEAR_HIGH, yearhigh);
		args.put(KEY_YEAR_LOW, yearlow);
		args.put(KEY_VOLUME, volume);
		
		return db.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
	}
}

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
	public static final String KEY_ROWID = "_id";
	public static final String KEY_NAME = "name";
	public static final String KEY_SYMBOL = "symbol";
	public static final String KEY_QUANTITY = "quantity";
	public static final String KEY_BUY_PRICE = "buyprice";

	//Database properties
	private static final String DATABASE_NAME = "StockTrader.db";
	private static final String DATABASE_TABLE = "stockinfo";
	private static final int DATABASE_VERSION = 10;

	//Database creation command
	private static final String DATABASE_CREATE = "create table stockinfo ("+ KEY_ROWID + " integer primary key autoincrement, " + 
																	KEY_NAME + " text not null, "+ 
																	KEY_SYMBOL + " text not null, " +
																	KEY_QUANTITY + " text not null," + 
																	KEY_BUY_PRICE + " text not null);";
	
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
			Log.w(StockTraderActivity.STOCK_DATABASE_TAG,"Creating " + DATABASE_TABLE + " data");
			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(StockTraderActivity.STOCK_DATABASE_TAG,"Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all data");
			db.execSQL("DROP TABLE IF EXISTS stockinfo");
			onCreate(db);
		}
	}

	//Open DB
	public DBAdapter open() throws SQLException {
		Log.w(StockTraderActivity.STOCK_DATABASE_TAG, "OPENNING DB...");
		db = DBHelper.getWritableDatabase();
		return this;
	}

	public void close(){
		Log.w(StockTraderActivity.STOCK_DATABASE_TAG, "CLOSING DB...");
		DBHelper.close();
	}

	//Inserts a stock into the database
	public long insertStock (String name, String symbol, 
			String quantity, String buyprice){

		ContentValues initialValues = new ContentValues ();
		initialValues.put(KEY_NAME, name);
		initialValues.put(KEY_SYMBOL, symbol);
		initialValues.put(KEY_QUANTITY, quantity);
		initialValues.put(KEY_BUY_PRICE, buyprice);
		
		return db.insert(DATABASE_TABLE, null, initialValues);

	}

	//deletes a particular stock
	public boolean deleteStock (long rowId){
		Log.w(StockTraderActivity.STOCK_DATABASE_TAG, "DELETING STOCK WITH ID: " +  rowId + "...");
		return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	}

	//Gets all the stocks
	public Cursor getAllStocks(){
		Log.w(StockTraderActivity.STOCK_DATABASE_TAG, "GETTING ALL STOCKS...");
		return  db.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_SYMBOL, KEY_QUANTITY, KEY_BUY_PRICE}, 
				null,null,null,null,null);
	}

	//Gets particular stock
	public Cursor getStock (long rowId) throws SQLException{
		Log.w(StockTraderActivity.STOCK_DATABASE_TAG, "GETTING SINGLE STOCK WITH ID: " +  rowId +"...");
		Cursor mCursor = db.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_SYMBOL, KEY_QUANTITY, KEY_BUY_PRICE},
				KEY_ROWID + "=" + rowId, null, null, null, null, null);
		if (mCursor != null){
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	//Edit a stock
	public boolean updateStock(long rowId, String name, String symbol, 
			String quantity, String buyprice){
		Log.w(StockTraderActivity.STOCK_DATABASE_TAG, "UPGRADING DATABASE...");
		ContentValues args = new ContentValues();
		args.put(KEY_NAME, name);
		args.put(KEY_SYMBOL, symbol);
		args.put(KEY_QUANTITY, quantity);
		args.put(KEY_BUY_PRICE, buyprice);
		
		return db.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
	}
	
	public void deleteDb(){
		Log.w(StockTraderActivity.STOCK_DATABASE_TAG, "DELETING " + DATABASE_TABLE + " DATABASE...");
		db.execSQL("DROP TABLE " + DATABASE_TABLE);
		Log.w(StockTraderActivity.STOCK_DATABASE_TAG, "CREATING " + DATABASE_TABLE + " DATABASE...");
		db.execSQL(DATABASE_CREATE);
	}
	
	public Long findStockIfExists(String name){
		
		Cursor stockNames = db.query(DATABASE_TABLE, 
				new String[] {KEY_ROWID, KEY_NAME, KEY_SYMBOL, KEY_QUANTITY, KEY_BUY_PRICE}, 
				null,null,null,null,null);
		
		if (stockNames.moveToFirst()){
			
			do{
				if (stockNames.getString(stockNames.getColumnIndex(KEY_NAME)).equals(name)){
					return Long.parseLong(stockNames.getString(stockNames.getColumnIndex(KEY_ROWID)));
				}
			}while (stockNames.moveToNext());
			
		}
		
		return null;
		
	}	
}





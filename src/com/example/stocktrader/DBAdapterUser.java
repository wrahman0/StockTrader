package com.example.stocktrader;

import java.sql.SQLException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapterUser {
	
	//T: Repeating constants from DBAdapter
	//Database columns
	static final String KEY_ROWID = "_id";
	static final String KEY_USERNAME = "username";
	static final String KEY_STOCKS_BOUGHT = "stocksbought";
	static final String KEY_STARTING_CASH = "startingcash";
	static final String KEY_CURRENT_CASH = "currentcash";
	static final String KEY_CURRENT_STOCK_VALUE = "currentstockvalue";
	static final String KEY_GAIN_LOSS = "gainloss";
	static final String KEY_STOCKS_OWNED = "stocksowned";
	static final String KEY_TOTAL_TRANSACTIONS = "totaltransactions";
	static final String KEY_POSITIVE_TRANSACTIONS = "positivetransactions";
	static final String KEY_NEGATIVE_TRANSACTIONS = "negativetransactions";

	//Database properties
	static final String DATABASE_NAME = "UserData.db";
	static final String DATABASE_TABLE = "userinfo";
	static final int DATABASE_VERSION = 4;

	//Database creation command
	static final String DATABASE_CREATE = "create table "+ DATABASE_TABLE +" ("+ KEY_ROWID + " integer primary key autoincrement, " + 
																	KEY_USERNAME + " text not null, "+ 
																	KEY_STOCKS_BOUGHT + " text not null, " +
																	KEY_STARTING_CASH + " text not null, "+ 
																	KEY_CURRENT_CASH + " text not null, " +
																	KEY_CURRENT_STOCK_VALUE + " text not null, " +
																	KEY_GAIN_LOSS + " text not null, " +
																	KEY_STOCKS_OWNED + " text not null, " +
																	KEY_TOTAL_TRANSACTIONS + " text not null, " +
																	KEY_POSITIVE_TRANSACTIONS + " text not null, " +
																	KEY_NEGATIVE_TRANSACTIONS + " text not null);";
	
	final Context context;

	DatabaseHelper DBHelper;
	SQLiteDatabase db;

	public DBAdapterUser (Context ctx){
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
			Log.w(StockTraderActivity.STOCK_DATABASE_TAG,"Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all data");
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			onCreate(db);
		}
	}

	//Open DB
	public DBAdapterUser open() throws SQLException {
		Log.w(StockTraderActivity.STOCK_DATABASE_TAG, "OPENNING DB...");
		db = DBHelper.getWritableDatabase();
		return this;
	}

	public void close(){
		Log.w(StockTraderActivity.STOCK_DATABASE_TAG, "CLOSING DB...");
		DBHelper.close();
	}

	//Inserts a stock into the database
	public long addUser (String username, String stocksbought, String startingcash, String currentcash, String currentstockvalue, String gainloss, String stocksowned, String totaltransactions, String positivetransactions, String negativetransactions){
		Log.w(StockTraderActivity.STOCK_DATABASE_TAG, "INSERTING USER WITH NAME:"+ username +", STOCKS:" + stocksbought + "..." + ", START CAPITAL:" + startingcash + ", CURRENT CAPITAL:" + currentcash);
		
		ContentValues initialValues = new ContentValues ();
		initialValues.put(KEY_USERNAME, username);
		initialValues.put(KEY_STOCKS_BOUGHT, stocksbought);
		initialValues.put(KEY_STARTING_CASH, startingcash);
		initialValues.put(KEY_CURRENT_CASH, currentcash);
		initialValues.put(KEY_CURRENT_STOCK_VALUE, currentstockvalue);
		initialValues.put(KEY_GAIN_LOSS, gainloss);
		initialValues.put(KEY_STOCKS_OWNED, stocksowned);
		initialValues.put(KEY_TOTAL_TRANSACTIONS, totaltransactions);
		initialValues.put(KEY_POSITIVE_TRANSACTIONS, positivetransactions);
		initialValues.put(KEY_NEGATIVE_TRANSACTIONS, negativetransactions);
		
		return db.insert(DATABASE_TABLE, null, initialValues);

	}

	//deletes a particular stock
	public boolean removeUser (long rowId){
		Log.w(StockTraderActivity.STOCK_DATABASE_TAG, "DELETING USER WITH ID: " +  rowId + "...");
		return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	}

	//Gets all the stocks
	public Cursor getAllUsers(){
		Log.w(StockTraderActivity.STOCK_DATABASE_TAG, "GETTING ALL USERS...");
		return  db.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_USERNAME, KEY_STOCKS_BOUGHT, KEY_STARTING_CASH, KEY_CURRENT_CASH, KEY_CURRENT_STOCK_VALUE, KEY_GAIN_LOSS, KEY_STOCKS_OWNED, KEY_TOTAL_TRANSACTIONS, KEY_POSITIVE_TRANSACTIONS, KEY_NEGATIVE_TRANSACTIONS}, null,null,null,null,null);
	}

	//Gets particular stock
	public Cursor getUser (long rowId) throws SQLException{
		Log.w(StockTraderActivity.STOCK_DATABASE_TAG, "GETTING SINGLE USER WITH ID: " +  rowId +"...");
		Cursor mCursor = db.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_USERNAME, KEY_STOCKS_BOUGHT, KEY_STARTING_CASH, KEY_CURRENT_CASH, KEY_CURRENT_STOCK_VALUE, KEY_GAIN_LOSS, KEY_STOCKS_OWNED, KEY_TOTAL_TRANSACTIONS, KEY_POSITIVE_TRANSACTIONS, KEY_NEGATIVE_TRANSACTIONS}, KEY_ROWID + "=" + rowId, null, null, null, null, null);
		if (mCursor != null){
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	public boolean updateUser(UserDetails userDetails){
		return updateUser(userDetails.get_id(),
							userDetails.getUsername()+"",
							userDetails.getStocksBought()+"",
							userDetails.getStartingCash()+"",
							userDetails.getCurrentCash()+"",
							userDetails.getCurrentStockValue()+"",
							userDetails.getGainLoss()+"",
							userDetails.getStocksOwned()+"",
							userDetails.getTotalTransactions()+"",
							userDetails.getPositiveTransactions()+"",
							userDetails.getNegativeTransactions()+"");
	}
	
	//Edit a stock
	public boolean updateUser(long rowId, String username, String stocksbought, 
			String startingcash, String currentcash, String currentstockvalue, 
			String gainloss, String stocksowned, String totaltransactions, 
			String positivetransactions, String negativetransactions){
		Log.w(StockTraderActivity.STOCK_DATABASE_TAG, "UPGRADING DATABASE...");
		ContentValues args = new ContentValues();
		
		args.put(KEY_USERNAME, username);
		args.put(KEY_STOCKS_BOUGHT, stocksbought);
		args.put(KEY_STARTING_CASH, startingcash);
		args.put(KEY_CURRENT_CASH, currentcash);
		args.put(KEY_CURRENT_STOCK_VALUE, currentstockvalue);
		args.put(KEY_GAIN_LOSS, gainloss);
		args.put(KEY_STOCKS_OWNED, stocksowned);
		args.put(KEY_TOTAL_TRANSACTIONS, totaltransactions);
		args.put(KEY_POSITIVE_TRANSACTIONS, positivetransactions);
		args.put(KEY_NEGATIVE_TRANSACTIONS, negativetransactions);
		
		return db.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
		
	}
	
	public void deleteDb(){
		Log.w(StockTraderActivity.STOCK_DATABASE_TAG, "DELETING " + DATABASE_TABLE + " DATABASE...");
		db.execSQL("DROP TABLE " + DATABASE_TABLE);
		Log.w(StockTraderActivity.STOCK_DATABASE_TAG, "CREATING " + DATABASE_TABLE + " DATABASE...");
		db.execSQL(DATABASE_CREATE);
	}
}

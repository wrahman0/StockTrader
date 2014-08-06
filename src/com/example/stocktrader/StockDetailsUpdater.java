package com.example.stocktrader;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

public class StockDetailsUpdater{
	private static final int UPDATE_INTERVAL = 10*1000;
	private static final String TAG = "StockDetailsUpdater";	
	
	private Handler mHandler;
	private Runnable mRunnable;

	private ArrayList<String>mTrackedStockList;
	private UpdateListener mUpdateListener;
	
	public StockDetailsUpdater(ArrayList<String>trackedStockList, UpdateListener updateListener){
		this.mTrackedStockList = trackedStockList;
		this.mUpdateListener = updateListener;
		this.mHandler = new Handler();
	}
	
	public void start(){
		stop();
		mRunnable = new Runnable(){
			public void run(){
				for(String stockSymbol:mTrackedStockList){
					UpdaterTask mSuggester = new UpdaterTask();
					mSuggester.execute(stockSymbol);
					
				}
				mHandler.postDelayed(mRunnable, UPDATE_INTERVAL);
			}
		};
		
		mRunnable.run();
	}
	
	public void stop(){
		mHandler.removeCallbacks(mRunnable);
	}
	
	public interface UpdateListener{
		public void onUpdate(String stockSymbol, StockDetails stockDetails);
	}
	
	private class UpdaterTask extends AsyncTask<String, Void, StockDetails>{
		private String mStockSymbol;

		@Override
		protected StockDetails doInBackground(String... arg0) {
			mStockSymbol = arg0[0];
			try {
				return NewXMLParser.parseStock(mStockSymbol);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(StockDetails result) {
			super.onPostExecute(result);
			
			Log.d(TAG, "Updated: "+mStockSymbol);
			mUpdateListener.onUpdate(mStockSymbol, result);
		}
	}
		
}

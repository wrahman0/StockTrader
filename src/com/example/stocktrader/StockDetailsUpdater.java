package com.example.stocktrader;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

public class StockDetailsUpdater{
	private static StockDetailsUpdater mUpdater;

	private static final int UPDATE_INTERVAL = 10*1000;
	private static final String TAG = "StockDetailsUpdater";	

	private Handler mHandler;
	private Runnable mRunnable;

	private ArrayList<String>mTrackedStockList;
	private UpdateListener mUpdateListener;

	public static void createUpdater(
			ArrayList<String>trackedStockList, UpdateListener updateListener){
		if(mUpdater!=null){
			mUpdater.stop();
		}
		mUpdater = new StockDetailsUpdater(trackedStockList, updateListener);
	}

	public static void stopUpdater(){
		if(mUpdater!=null){
			mUpdater.stop();
		}
	}

	public static void startUpdater(){
		if(mUpdater!=null){
			mUpdater.start();
		}
	}

	private StockDetailsUpdater(ArrayList<String>trackedStockList, UpdateListener updateListener){
		this.mTrackedStockList = trackedStockList;
		this.mUpdateListener = updateListener;
		this.mHandler = new Handler();
	}

	private void start(){
		Log.d(TAG, "Updater Started");

		mUpdater.stop();
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
		Log.d(TAG, mTrackedStockList.size()+" to be updated");
	}

	private void stop(){
		Log.d(TAG, "Updater Stopped");
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

			if(result!=null){
				Log.d(TAG, "Successfully updated: "+mStockSymbol);
			}
			
			mUpdateListener.onUpdate(mStockSymbol, result);
		}
	}

}

package com.example.stocktrader;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class MyAccountFragment extends Fragment{

	private UserDetails theUser;

	private TextView accUsername;
	private TextView accStartingCapital;
	private TextView accCurrentCapital;
	private TextView accCurrentStockValue;
	private TextView accGainLoss;
	private TextView accStocksBought;
	private TextView accStocksOwned;
	private TextView accTotalTransactions;
	private TextView accPositiveTransactions;
	private TextView accNegativeTransactions;

	private Button accDeleteUserButton;
	
	private ArrayList<String> mTrackedStockList = new ArrayList<String>();
	private HashMap<String, MyBoughtStockInfoHolder> mStockHashMap = new HashMap<String, MyBoughtStockInfoHolder>();
	
	private class MyBoughtStockInfoHolder {
		public String stockSymbol;
		public int quantity;
		public double boughtPrice;
		public StockDetails mStockDetails;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.my_account, null);

		// Find the views
		accUsername = (TextView) view.findViewById(R.id.accUsername);
		accStartingCapital = (TextView) view.findViewById(R.id.accStartingCapital);
		accCurrentCapital = (TextView) view.findViewById(R.id.accCurrentCapital);
		accCurrentStockValue = (TextView) view.findViewById(R.id.accCurrentStockValue);
		accGainLoss = (TextView) view.findViewById(R.id.accGainLoss);
		accStocksBought = (TextView) view.findViewById(R.id.accStocksBought);
		accStocksOwned = (TextView) view.findViewById(R.id.accStocksOwned);
		accTotalTransactions = (TextView) view.findViewById(R.id.accTotalTransactions);
		accPositiveTransactions = (TextView) view.findViewById(R.id.accPositiveTransactions);
		accNegativeTransactions = (TextView) view.findViewById(R.id.accNegativeTransactions);
		accDeleteUserButton = (Button) view.findViewById(R.id.accDeleteUserButton);
		
		getUser();
		refreshViews();
		
		accDeleteUserButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				new AlertDialog.Builder(getActivity())
				.setTitle("Delete Account")
				.setMessage("Do you want to permanently delete the user?")
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						DBAdapterUser dbUser = new DBAdapterUser(getActivity());
						try{
							dbUser.open();	
						}catch(SQLException e){
							e.printStackTrace();
						}

						DBAdapter db = new DBAdapter(getActivity());
						try{
							db.open();	
						}catch(SQLException e){
							e.printStackTrace();
						}

						db.deleteDb();
						dbUser.deleteDb();
						db.close();
						dbUser.close();

						Intent intent = new Intent(getActivity(), UserSetupActivity.class);
						getActivity().startActivity(intent);
						Toast.makeText(getActivity(), "Deleting Account", Toast.LENGTH_SHORT).show();
						getActivity().finish();
						
					}
				}).setNegativeButton(android.R.string.no, null).show();
			}
		});

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		StockTraderActivity sta = (StockTraderActivity)getActivity();

		if(sta.getCurrentFragment() == MyAccountFragment.this){
			Log.d(StockTraderActivity.APP_NAME_TAG, "MyAccountFragment in view");
			
			getUser();
			refreshViews();

			mTrackedStockList.clear();
			mStockHashMap.clear();
			getUserStocksFromDB();
			

			StockDetailsUpdater.createUpdater(mTrackedStockList,
					new StockDetailsUpdater.UpdateListener() {

				@Override
				public void onUpdate(String stockSymbol, StockDetails stockDetails){
					if(stockDetails!=null){
						MyBoughtStockInfoHolder holder = mStockHashMap.get(stockSymbol);
						holder.mStockDetails = stockDetails;
						
						updateIfAllStockDetailsObtained();
					}

				}
			});
			StockDetailsUpdater.startUpdater();
		}
	}
	
	private void getUserStocksFromDB(){
		DBAdapter db = new DBAdapter(getActivity());
		try{
			db.open();
			Cursor allStocksCursor = db.getAllStocks();
			allStocksCursor.moveToFirst();
			
			do{
				MyBoughtStockInfoHolder stockHolder = new MyBoughtStockInfoHolder();
				
				//Get values from database
				String symbol = allStocksCursor.getString(allStocksCursor.getColumnIndex(DBAdapter.KEY_SYMBOL));
				int quantity = allStocksCursor.getInt(allStocksCursor.getColumnIndex(DBAdapter.KEY_QUANTITY));
				Double buyPrice  = allStocksCursor.getDouble(allStocksCursor.getColumnIndex(DBAdapter.KEY_BUY_PRICE));
				
				//Set the views/info
				stockHolder.stockSymbol = symbol;
				stockHolder.quantity = quantity;
				stockHolder.boughtPrice = buyPrice;
				
				mTrackedStockList.add(symbol);
				mStockHashMap.put(symbol, stockHolder);
				
			}while(allStocksCursor.moveToNext());
			
		}catch(SQLException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}

		db.close();
	}
	
	private void updateIfAllStockDetailsObtained() {
		float myStockValue = (float) 0.0;
		for(String stockSymbol:mTrackedStockList){
			MyBoughtStockInfoHolder holder = mStockHashMap.get(stockSymbol);
			if(holder.mStockDetails!=null){
				double currentPrice = Double.parseDouble(holder.mStockDetails.getLastTradePriceOnly());
				myStockValue+=(currentPrice*holder.quantity);
			}else{
				return;
			}
		}
		theUser.setCurrentStockValue(myStockValue);
		refreshViews();
		
		saveUserDetailsToDB();
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		saveUserDetailsToDB();
	}
	
	private void saveUserDetailsToDB(){
		
		DBAdapterUser dbUser = new DBAdapterUser (getActivity());
		try{
			dbUser.open();
			dbUser.updateUser(theUser);
		}catch(SQLException e){
			e.printStackTrace();
		}
		dbUser.close();
	}

	private void refreshViews(){

		setTextViewColors();
		accUsername.setText(String.valueOf(theUser.getUsername()));
		accStartingCapital.setText("$"+ String.format("%.2f", theUser.getStartingCash()));
		accCurrentCapital.setText("$"+ String.format("%.2f", theUser.getCurrentCash()));
		accCurrentStockValue.setText("$"+ String.format("%.2f", theUser.getCurrentStockValue()));
		accGainLoss.setText("$" + String.format("%.2f", theUser.getGainLoss()));
		accStocksBought.setText(String.valueOf(theUser.getStocksBought()));
		accStocksOwned.setText(String.valueOf(theUser.getStocksOwned()));
		accTotalTransactions.setText(String.valueOf(theUser.getTotalTransactions()));
		accPositiveTransactions.setText(String.valueOf(theUser.getPositiveTransactions()));
		accNegativeTransactions.setText(String.valueOf(theUser.getNegativeTransactions()));

	}

	private void setTextViewColors(){

		//Set the color of the gain loss textview
		if (theUser.getCurrentStockValue()>0.0){
			accCurrentStockValue.setTextColor(getResources().getColor(R.color.card_color_positive));
		}else{
			accCurrentStockValue.setTextColor(getResources().getColor(R.color.card_color_negative));
		}

		//Set the color of the gain loss textview
		if (theUser.getGainLoss()>0.0){
			accGainLoss.setTextColor(getResources().getColor(R.color.card_color_positive));
		}else{
			accGainLoss.setTextColor(getResources().getColor(R.color.card_color_negative));
		}
	}

	private void getUser(){

		DBAdapterUser dbUser = new DBAdapterUser (getActivity());
		try{
			dbUser.open();
		}catch(SQLException e){
			e.printStackTrace();
		}
		Cursor userCursor = dbUser.getAllUsers();
		userCursor.moveToFirst();
		theUser = new UserDetails (userCursor);
		dbUser.close();

	}

}

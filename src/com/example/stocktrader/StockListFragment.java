package com.example.stocktrader;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class StockListFragment extends Fragment{
	
	private static final long serialVersionUID = 1L;
	private DBAdapter db;
	private Cursor allStocksCursor;
	
	private ArrayList<String> mTrackedStockList = new ArrayList<String>();
	private HashMap<String, BoughtStockCardHolder> mStockHashMap = new HashMap<String, BoughtStockCardHolder>();
	
	//Views
	private TableLayout stockList;
	
	private boolean parsingNews = true;

	//The bundle that will hold the stock and news class
	private Bundle bundle = new Bundle();
	
	private class BoughtStockCardHolder {
		public String stockName;
		public String stockSymbol;
		public int quantity;
		public double boughtPrice;
		public StockDetails mStockDetails;
		
		private ImageView gainLossImageView;
		private TextView stockNameTextView;
		private TextView stockSymbolTextView;
		private TextView lastTradePriceTextView;
		private TextView changeTextView;
		private TextView stockQuantityTextView;
		private TextView gainLossTextView;
		
		public BoughtStockCardHolder(View card){
			gainLossImageView = (ImageView) card.findViewById(R.id.gainLossImageView);
			stockNameTextView = (TextView) card.findViewById (R.id.stockNameTextView);
			stockSymbolTextView = (TextView) card.findViewById (R.id.stockSymbolTextView);
			lastTradePriceTextView = (TextView) card.findViewById (R.id.lastTradePriceTextView);
			changeTextView = (TextView) card.findViewById (R.id.changeTextView);
			stockQuantityTextView = (TextView) card.findViewById (R.id.stockQuantityTextView);
			gainLossTextView = (TextView) card.findViewById(R.id.gainLossTextView);
		}
		
		public void refreshViews(){
			if(mStockDetails!=null){
				double currentPrice = Double.parseDouble(mStockDetails.getLastTradePriceOnly());
				double priceChange = currentPrice - boughtPrice;
				double percentGained =  priceChange/ boughtPrice * 100;
				
				changeTextView.setText(String.format("%.2f",priceChange));
				lastTradePriceTextView.setText(String.format("%.2f",currentPrice));
				gainLossTextView.setText(String.format("%.2f",percentGained) + "%");
				
				if (percentGained > 0.0){
					changeTextView.setTextColor(getResources().getColor(R.color.card_color_positive));
					gainLossImageView.setImageResource(R.drawable.arrow_positive);
				}else {
					changeTextView.setTextColor(getResources().getColor(R.color.card_color_negative));
					gainLossImageView.setImageResource(R.drawable.arrow_negative);
				}

			}
		}
		
		public void initViews(View card){
			//Find the views
			stockNameTextView.setText(stockName);
			stockSymbolTextView.setText(stockSymbol);
			stockQuantityTextView.setText("x" + quantity);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.stock_list, container, false);

		stockList = (TableLayout) view.findViewById(R.id.stockList);

		if (openDB()){
			populateView();	
		}

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		mTrackedStockList.clear();
		mStockHashMap.clear();
		stockList.removeAllViews();
		if (openDB()){
			populateView();	
		}

		db.close();

	}

	private boolean openDB(){

		//Open the db for stocks
		this.db = new DBAdapter(getActivity());

		try{
			db.open();
		}catch(SQLException e){
			e.printStackTrace();
		}

		allStocksCursor = db.getAllStocks();
		return allStocksCursor.moveToFirst();

	}

	private void populateView(){
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
		
		OnClickListener stockCardListener = new OnClickListener(){

			@Override
			public void onClick(View v) {
				//Get the stock symbol from the card which is the parent of the imageButton
				TableRow cardTableRow = (TableRow) v.getParent();
				String stockSymbol = ((TextView)cardTableRow.findViewById (R.id.stockSymbolTextView))
						.getText()
						.toString();
				
				StockDetails stockDetails = mStockHashMap.get(stockSymbol).mStockDetails;
				
				if(stockDetails!=null){
					Bundle b = new Bundle();
					b.putSerializable(DetailsStockViewActivity.STOCK_NAME_EXTRA, stockDetails);
					Intent intent = new Intent(getActivity(), DetailsStockViewActivity.class);
					intent.putExtras(b);
					startActivity(intent);
				}
			}
			
		};
		
		OnClickListener sellButtonListener = new OnClickListener(){

			@Override
			public void onClick(View v) {
				//Toast.makeText(getActivity(), "Processing Transaction...", Toast.LENGTH_SHORT).show();
				showSellDialog(v);
				
			}
			
		};

		do{
			//Get values from database
			String symbol = allStocksCursor.getString(allStocksCursor.getColumnIndex(DBAdapter.KEY_SYMBOL));
			String name = allStocksCursor.getString(allStocksCursor.getColumnIndex(DBAdapter.KEY_NAME));
			int quantity = allStocksCursor.getInt(allStocksCursor.getColumnIndex(DBAdapter.KEY_QUANTITY));
			Double buyPrice  = allStocksCursor.getDouble(allStocksCursor.getColumnIndex(DBAdapter.KEY_BUY_PRICE));

			View card = inflater.inflate(R.layout.stock_bought_card, null);
			BoughtStockCardHolder stockHolder = new BoughtStockCardHolder(card);

			ImageButton stockSell = (ImageButton) card.findViewById(R.id.stockSell);
			TableRow stockBlock = (TableRow)card.findViewById(R.id.stockBlock);
			
			//Set the views/info
			stockHolder.stockName = name;
			stockHolder.stockSymbol = symbol;
			stockHolder.quantity = quantity;
			stockHolder.boughtPrice = buyPrice;
			
			stockHolder.initViews(card);

			//Set the listener for the stock card
			stockBlock.setOnClickListener(stockCardListener);
			
			//Set the listener for the stock card sell button
			stockSell.setOnClickListener(sellButtonListener);
			
			mTrackedStockList.add(symbol);
			mStockHashMap.put(symbol, stockHolder);
			stockList.addView(card);
		}while(allStocksCursor.moveToNext());


		StockDetailsUpdater.createUpdater(mTrackedStockList,
				new StockDetailsUpdater.UpdateListener() {

			@Override
			public void onUpdate(String stockSymbol, StockDetails stockDetails){
				if(stockDetails!=null){
					BoughtStockCardHolder holder = mStockHashMap.get(stockSymbol);
					holder.mStockDetails = stockDetails;
					holder.refreshViews();
				}

			}
		});
		StockDetailsUpdater.startUpdater();

	}
	
	private void showSellDialog(View v){
		
		TableRow stockRow = (TableRow) v.getParent().getParent();
		String stockSymbol = ((TextView) stockRow.findViewById(R.id.stockSymbolTextView))
				.getText()
				.toString();
		
		StockDetails stockDetails = mStockHashMap.get(stockSymbol).mStockDetails;
		
		if(stockDetails!=null){
			
			Bundle b = new Bundle();
			b.putSerializable(DetailsStockViewActivity.STOCK_NAME_EXTRA, stockDetails);
			b.putInt(StockTraderActivity.STOCK_QUANTITY_TAG, mStockHashMap.get(stockSymbol).quantity);
			FragmentManager manager = getActivity().getFragmentManager();
			SellDialog sellDialog = new SellDialog(this);
			
			sellDialog.setArguments(b);
			sellDialog.show(manager, "SellDialog");
		}

	}
}

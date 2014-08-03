package com.example.stocktrader;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;

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

public class StockListFragment extends Fragment implements OnParseComplete, Serializable{
	
	private static final long serialVersionUID = 1L;
	private DBAdapter db;
	private Cursor allStocks;
	
	public StockDetails theStock;
	
	//Views
	private TextView stockNameTextView;
	private TextView stockSymbolTextView;
	private TextView lastTradePriceTextView;
	private TextView changeTextView;
	private TextView stockQuantityTextView;
	private TextView gainLossTextView;
	private TableLayout stockList;
	
	private boolean parsingNews = true;
	
	private XMLParser xml;

	//The bundle that will hold the stock and news class
	private Bundle bundle = new Bundle();

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
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);

		// Make sure that we are currently visible
		if (this.isVisible()) {

			stockList.removeAllViews();
			if (openDB()){
				populateView();	
			}
			
			db.close();

		}

	}

	private boolean openDB(){

		//Open the db for stocks
		this.db = new DBAdapter(getActivity());

		try{
			db.open();
		}catch(SQLException e){
			e.printStackTrace();
		}

		allStocks = db.getAllStocks();
		return allStocks.moveToFirst();

	}

	private void populateView(){

		do{
			renderStockCard(stockList, allStocks);
		}while(allStocks.moveToNext());

	}

	private void renderStockCard(TableLayout tableLayout, Cursor stockRow){

		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService (Context.LAYOUT_INFLATER_SERVICE);

		View card = inflater.inflate(R.layout.stock_bought_card, null);

		ImageButton stockSell = (ImageButton) card.findViewById(R.id.stockSell);
		ImageView gainLossImageView = (ImageView) card.findViewById(R.id.gainLossImageView);
		float percentGained =  (Float.parseFloat(stockRow.getString(stockRow.getColumnIndex("lasttradepriceonly"))) - Float.parseFloat(stockRow.getString(stockRow.getColumnIndex("buyprice")))) / Float.parseFloat(stockRow.getString(stockRow.getColumnIndex("buyprice"))) * 100;
		
		TableRow stockBlock = (TableRow)card.findViewById(R.id.stockBlock);
		
		//Find the views
		stockNameTextView = (TextView) card.findViewById (R.id.stockNameTextView);
		stockSymbolTextView = (TextView) card.findViewById (R.id.stockSymbolTextView);
		lastTradePriceTextView = (TextView) card.findViewById (R.id.lastTradePriceTextView);
		changeTextView = (TextView) card.findViewById (R.id.changeTextView);
		stockQuantityTextView = (TextView) card.findViewById (R.id.stockQuantityTextView);
		gainLossTextView = (TextView) card.findViewById(R.id.gainLossTextView);
		
		//Set the views
		stockNameTextView.setText(stockRow.getString(stockRow.getColumnIndex("name")));
		stockSymbolTextView.setText(stockRow.getString(stockRow.getColumnIndex("symbol")));
		changeTextView.setText(stockRow.getString(stockRow.getColumnIndex("change")));
		lastTradePriceTextView.setText(stockRow.getString(stockRow.getColumnIndex("lasttradepriceonly")));
		
		//Set the color accordingly
		if (Float.parseFloat(stockRow.getString(stockRow.getColumnIndex("change"))) > 0.0){
			changeTextView.setTextColor(getResources().getColor(R.color.card_color_positive));
		}else {
			changeTextView.setTextColor(getResources().getColor(R.color.card_color_negative));
		}
		
		stockQuantityTextView.setText("x" + stockRow.getString(stockRow.getColumnIndex("quantity")));
		gainLossTextView.setText(String.format("%.2f",percentGained) + "%");
		
		//Setting the gained lost indicator based on the percentGained
		if (percentGained <= 0.0){
			//Set it to lost indicator
			gainLossImageView.setImageResource(R.drawable.arrow_negative);
		}
		
		theStock = new StockDetails(
				stockRow.getString(stockRow.getColumnIndex("name")), stockRow.getString(stockRow.getColumnIndex("symbol")), 
				stockRow.getString(stockRow.getColumnIndex("exchange")), stockRow.getString(stockRow.getColumnIndex("lasttradepriceonly")), 
				stockRow.getString(stockRow.getColumnIndex("change")), stockRow.getString(stockRow.getColumnIndex("dayshigh")), 
				stockRow.getString(stockRow.getColumnIndex("dayslow")), stockRow.getString(stockRow.getColumnIndex("yearhigh")), 
				stockRow.getString(stockRow.getColumnIndex("yearlow")),stockRow.getString(stockRow.getColumnIndex("volume"))
				);
		
		//Set the listener for the stock card
		stockBlock.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				//Get the stock symbol from the card which is the parent of the imageButton
				TableRow cardTableRow = (TableRow) v.getParent();
				TextView stockSymbolTextView = (TextView) cardTableRow.findViewById (R.id.stockSymbolTextView);
				xml = new XMLParser(StockListFragment.this);
				try {
					xml.parseStock(stockSymbolTextView.getText().toString());
				} catch (UnsupportedEncodingException e) {
					Log.e(StockTraderActivity.APP_NAME_TAG, "Query cannot be encoded.");
					e.printStackTrace();
				}
			}
			
		});
		
		//Set the listener for the stock card sell button
		stockSell.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				parsingNews = false;
				Toast.makeText(getActivity(), "Processing Transaction...", Toast.LENGTH_SHORT).show();
				showSellDialog(v);
				
			}
			
		});

		tableLayout.addView(card);
	}
	
	private void showSellDialog(View v){
		
		XMLParser xml = new XMLParser(StockListFragment.this);
		TableRow stockRow = (TableRow) v.getParent().getParent();
		TextView stockSymbol = (TextView) stockRow.findViewById(R.id.stockSymbolTextView);
		try {
			xml.parseStock(stockSymbol.getText().toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void OnParseCompleted(StockDetails theStock) {
		
		if (theStock == null) {
			Toast.makeText(getActivity().getBaseContext(), R.string.invalid_search_alert, Toast.LENGTH_LONG).show();
		}else {
			
			bundle.putSerializable(DetailsStockViewActivity.STOCK_NAME_EXTRA, theStock);
			
			if (parsingNews){
				try {
					XMLNewsParser xmlNews = new XMLNewsParser(theStock.getName(), StockListFragment.this); //TODO:Change News parser to have a .parse method
				} catch (UnsupportedEncodingException e) {
					Log.e(StockTraderActivity.APP_NAME_TAG, "Company Name can not be encoded");
				}	
			}else{
				//Bundle all thats needed for the sell dialog
				bundle.putInt(StockTraderActivity.STOCK_QUANTITY_TAG, Integer.parseInt(stockQuantityTextView.getText().toString().substring(1, stockQuantityTextView.getText().toString().length())));
				FragmentManager manager = getActivity().getFragmentManager();
				SellDialog sellDialog = new SellDialog();
				sellDialog.setArguments(bundle);
				sellDialog.show(manager, "SellDialog");
			}
			
			parsingNews = true;
			
		}

	}

	@Override
	public void OnParseCompleted(ArrayList<NewsDetails> news) {

		if (news.isEmpty()) {
			Toast.makeText(getActivity().getBaseContext(), R.string.news_not_found, Toast.LENGTH_LONG).show();
			news = null;
		}

		Intent intent = new Intent(getActivity(), DetailsStockViewActivity.class);			
		DataWrapper newsData = new DataWrapper(news);
		bundle.putSerializable(DetailsStockViewActivity.NEWS_ARRAYLIST_EXTRA, newsData);
		intent.putExtras(bundle);
		startActivity(intent);

	}


}

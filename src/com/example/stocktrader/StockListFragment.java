package com.example.stocktrader;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;

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

	private XMLParser xml;

	//The bundle that will hold the stock and news class
	Bundle bundle = new Bundle();

	TableLayout stockList;
	StockDetails theStock;

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
		
		TextView stockNameTextView = (TextView) card.findViewById (R.id.stockNameTextView);
		TextView stockSymbolTextView = (TextView) card.findViewById (R.id.stockSymbolTextView);
		TextView lastTradePriceTextView = (TextView) card.findViewById (R.id.lastTradePriceTextView);
		TextView changeTextView = (TextView) card.findViewById (R.id.changeTextView);
		TextView stockQuantityTextView = (TextView) card.findViewById (R.id.stockQuantityTextView);
		TextView gainLossTextView = (TextView) card.findViewById(R.id.gainLossTextView);
		
		
		
		stockNameTextView.setText(stockRow.getString(stockRow.getColumnIndex("name")));
		stockSymbolTextView.setText(stockRow.getString(stockRow.getColumnIndex("symbol")));
		changeTextView.setText(stockRow.getString(stockRow.getColumnIndex("change")));
		lastTradePriceTextView.setText(stockRow.getString(stockRow.getColumnIndex("lasttradepriceonly")));
		stockQuantityTextView.setText("x" + stockRow.getString(stockRow.getColumnIndex("quantity")));
		
		Log.d("FORMULA", "First:" + Float.parseFloat(stockRow.getString(stockRow.getColumnIndex("lasttradepriceonly"))) + ", Second:" +  Float.parseFloat(stockRow.getString(stockRow.getColumnIndex("buyprice"))));
		
		gainLossTextView.setText( String.valueOf( (Float.parseFloat(stockRow.getString(stockRow.getColumnIndex("lasttradepriceonly"))) - Float.parseFloat(stockRow.getString(stockRow.getColumnIndex("buyprice")))) / Float.parseFloat(stockRow.getString(stockRow.getColumnIndex("buyprice")))) + "%");
		
		this.theStock = new StockDetails(stockRow.getString(1),stockRow.getString(2), stockRow.getString(3), stockRow.getString(4), stockRow.getString(5), stockRow.getString(6), stockRow.getString(7), stockRow.getString(8), stockRow.getString(9), stockRow.getString(10));

		stockSell.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {

				//Get the stock symbol from the card which is the parent of the imageButton
				TableRow cardTableRow = (TableRow) v.getParent();
				TextView stockSymbolTextView = (TextView) cardTableRow.findViewById (R.id.stockSymbolTextView);

				xml = new XMLParser(StockListFragment.this);
				try {
					xml.parseStock(stockSymbolTextView.getText().toString());
				} catch (UnsupportedEncodingException e) {
					Log.e(StockTraderActivity.TAG, "Query cannot be encoded.");
					e.printStackTrace();
				}

			}
		});

		tableLayout.addView(card);
	}


	@Override
	public void OnParseCompleted(StockDetails theStock) {
		if (theStock == null) {
			Toast.makeText(getActivity().getBaseContext(), R.string.invalid_search_alert, Toast.LENGTH_LONG).show();
		}else {
			bundle.putSerializable(DetailsStockViewActivity.STOCK_NAME_EXTRA, theStock);
			try {
				XMLNewsParser xmlNews = new XMLNewsParser(theStock.getName(), StockListFragment.this);
			} catch (UnsupportedEncodingException e) {
				Log.e(StockTraderActivity.TAG, "Company Name can not be encoded");
			}
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

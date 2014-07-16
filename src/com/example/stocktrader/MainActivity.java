package com.example.stocktrader;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.sql.SQLException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnParseComplete,Serializable{

	private static final long serialVersionUID = 1L;

	public static final String TAG = "StockTraderMainActivity";

	private Button mSearchStockButton;
	private EditText mStockSymbolEditText;
	private TableLayout mStockListTableLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//get reference to components
		mSearchStockButton = (Button) findViewById (R.id.addStock);
		mStockListTableLayout = (TableLayout) findViewById (R.id.stockListTableLayout);
		mStockSymbolEditText = (EditText) findViewById (R.id.cardStockSymbol);
		
		populateView(mStockListTableLayout);
		
		mSearchStockButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				XMLParser xml = new XMLParser(mStockSymbolEditText.getText().toString(), MainActivity.this);
			}
		});
	}
	
	Bundle bundle = new Bundle();
	
	public void OnParseCompleted(StockDetails theStock){
		if (theStock == null) {
			Toast.makeText(getBaseContext(), R.string.invalid_search_alert, Toast.LENGTH_LONG).show();
		}else {
			//Empty the search bar
			mStockSymbolEditText.setText("");
			Bundle bundle = new Bundle();
			Intent intent = new Intent(MainActivity.this, DetailsStockViewActivity.class);
			bundle.putSerializable(DetailsStockViewActivity.STOCK_NAME_EXTRA, theStock);
			try {
				XMLNewsParser xmlNews = new XMLNewsParser(theStock.getName(), MainActivity.this);
			} catch (UnsupportedEncodingException e) {
				Log.e(TAG, "Company Name can not be encoded");
			}
		}
	}
	
	public void OnParseCompleted(ArrayList<NewsDetails> news){
		if (news.isEmpty()) {
			Toast.makeText(getBaseContext(), R.string.news_not_found, Toast.LENGTH_LONG).show();
		} else {
			Intent intent = new Intent(MainActivity.this, DetailsStockViewActivity.class);
			DataWrapper newsData = new DataWrapper(news);
			bundle.putSerializable(DetailsStockViewActivity.NEWS_ARRAYLIST_EXTRA, newsData);
			intent.putExtras(bundle);
			startActivity(intent);
		}
	}

	//Populates the view with the stocks from the db
	private void populateView (TableLayout tableLayout){
		
		DBAdapter db = new DBAdapter(this);
		try {
			db.open();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		Cursor cursor = db.getAllStocks();
		if (cursor.moveToFirst()){
			do {
				renderCardToView(tableLayout, cursor);
			}while (cursor.moveToNext());
		}
		
		db.close();
		
	}

	private void renderCardToView(TableLayout tableLayout, Cursor stockRow) {
		
		LayoutInflater inflater = (LayoutInflater) getSystemService (Context.LAYOUT_INFLATER_SERVICE);
		View card = inflater.inflate(R.layout.stock_card, null);
		ImageButton detailsButton = (ImageButton) card.findViewById(R.id.stockDetailsButton);
		
		//Set the card text here
		TextView cardStockName = (TextView) card.findViewById (R.id.cardStockName);
		TextView cardStockSymbol = (TextView) card.findViewById (R.id.cardStockSymbol);
		TextView cardExchange = (TextView) card.findViewById (R.id.cardExchange);
		TextView cardLastTradePriceOnly = (TextView) card.findViewById (R.id.cardLastTradePriceOnly);
		TextView cardChange = (TextView) card.findViewById (R.id.cardChange);
		
		cardStockName.setText(stockRow.getString(1));
		cardStockSymbol.setText(stockRow.getString(2));
		cardChange.setText(stockRow.getString(3));
		cardExchange.setText(stockRow.getString(4));
		cardLastTradePriceOnly.setText(stockRow.getString(5));
		
		detailsButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {

				//Get the stock symbol from the card which is the parent of the imageButton
				TableRow cardTableRow = (TableRow) v.getParent();
				TextView stockSymbolTextView = (TextView) cardTableRow.findViewById(R.id.cardStockSymbol);
				Log.i(TAG, stockSymbolTextView.getText().toString());

				//Intent to start the details activity
				Intent intent = new Intent(MainActivity.this, DetailsStockViewActivity.class);
				intent.putExtra(DetailsStockViewActivity.STOCK_NAME_EXTRA, 
						stockSymbolTextView.getText().toString());
				startActivity(intent);

			}

		});
		
		tableLayout.addView(card);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}

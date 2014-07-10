package com.example.stocktrader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DetailsStockView extends Activity implements OnParseComplete{

	String stockName = "";
	StockDetails theStock;
	
	//Get the views that we will modify later
	TextView detailsName;
	TextView detailsSymbol;
	TextView detailsExchange;
	TextView detailsLastTradePriceOnly;
	TextView detailsChange;
	TextView detailsDaysHigh;
	TextView detailsDaysLow;
	TextView detailsYearHigh;
	TextView detailsYearLow;
	TextView detailsUserMoney;

	//Buttons
	Button detailsBuyStock;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details_stock_view);

		Intent intent = getIntent();
		stockName = intent.getStringExtra("stock_name");

		findViews();

		//Constructing the URL
		XMLParser xml = new XMLParser (stockName, this);
	}
	
	private void addButtonListeners(){
		detailsBuyStock.setOnClickListener(buyStockListener);
	}
	
	public void OnParseCompleted(StockDetails theStock){
		this.theStock = theStock;
		if (theStock == null) {
			Toast.makeText(getBaseContext(), R.string.invalid_search_alert, Toast.LENGTH_LONG).show();
		} else {
			detailsName.setText(theStock.getName());
			detailsSymbol.setText(theStock.getSymbol());
			detailsChange.setText("Change: " + theStock.getChange());
			detailsExchange.setText(theStock.getExchange());
			detailsLastTradePriceOnly.setText("Last Trade Price: " + theStock.getLastTradePriceOnly());
			detailsDaysHigh.setText("Days High: " + theStock.getDaysHigh());
			detailsDaysLow.setText("Days Low: " + theStock.getDaysLow());
			detailsYearHigh.setText("Year High: " + theStock.getYearHigh());
			detailsYearLow.setText("Year Low: " + theStock.getYearLow());	
			
			//Retrieve user cash from the db
			//Temp value
			detailsUserMoney.setText("$10,000");

			addButtonListeners();
		}
	}

	private void findViews(){
		//TextViews
		detailsName = (TextView) findViewById (R.id.detailsName);
		detailsSymbol = (TextView) findViewById (R.id.detailsSymbol);
		detailsExchange = (TextView) findViewById (R.id.detailsExchange);
		detailsLastTradePriceOnly = (TextView) findViewById (R.id.detailsLastTradePriceOnly);
		detailsChange = (TextView) findViewById (R.id.detailsChange);
		detailsDaysHigh = (TextView) findViewById (R.id.detailsDaysHigh);
		detailsDaysLow = (TextView) findViewById (R.id.detailsDaysLow);
		detailsYearHigh = (TextView) findViewById (R.id.detailsYearHigh);
		detailsYearLow = (TextView) findViewById (R.id.detailsYearLow);
		detailsUserMoney = (TextView) findViewById (R.id.detailsUserMoney);
		
		//Button
		detailsBuyStock = (Button) findViewById (R.id.detailsBuyButton);
	}
	
	//Listeners
	public OnClickListener buyStockListener = new OnClickListener (){
		@Override			
		public void onClick(View v) {
		}
	};
}
package com.example.stocktrader;

import java.io.Serializable;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class DetailsStockViewActivity extends Activity implements Serializable{
	
	private static final long serialVersionUID = 1L;

	public static final String STOCK_NAME_EXTRA = "stock name";
	
	private StockDetails theStock;
	
	//Get the views that we will modify later
	private TextView detailsName;
	private TextView detailsSymbol;
	private TextView detailsExchange;
	private TextView detailsLastTradePriceOnly;
	private TextView detailsChange;
	private TextView detailsDaysHigh;
	private TextView detailsDaysLow;
	private TextView detailsYearHigh;
	private TextView detailsYearLow;
	private TextView detailsUserMoney;

	//Buttons
	private Button detailsBuyStock;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details_stock_view);

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		this.theStock = (StockDetails) bundle.getSerializable(STOCK_NAME_EXTRA);

		//TextViews
		detailsName = (TextView) findViewById (R.id.detailsName);
		detailsSymbol = (TextView) findViewById (R.id.cardLastTradePriceOnly);
		detailsExchange = (TextView) findViewById (R.id.detailsExchange);
		detailsLastTradePriceOnly = (TextView) findViewById (R.id.cardExchange);
		detailsChange = (TextView) findViewById (R.id.cardChange);
		detailsDaysHigh = (TextView) findViewById (R.id.detailsDaysHigh);
		detailsDaysLow = (TextView) findViewById (R.id.detailsDaysLow);
		detailsYearHigh = (TextView) findViewById (R.id.detailsYearHigh);
		detailsYearLow = (TextView) findViewById (R.id.detailsYearLow);
		detailsUserMoney = (TextView) findViewById (R.id.detailsUserMoney);
		
		//Button
		detailsBuyStock = (Button) findViewById (R.id.detailsBuyButton);
		detailsBuyStock.setOnClickListener(new BuyStockListener());
		
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

	}
	
	//Listeners
	private class BuyStockListener implements OnClickListener {
		@Override			
		public void onClick(View v) {
		}
	}
}
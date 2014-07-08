package com.example.stocktrader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;

public class MainActivity extends Activity {
	
	Button addStocksButton;
	EditText stockSymbolEditText;
	
	TableLayout stockListTableLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		findViews();
		addButtonListeners();
		
	}
	
	private void findViews(){
		addStocksButton = (Button) findViewById (R.id.addStock);
		stockListTableLayout = (TableLayout) findViewById (R.id.stockListTableLayout);
		stockSymbolEditText = (EditText) findViewById (R.id.stockSymbolEditText);
	}
	
	private void addButtonListeners(){
		addStocksButton.setOnClickListener(addStocksListener);
	}
	
	//Add Stocks button listener
	public OnClickListener addStocksListener = new OnClickListener(){

		public void onClick(View v) {
			
			LayoutInflater inflater = (LayoutInflater) getSystemService (Context.LAYOUT_INFLATER_SERVICE);
			View card = inflater.inflate(R.layout.stock_card, null);
			Button detailsButton = (Button) card.findViewById(R.id.stockDetailsButton);
			detailsButton.setOnClickListener(stockDetailsListener);
			stockListTableLayout.addView(card);
			
		}
		
	};
	
	public OnClickListener stockDetailsListener = new OnClickListener (){

		@Override
		public void onClick(View v) {
			
			//Intent to start the details activity
			Intent intent = new Intent();
			
			//Get the stock name
			String stockSymbol = stockSymbolEditText.getText().toString();
			
			if (stockSymbol != null && stockSymbol.length() > 0){
				intent.putExtra("stock_name", stockSymbol);
				//TODO:Stock activity
			}else{
				//TODO: Raise Alert Box
			}
			
			
		}
		
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}

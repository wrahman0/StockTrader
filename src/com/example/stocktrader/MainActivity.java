package com.example.stocktrader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
			ImageButton detailsButton = (ImageButton) card.findViewById(R.id.stockDetailsButton);
			detailsButton.setOnClickListener(stockDetailsListener);
			stockListTableLayout.addView(card);
			
		}
		
	};
	
	public OnClickListener stockDetailsListener = new OnClickListener (){

		@Override
		public void onClick(View v) {
		
			//Intent to start the details activity
			Intent intent = new Intent();
			TableRow cardTableRow = (TableRow) v.getParent();
			TextView stockSymbolTextView = (TextView) cardTableRow.findViewById(R.id.stockSymbolEditText);
			intent.putExtra("stock_name", stockSymbolTextView.getText().toString());
			
		}
		
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}

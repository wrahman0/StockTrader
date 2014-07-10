package com.example.stocktrader;

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

public class MainActivity extends Activity {
	
	public static final String TAG = "STOCKTRADER"; 
	
	Button addStocksButton;
	EditText stockSymbolEditText;
	TableLayout stockListTableLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViews();
		addButtonListeners();
		
		//populate with stocks
		DBAdapter db = new DBAdapter(this);
		
		try {
			db.open();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		long id = db.insertStock("Google Inc.", "GOOG");
		id = db.insertStock("Apple Inc.", "AAPL");
		db.close();
		
		//Getting all the contacts
		try {
			db.open();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		Cursor c = db.getAllStocks();
		if(c.moveToFirst()){
			do {
				DisplayStock(c);
			}while (c.moveToNext());
		}
		
		db.close();
		
	}
	
	public void DisplayStock (Cursor c){
		Toast.makeText(this, "id: " + c.getString(0) + "\n" +
							 "Name: " + c.getString(1) + "\n" +
							 "Symbol: " + c.getString(2) + "\n", Toast.LENGTH_SHORT).show();
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
			
			//Get the stock symbol and clear the search bar
			String stockSymbol = stockSymbolEditText.getText().toString();
			stockSymbolEditText.setText("");
			
			if (stockSymbol != null && stockSymbol.length() > 0){
				ImageButton detailsButton = (ImageButton) card.findViewById(R.id.stockDetailsButton);
				detailsButton.setOnClickListener(stockDetailsListener);
				stockListTableLayout.addView(card);
			}else{
				Toast.makeText(getBaseContext(), R.string.empty_search_alert, Toast.LENGTH_LONG).show();
			}
			
		}
		
	};
	
	public OnClickListener stockDetailsListener = new OnClickListener (){

		@Override
		public void onClick(View v) {
		
			//Get the stock symbol from the card which is the parent of the imageButton
			TableRow cardTableRow = (TableRow) v.getParent();
			TextView stockSymbolTextView = (TextView) cardTableRow.findViewById(R.id.stockSymbolEditText);
			Log.e(TAG, stockSymbolTextView.getText().toString());
			
			//Intent to start the details activity
			Intent intent = new Intent(MainActivity.this, DetailsStockView.class);
			intent.putExtra("stock_name", stockSymbolTextView.getText().toString());
			startActivity(intent);
			
		}
		
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}

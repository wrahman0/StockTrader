package com.example.stocktrader;

import java.io.Serializable;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
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
		mStockSymbolEditText = (EditText) findViewById (R.id.stockSymbolEditText);


		mSearchStockButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {

				XMLParser xml = new XMLParser(mStockSymbolEditText.getText().toString(), MainActivity.this);

			}

		});

	}

	public void OnParseCompleted(StockDetails theStock){
		if (theStock == null) {

			Toast.makeText(getBaseContext(), R.string.invalid_search_alert, Toast.LENGTH_LONG).show();

		}else {

			Bundle bundle = new Bundle();
			Intent intent = new Intent(MainActivity.this, DetailsStockViewActivity.class);
			bundle.putSerializable(DetailsStockViewActivity.STOCK_NAME_EXTRA, theStock);
			intent.putExtras(bundle);
			startActivity(intent);

		}

	}

	//Add Stocks button listener
	//	public OnClickListener addStocksListener = new OnClickListener(){
	//		public void onClick(View v) {
	//			LayoutInflater inflater = (LayoutInflater) getSystemService (Context.LAYOUT_INFLATER_SERVICE);
	//			View card = inflater.inflate(R.layout.stock_card, null);
	//			
	//			//Get the stock symbol and clear the search bar
	//			String stockSymbol = mStockSymbolEditText.getText().toString();
	//			mStockSymbolEditText.setText("");
	//			if (stockSymbol != null && stockSymbol.length() > 0){
	//				ImageButton detailsButton = (ImageButton) card.findViewById(R.id.stockDetailsButton);
	//				detailsButton.setOnClickListener(stockDetailsListener);
	//				mStockListTableLayout.addView(card);
	//			}else{
	//				Toast.makeText(getBaseContext(), R.string.empty_search_alert, Toast.LENGTH_LONG).show();
	//			}
	//			
	//
	//		}
	//	};

	//Used to populate the scroll view with purchased stocks
	private class StockDetailsListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			//T: get all the views you need in onCreate
			//Get the stock symbol from the card which is the parent of the imageButton
			TableRow cardTableRow = (TableRow) v.getParent();
			TextView stockSymbolTextView = (TextView) cardTableRow.findViewById(R.id.stockSymbolEditText);
			Log.i(TAG, stockSymbolTextView.getText().toString());

			//Intent to start the details activity
			Intent intent = new Intent(MainActivity.this, DetailsStockViewActivity.class);
			intent.putExtra(DetailsStockViewActivity.STOCK_NAME_EXTRA, 
					stockSymbolTextView.getText().toString());
			startActivity(intent);	
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}

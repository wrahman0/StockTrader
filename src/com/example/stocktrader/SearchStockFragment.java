package com.example.stocktrader;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class SearchStockFragment extends Fragment implements OnParseComplete,Serializable{

	private static final long serialVersionUID = 1L;

	private SearchView mSearchView;
	private ListView mListView;
	
	private StockListAdapter mStockListAdapter;
	private XMLParser mXMLParser;

	private ArrayList<String>suggestedStockList = new ArrayList<String>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.search_stock,
				container, false);

		//get reference to components
		mSearchView = (SearchView)view.findViewById(R.id.searchView);
		mListView = (ListView)view.findViewById(R.id.searchListView);

		mSearchView.setSubmitButtonEnabled(true);
		mSearchView.setQueryHint(getString(R.string.stock_search_bar_hint));
		mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){

			@Override
			public boolean onQueryTextSubmit(String query) {
				StockSymbolSuggester mSuggester = new StockSymbolSuggester();
				mSuggester.execute(query);
				//XMLParser xml = new XMLParser(query, SearchStockFragment.this);

				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				// TODO Auto-generated method stub
				return false;
			}


		});
		
		mXMLParser = new XMLParser(SearchStockFragment.this);
		mStockListAdapter = new StockListAdapter(getActivity(), suggestedStockList, mXMLParser);
		mListView.setAdapter(mStockListAdapter);

		//  DBAdapterUser db = new DBAdapterUser(this);
		//  try {
		//   db.open();
		//  } catch (SQLException e) {
		//   // TODO Auto-generated catch block
		//   e.printStackTrace();
		//  }
		//  db.addUser("Wasiur", "0", "12000", "12000");
		//  db.close();

		//  if(container!=null)
		//   container.addView(view);
		return view;
	}

	Bundle bundle = new Bundle();

	public void OnParseCompleted(StockDetails theStock){
		if (theStock == null) {
			Toast.makeText(getActivity().getBaseContext(), R.string.invalid_search_alert, Toast.LENGTH_LONG).show();
		}else {
			//Empty the search bar
			mSearchView.setQuery("", false);
			
			bundle.putSerializable(DetailsStockViewActivity.STOCK_NAME_EXTRA, theStock);
			try {
				XMLNewsParser xmlNews = new XMLNewsParser(theStock.getName(), SearchStockFragment.this);
			} catch (UnsupportedEncodingException e) {
				Log.e(StockTraderActivity.TAG, "Company Name can not be encoded");
			}
		}
	}

	public void OnParseCompleted(ArrayList<NewsDetails> news){
		if (news.isEmpty()) {
			Toast.makeText(getActivity().getBaseContext(), R.string.news_not_found, Toast.LENGTH_LONG).show();
		} else {
			Intent intent = new Intent(getActivity(), DetailsStockViewActivity.class);			
			DataWrapper newsData = new DataWrapper(news);
			bundle.putSerializable(DetailsStockViewActivity.NEWS_ARRAYLIST_EXTRA, newsData);
			intent.putExtras(bundle);
			startActivity(intent);
		}
	}
	
	private void updateStockListing() {
		// TODO Auto-generated method stub
		mStockListAdapter.notifyDataSetChanged();
	}

	//Populates the view with the stocks from the db
	private void populateView (TableLayout tableLayout){
		DBAdapter db = new DBAdapter(getActivity());

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

		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService (Context.LAYOUT_INFLATER_SERVICE);

		View card = inflater.inflate(R.layout.stock_card, null);
		ImageButton detailsButton = (ImageButton) card.findViewById(R.id.stockDetailsButton);

		//Set the card text here
		TextView cardStockName = (TextView) card.findViewById (R.id.cardStockName);
		TextView cardStockSymbol = (TextView) card.findViewById (R.id.cardStockSymbol);
		TextView cardExchange = (TextView) card.findViewById (R.id.detailsLastTradePriceOnly);
		TextView cardLastTradePriceOnly = (TextView) card.findViewById (R.id.detailsSymbol);
		TextView cardChange = (TextView) card.findViewById (R.id.detailsChange);

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
				Log.i(StockTraderActivity.TAG, stockSymbolTextView.getText().toString());

				//Intent to start the details activity
				Intent intent = new Intent(getActivity(), DetailsStockViewActivity.class);
				intent.putExtra(DetailsStockViewActivity.STOCK_NAME_EXTRA, 
						stockSymbolTextView.getText().toString());
				startActivity(intent);

			}
		});
		StockDetails theStock = new StockDetails(stockRow.getString(1),stockRow.getString(2), stockRow.getString(3), stockRow.getString(4), stockRow.getString(5), stockRow.getString(6), stockRow.getString(7), stockRow.getString(8), stockRow.getString(9));
		detailsButton.setOnClickListener(new DetailsListener (theStock));
		tableLayout.addView(card);

	}

	private class DetailsListener implements OnClickListener {

		StockDetails theStock;

		public DetailsListener (StockDetails theStock){
			this.theStock = theStock;
		}

		@Override
		public void onClick(View v) {

			//Get the stock symbol from the card which is the parent of the imageButton
			TableRow cardTableRow = (TableRow) v.getParent();
			TextView stockSymbolTextView = (TextView) cardTableRow.findViewById(R.id.cardStockSymbol);
			Log.i(StockTraderActivity.TAG, stockSymbolTextView.getText().toString());

			//Intent to start the details activity
			Intent intent = new Intent(getActivity(), DetailsStockViewActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable(DetailsStockViewActivity.STOCK_NAME_EXTRA, theStock);
			intent.putExtras(bundle);
			startActivity(intent);

		}

	}
	
	private class StockSymbolSuggester extends AsyncTask<String, Void, Void> {
		private static final String TAG = "StockSymbolSuggester";
		
		private static final String QUERY_PLACEHOLDER = "MYQUERY";
		private String queryUrl = 
						"http://d.yimg.com/autoc.finance.yahoo.com/autoc?query="
								+QUERY_PLACEHOLDER
						+"&callback=YAHOO.Finance.SymbolSuggest.ssCallback";

		// JSON Node names
		private static final String RESULT_SET = "ResultSet";
		private static final String QUERY_RESULT = "Result";
		private static final String STOCK_SYMBOL = "symbol";
		
		private ArrayList<String>stockList = new ArrayList<String>();
		
		private String getJSONString(String str){
			
			str = str.replaceFirst("YAHOO.Finance.SymbolSuggest.ssCallback\\(", "");
			str = str.substring(0, str.length()-1);
			
			return str;
			
		}

		@Override
		protected Void doInBackground(String... arg0) {
			String query = arg0[0];
			
			// Creating service handler class instance
			WebServiceHandler wsh = new WebServiceHandler();
			
			//Making a request to url and getting response
			String jsonStr = getJSONString(wsh.makeWebServiceGet(
					queryUrl.replaceFirst(QUERY_PLACEHOLDER, query)));

			if (jsonStr != null) {
				try {
					JSONObject jsonObj = new JSONObject(jsonStr);
					JSONObject jsonObjResultSet = jsonObj.getJSONObject(RESULT_SET);

					JSONArray jsonArrResults = jsonObjResultSet.getJSONArray(QUERY_RESULT);
						
						for(int i=0;i<jsonArrResults.length();i++){
							JSONObject jsonObjStock = jsonArrResults.getJSONObject(i);

							String symbol = jsonObjStock.getString(STOCK_SYMBOL);
							stockList.add(symbol);

						}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				Log.e("ServiceHandler", "Couldn't get any data from the url");
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			
			for(String stock_symbol:stockList){
				Log.i(TAG, stock_symbol);
			}

			suggestedStockList.clear();
			suggestedStockList.addAll(stockList);
			updateStockListing();
		}

	}

}

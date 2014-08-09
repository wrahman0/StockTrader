package com.example.stocktrader;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

public class SearchStockFragment extends Fragment{

	private static final long serialVersionUID = 1L;

	private SearchView mSearchView;
	private ListView mListView;
	
	Bundle bundle = new Bundle();

	private StockListAdapter mStockListAdapter;

	private ArrayList<String>suggestedStockList = new ArrayList<String>();
	private ArrayList<String>stockNamesList = new ArrayList<String>();

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
				StockDetailsUpdater.stopUpdater();
				
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
		
		mStockListAdapter = new StockListAdapter(getActivity(), suggestedStockList, stockNamesList);
		mListView.setAdapter(mStockListAdapter);
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		if(isVisible()){
			StockDetailsUpdater.createUpdater(suggestedStockList,
					new StockDetailsUpdater.UpdateListener() {

				@Override
				public void onUpdate(String stockSymbol, StockDetails stockDetails){
					mStockListAdapter.updateStockToHashMap(stockSymbol, stockDetails);
				}
			});
			StockDetailsUpdater.startUpdater();
		}

	}

	private void updateStockListing() {
		mStockListAdapter.notifySearchQueryChanged();
	}

	private class StockSymbolSuggester extends AsyncTask<String, Void, Void> {
		private static final String TAG = "StockSymbolSuggester";

		private String query = "";
		
		private static final String url_first = "http://d.yimg.com/autoc.finance.yahoo.com/autoc?query=";
		private static final String url_second = "&callback=YAHOO.Finance.SymbolSuggest.ssCallback";
		

		// JSON Node names
		private static final String RESULT_SET = "ResultSet";
		private static final String QUERY_RESULT = "Result";
		private static final String STOCK_SYMBOL = "symbol";
		private static final String STOCK_NAME = "name";

		private ArrayList<String>symbolsList = new ArrayList<String>();
		private ArrayList<String>namesList = new ArrayList<String>();

		private String getJSONString(String str){
			if (str==null){
				return null;
			}
			
			str = str.replaceFirst("YAHOO.Finance.SymbolSuggest.ssCallback\\(", "");
			str = str.substring(0, str.length()-1);

			return str;

		}

		@Override
		protected Void doInBackground(String... arg0) {
			try {
				query = URLEncoder.encode(arg0[0], "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			String url = url_first + query + url_second;
			
			// Creating service handler class instance
			WebServiceHandler wsh = new WebServiceHandler();

			//Making a request to url and getting response
			String jsonStr = getJSONString(wsh.makeWebServiceGet(url));

			if (jsonStr != null) {
				try {
					JSONObject jsonObj = new JSONObject(jsonStr);
					JSONObject jsonObjResultSet = jsonObj.getJSONObject(RESULT_SET);

					JSONArray jsonArrResults = jsonObjResultSet.getJSONArray(QUERY_RESULT);

					for(int i=0;i<jsonArrResults.length();i++){
						JSONObject jsonObjStock = jsonArrResults.getJSONObject(i);

						String symbol = jsonObjStock.getString(STOCK_SYMBOL);
						String name = jsonObjStock.getString(STOCK_NAME);
						symbolsList.add(symbol);
						namesList.add(name);

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

			for(String stock_symbol:symbolsList){
				Log.i(TAG, stock_symbol);
			}
			
			if(symbolsList.size() == 0) {
				Toast.makeText(getActivity().getBaseContext(), R.string.no_search_results, Toast.LENGTH_LONG).show();
			}
			
			suggestedStockList.clear();
			stockNamesList.clear();
			
			suggestedStockList.addAll(symbolsList);
			stockNamesList.addAll(namesList);
			
			StockDetailsUpdater.createUpdater(suggestedStockList,
					new StockDetailsUpdater.UpdateListener() {

				@Override
				public void onUpdate(String stockSymbol, StockDetails stockDetails){
					mStockListAdapter.updateStockToHashMap(stockSymbol, stockDetails);
				}
			});
			StockDetailsUpdater.startUpdater();
			
			updateStockListing();
		}

	}

}
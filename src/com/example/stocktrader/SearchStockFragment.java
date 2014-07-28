package com.example.stocktrader;

import java.io.Serializable;
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

public class SearchStockFragment extends Fragment implements OnParseComplete,Serializable{

	private static final long serialVersionUID = 1L;

	private SearchView mSearchView;
	private ListView mListView;
	
	Bundle bundle = new Bundle();

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
		return view;
	}
	
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
			news = null;
		}
		
		Intent intent = new Intent(getActivity(), DetailsStockViewActivity.class);			
		DataWrapper newsData = new DataWrapper(news);
		bundle.putSerializable(DetailsStockViewActivity.NEWS_ARRAYLIST_EXTRA, newsData);
		intent.putExtras(bundle);
		startActivity(intent);
		
	}

	private void updateStockListing() {
		mStockListAdapter.notifyDataSetChanged();
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

		private ArrayList<String>stockList = new ArrayList<String>();

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
			
			if(stockList.size() == 0) {
				Toast.makeText(getActivity().getBaseContext(), R.string.no_search_results, Toast.LENGTH_LONG).show();
			}
			
			suggestedStockList.clear();
			suggestedStockList.addAll(stockList);
			updateStockListing();
		}

	}

}
package com.example.stocktrader;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class StockListAdapter extends ArrayAdapter<String> {
	private final Context context;
	private ArrayList<String>suggestedStockList;
	private XMLParser mXMLParser;
	
	private static class StockCardHolder {
		public TextView symbol;
	}
	
	public StockListAdapter(Context context, ArrayList<String>suggestedStockList, XMLParser parser){
		super(context, R.layout.stock_card, suggestedStockList);
		this.context = context;
		this.suggestedStockList = suggestedStockList;
		this.mXMLParser = parser;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View stockCardView = convertView;
		// reuse views
		if (stockCardView == null) {
			LayoutInflater inflater = (LayoutInflater)context.getSystemService
					(Context.LAYOUT_INFLATER_SERVICE);
			stockCardView = inflater.inflate(R.layout.stock_card, null);
			// configure view holder
			StockCardHolder stockCardHolder = new StockCardHolder();
			stockCardHolder.symbol = (TextView) stockCardView.findViewById(R.id.cardStockName);
			stockCardView.setTag(stockCardHolder);
		}
		
		StockCardHolder stockCardHolder = (StockCardHolder)stockCardView.getTag();
		stockCardHolder.symbol.setText(suggestedStockList.get(position));
		
		stockCardView.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				StockCardHolder stockCardHolder = (StockCardHolder)v.getTag();
				String query = (String) stockCardHolder.symbol.getText();
				try {
					mXMLParser.parseStock(query);
				} catch (UnsupportedEncodingException e) {
					Log.e(StockTraderActivity.APP_NAME_TAG, "Query cannot be encoded.");
					e.printStackTrace();
				}
			}
			
		});
		
		return stockCardView;
	}

} 

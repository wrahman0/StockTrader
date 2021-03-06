package com.wasiur.stocktrader;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wasiur.data.StockDetails;

public class StockListAdapter extends ArrayAdapter<String> {
	private final Context context;
	private ArrayList<String>suggestedStockList;
	private ArrayList<String>stockNamesList;
	private HashMap<String, StockDetails> mStockHashMap = new HashMap<String, StockDetails>();
	
	private class StockCardHolder {
		public TextView symbolTextView;
		public TextView nameTextView;
		public TextView currentPriceTextView;
		public TextView changeTextView;
		
		public ImageButton arrowImageButton;
		public ProgressBar progressBar;
	}
	
	public StockListAdapter(Context context, 
			ArrayList<String>suggestedStockList, ArrayList<String>stockNamesList){
		super(context, R.layout.stock_card, suggestedStockList);
		this.context = context;
		this.suggestedStockList = suggestedStockList;
		this.stockNamesList = stockNamesList;
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
			stockCardHolder.symbolTextView = (TextView) stockCardView.findViewById(R.id.cardStockSymbol);
			stockCardHolder.nameTextView = (TextView) stockCardView.findViewById(R.id.cardStockName);
			stockCardHolder.currentPriceTextView = (TextView) stockCardView.findViewById(R.id.detailsLastTradePriceOnly);
			stockCardHolder.changeTextView = (TextView) stockCardView.findViewById(R.id.detailsChange);
			stockCardHolder.arrowImageButton = (ImageButton) stockCardView.findViewById(R.id.stockDetailsButton);
			stockCardHolder.progressBar = (ProgressBar) stockCardView.findViewById(R.id.progressBar1);
			stockCardView.setTag(stockCardHolder);
			
		}
		
		String stockSymbol = suggestedStockList.get(position);
		StockCardHolder stockCardHolder = (StockCardHolder)stockCardView.getTag();
		stockCardHolder.symbolTextView.setText(suggestedStockList.get(position));
		stockCardHolder.nameTextView.setText(stockNamesList.get(position));

		StockDetails stockDetails = mStockHashMap.get(stockSymbol);
		if(stockDetails!=null){
			double currentPrice = Double.parseDouble(stockDetails.getLastTradePriceOnly());
			double priceChange = Double.parseDouble(stockDetails.getChange());

			stockCardHolder.changeTextView.setText(String.format("%.2f",priceChange));
			stockCardHolder.currentPriceTextView.setText("$"+String.format("%.2f",currentPrice));

			if (priceChange > 0.0){
				stockCardHolder.changeTextView.setTextColor(
						context.getResources().getColor(R.color.card_color_positive));
			}else {
				stockCardHolder.changeTextView.setTextColor(
						context.getResources().getColor(R.color.card_color_negative));
			}
			
			stockCardHolder.progressBar.setVisibility(View.GONE);
			stockCardHolder.progressBar.setIndeterminate(false);
			stockCardHolder.arrowImageButton.setVisibility(View.VISIBLE);
		}else{
			stockCardHolder.changeTextView.setText("??.??");
			stockCardHolder.currentPriceTextView.setText("$??.??");
			

			stockCardHolder.arrowImageButton.setVisibility(View.GONE);
			stockCardHolder.progressBar.setIndeterminate(true);
			stockCardHolder.progressBar.setVisibility(View.VISIBLE);
		}
		
		stockCardView.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				StockCardHolder stockCardHolder = (StockCardHolder)v.getTag();
				String stockSymbol = (String) stockCardHolder.symbolTextView.getText();
				
				if(mStockHashMap.containsKey(stockSymbol)){
					StockDetails stockDetails = mStockHashMap.get(stockSymbol);

					Log.d(StockTraderActivity.APP_NAME_TAG, "Clicked: "+stockSymbol);

					if(stockDetails!=null){
						Bundle b = new Bundle();
						b.putSerializable(DetailsStockViewActivity.STOCK_NAME_EXTRA, stockDetails);
						Intent intent = new Intent(context, DetailsStockViewActivity.class);
						intent.putExtras(b);
						context.startActivity(intent);
					}
				}
			}
			
		});

		return stockCardView;
	}
    
    public void notifySearchQueryChanged() {
        mStockHashMap.clear();
        
        for(String stockSymbol:suggestedStockList){
        	mStockHashMap.put(stockSymbol, null);
        }
        
        notifyDataSetChanged();
    }
    
    public void updateStockToHashMap(String stockSymbol, StockDetails stockDetails){

		if(stockDetails!=null){
			mStockHashMap.put(stockSymbol, stockDetails);
			notifyDataSetChanged();
		}
    }

} 

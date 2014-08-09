package com.example.stocktrader;

import java.io.UnsupportedEncodingException;
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
import android.widget.TextView;

public class StockListAdapter extends ArrayAdapter<String> {
	private final Context context;
	private ArrayList<String>suggestedStockList;
	private HashMap<String, StockCardHolder> mStockHashMap = new HashMap<String, StockCardHolder>();
	
	private static class StockCardHolder {
		public TextView symbol;
		public StockDetails mStockDetails;
		
		public void refreshViews(){
			if(mStockDetails!=null){
			}
		}
	}
	
	public StockListAdapter(Context context, ArrayList<String>suggestedStockList){
		super(context, R.layout.stock_card, suggestedStockList);
		this.context = context;
		this.suggestedStockList = suggestedStockList;
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
				String stockSymbol = (String) stockCardHolder.symbol.getText();
				
				if(mStockHashMap.containsKey(stockSymbol)){
					StockDetails stockDetails = mStockHashMap.get(stockSymbol).mStockDetails;

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
	
    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        mStockHashMap.clear();
        
        for(String stockSymbol:suggestedStockList){
        	mStockHashMap.put(stockSymbol, new StockCardHolder());
        }
    }
    
    public void updateStockToHashMap(String stockSymbol, StockDetails stockDetails){
		StockCardHolder holder = mStockHashMap.get(stockSymbol);
		if(stockDetails!=null && holder!=null){
			holder.mStockDetails = stockDetails;
			holder.refreshViews();
		}
    }

} 

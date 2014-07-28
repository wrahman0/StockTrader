package com.example.stocktrader;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;

import org.jsoup.Jsoup;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DetailsStockViewActivity extends Activity implements Serializable{

	private static final long serialVersionUID = 1L;

	public static final String STOCK_NAME_EXTRA = "stock name";
	public static final String NEWS_ARRAYLIST_EXTRA = "news arraylist";

	private StockDetails theStock;
	private ArrayList<NewsDetails> news;
	private NewsDetails theNews;
	
	private LinearLayout newsLinearLayout;
	
	//Get the views for stock that we will modify later
	private TextView detailsName;
	private TextView detailsSymbol;
	private TextView detailsExchange;
	private TextView detailsLastTradePriceOnly;
	private TextView detailsChange;
	private TextView detailsDaysHigh;
	private TextView detailsDaysLow;
	private TextView detailsYearHigh;
	private TextView detailsYearLow;

	//Buttons
	private ImageButton detailsBuyStock;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.details_redesigned);

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		theStock = (StockDetails) bundle.getSerializable(STOCK_NAME_EXTRA);
		news = ((DataWrapper) bundle.getSerializable(NEWS_ARRAYLIST_EXTRA)).getNews();

		if (news==null){
			theNews=null;
		}else{
			theNews = news.get(0);	
		}

		//TextViews for Stock
		detailsName = (TextView) findViewById (R.id.detailsName);
		detailsSymbol = (TextView) findViewById (R.id.detailsSymbol);
		detailsExchange = (TextView) findViewById (R.id.detailsExchange);
		detailsLastTradePriceOnly = (TextView) findViewById (R.id.detailsLastTradePriceOnly);
		detailsChange = (TextView) findViewById (R.id.detailsChange);
		detailsDaysHigh = (TextView) findViewById (R.id.detailsDaysHigh);
		detailsDaysLow = (TextView) findViewById (R.id.detailsDaysLow);
		detailsYearHigh = (TextView) findViewById (R.id.detailsYearHigh);
		detailsYearLow = (TextView) findViewById (R.id.detailsYearLow);
		
		//LinearLayout for the news
		newsLinearLayout = (LinearLayout) findViewById(R.id.newsLinearLayout);

		//Button
		detailsBuyStock = (ImageButton) findViewById (R.id.detailsBuyButton);
		detailsBuyStock.setOnClickListener(new BuyStockListener());

		//Setting the stock information
		detailsName.setText(theStock.getName());
		detailsSymbol.setText(theStock.getSymbol());
		detailsChange.setText("Change: " + theStock.getChange());
		detailsExchange.setText(theStock.getExchange());
		detailsLastTradePriceOnly.setText("Last Trade Price: " + theStock.getLastTradePriceOnly());
		detailsDaysHigh.setText("Days High: " + theStock.getDaysHigh());
		detailsDaysLow.setText("Days Low: " + theStock.getDaysLow());
		detailsYearHigh.setText("Year High: " + theStock.getYearHigh());
		detailsYearLow.setText("Year Low: " + theStock.getYearLow());	

		//Setting the news information. Jsoup.parse removes all the HTML tags
		if (news!=null){
			for (int i = 0; i < news.size(); i++){
				theNews=news.get(i);
				if (theNews!=null){
					
					LayoutInflater inflater = (LayoutInflater) getSystemService (Context.LAYOUT_INFLATER_SERVICE);
					View newsCard = inflater.inflate(R.layout.news_card, null);
					
					//Get the news card views
					TextView newsCardBody = (TextView) newsCard.findViewById(R.id.newsCardBody);
					
					//Set the news card views
					newsCardBody.setText(Jsoup.parse(theNews.getContent()).text());
					
					//Add the views to the table layout thats found in the details stock view activity
					newsLinearLayout.addView(newsCard);
					
				}
			}
		}else{
			LayoutInflater inflater = (LayoutInflater) getSystemService (Context.LAYOUT_INFLATER_SERVICE);
			View newsCard = inflater.inflate(R.layout.news_no_recent_news, null);
			//Add the views to the table layout thats found in the details stock view activity
			newsLinearLayout.addView(newsCard);
		}
		
	}

	//Gets the current user
	private UserDetails getUserDetails(){
		DBAdapterUser db = new DBAdapterUser(this);
		UserDetails theUser = null;

		try {
			db.open();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		Cursor cursor = db.getAllUsers();
		if (cursor.moveToFirst()){
			theUser = new UserDetails (cursor);
		}

		return theUser; 
	}

	//Listeners
	private class BuyStockListener implements OnClickListener {
		@Override			
		public void onClick(View v) {
			showPurchaseDialog(v);
		}
	}
	
	private void showPurchaseDialog(View v){
		FragmentManager manager = getFragmentManager();
		PurchaseDialog purchaseDialog = new PurchaseDialog();
		Bundle bundle = new Bundle();
		bundle.putSerializable(DetailsStockViewActivity.STOCK_NAME_EXTRA, theStock);
		purchaseDialog.setArguments(bundle);
		purchaseDialog.show(manager, "PurchaseDialog");
	}
}
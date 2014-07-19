package com.example.stocktrader;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;

import org.jsoup.Jsoup;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

public class DetailsStockViewActivity extends Activity implements Serializable{
	
	private static final long serialVersionUID = 1L;

	public static final String STOCK_NAME_EXTRA = "stock name";
	public static final String NEWS_ARRAYLIST_EXTRA = "news arraylist";
	
	private StockDetails theStock;
	private ArrayList<NewsDetails> news;
	private NewsDetails theNews;
	
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
//	private TextView detailsUserMoney;
	
	//Get the views for news that we will modify later
	private TextView detailsTitle;
	private TextView detailsContent;
	private TextView detailsPublisher;
	private TextView detailsPublishedDate;
//	private TextView detailsNewsURL;
	
	//Buttons
	private ImageButton detailsBuyStock;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        
		setContentView(R.layout.details_redesigned);

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		this.theStock = (StockDetails) bundle.getSerializable(STOCK_NAME_EXTRA);
		this.news = ((DataWrapper) bundle.getSerializable(NEWS_ARRAYLIST_EXTRA)).getNews();
		this.theNews = this.news.get(0);
		
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
		
		//TextViews for News
		detailsTitle = (TextView) findViewById (R.id.detailsTitle);
		detailsContent = (TextView) findViewById (R.id.detailsContent);
		detailsPublisher = (TextView) findViewById (R.id.detailsPublisher);
		detailsPublishedDate = (TextView) findViewById (R.id.detailsPublishedDate);
		
		//Button
		detailsBuyStock = (ImageButton) findViewById (R.id.detailsBuyButton);
		detailsBuyStock.setOnClickListener(new BuyStockListener());
		
		detailsName.setText(theStock.getName());
		detailsSymbol.setText(theStock.getSymbol());
		detailsChange.setText("Change: " + theStock.getChange());
		detailsExchange.setText(theStock.getExchange());
		detailsLastTradePriceOnly.setText("Last Trade Price: " + theStock.getLastTradePriceOnly());
		detailsDaysHigh.setText("Days High: " + theStock.getDaysHigh());
		detailsDaysLow.setText("Days Low: " + theStock.getDaysLow());
		detailsYearHigh.setText("Year High: " + theStock.getYearHigh());
		detailsYearLow.setText("Year Low: " + theStock.getYearLow());	
		
		detailsTitle.setText(Jsoup.parse(theNews.getTitle()).text());
		detailsContent.setText(Jsoup.parse(theNews.getContent()).text());
		detailsPublisher.setText(Jsoup.parse(theNews.getPublisher()).text());
		detailsPublishedDate.setText("Published Date: " + theNews.getPublishedDate());

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
			
		}
	}
}
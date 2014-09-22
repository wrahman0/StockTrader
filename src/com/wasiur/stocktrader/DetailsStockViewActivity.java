package com.wasiur.stocktrader;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;

import org.jsoup.Jsoup;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wasiur.data.NewsDetails;
import com.wasiur.data.StockDetails;
import com.wasiur.data.StockDetailsUpdater;
import com.wasiur.data.UserDetails;
import com.wasiur.database.DBAdapterUser;
import com.wasiur.parsing.OnParseComplete;
import com.wasiur.parsing.XMLNewsParser;

public class DetailsStockViewActivity extends Activity implements OnParseComplete{

	private static final long serialVersionUID = 1L;

	public static final String STOCK_NAME_EXTRA = "stock name";

	private ArrayList<String> mStockSymbol = new ArrayList<String>();
	private StockDetails theStock;

	private LinearLayout newsLinearLayout;
	private ProgressBar newsProgressBar;

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
	
	private XMLNewsParser mNewsParser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.details_redesigned);

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		theStock = (StockDetails) bundle.getSerializable(STOCK_NAME_EXTRA);

		// Find the views
		findViews();

		mStockSymbol.add(theStock.getSymbol());

		detailsBuyStock.setOnClickListener(new BuyStockListener());

		displayStockInfo();
		
		try {
			newsLinearLayout.setVisibility(View.GONE);
			newsProgressBar.setIndeterminate(true);
			newsProgressBar.setVisibility(View.VISIBLE);
			
			mNewsParser = new XMLNewsParser(theStock.getSymbol(),this);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void displayStockInfo() {
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
	}

	@Override
	public void onResume(){
		super.onResume();

		StockDetailsUpdater.createUpdater(mStockSymbol,
				new StockDetailsUpdater.UpdateListener() {

			@Override
			public void onUpdate(String stockSymbol, StockDetails stockDetails){
				if(stockDetails!=null && stockSymbol.equals(mStockSymbol.get(0))){
					theStock = stockDetails;
					displayStockInfo();
				}
			}

		});
		StockDetailsUpdater.startUpdater();
	}

	@Override
	public void onPause(){
		super.onPause();
		StockDetailsUpdater.stopUpdater();
	}

	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		ImageView bmImage;

		public DownloadImageTask(ImageView bmImage) {
			this.bmImage = bmImage;
		}

		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];
			Bitmap mIcon11 = null;
			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);
			} catch (Exception e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			return mIcon11;
		}

		protected void onPostExecute(Bitmap result) {
			bmImage.setImageBitmap(result);
		}
	}

	private void findViews(){
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
		newsLinearLayout = (LinearLayout)findViewById(R.id.newsLinearLayout);

		//Button
		detailsBuyStock = (ImageButton)findViewById(R.id.detailsBuyButton);
		
		//ProgressBar
		newsProgressBar = (ProgressBar)findViewById(R.id.newsProgressBar);
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
			showPurchaseDialog();
		}
	}

	private void showPurchaseDialog(){
		FragmentManager manager = getFragmentManager();
		PurchaseDialog purchaseDialog = new PurchaseDialog();
		Bundle bundle = new Bundle();
		bundle.putSerializable(DetailsStockViewActivity.STOCK_NAME_EXTRA, theStock);
		purchaseDialog.setArguments(bundle);
		purchaseDialog.show(manager, "PurchaseDialog");
	}

	@Override
	public void OnParseCompleted(ArrayList<NewsDetails> news) {

		newsProgressBar.setVisibility(View.GONE);
		newsProgressBar.setIndeterminate(false);
		newsLinearLayout.setVisibility(View.VISIBLE);
		
		//Setting the news information. Jsoup.parse removes all the HTML tags
		if (news!=null){
			NewsDetails theNews;
			for (int i = 0; i < news.size(); i++){
				theNews=news.get(i);
				if (theNews!=null){

					LayoutInflater inflater = (LayoutInflater) getSystemService (Context.LAYOUT_INFLATER_SERVICE);
					View newsCard = inflater.inflate(R.layout.news_card, null);

					newsCard.setTag(theNews);

					//Get the news card views
					TextView newsCardBody = (TextView) newsCard.findViewById(R.id.newsCardBody);
					ImageView newsImage = (ImageView) newsCard.findViewById(R.id.newsCardImageView);

					//Set the news card views
					newsCardBody.setText(Jsoup.parse(theNews.getDescription()).text());

					if(!(theNews.getImage().getSource() == null || theNews.getImage().getSource().isEmpty())) {
						new DownloadImageTask(newsImage).execute("http:" + theNews.getImage().getSource()); 
					}

					newsCard.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Uri uri = Uri.parse(((NewsDetails) v.getTag()).getLink());
							Intent intent = new Intent(Intent.ACTION_VIEW, uri);
							startActivity(intent);
						}
					});

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
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		mNewsParser.cancelTask();
	}
}
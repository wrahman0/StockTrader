package com.example.stocktrader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class DetailsStockView extends Activity implements OnParseComplete{

	String stockName = "";

	static final String yqlFirst = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quote%20where%20symbol%20in%20(%22";
	static final String yqlSecond = "%22)&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";

	//Get the views that we will modify later
	TextView detailsName;
	TextView detailsSymbol;
	TextView detailsExchange;
	TextView detailsLastTradePriceOnly;
	TextView detailsChange;
	TextView detailsDaysHigh;
	TextView detailsDaysLow;
	TextView detailsYearHigh;
	TextView detailsYearLow;

	String name = "";
	String symbol = "";
	String exchange = "";
	String lastTradePriceOnly = "";
	String change = "";
	String daysHigh = "";
	String daysLow = "";
	String yearHigh = "";
	String yearLow = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details_stock_view);

		Intent intent = getIntent();
		stockName = intent.getStringExtra("stock_name");

		findViews();

		Log.e(MainActivity.TAG, "Retrieving the stock info for " + stockName);

		//Constructing the URL
		XMLParser xml = new XMLParser (stockName, this);

	}
	
	public void OnTaskCompleted(StockDetails theStock){
		
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

//	private class MyAsyncTask extends AsyncTask<String, String, String>{
//
//		@Override
//		protected String doInBackground(String... args) {
//
//			try{
//
//				URL url = new URL(args[0]);
//				URLConnection connection = url.openConnection();
//				HttpURLConnection httpConnection = (HttpURLConnection) connection;
//				int responseCode = httpConnection.getResponseCode();
//				Log.e(MainActivity.TAG, "1");
//				//Proper connection is made
//				if (responseCode == HttpURLConnection.HTTP_OK){
//
//					InputStream in = httpConnection.getInputStream();
//					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//					DocumentBuilder db = dbf.newDocumentBuilder();
//					Log.e(MainActivity.TAG, "2");
//					//Getting the DOM
//					Document dom = db.parse(in);
//
//					//Root
//					Element ele = dom.getDocumentElement();
//
//					//Getting the quote tag
//					NodeList nl = ele.getElementsByTagName("quote");
//					Log.e(MainActivity.TAG, "3");
//					//Make sure that we indeed got the quote tag
//					if (nl != null && nl.getLength() > 0){
//						Log.e(MainActivity.TAG, "4");
//						StockDetails theStock = extractStockInformation(ele);
//						
//						//Setting the vars with the new parsed info
//						name = theStock.getName();
//						symbol = theStock.getSymbol();
//						exchange = theStock.getExchange();
//						lastTradePriceOnly = theStock.getLastTradePriceOnly();
//						change = theStock.getChange();
//						daysHigh = theStock.getDaysHigh();
//						daysLow = theStock.getDaysLow();
//						yearHigh = theStock.getYearHigh();
//						yearLow = theStock.getYearLow();
//
//					}
//
//				}
//
//			}catch (MalformedURLException e) {
//				Log.d(MainActivity.TAG, "MalformedURLException", e);
//			} catch (IOException e) {
//				Log.d(MainActivity.TAG, "IOException", e);
//			} catch (ParserConfigurationException e) {
//				Log.d(MainActivity.TAG, "Parser Configuration Exception", e);
//			} catch (SAXException e) {
//				Log.d(MainActivity.TAG, "SAX Exception", e);
//			}
//			finally {
//			}
//
//
//			return null;
//		}
//		
//		protected void onPostExecute(String result){
//			
//			
//
//		}
//		
//		private StockDetails extractStockInformation (Element root){
//
//			name = getTextValue (root, "Name");
//			symbol = getTextValue (root, "Symbol");
//			exchange = getTextValue (root, "StockExchange");
//			lastTradePriceOnly = getTextValue (root, "LastTradePriceOnly");
//			change = getTextValue (root, "Change");
//			daysHigh = getTextValue (root, "DaysHigh");
//			daysLow = getTextValue (root, "DaysLow");
//			yearHigh = getTextValue (root, "YearHigh");
//			yearLow = getTextValue (root, "YearLow");
//
//			StockDetails theStock = new StockDetails (name,symbol,exchange,lastTradePriceOnly, change, daysHigh, daysLow, yearHigh, yearLow);
//			return theStock;
//
//		}
//
//		//Given the root and the tag name within the root, returns the value inside the tag
//		private String getTextValue (Element root, String tag){
//
//			String result = null;
//			NodeList nl = root.getElementsByTagName(tag);
//			//Verify the result
//			if (nl != null && nl.getLength() > 0){
//				Element element = (Element) nl.item(0);
//				result = element.getFirstChild().getNodeValue();
//			}
//			return result;
//
//		}
//
//	}

	private void findViews(){

		detailsName = (TextView) findViewById (R.id.detailsName);
		detailsSymbol = (TextView) findViewById (R.id.detailsSymbol);
		detailsExchange = (TextView) findViewById (R.id.detailsExchange);
		detailsLastTradePriceOnly = (TextView) findViewById (R.id.detailsLastTradePriceOnly);
		detailsChange = (TextView) findViewById (R.id.detailsChange);
		detailsDaysHigh = (TextView) findViewById (R.id.detailsDaysHigh);
		detailsDaysLow = (TextView) findViewById (R.id.detailsDaysLow);
		detailsYearHigh = (TextView) findViewById (R.id.detailsYearHigh);
		detailsYearLow = (TextView) findViewById (R.id.detailsYearLow);


	}



}

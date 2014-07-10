package com.example.stocktrader;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.os.AsyncTask;
import android.util.Log;


//Handles all the xml parsing
//Takes the stock symbol as an argument for the contructor
public class XMLParser {
	
	private OnParseComplete listener;
	
	String url;
	StockDetails theStock;

	static final String yqlFirst = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quote%20where%20symbol%20in%20(%22";
	static final String yqlSecond = "%22)&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";

	//Needed for the constructor call to the StockDetails
	String name = "";
	String symbol = "";
	String exchange = "";
	String lastTradePriceOnly = "";
	String change = "";
	String daysHigh = "";
	String daysLow = "";
	String yearHigh = "";
	String yearLow = "";


	public XMLParser(String stock, OnParseComplete listener) {
		
		url = yqlFirst + stock + yqlSecond;
		new MyAsyncTask().execute(url);
		this.listener = listener;
		
	}


	private class MyAsyncTask extends AsyncTask<String, String, String>{

		@Override
		protected String doInBackground(String... args) {

			try{

				URL url = new URL(args[0]);
				URLConnection connection = url.openConnection();
				HttpURLConnection httpConnection = (HttpURLConnection) connection;
				int responseCode = httpConnection.getResponseCode();

				//Proper connection is made
				if (responseCode == HttpURLConnection.HTTP_OK){

					InputStream in = httpConnection.getInputStream();
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					DocumentBuilder db = dbf.newDocumentBuilder();

					//Getting the DOM
					Document dom = db.parse(in);

					//Root
					Element ele = dom.getDocumentElement();

					//Getting the quote tag
					NodeList nl = ele.getElementsByTagName("quote");

					//Make sure that we indeed got the quote tag
					if (nl != null && nl.getLength() > 0){
						theStock = extractStockInformation(ele);
					}

				}

			}catch (MalformedURLException e) {
				Log.d(MainActivity.TAG, "MalformedURLException", e);
			} catch (IOException e) {
				Log.d(MainActivity.TAG, "IOException", e);
			} catch (ParserConfigurationException e) {
				Log.d(MainActivity.TAG, "Parser Configuration Exception", e);
			} catch (SAXException e) {
				Log.d(MainActivity.TAG, "SAX Exception", e);
			}
			finally {
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			listener.OnParseCompleted(theStock);
		}

		private StockDetails extractStockInformation (Element root){

			name = getTextValue (root, "Name");
			symbol = getTextValue (root, "Symbol");
			exchange = getTextValue (root, "StockExchange");
			lastTradePriceOnly = getTextValue (root, "LastTradePriceOnly");
			change = getTextValue (root, "Change");
			daysHigh = getTextValue (root, "DaysHigh");
			daysLow = getTextValue (root, "DaysLow");
			yearHigh = getTextValue (root, "YearHigh");
			yearLow = getTextValue (root, "YearLow");

			StockDetails theStock = new StockDetails (name,symbol,exchange,lastTradePriceOnly,change,daysHigh,daysLow,yearHigh,yearLow);
			return theStock;

		}

		//Given the root and the tag name within the root, returns the value inside the tag
		private String getTextValue (Element root, String tag){

			String result = null;
			NodeList nl = root.getElementsByTagName(tag);
			//Verify the result
			if (nl != null && nl.getLength() > 0){
				Element element = (Element) nl.item(0);
				result = element.getFirstChild().getNodeValue();
			}
			return result;

		}

	}



}

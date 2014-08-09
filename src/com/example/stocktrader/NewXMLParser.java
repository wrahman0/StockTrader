package com.example.stocktrader;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.util.Log;

public class NewXMLParser {
	private static URL url;
	private static StockDetails theStock;

	private static final String YQL_FIRST = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quote%20where%20symbol%20in%20(%22";
	private static final String YQL_SECOND = "%22)&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";

	//Needed for the constructor call to the StockDetails
	private static String name = "";
	private static String symbol = "";
	private static String exchange = "";
	private static String lastTradePriceOnly = "";
	private static String change = "";
	private static String daysHigh = "";
	private static String daysLow = "";
	private static String yearHigh = "";
	private static String yearLow = "";
	private static String volume = "";

	public static StockDetails parseStock(String stock) throws UnsupportedEncodingException {

		try{
			url = new URL(YQL_FIRST + URLEncoder.encode(stock, "UTF-8") + YQL_SECOND);
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
			theStock = null;
			Log.e(StockTraderActivity.APP_NAME_TAG, "MalformedURLException", e);
		} catch (IOException e) {
			theStock = null;
			Log.e(StockTraderActivity.APP_NAME_TAG, "IOException", e);
		} catch (ParserConfigurationException e) {
			theStock = null;
			Log.e(StockTraderActivity.APP_NAME_TAG, "Parser Configuration Exception", e);
		} catch (SAXException e) {
			theStock = null;
			Log.e(StockTraderActivity.APP_NAME_TAG, "SAX Exception", e);
		}

		return theStock;
	}


	private static StockDetails extractStockInformation (Element root) {
		StockDetails theStock;
		try {
			name = getTextValue (root, "Name");
			symbol = getTextValue (root, "Symbol");
			exchange = getTextValue (root, "StockExchange");
			lastTradePriceOnly = getTextValue (root, "LastTradePriceOnly");
			change = getTextValue (root, "Change");
			daysHigh = getTextValue (root, "DaysHigh");
			daysLow = getTextValue (root, "DaysLow");
			yearHigh = getTextValue (root, "YearHigh");
			yearLow = getTextValue (root, "YearLow");
			volume = getTextValue (root, "Volume");
			theStock = new StockDetails (name,symbol,exchange,lastTradePriceOnly, change, daysHigh, daysLow, yearHigh, yearLow, volume);
		} catch (final NullPointerException e) {
			theStock = null;
		} 
		return theStock;
	}

	//Given the root and the tag name within the root, returns the value inside the tag
	private static String getTextValue (Element root, String tag) {
		String result = null;
		NodeList nl = root.getElementsByTagName(tag);
		//Verify the result
		if (nl != null && nl.getLength() > 0){
			Element element = (Element) nl.item(0);
			try {
				result = element.getFirstChild().getNodeValue();
			} catch(final NullPointerException e) {
				throw e;
			}
		}
		return result;
	}
}

package com.example.stocktrader;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.os.AsyncTask;
import android.util.Log;

//Handles all the xml parsing for the stock news
//Takes the stock name as an argument for the name
public class XMLNewsParser {
	private OnParseComplete listener;
	
	private String url;
	NewsDetails theNews;
	ArrayList<NewsDetails> news = new ArrayList<NewsDetails>();
	
	private static final String YQL_FIRST = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20google.news%20where%20q%20%3D%20%22";
	private static final String YQL_SECOND = "%22&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
	
	//Needed for the constructor call to the NewsDetails
	String content = "";
	String newsURL = "";
	String title = "";
	String publisher = "";
	String publishedDate = "";
//	String imageURL = "";
//	String imagePublisher = "";
//	String imageWidth = "";
//	String imageHeight = "";

	public XMLNewsParser(String companyName, OnParseComplete listener) throws UnsupportedEncodingException {
		url = YQL_FIRST + URLEncoder.encode(companyName, "UTF-8") + YQL_SECOND;
		Log.i(StockTraderActivity.TAG, url);
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
					NodeList nl = ele.getElementsByTagName("results");
					
					//Make sure that we indeed got the quote tag
					if (nl != null && nl.getLength() > 0){
						Element results = (Element) nl.item(0);
						NodeList newsResults = results.getElementsByTagName("results");
						if (newsResults != null && newsResults.getLength() > 0) {
							for(int i = 0; i < newsResults.getLength(); i++) {
								Log.i(StockTraderActivity.TAG, String.valueOf(i));
								theNews = extractNewsInformation((Element) newsResults.item(i));
								if(theNews != null) {
									news.add(theNews);
								}
							}
						}
					} else {
						Log.e(StockTraderActivity.TAG, "Could not find the results tag while parsing News");
					}
				}
			}catch (MalformedURLException e) {
				Log.e(StockTraderActivity.TAG, "MalformedURLException", e);
			} catch (IOException e) {
				Log.e(StockTraderActivity.TAG, "IOException", e);
			} catch (ParserConfigurationException e) {
				Log.e(StockTraderActivity.TAG, "Parser Configuration Exception", e);
			} catch (SAXException e) {
				Log.e(StockTraderActivity.TAG, "SAX Exception", e);
			}
			finally {
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			listener.OnParseCompleted(news);
		}

		private NewsDetails extractNewsInformation (Element result) {
			NewsDetails theNews;
			//ImageDetails theImage;
			try {
				content = getTextValue (result, "content");			
				newsURL = getTextValue (result, "unescapedUrl");
				title = getTextValue (result, "title");
				publisher = getTextValue (result, "publisher");
				publishedDate = getTextValue (result, "publishedDate");
				
				Log.i(StockTraderActivity.TAG, content);
				Log.i(StockTraderActivity.TAG, newsURL);
				Log.i(StockTraderActivity.TAG, title);
				Log.i(StockTraderActivity.TAG, publisher);
				Log.i(StockTraderActivity.TAG, publishedDate);
				//theImage = extractImageInformation(result.getElementsByTagName("image"));
			} catch (final NullPointerException e) {
				theNews = null;
				return theNews;
			} 
			//ImageDetails theImage = new ImageDetails(imageURL, imagePublisher, Integer.parseInt(imageWidth), 
					//Integer.parseInt(imageHeight));
			theNews = new NewsDetails (content, newsURL, title, publisher, publishedDate);
			return theNews;
		}
		
//		private ImageDetails extractImageInformation (Element result) {
//			ImageDetails theImage;
//			try {
//				imageURL = getTextValue (result, "DaysHigh");
//				imagePublisher = getTextValue (result, "DaysHigh");
//				imageWidth = getTextValue (result, "DaysHigh");
//				imageHeight = getTextValue (result, "DaysHigh");
//			} catch (final NullPointerException e) {
//				theImage = null;
//				return theImage;
//			} 
//			ImageDetails theImage = new ImageDetails(imageURL, imagePublisher, Integer.parseInt(imageWidth), 
//					Integer.parseInt(imageHeight));
//			return theImage;
//		}

		//Given the root and the tag name within the root, returns the value inside the tag
		private String getTextValue (Element root, String tag) {
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
}

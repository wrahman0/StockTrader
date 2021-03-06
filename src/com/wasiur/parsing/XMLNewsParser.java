package com.wasiur.parsing;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
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
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.wasiur.data.NewsDetails;
import com.wasiur.stocktrader.StockTraderActivity;

import android.os.AsyncTask;
import android.util.Log;

//Handles all the xml parsing for the stock news
//Takes the stock name as an argument for the name
public class XMLNewsParser {
	private OnParseComplete listener;
	
	private String url;
	NewsDetails theNews;
	ArrayList<NewsDetails> news = new ArrayList<NewsDetails>();
	
	private static final String YQL_FIRST = "https://news.google.com/news/feeds?q=";
	private static final String YQL_SECOND = "&output=rss";
	
	//Needed for the constructor call to the NewsDetails
	private String newsTitle = "";
	private String newsLink = "";
	private String publishedDate = "";
	private String description = "";
	
	private MyAsyncTask mTask;

	public XMLNewsParser(String companyName, OnParseComplete listener) throws UnsupportedEncodingException {
		this.listener = listener;
		url = YQL_FIRST + URLEncoder.encode(companyName, "UTF-8") + YQL_SECOND;
		Log.i(StockTraderActivity.APP_NAME_TAG, url);
		mTask = new MyAsyncTask();
		mTask.execute(url);
	}
	
	public void cancelTask(){
		Log.d(StockTraderActivity.APP_NAME_TAG, "NewsParser cancelled");
		mTask.cancel(true);
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
					
					String xml = printDocument(dom);
					Log.i(StockTraderActivity.APP_NAME_TAG, xml);
					
					//Root
					Element ele = dom.getDocumentElement();

					//Getting the quote tag
					NodeList nl = ele.getElementsByTagName("channel");
					
					//Make sure that we indeed got the quote tag
					if (nl != null && nl.getLength() > 0){
						Element results = (Element) nl.item(0);
						NodeList newsResults = results.getElementsByTagName("item");
						if (newsResults != null && newsResults.getLength() > 0) {
							for(int i = 0; i < newsResults.getLength(); i++) {
								Log.i(StockTraderActivity.APP_NAME_TAG, String.valueOf(i));
								theNews = extractNewsInformation((Element) newsResults.item(i));
								if(theNews != null) {
									news.add(theNews);
								}
							}
						} else {
							Log.e(StockTraderActivity.APP_NAME_TAG, "Could not find any news related to this company.");
						}
					} else {
						Log.e(StockTraderActivity.APP_NAME_TAG, "Could not find the results tag while parsing News.");
					}
				}
			}catch (MalformedURLException e) {
				Log.e(StockTraderActivity.APP_NAME_TAG, "MalformedURLException", e);
			} catch (IOException e) {
				Log.e(StockTraderActivity.APP_NAME_TAG, "IOException", e);
			} catch (ParserConfigurationException e) {
				Log.e(StockTraderActivity.APP_NAME_TAG, "Parser Configuration Exception", e);
			} catch (SAXException e) {
				Log.e(StockTraderActivity.APP_NAME_TAG, "SAX Exception", e);
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e(StockTraderActivity.APP_NAME_TAG, "SAX Exception", e);
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
				newsTitle = getTextValue (result, "title");			
				newsLink = getTextValue (result, "link");
				publishedDate = getTextValue (result, "pubDate");
				description = getTextValue (result, "description");
				
				Log.i(StockTraderActivity.APP_NAME_TAG, newsTitle);
				Log.i(StockTraderActivity.APP_NAME_TAG, newsLink);
				Log.i(StockTraderActivity.APP_NAME_TAG, publishedDate);
				Log.i(StockTraderActivity.APP_NAME_TAG, description);

				//theImage = extractImageInformation(result.getElementsByTagName("image"));
			} catch (final NullPointerException e) {
				theNews = null;
				return theNews;
			} 
			theNews = new NewsDetails (newsTitle, newsLink, publishedDate, description);
			return theNews;
		}
		

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
		
		private String printDocument(Document doc) throws IOException, TransformerException{
		    Transformer transformer = TransformerFactory.newInstance().newTransformer();
		    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		    StreamResult result = new StreamResult(new StringWriter());
		    transformer.transform(new DOMSource(doc), result);
		    String xmlString = result.getWriter().toString();
		    return xmlString;
		}
	}
}

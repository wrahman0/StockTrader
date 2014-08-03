package com.example.stocktrader;

import java.io.Serializable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.util.Log;

public class NewsDetails implements Serializable{
	private static final long serialVersionUID = 1L;
	private String content;
	private String newsURL;
	private String title;
	private String link;
	private String publishedDate;
	private String description;
	private ImageDetails image; 
	
	public NewsDetails(String title, String link, String publishedDate, String description) {
		this.title = title;
		this.link = link;
		this.publishedDate = publishedDate;
		this.description = description;
		getImageURL();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getPublishedDate() {
		return publishedDate;
	}

	public void setPublishedDate(String publishedDate) {
		this.publishedDate = publishedDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public ImageDetails getImage() {
		return image;
	}

	public void setImage(ImageDetails image) {
		this.image = image;
	}
	
	private void getImageURL() {
		Document doc = Jsoup.parse(description);
		Element image = doc.select("img").get(0);
		 
		Log.i("NewsDetails", "\nsrc : " + image.attr("src"));
		Log.i("NewsDetails", "height : " + image.attr("width"));
		Log.i("NewsDetails", "height : " + image.attr("height"));
		
		this.image = new ImageDetails(image.attr("src"), Integer.parseInt(image.attr("width")), Integer.parseInt(image.attr("height")));
	}
}

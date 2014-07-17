package com.example.stocktrader;

import java.io.Serializable;

public class NewsDetails implements Serializable{
	private String content;
	private String newsURL;
	private String title;
	private String publisher;
	private String publishedDate;
	//private ImageDetails image;
	
	public NewsDetails(String content, String newsURL, String title,
			String publisher, String publishedDate) {
		super();
		this.content = content;
		this.newsURL = newsURL;
		this.title = title;
		this.publisher = publisher;
		this.publishedDate = publishedDate;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUrl() {
		return newsURL;
	}

	public void setUrl(String newsURL) {
		this.newsURL = newsURL;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getPublishedDate() {
		return publishedDate;
	}

	public void setPublishedDate(String publishedDate) {
		this.publishedDate = publishedDate;
	}
}

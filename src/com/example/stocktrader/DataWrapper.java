package com.example.stocktrader;

import java.io.Serializable;
import java.util.ArrayList;

public class DataWrapper implements Serializable{
	private ArrayList<NewsDetails> news;

	public DataWrapper(ArrayList<NewsDetails> news) {
		this.news = news;
	}

	public ArrayList<NewsDetails> getNews() {
		return news;
	}

	public void setNews(ArrayList<NewsDetails> news) {
		this.news = news;
	}
}

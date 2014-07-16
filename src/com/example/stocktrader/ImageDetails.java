package com.example.stocktrader;

public class ImageDetails {
	private String url;
	private String publisher;
	private int width;
	private int height;
	
	public ImageDetails(String url, String publisher, int width,
			int height) {
		this.url = url;
		this.publisher = publisher;
		this.width = width;
		this.height = height;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
}
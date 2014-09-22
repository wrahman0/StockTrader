package com.wasiur.data;

import java.io.Serializable;

public class ImageDetails implements Serializable{
	private static final long serialVersionUID = 1L;
	private String source;
	private int width;
	private int height;
	
	public ImageDetails(String source, int width, int height) {
		this.source = source;
		this.width = width;
		this.height = height;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
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

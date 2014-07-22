package com.example.stocktrader;

import android.database.Cursor;

public class UserDetails {
	
	private long _id;
	private String username = "";
	private int stocksBought = 0;
	private float startingCash = (float) 0.0;
	private float currentCash = (float) 0.0;
	
	public UserDetails(Cursor userRow) {
		this._id = (long) userRow.getFloat(0);
		this.username = userRow.getString(1);
		this.stocksBought = Integer.parseInt(userRow.getString(2));
		this.startingCash = Float.parseFloat(userRow.getString(3));
		this.currentCash = Float.parseFloat(userRow.getString(4));
	}
	

	public long get_id() {
		return _id;
	}


	public void set_id(long _id) {
		this._id = _id;
	}


	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getStocksBought() {
		return stocksBought;
	}

	public void setStocksBought(int stocksBought) {
		this.stocksBought = stocksBought;
	}

	public float getStartingCash() {
		return startingCash;
	}

	public void setStartingCash(float startingCash) {
		this.startingCash = startingCash;
	}

	public float getCurrentCash() {
		return currentCash;
	}

	public void setCurrentCash(float currentCash) {
		this.currentCash = currentCash;
	}
	
}

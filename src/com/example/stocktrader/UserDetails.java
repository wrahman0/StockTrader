package com.example.stocktrader;

import android.database.Cursor;

public class UserDetails {
	
	private String username = "";
	private int stocksBought = 0;
	private int startingCash = 0;
	private int currentCash = 0;
	
	public UserDetails(Cursor userRow) {
		this.username = userRow.getString(1);
		this.stocksBought = Integer.parseInt(userRow.getString(2));
		this.startingCash = Integer.parseInt(userRow.getString(3));
		this.currentCash = Integer.parseInt(userRow.getString(4));
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

	public int getStartingCash() {
		return startingCash;
	}

	public void setStartingCash(int startingCash) {
		this.startingCash = startingCash;
	}

	public int getCurrentCash() {
		return currentCash;
	}

	public void setCurrentCash(int currentCash) {
		this.currentCash = currentCash;
	}
	
}

package com.example.stocktrader;

import java.sql.SQLException;

import android.content.Context;
import android.database.Cursor;

public class UserDetails {
	
	private long _id;
	private String username = "";
	private float startingCash = (float) 0.0;
	private float currentCash = (float) 0.0;
	private float currentStockValue = (float) 0.0;
	private float gainLoss = (float) 0.0;
	private int stocksBought = 0;
	private int stocksOwned = 0;
	private int totalTransactions = 0;
	private int positiveTransactions = 0;
	private int negativeTransactions = 0;

	public UserDetails(Cursor userRow) {
		this._id = (long) userRow.getFloat(0);
		this.username = userRow.getString(1);
		this.stocksBought = Integer.parseInt(userRow.getString(2));
		this.startingCash = Float.parseFloat(userRow.getString(3));
		this.currentCash = Float.parseFloat(userRow.getString(4));
		this.currentStockValue = Float.parseFloat(userRow.getString(5));
		this.gainLoss = Float.parseFloat(userRow.getString(6));
		this.stocksOwned = Integer.parseInt(userRow.getString(7));
		this.totalTransactions = Integer.parseInt(userRow.getString(8));
		this.positiveTransactions = Integer.parseInt(userRow.getString(9));
		this.negativeTransactions = Integer.parseInt(userRow.getString(10));
	}
	
	public float getCurrentStockValue(Context ctx) {
		
		setCurrentStockValue((float)0.0); 
		
		//Open stocklist db
		DBAdapter db = new DBAdapter(ctx);
		try{
			db.open();
		}catch (SQLException e){
			e.printStackTrace();
		}
		
		Cursor allStocks = db.getAllStocks();
		
		if (!allStocks.moveToFirst()){
			return (float) 0.0;
		}
		
		//Calculate the value of the stocks
		do {
			this.currentStockValue += Float.parseFloat(allStocks.getString(allStocks.getColumnIndex("lasttradepriceonly")));
		}while (allStocks.moveToNext());
		
		return this.currentStockValue;
		
	}


	public void setCurrentStockValue(float currentStockValue) {
		this.currentStockValue = currentStockValue;
	}


	public float getGainLoss() {
		return currentCash + currentStockValue - startingCash;
	}


	public void setGainLoss(float gainLoss) {
		this.gainLoss = gainLoss;
	}


	public int getStocksOwned() {
		return stocksOwned;
	}


	public void setStocksOwned(int stocksOwned) {
		this.stocksOwned = stocksOwned;
	}


	public int getTotalTransactions() {
		return totalTransactions;
	}


	public void setTotalTransactions(int totalTransactions) {
		this.totalTransactions = totalTransactions;
	}


	public int getPositiveTransactions() {
		return positiveTransactions;
	}


	public void setPositiveTransactions(int positiveTransactions) {
		this.positiveTransactions = positiveTransactions;
	}


	public int getNegativeTransactions() {
		return negativeTransactions;
	}


	public void setNegativeTransactions(int negativeTransactions) {
		this.negativeTransactions = negativeTransactions;
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

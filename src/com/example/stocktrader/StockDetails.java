package com.example.stocktrader;

public class StockDetails {
	String name = "";
	String symbol = "";
	String exchange = "";
	String lastTradePriceOnly = "";
	String change = "";
	String daysHigh = "";
	String daysLow = "";
	String yearHigh = "";
	String yearLow = "";

	public StockDetails(String name, String symbol, String exchange,
			String lastTradePriceOnly, String change, String daysHigh,
			String daysLow, String yearHigh, String yearLow) {
		this.name = name;
		this.symbol = symbol;
		this.exchange = exchange;
		this.lastTradePriceOnly = lastTradePriceOnly;
		this.change = change;
		this.daysHigh = daysHigh;
		this.daysLow = daysLow;
		this.yearHigh = yearHigh;
		this.yearLow = yearLow;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public String getExchange() {
		return exchange;
	}
	public void setExchange(String exchange) {
		this.exchange = exchange;
	}
	public String getLastTradePriceOnly() {
		return lastTradePriceOnly;
	}
	public void setLastTradePriceOnly(String lastTradePriceOnly) {
		this.lastTradePriceOnly = lastTradePriceOnly;
	}
	public String getChange() {
		return change;
	}
	public void setChange(String change) {
		this.change = change;
	}
	public String getDaysHigh() {
		return daysHigh;
	}
	public void setDaysHigh(String daysHigh) {
		this.daysHigh = daysHigh;
	}
	public String getDaysLow() {
		return daysLow;
	}
	public void setDaysLow(String daysLow) {
		this.daysLow = daysLow;
	}
	public String getYearHigh() {
		return yearHigh;
	}
	public void setYearHigh(String yearHigh) {
		this.yearHigh = yearHigh;
	}
	public String getYearLow() {
		return yearLow;
	}
	public void setYearLow(String yearLow) {
		this.yearLow = yearLow;
	}	
}
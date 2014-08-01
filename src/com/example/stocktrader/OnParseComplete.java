package com.example.stocktrader;

import java.util.ArrayList;

public interface OnParseComplete {
	
	void OnParseCompleted (StockDetails theStock);
	
	void OnParseCompleted(ArrayList<NewsDetails> news);
	
}
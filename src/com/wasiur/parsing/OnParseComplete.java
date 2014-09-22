package com.wasiur.parsing;

import java.util.ArrayList;

import com.wasiur.data.NewsDetails;

public interface OnParseComplete {

	void OnParseCompleted(ArrayList<NewsDetails> news);	
}
package com.example.stocktrader;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;

public class StockTraderActivity extends FragmentActivity {
	public static final String TAG = "StockTraderActivity";
	
	private FragmentTabHost mFragmentTabHost;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stock_trader_main);
		
		mFragmentTabHost = (FragmentTabHost)findViewById(R.id.tabHost);
        mFragmentTabHost.setup(this, getSupportFragmentManager(),R.layout.stock_trader_main);
        
        mFragmentTabHost.addTab(mFragmentTabHost.newTabSpec("search").setIndicator("Search"),
        		SearchStockFragment.class, null);
        
        //temporary placeholder tabs
        mFragmentTabHost.addTab(mFragmentTabHost.newTabSpec("my stocks").setIndicator("My Stocks"),
        		SearchStockFragment.class, null);
        mFragmentTabHost.addTab(mFragmentTabHost.newTabSpec("account").setIndicator("Account"),
        		SearchStockFragment.class, null);
//        mFragmentTabHost.addTab(mFragmentTabHost.newTabSpec("search").setIndicator("Search"),
//                null, null);
//        mFragmentTabHost.addTab(mFragmentTabHost.newTabSpec("account").setIndicator("Account"),
//                null, null);

	}
	
}

package com.wasiur.stocktrader;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.WindowManager;

import com.wasiur.data.StockDetailsUpdater;

public class StockTraderActivity extends FragmentActivity {
	
	public static final String APP_NAME_TAG = "StockTrader";
	public static final String STOCK_NAME_TAG = "stock name";
	public static final String STOCK_QUANTITY_TAG = "stock quantity";
	public static final String STOCK_DATABASE_TAG = "DBAdapter";

	private ViewPager mViewPager;
	private TabFragmentPagerAdapter mPagerAdapter;
	private ArrayList<Fragment> mFragments = new ArrayList<Fragment>();

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.stock_trader_main);

		mViewPager = (ViewPager)findViewById(R.id.pager);
		
		mFragments.add(new SearchStockFragment());
		mFragments.add(new StockListFragment());
		mFragments.add(new MyAccountFragment());
		
		mPagerAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager());
		mViewPager.setOffscreenPageLimit(2);
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				Log.d(APP_NAME_TAG,"onPageSelected: "+position);
				// When swiping between pages, select the
				// corresponding tab.
				getActionBar().setSelectedNavigationItem(position);
				mFragments.get(position).onResume();
			}
		});

		final ActionBar actionBar = getActionBar();
		// Specify that tabs should be displayed in the action bar.
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create a tab listener that is called when the user changes tabs.
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {

			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				mViewPager.setCurrentItem(tab.getPosition());
			}

			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub

			}

		};

		//get string array for tab labels
		Resources res = getResources();
		String[] tabLabels = res.getStringArray(R.array.tab_labels);

		for(String tabLabel:tabLabels){
			actionBar.addTab(actionBar.newTab()
					.setText(tabLabel)
					.setTabListener(tabListener));
		}
		
	}
	
	@Override
	public void onResume(){
		super.onPause();
		StockDetailsUpdater.startUpdater();
	}
	
	@Override
	public void onPause(){
		super.onPause();
		StockDetailsUpdater.stopUpdater();
	}

	public class TabFragmentPagerAdapter extends FragmentStatePagerAdapter {
		public TabFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			Fragment fragment = mFragments.get(i);

			return fragment;
			
		}

		@Override
		public int getCount() {
			return mFragments.size();
		}

	}
	
	public Fragment getCurrentFragment(){
		
		Log.d(APP_NAME_TAG, ""+mViewPager.getCurrentItem());
		return mFragments.get(mViewPager.getCurrentItem());
	}

}

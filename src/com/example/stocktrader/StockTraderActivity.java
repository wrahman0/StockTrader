package com.example.stocktrader;

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
import android.view.WindowManager;

public class StockTraderActivity extends FragmentActivity {
	
	public static final String APP_NAME_TAG = "StockTrader";
	public static final String STOCK_NAME_TAG = "stock name";
	public static final String STOCK_QUANTITY_TAG = "stock quantity";

	private ViewPager mViewPager;
	private TabFragmentPagerAdapter mPagerAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.stock_trader_main);

		mViewPager = (ViewPager)findViewById(R.id.pager);

		mPagerAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager());
		mViewPager.setOffscreenPageLimit(2);
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				// When swiping between pages, select the
				// corresponding tab.
				getActionBar().setSelectedNavigationItem(position);
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

	public class TabFragmentPagerAdapter extends FragmentStatePagerAdapter {
		public TabFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			Fragment fragment = null;
			if (i == 0){
				fragment = new SearchStockFragment();	
			}else if (i == 1){
				fragment = new StockListFragment();	
			}else if (i == 2){
				fragment = new MyAccountFragment();	
			}

			return fragment;
			
		}

		@Override
		public int getCount() {
			return 3;
		}

	}

}

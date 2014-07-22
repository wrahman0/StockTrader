package com.example.stocktrader;

import java.sql.SQLException;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MyAccountFragment extends Fragment{
	
	private UserDetails theUser;
	
	private TextView accUsername;
	private TextView accStartingCapital;
	private TextView accCurrentCapital;
	private TextView accCurrentStockValue;
	private TextView accGainLoss;
	private TextView accStocksBought;
	private TextView accStocksOwned;
	private TextView accTotalTransactions;
	private TextView accPositiveTransactions;
	private TextView accNegativeTransactions;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.my_account, null);
		
		findViews(view);
		getUser();
		setStaticViews();
		
		return view;
	}
	
	private void setStaticViews(){
		accUsername.setText(String.valueOf(theUser.getUsername()));
		accStartingCapital.setText(String.valueOf(theUser.getStartingCash()));
		accCurrentCapital.setText(String.valueOf(theUser.getCurrentCash()));
		accCurrentStockValue.setText(String.valueOf(theUser.getCurrentStockValue()));
		accGainLoss.setText(String.valueOf(theUser.getGainLoss()));
		accStocksBought.setText(String.valueOf(theUser.getStocksBought()));
		accStocksOwned.setText(String.valueOf(theUser.getStocksOwned()));
		accTotalTransactions.setText(String.valueOf(theUser.getTotalTransactions()));
		accPositiveTransactions.setText(String.valueOf(theUser.getPositiveTransactions()));
		accNegativeTransactions.setText(String.valueOf(theUser.getNegativeTransactions()));
	}
	
	private void getUser(){
		
		DBAdapterUser dbUser = new DBAdapterUser (getActivity());
		
		try{
			dbUser.open();
		}catch(SQLException e){
			e.printStackTrace();
		}
		
		Cursor userCursor = dbUser.getAllUsers();
		
		userCursor.moveToFirst();
		
		theUser = new UserDetails (userCursor);
		
	}
	
	private void findViews(View view){
		
		accUsername = (TextView) view.findViewById(R.id.accUsername);
		accStartingCapital = (TextView) view.findViewById(R.id.accStartingCapital);
		accCurrentCapital = (TextView) view.findViewById(R.id.accCurrentCapital);
		accCurrentStockValue = (TextView) view.findViewById(R.id.accCurrentStockValue);
		accGainLoss = (TextView) view.findViewById(R.id.accGainLoss);
		accStocksBought = (TextView) view.findViewById(R.id.accStocksBought);
		accStocksOwned = (TextView) view.findViewById(R.id.accStocksOwned);
		accTotalTransactions = (TextView) view.findViewById(R.id.accTotalTransactions);
		accPositiveTransactions = (TextView) view.findViewById(R.id.accPositiveTransactions);
		accNegativeTransactions = (TextView) view.findViewById(R.id.accNegativeTransactions);
		
	}
	
}

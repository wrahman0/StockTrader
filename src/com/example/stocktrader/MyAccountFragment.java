package com.example.stocktrader;

import java.sql.SQLException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

	private Button accDeleteUserButton;

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.my_account, null);

		// Find the views
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
		accDeleteUserButton = (Button) view.findViewById(R.id.accDeleteUserButton);
		
		getUser();
		setStaticViews();
		
		accDeleteUserButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				new AlertDialog.Builder(getActivity())
				.setTitle("Delete Account")
				.setMessage("Do you want to permanently delete the user?")
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						DBAdapterUser dbUser = new DBAdapterUser(getActivity());
						try{
							dbUser.open();	
						}catch(SQLException e){
							e.printStackTrace();
						}

						DBAdapter db = new DBAdapter(getActivity());
						try{
							db.open();	
						}catch(SQLException e){
							e.printStackTrace();
						}

						db.deleteDb();
						dbUser.deleteDb();
						db.close();
						dbUser.close();

						Intent intent = new Intent(getActivity(), UserSetupActivity.class);
						getActivity().startActivity(intent);
						Toast.makeText(getActivity(), "Deleting Account", Toast.LENGTH_SHORT).show();
						getActivity().finish();
						
					}
				}).setNegativeButton(android.R.string.no, null).show();
			}
		});

		return view;
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {

		super.setUserVisibleHint(isVisibleToUser);
		// Make sure that we are currently visible
		if (this.isVisible()) {
			getUser();
			setStaticViews();
		}

	}

	private void setStaticViews(){

		setTextViewColors();
		accUsername.setText(String.valueOf(theUser.getUsername()));
		accStartingCapital.setText("$"+ String.format("%.2f", theUser.getStartingCash()));
		accCurrentCapital.setText("$"+ String.format("%.2f", theUser.getCurrentCash()));
		accCurrentStockValue.setText("$"+ String.format("%.2f", theUser.getCurrentStockValue(getActivity())));
		accGainLoss.setText("$" + String.format("%.2f", theUser.getGainLoss()));
		accStocksBought.setText(String.valueOf(theUser.getStocksBought()));
		accStocksOwned.setText(String.valueOf(theUser.getStocksOwned()));
		accTotalTransactions.setText(String.valueOf(theUser.getTotalTransactions()));
		accPositiveTransactions.setText(String.valueOf(theUser.getPositiveTransactions()));
		accNegativeTransactions.setText(String.valueOf(theUser.getNegativeTransactions()));

	}

	private void setTextViewColors(){

		//Set the color of the gain loss textview
		if (theUser.getCurrentStockValue(getActivity())>0.0){
			accCurrentStockValue.setTextColor(getResources().getColor(R.color.card_color_positive));
		}else{
			accCurrentStockValue.setTextColor(getResources().getColor(R.color.card_color_negative));
		}

		//Set the color of the gain loss textview
		if (theUser.getGainLoss()>0.0){
			accGainLoss.setTextColor(getResources().getColor(R.color.card_color_positive));
		}else{
			accGainLoss.setTextColor(getResources().getColor(R.color.card_color_negative));
		}
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
		dbUser.close();

	}

}

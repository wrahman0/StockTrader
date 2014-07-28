package com.example.stocktrader;

import java.sql.SQLException;

import android.app.DialogFragment;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PurchaseDialog extends DialogFragment implements View.OnClickListener{

	View dialogView;

	private StockDetails theStock;
	private UserDetails theUser;

	private DBAdapterUser dbUser;
	private DBAdapter db;

	private Button cancelButton, purchaseButton;

	//	Static Information
	private TextView purchaseStockName;
	private TextView purchaseStockSymbol;
	private TextView purchaseStockPrice;
	private TextView purchaseStockVolume;
	private TextView purchaseBrokerFee;
	private TextView purchaseUserBank;

	//	Dynamic Information
	private TextView purchaseTotalStockPrice;
	private TextView purchaseTax;
	private TextView purchaseOverallTotal;

	//	Quantity information
	private EditText purchaseQuantityEditText;
	private int volume;
	private float taxRate = (float) 0.13;
	private float brokerFee = (float) 9.95;
	private int quantity = 1;
	private float tax;
	private float totalCost;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.purchase_dialog, null);

		dialogView = view;
		openDB();

		theStock = (StockDetails) getArguments().getSerializable(DetailsStockViewActivity.STOCK_NAME_EXTRA);

		findViews();

		//Set the text
		setStaticInfo(theStock);
		setDynamicInfo(theStock);

		//Set title of the dialog box
		getDialog().setTitle(R.string.purchase_dialog_title);

		//Find the buttons
		cancelButton = (Button) view.findViewById(R.id.negativeButton);
		purchaseButton = (Button) view.findViewById(R.id.positiveButton);

		//OnClickListeners
		cancelButton.setOnClickListener(this);
		purchaseButton.setOnClickListener(this);

		purchaseQuantityEditText.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				if (s.length()!=0){
					if(Integer.parseInt(s.toString()) > volume){
						quantity = volume;
						purchaseQuantityEditText.setText(String.valueOf(volume));
					}else{
						quantity = Integer.parseInt(s.toString());
					}
					setDynamicInfo(theStock);
				}

			}

		});

		//Window cannot be closed unless purchase or cancel is pressed
		setCancelable(false);

		return view; 
	}

	private void openDB(){
		//Open db for user
		this.dbUser = new DBAdapterUser (getActivity());

		try{
			dbUser.open();
		}catch(SQLException e){
			e.printStackTrace();
		}

		//Open the db for stocks
		this.db = new DBAdapter(getActivity());

		try{
			db.open();
		}catch(SQLException e){
			e.printStackTrace();
		}

		Cursor userCursor = dbUser.getAllUsers();
		userCursor.moveToFirst();
		this.theUser = new UserDetails(userCursor);	
	}

	private void findViews(){
		//	Static Information
		purchaseStockName = (TextView) dialogView.findViewById(R.id.purchaseStockName);
		purchaseStockSymbol = (TextView) dialogView.findViewById(R.id.purchaseStockSymbol);
		purchaseStockPrice = (TextView) dialogView.findViewById(R.id.purchaseStockPrice);
		purchaseStockVolume = (TextView) dialogView.findViewById(R.id.purchaseStockVolume);
		purchaseBrokerFee = (TextView) dialogView.findViewById(R.id.purchaseBrokerFee);
		purchaseUserBank = (TextView) dialogView.findViewById(R.id.purchaseUserBank);

		//	Dynamic Information
		purchaseTotalStockPrice = (TextView) dialogView.findViewById(R.id.purchaseTotalStockPrice);
		purchaseTax = (TextView) dialogView.findViewById(R.id.purchaseTax);
		purchaseOverallTotal = (TextView) dialogView.findViewById(R.id.purchaseOverallTotal);

		//	Quantity information
		purchaseQuantityEditText = (EditText) dialogView.findViewById(R.id.purchaseQuantityEditText);
	}

	private void setStaticInfo(StockDetails theStock){
		purchaseStockName.setText(theStock.getName());
		purchaseStockSymbol.setText(theStock.getSymbol());
		purchaseStockPrice.setText("$"+theStock.getLastTradePriceOnly());
		purchaseBrokerFee.setText("$"+String.valueOf(brokerFee));
		purchaseStockVolume.setText(theStock.getVolume());
		purchaseUserBank.setText("$"+String.valueOf(theUser.getCurrentCash()));

		volume = Integer.parseInt(theStock.getVolume());
	}

	private void setDynamicInfo(StockDetails theStock){

		tax = Float.parseFloat(theStock.getLastTradePriceOnly()) * quantity * taxRate;
		totalCost = tax + Float.parseFloat(theStock.getLastTradePriceOnly()) * quantity + brokerFee;

		purchaseTotalStockPrice.setText("$"+String.format("%.2f", Float.parseFloat(theStock.getLastTradePriceOnly())));
		purchaseTax.setText("$"+String.format("%.2f", tax));
		purchaseOverallTotal.setText("$"+ String.format("%.2f", totalCost));

	}

	public void onClick(View v){
		if (v.getId()==R.id.negativeButton){
			dismiss();
		}else if (v.getId()==R.id.positiveButton){

			//Check if the user has enough money
			if (totalCost < theUser.getCurrentCash()){
				//Allow the purchase
				db.insertStock(theStock.getName(), theStock.getSymbol(), theStock.getChange(), theStock.getExchange(),theStock.getLastTradePriceOnly(), theStock.getDaysHigh(), theStock.getDaysLow(), theStock.getYearHigh(), theStock.getYearLow(), theStock.getVolume(), String.valueOf(quantity));
				dbUser.updateUser(theUser.get_id(), theUser.getUsername(), String.valueOf(theUser.getStocksBought()+1), String.valueOf(theUser.getStartingCash()), String.valueOf(theUser.getCurrentCash() - totalCost ), "0", "0", String.valueOf(theUser.getStocksOwned()+1), String.valueOf(theUser.getTotalTransactions()+1), String.valueOf(theUser.getPositiveTransactions()), String.valueOf(theUser.getNegativeTransactions()));
			}else{
				//Decline the purchase
				Toast.makeText(getActivity(), "Not Enough Money", Toast.LENGTH_SHORT).show();
			}

			dbUser.close();
			db.close();
			dismiss();
		}
	}

}

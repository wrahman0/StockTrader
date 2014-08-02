package com.example.stocktrader;

import java.sql.SQLException;

import android.app.DialogFragment;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SellDialog extends DialogFragment implements View.OnClickListener{

	private View dialogView;

	private StockDetails theStock;
	private UserDetails theUser;

	private DBAdapterUser dbUser;
	private DBAdapter db;

	private Button cancelButton, sellButton;

	//	Static Information
	private TextView sellStockName;
	private TextView sellStockSymbol;
	private TextView sellStockPrice;
	private TextView sellUserBank;
	private TextView sellStockQuantity;

	//	Dynamic Information
	private TextView sellTotalStockPrice;
	private TextView sellOverallTotal;
	private TextView sellUserUpdatedBank;
	
	//	Quantity information
	private EditText sellQuantityEditText;
	private int volume; // Volume is the maximum amount of stocks that user can sell
	private int quantity = 1; // Quantity is the amount of stocks that user is wanting to sell at the moment
	private float totalCost;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.sell_dialog, null);
		theStock = (StockDetails) getArguments().getSerializable(DetailsStockViewActivity.STOCK_NAME_EXTRA);
		
		//Set the global view
		dialogView = view;
		
		//Get the volume
		volume = getArguments().getInt(StockTraderActivity.STOCK_QUANTITY_TAG);
		openDB();
		findViews();

		//Set the text
		setStaticInfo(theStock);
		setDynamicInfo(theStock);

		//Set title of the dialog box
		getDialog().setTitle(R.string.sell_dialog_title);

		//Find the buttons
		cancelButton = (Button) view.findViewById(R.id.negativeButton);
		sellButton = (Button) view.findViewById(R.id.positiveButton);

		//OnClickListeners
		cancelButton.setOnClickListener(this);
		sellButton.setOnClickListener(this);
		sellQuantityEditText.addTextChangedListener(new TextWatcher(){

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
						sellQuantityEditText.setText(String.valueOf(volume));
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
		sellStockName = (TextView) dialogView.findViewById(R.id.sellStockName);
		sellStockSymbol = (TextView) dialogView.findViewById(R.id.sellStockSymbol);
		sellStockPrice = (TextView) dialogView.findViewById(R.id.sellStockPrice);
		sellUserBank = (TextView) dialogView.findViewById(R.id.sellUserBank);
		sellStockQuantity = (TextView) dialogView.findViewById(R.id.sellStockQuantity);
		
		
		//	Dynamic Information
		sellTotalStockPrice = (TextView) dialogView.findViewById(R.id.sellTotalStockPrice);
		sellOverallTotal = (TextView) dialogView.findViewById(R.id.sellOverallTotal);
		sellUserUpdatedBank = (TextView) dialogView.findViewById(R.id.sellUserUpdatedBank);
		
		//	Quantity information
		sellQuantityEditText = (EditText) dialogView.findViewById(R.id.sellQuantityEditText);
	}

	private void setStaticInfo(StockDetails theStock){
		sellStockName.setText(theStock.getName());
		sellStockSymbol.setText(theStock.getSymbol());
		sellStockPrice.setText("$"+theStock.getLastTradePriceOnly());
		sellUserBank.setText("$"+String.valueOf(theUser.getCurrentCash()));
		sellStockQuantity.setText(String.valueOf(volume));
	}

	private void setDynamicInfo(StockDetails theStock){ // Gets called on quantity edit text change
		totalCost = Float.parseFloat(theStock.getLastTradePriceOnly()) * quantity;
		sellTotalStockPrice.setText("$"+String.format("%.2f", Float.parseFloat(theStock.getLastTradePriceOnly())*quantity));
		sellOverallTotal.setText("$"+ String.format("%.2f", totalCost));
		sellUserUpdatedBank.setText("$" + String.format("%.2f", theUser.getCurrentCash() + totalCost));
	}

	public void onClick(View v){
		if (v.getId()==R.id.negativeButton){
			dismiss();
		}else if (v.getId()==R.id.positiveButton){
			
			openDB();

			//Find the stock that we are dealing with
			Cursor cursor = db.getAllStocks();
			cursor.moveToFirst();
			
			//Find the stock
			do{
				if (cursor.getString(cursor.getColumnIndex("symbol")).equals(theStock.getSymbol())){
					break;
				}
			}while (cursor.moveToNext());
			
			if (quantity == volume){
				//Delete the stock from the db
				db.deleteStock(Long.parseLong(cursor.getString(cursor.getColumnIndex("_id"))));
			}else{
				//Update the stock with the new quantity
				db.updateStock(Long.parseLong(cursor.getString(cursor.getColumnIndex("_id"))), theStock.getName(), 
						theStock.getSymbol(), theStock.getChange(), theStock.getExchange(), theStock.getLastTradePriceOnly(), 
						theStock.getDaysHigh(), theStock.getDaysLow(), theStock.getYearHigh(), theStock.getYearLow(),
						theStock.getVolume(), String.valueOf(Integer.parseInt(cursor.getString(cursor.getColumnIndex("quantity"))) - quantity ), 
						cursor.getString(cursor.getColumnIndex("buyprice")));
				
			}
			dbUser.updateUser(theUser.get_id(), theUser.getUsername(), String.valueOf(theUser.getStocksBought()+1), String.valueOf(theUser.getStartingCash()), String.valueOf(theUser.getCurrentCash() + totalCost ), "0", "0", String.valueOf(theUser.getStocksOwned()-1), String.valueOf(theUser.getTotalTransactions()+1), String.valueOf(theUser.getPositiveTransactions()), String.valueOf(theUser.getNegativeTransactions()));
			dbUser.close();
			db.close();
			dismiss();
			
		}
	}

}

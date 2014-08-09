package com.example.stocktrader;

import java.sql.SQLException;
import java.util.ArrayList;

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
import android.widget.Toast;

public class PurchaseDialog extends DialogFragment implements View.OnClickListener{
	
	private ArrayList<String> mStockSymbol = new ArrayList<String>();
	
	private View dialogView;

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
	//private TextView purchaseBrokerFee;
	private TextView purchaseUserBank;

	//	Dynamic Information
	private TextView purchaseTotalStockPrice;
	//private TextView purchaseTax;
	private TextView purchaseOverallTotal;

	//	Quantity information
	private EditText purchaseQuantityEditText;
	private int volume;
//	private float taxRate = (float) 0.13;
//	private float brokerFee = (float) 9.95;
	private int quantity = 1;
	//private float tax;
	private float totalCost;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.purchase_dialog, null);

		dialogView = view;
		openDB();

		theStock = (StockDetails) getArguments().getSerializable(DetailsStockViewActivity.STOCK_NAME_EXTRA);

		findViews();
		
		mStockSymbol.add(theStock.getSymbol());

		//Set the text
		setStaticInfo();
		setDynamicInfo();

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
					setDynamicInfo();
				}
				
			}

		});

		//Window cannot be closed unless purchase or cancel is pressed
		setCancelable(false);

		return view; 
	}
	
	@Override
	public void onResume(){
		super.onResume();

		StockDetailsUpdater.createUpdater(mStockSymbol,
				new StockDetailsUpdater.UpdateListener() {

			@Override
			public void onUpdate(String stockSymbol, StockDetails stockDetails){
				if(stockDetails!=null && stockSymbol.equals(mStockSymbol.get(0))){
					theStock = stockDetails;
					setDynamicInfo();
				}
			}
			
		});
		StockDetailsUpdater.startUpdater();
	}
	
	@Override
	public void onPause(){
		super.onPause();
		StockDetailsUpdater.stopUpdater();
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
		//purchaseBrokerFee = (TextView) dialogView.findViewById(R.id.purchaseBrokerFee);
		purchaseUserBank = (TextView) dialogView.findViewById(R.id.purchaseUserBank);

		//	Dynamic Information
		purchaseTotalStockPrice = (TextView) dialogView.findViewById(R.id.purchaseTotalStockPrice);
		//purchaseTax = (TextView) dialogView.findViewById(R.id.purchaseTax);
		purchaseOverallTotal = (TextView) dialogView.findViewById(R.id.purchaseOverallTotal);

		//	Quantity information
		purchaseQuantityEditText = (EditText) dialogView.findViewById(R.id.purchaseQuantityEditText);
	}

	private void setStaticInfo(){
		purchaseStockName.setText(theStock.getName());
		purchaseStockSymbol.setText(theStock.getSymbol());
		purchaseStockPrice.setText("$"+theStock.getLastTradePriceOnly());
		//purchaseBrokerFee.setText("$"+String.valueOf(brokerFee));
		purchaseStockVolume.setText(theStock.getVolume());
		purchaseUserBank.setText("$"+String.valueOf(theUser.getCurrentCash()));

		volume = Integer.parseInt(theStock.getVolume());
	}

	private void setDynamicInfo(){

		//tax = Float.parseFloat(theStock.getLastTradePriceOnly()) * quantity * taxRate;
		totalCost = Float.parseFloat(theStock.getLastTradePriceOnly()) * quantity;

		purchaseTotalStockPrice.setText("$"+String.format("%.2f", Float.parseFloat(theStock.getLastTradePriceOnly())));
		//purchaseTax.setText("$"+String.format("%.2f", tax));
		purchaseOverallTotal.setText("$"+ String.format("%.2f", totalCost));

	}

	@Override
	public void onClick(View v){
		if (v.getId()==R.id.negativeButton){
			
			dismiss();
			
		}else if (v.getId()==R.id.positiveButton){

			//Check if the user has enough money
			if (totalCost <= theUser.getCurrentCash()){
				//Allow the purchase
				//Check if the stock exists
				Cursor dbRow = db.findStockIfExists(theStock.getName());
				if (dbRow!=null){
					
					//Get all the stocks
					Cursor allStock = db.getAllStocks();
					allStock.moveToFirst();
					
					db.updateStock(Long.parseLong(dbRow.getString(dbRow.getColumnIndex(DBAdapter.KEY_ROWID))),
							theStock.getName(), 
							theStock.getSymbol(), 
							String.valueOf(Integer.parseInt(
									dbRow.getString(dbRow.getColumnIndex(DBAdapter.KEY_QUANTITY))) + quantity),
							theStock.getLastTradePriceOnly()
					);
					
				}else{
					Log.e("DEBUGGING", "INSERTING NEW STOCK");
					db.insertStock(
							theStock.getName(), theStock.getSymbol(), 
							String.valueOf(quantity), theStock.getLastTradePriceOnly()
					);
				}
				
				dbUser.updateUser(theUser.get_id(), 
						theUser.getUsername(), 
						String.valueOf(theUser.getStocksBought()+1), 
						String.valueOf(theUser.getStartingCash()), 
						String.valueOf(theUser.getCurrentCash() - totalCost ), 
						String.valueOf(theUser.getCurrentStockValue()), 
						String.valueOf(theUser.getGainLoss()), 
						String.valueOf(theUser.getStocksOwned()+1), 
						String.valueOf(theUser.getTotalTransactions()+1), 
						String.valueOf(theUser.getPositiveTransactions()), 
						String.valueOf(theUser.getNegativeTransactions()));	
				
				
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

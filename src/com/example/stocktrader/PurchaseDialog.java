package com.example.stocktrader;

import android.app.DialogFragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PurchaseDialog extends DialogFragment implements View.OnClickListener{
	
	View dialogView;
	
	private StockDetails theStock;
	private Button cancelButton, purchaseButton;
	
	//	Static Information
	private TextView purchaseStockName;
	private TextView purchaseStockSymbol;
	private TextView purchaseStockPrice;
	private TextView purchaseBrokerFee;
	
	//	Dynamic Information
	private TextView purchaseTotalStockPrice;
	private TextView purchaseTax;
	private TextView purchaseOverallTotal;
	
	//	Quantity information
	private EditText purchaseQuantityEditText;
	
	private float taxRate = (float) 0.13;
	private float brokerFee = (float) 9.95;
	private int quantity = 1;
	private float tax;
	private float totalCost;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.purchase_dialog, null);
		
		dialogView = view;
		
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
					quantity = Integer.parseInt(s.toString());
					setDynamicInfo(theStock);
				}
				
			}
			
		});
		
		//Window cannot be closed unless purchase or cancel is pressed
		setCancelable(false);
		
		return view; 
	}
	
	private void findViews(){
		//	Static Information
		purchaseStockName = (TextView) dialogView.findViewById(R.id.purchaseStockName);
		purchaseStockSymbol = (TextView) dialogView.findViewById(R.id.purchaseStockSymbol);
		purchaseStockPrice = (TextView) dialogView.findViewById(R.id.purchaseStockPrice);
		purchaseBrokerFee = (TextView) dialogView.findViewById(R.id.purchaseBrokerFee);
		
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
			//TODO:@Wasiur add to the user's stocks db
			dismiss();
		}
	}

}

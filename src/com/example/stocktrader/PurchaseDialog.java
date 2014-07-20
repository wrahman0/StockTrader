package com.example.stocktrader;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;

public class PurchaseDialog extends DialogFragment implements View.OnClickListener{
	
	private Button cancelButton, purchaseButton;
	private NumberPicker stockQuantityPicker;
	
	private int minStocks = 0;
	private int maxStocks = 50;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.purchase_dialog, null);

		//Set title of the dialog box
		getDialog().setTitle(R.string.purchase_dialog_title);
		
		//Find the buttons
		cancelButton = (Button) view.findViewById(R.id.negativeButton);
		purchaseButton = (Button) view.findViewById(R.id.positiveButton);
		
		//Find the picker
		stockQuantityPicker = (NumberPicker) view.findViewById(R.id.purchaseDialogQuantity);
		
		//Min and max values for the picker
		stockQuantityPicker.setMinValue(minStocks);
		stockQuantityPicker.setMaxValue(maxStocks);
		
		//Set initial value
		stockQuantityPicker.setValue(maxStocks/2);
		
		//OnClickListeners
		cancelButton.setOnClickListener(this);
		purchaseButton.setOnClickListener(this);
		
		//Window cannot be closed unless purchase or cancel is pressed
		setCancelable(false);
		
		return view; 
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

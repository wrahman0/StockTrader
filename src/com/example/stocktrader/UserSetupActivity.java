package com.example.stocktrader;

import java.sql.SQLException;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UserSetupActivity extends Activity{
	
	EditText signupUsername;
	EditText signupStartingCash;
	Button userSetupComplete;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.user_signup);
		
		//Check if the db is created for the user. 
		//If so go to searchstockfragment activity
		//Else stay on this page and get the user information
		if(userDatabaseCreated()){
			Intent intent = new Intent(this, StockTraderActivity.class);
			startActivity(intent);
			finish();
		}
		
		//View References
		signupUsername = (EditText) findViewById(R.id.signupUsername);
		signupStartingCash = (EditText) findViewById(R.id.signupStartingCash);
		userSetupComplete = (Button) findViewById(R.id.userSetupComplete);
		
		//Button Listener
		userSetupComplete.setOnClickListener(userSetupCompleteListener);
		
	}
	
	private OnClickListener userSetupCompleteListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			
			String username = signupUsername.getText().toString();
			int startingCash = 0;
			if(signupStartingCash.getText().length()>0){
				startingCash = Integer.parseInt(signupStartingCash.getText().toString());
			}else{
				Toast.makeText(getBaseContext(), 
						"Starting cash must more than $1,000", Toast.LENGTH_SHORT).show();
				return;
			}
			
			if (username.length() >= 4 && startingCash >= 1000){
				
				//Enter the user to the database
				DBAdapterUser db = new DBAdapterUser(UserSetupActivity.this);
				try{
					db.open();
				}catch(SQLException e){
					e.printStackTrace();
				}
				db.addUser(username, "0", String.valueOf(startingCash), String.valueOf(startingCash), "0", "0", "0", "0", "0", "0");
				db.close();
				
				//Start the main program
				Intent intent = new Intent(UserSetupActivity.this, StockTraderActivity.class);
				startActivity(intent);
				finish();
				
			}else if (username.length() < 4){
				Toast.makeText(getBaseContext(), "Username must be longer than 4 characters", Toast.LENGTH_SHORT).show();
			}else if (startingCash < 1000){
				Toast.makeText(getBaseContext(), "Starting cash must more than $1,000", Toast.LENGTH_SHORT).show();
			}else{
				Log.e("USERCREATION", "EXCEPTION FOUND IN USER CREATION");
			}
			
		}
		
	};

	private boolean userDatabaseCreated(){
		DBAdapterUser db = new DBAdapterUser(this);
		try{
			db.open();
			try{
				Cursor c = db.getAllUsers();
				if (!c.moveToFirst()){
					return false;
				}
			}catch(Exception e){
				return false;
			}
			
			db.close();
			return true;
		}catch (SQLException e){
			return false;
		}
	}

}

package com.peacock.tipcalculator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

public class DisplayAmountToPay extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_amount_to_pay);
		

		Intent intent = getIntent();
		String message = intent.getStringExtra(MainActivity.HOW_MUCH_TO_PAY_MESSAGE);
		String additionalMessage = 
				intent.getStringExtra(MainActivity.HOW_MUCH_TO_PAY_ADDITIONAL_MESSAGE);
		String option_one = intent.getStringExtra(MainActivity.OPTION_ONE);
		String option_two = intent.getStringExtra(MainActivity.OPTION_TWO);
		String option_three = intent.getStringExtra(MainActivity.OPTION_THREE);
		
		TextView textView;
		
		textView = (TextView) findViewById(R.id.how_much_to_pay_message);
		textView.setText(message);
		
		textView = (TextView) findViewById(R.id.how_much_to_pay_additional_message);
		textView.setText(additionalMessage);
		
		

		textView = (TextView) findViewById(R.id.option_one);
		textView.setText(option_one);
		
		textView = (TextView) findViewById(R.id.option_two);
		textView.setText(option_two);
		
		textView = (TextView) findViewById(R.id.option_three);
		textView.setText(option_three);
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

}

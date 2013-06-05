package com.peacock.tipcalculator;

import java.text.DecimalFormat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

	private static DecimalFormat poundFormat = new DecimalFormat("£0.00");
	private static DecimalFormat penceFormat = new DecimalFormat("0p");

	private static String formatAmount(double amount) {
		if (amount < 1.0) return penceFormat.format(amount*100);
		return poundFormat.format(amount);
	}	

	public static final String HOW_MUCH_TO_PAY_MESSAGE =
			"com.example.theidiotspaycalculator.HOW_MUCH_TO_PAY_MESSAGE";
	public static final String HOW_MUCH_TO_PAY_ADDITIONAL_MESSAGE =
			"com.example.theidiotspaycalculator.HOW_MUCH_TO_PAY_ADDITIONAL_MESSAGE";
	public static final String OPTION_ONE =
			"com.example.theidiotspaycalculator.OPTION_ONE";
	public static final String OPTION_TWO =
			"com.example.theidiotspaycalculator.OPTION_TWO";
	public static final String OPTION_THREE =
			"com.example.theidiotspaycalculator.OPTION_THREE";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.title_activity_main);
        setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private int getIntFromEditTextId(int editTextId) {
		EditText editText = (EditText) findViewById(editTextId);
		String s = editText.getText().toString();
		if (s.length()>0) return Integer.valueOf(s);
		return 0;
	}

	private double getDoubleFromEditTextId(int editTextId) {
		EditText editText = (EditText) findViewById(editTextId);
		String s = editText.getText().toString();
		if (s.length()>0) return Double.valueOf(s);
		return 0.0d;
	}

	private boolean getBooleanFromToggleBottonId(int toggleButtonId) {
		ToggleButton toggleButton = (ToggleButton) findViewById(toggleButtonId);
		return toggleButton.isChecked();
	}

	private double getTipRatioFromRadioGroupId(int radioGroupId) {
		RadioGroup radioGroup = (RadioGroup) findViewById(radioGroupId);
		int checkedId = radioGroup.getCheckedRadioButtonId();
		switch (checkedId) {
		case R.id.service_crap:
			return 0.0d;
		case R.id.service_wonderful:
			return 0.15d;
		default:
			return 0.1;
		}
	}

	/*	private boolean getWasGreatServiceFromRadioGroupId(int radioGroupId) {
		RadioGroup radioGroup = (RadioGroup) findViewById(radioGroupId);
		int checkedId = radioGroup.getCheckedRadioButtonId();
		return checkedId == R.id.service_wonderful;
	}*/

	private double nextFifty(double d) {
		d *= 2;
		d = Math.ceil(d);
		d /= 2;
		return d;
	}
	private double nearestFifty(double d) {
		d *= 2;
		d = Math.round(d);
		d /= 2;
		return d;
	}
	private double nextTen(double d) {
		d *= 10;
		d = Math.ceil(d);
		d /= 10;
		return d;
	}

	private double amountToPay(double billAmount, int numPeople,
			int numPayedByDept, int numPayingPersons) {
		double deptPays = nextFifty(billAmount/numPeople) * numPayedByDept;
		double leftToPay = billAmount-deptPays;
		double amtPerPayingPersons = leftToPay/numPayingPersons;
		double roundedAmount = nextFifty(amtPerPayingPersons);
		return roundedAmount;
	}
	
	public void calculateWhatToPay(View view) {
		int numPeople = getIntFromEditTextId(R.id.num_people);
		int numSpeakers = getIntFromEditTextId(R.id.num_speakers);
		int numGuests = getIntFromEditTextId(R.id.num_guests);
		int numLayabouts = getIntFromEditTextId(R.id.num_layabouts);
		double billAmount = getDoubleFromEditTextId(R.id.bill_amount);
		boolean tipIncluded = getBooleanFromToggleBottonId(R.id.tip_included);
		double tipRatio = getTipRatioFromRadioGroupId(R.id.service_group);

		int numPayedByDept = (numSpeakers * 4);

		double totalAmount = billAmount;
		if (!tipIncluded) totalAmount += billAmount * tipRatio;

		double deptAmount = numPayedByDept * nextFifty(totalAmount/numPeople);
		double toPay = totalAmount - deptAmount;

		int realPeople = numPeople - numSpeakers - numGuests - numLayabouts;
		
		String message = "";
		String additionalMessage = "";
		if (numPeople<=0) 
			message = "If there aren't a positive number of people dining, who exactly are you?";
		else if (numPeople == 1)
			message = "There's only one of you, how exactly would you like me to split the bill?";
		else if (numPeople-numSpeakers-numGuests-numLayabouts < 0)
			message = "This is supposed to be an app for idiots, but if you can't even count " +
					"we're going to have problems. Go back and try again.";
		else if (numPayedByDept >= numPeople)
			message = "Looks like you've invited enough speakers so the department pays for " +
					"everything. Enjoy the free meal.";
		else if (numPeople-numSpeakers-numGuests-numLayabouts == 0)
			message = "It looks like everyone is getting a free meal but no one is paying," +
					" I'm not sure how you're " +
					"going to explain this to the restaurant. Good luck!";
		else {
			double roundedAmount = amountToPay(totalAmount, numPeople, numPayedByDept, realPeople);

			message = "It looks like the grown-ups are going to have to pay " +
					formatAmount(roundedAmount) + " each.";
			if (roundedAmount < 15) 
				additionalMessage = "Not bad, MacQuarrie would have made you pay £15.";
			else if (roundedAmount == 15)
				additionalMessage = "Good work, MacQuarrie would be proud.";
			else if (roundedAmount < 20) 
				additionalMessage = "I suppose that's not too bad, still more than "
						+ "the expected £15; MacQuarrie wouldn't be happy.";
			else if (numLayabouts>0) {				
				double ratio = ((double)(numPeople-numLayabouts))/((double)numPeople);
				double newRoundedAmount = amountToPay(totalAmount*ratio,
						numPeople-numLayabouts, numPayedByDept, realPeople);
				
				double roundedSaving = roundedAmount - newRoundedAmount;
				additionalMessage = "Wow, an expensive one this week, perhaps you " +
						"should discourage the layabouts from attending, you'd have ";
				if (roundedSaving < roundedAmount)
					additionalMessage += "saved " + formatAmount(roundedSaving) + " each.";
				else
					additionalMessage += "had a free meal.";

			}
		}


		String option_one = "";
		String option_two = "";
		String option_three = "";
		
		if ((toPay>0.0) &&
				(numPeople>0) &&
				(realPeople>0) &&
				(numLayabouts>0)) {
			option_one = "\nHere are a couple of other options:";
			option_two = " - " + studentHalfPrice(toPay, numPeople, realPeople, numLayabouts);
			option_three = " - " + studentFullPrice(toPay, numPeople, realPeople, numLayabouts);
		}
		
		Intent intent = new Intent(this, DisplayAmountToPay.class);
		intent.putExtra(HOW_MUCH_TO_PAY_MESSAGE, message);
		intent.putExtra(HOW_MUCH_TO_PAY_ADDITIONAL_MESSAGE, additionalMessage);
		intent.putExtra(OPTION_ONE, option_one);
		intent.putExtra(OPTION_TWO, option_two);
		intent.putExtra(OPTION_THREE, option_three);
		startActivity(intent);

	}


	String studentFree(double toPay, 
			int totalPeople, int realPeople, int numStudents) {

		double amtEach = nearestFifty(toPay / realPeople);
		double leftOver = toPay - amtEach * realPeople;
		double stuAmt = leftOver / numStudents;

		if (((amtEach <= 1.5) && (stuAmt>0.0)) ||
				(stuAmt>1.0)) {
			amtEach = nextFifty(toPay/realPeople);
			stuAmt = 0.0;
		}

		String message;
		message = "If the the grown-ups pay " +
				formatAmount(amtEach) + " each";
		if (stuAmt > 0.0) message += " then the students need only rustle up " + 
				formatAmount(stuAmt*numStudents) + " between them.";
		else message += " then the students can have a free meal.";

		return message;
	}

	String studentHalfPrice(double toPay, 
			int totalPeople, int realPeople, int numStudents) {

		double amtEach = nextFifty(2*toPay / (2*realPeople+numStudents));
		double stuAmt = amtEach / 2;
		if (amtEach*realPeople + (stuAmt-0.25)*numStudents>toPay)
			stuAmt -= 0.25;

		String message;
		message = "You could have the grown-ups pay " +
				formatAmount(amtEach) + " each";
		message += " and then each student need only pay " + 
				formatAmount(stuAmt) + ".";

		return message;
	}

	String studentFullPrice(double toPay, 
			int totalPeople, int realPeople, int numStudents) {

		realPeople += numStudents;

		double amtEach = realPeople>2?
				nearestFifty(toPay/realPeople):
					nextFifty(toPay/realPeople);
				double leftOver = toPay - amtEach * realPeople;

				if (leftOver>2.0) {
					amtEach =  nextFifty(toPay/realPeople);
					leftOver = 0.0;
				}
				leftOver = nextTen(leftOver);

				String message;
				message = "Instead of giving the students a free ride, why not make " +
						"everyone pay " + formatAmount(amtEach)+ "?";
				if (leftOver>0.0)
					message += " Though someone might have to chuck in an extra " + formatAmount(leftOver);

				return message;
	}


}

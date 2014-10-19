package com.example.android.navigationdrawerexample;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

public class SettingsActivity extends Activity {
   
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
        // enable ActionBar app icon to behave as action to toggle nav drawer
		getActionBar().setTitle("Settings");
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		default:
			return super.onOptionsItemSelected(item);
		}		
	}
	
}

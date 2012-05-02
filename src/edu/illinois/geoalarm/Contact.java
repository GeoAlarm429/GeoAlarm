package edu.illinois.geoalarm;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class Contact extends Activity
{

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact);

		SharedPreferences settings = getSharedPreferences("GeoAlarm", Activity.MODE_PRIVATE);
		View v = findViewById(R.id.optionsTopLayout);
		v.setBackgroundColor(settings.getInt("color_value", R.color.Blue));
	}

    @Override
	public void onResume()
	{
		SharedPreferences settings = getSharedPreferences("GeoAlarm", Activity.MODE_PRIVATE);
		View v = findViewById(R.id.optionsTopLayout);
		v.setBackgroundColor(settings.getInt("color_value", R.color.Blue));
		super.onResume();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch (item.getItemId()) 
		{
		case R.id.options:
			Intent optionIntent = new Intent(Contact.this, Options.class);
			startActivityForResult(optionIntent, 0);
			return true;
		case R.id.contact:
			Intent contactIntent = new Intent(Contact.this, Contact.class);
			startActivityForResult(contactIntent, 0);
			return true;
		}
		return false;
	}

}

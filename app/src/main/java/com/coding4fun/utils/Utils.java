package com.coding4fun.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class Utils {
	
	Context context;
	
	public Utils(Context context) {
		this.context = context;
	}
	
	
	public boolean isOnline () {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting())
			return true;
		return false;
	}
	
	public boolean isOnline_WIFI () {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			if (netInfo.getType() == ConnectivityManager.TYPE_WIFI)
				return true;
		}
		return false;
	}
	
	public void toast_short (String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}
	
	public void toast_long (String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	}
	
	public void hideKeyboard(View view) {
	    //View view = getCurrentFocus();
	    if (view != null) {
	        ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).
	            hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	    }
	}
	
	public void alertErrorAndExit(String msg) {
		AlertDialog.Builder d = new AlertDialog.Builder(context);
		d.setCancelable(false);
		d.setTitle("Oppps!");
		d.setMessage(msg + "!\n"+"App will be terminated!");
		d.setPositiveButton("EXIT", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//exit
				//System.exit(0);
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		});
		d.show();
	}
	
	public void removeLocalPreferences () {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		prefs.edit().clear().commit();
	}
	
}
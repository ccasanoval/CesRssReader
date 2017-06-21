package com.cesoft.cesrssreader;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

////////////////////////////////////////////////////////////////////////////////////////////////////
// Created by Cesar Casanova on 21/06/2017.
public class Util
{
	private static final String TAG = Util.class.getCanonicalName();

	//----------------------------------------------------------------------------------------------
	public static boolean isOnline()
	{
		ConnectivityManager cm = (ConnectivityManager)App.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		return netInfo != null && netInfo.isConnectedOrConnecting();
	}

	//----------------------------------------------------------------------------------------------
	public static Date str2date(String str)
	{
		if(str == null)return null;
		try
		{
			//SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm zzz");//Wed, 07 Jun 2017 16:00 EDT
			SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.US);
			return format.parse(str.substring(5, 22));
		}
		catch(Exception e)
		{
			//Log.e(TAG, "str2date:e:-----------------------------------------------------------------"+str, e);
			return null;
		}
	}
}

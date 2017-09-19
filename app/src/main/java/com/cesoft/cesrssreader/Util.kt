package com.cesoft.cesrssreader

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*


////////////////////////////////////////////////////////////////////////////////////////////////////
// Created by Cesar Casanova on 21/06/2017.
object Util
{
	//private val TAG = Util::class.java.canonicalName

	//----------------------------------------------------------------------------------------------
	val isOnline: Boolean
		get()
		{
			val cm = App.instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
			val netInfo = cm.activeNetworkInfo
			return netInfo != null && netInfo.isConnectedOrConnecting
		}

	//----------------------------------------------------------------------------------------------
	fun str2date(str: String?): Date?
	{
		if(str == null) return null
		return try
		{
			val format = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.US)
			format.parse(str.substring(5, 22))
		}
		catch(e: Exception)
		{
			//Log.e(TAG, "str2date:e:-----------------------------------------------------------------"+str, e);
			null
		}
	}

	//----------------------------------------------------------------------------------------------
	//@JvmOverloads
	fun log(tag: String, msg: String, e: Throwable? = null)
	{
		if(BuildConfig.DEBUG)
			Log.e(tag, msg, e)
	}
}

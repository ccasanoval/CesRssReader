package com.cesoft.cesrssreader.net;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.cesoft.cesrssreader.model.RssFeedModel;
import com.cesoft.cesrssreader.model.RssParser;

import java.io.InputStream;
import java.net.URL;

////////////////////////////////////////////////////////////////////////////////////////////////////
// Created by Cesar Casanova on 11/06/2017.
//TODO: RxJava
public class FetchRss extends AsyncTask<String, Void, Boolean>
{
	private static final String TAG = FetchRss.class.getSimpleName();
	
	private Callback _cb = null;
	private RssFeedModel _Feed = null;

	
	////////////////////////////////////////////////////////////////////////////////////////////////
	public interface Callback
	{
		void onPreExecute();
		void onPostExecute(Boolean success, RssFeedModel feed);
	}

	//----------------------------------------------------------------------------------------------
	public FetchRss(Callback cb)
	{
		_cb = cb;
	}
	
	//----------------------------------------------------------------------------------------------
	@Override
	protected void onPreExecute()
	{
		_cb.onPreExecute();
	}

	//----------------------------------------------------------------------------------------------
	@Override
	protected Boolean doInBackground(String... sURL)
	{
		if(sURL.length < 1)return false;
		String _sURL = sURL[0];
		if(TextUtils.isEmpty(_sURL))return false;
		InputStream inputStream = null;
		try
		{
			if( !_sURL.startsWith("http://") && !_sURL.startsWith("https://"))
				_sURL = "http://" + _sURL;
			URL url = new URL(_sURL);
			inputStream = url.openConnection().getInputStream();
			_Feed = RssParser.parseFeed(inputStream);
			_Feed.getFuente().setUrl(_sURL);
			return true;
		}
		catch(Exception e)
		{
			Log.e(TAG, "FetchRssTask:doInBackground:e: ", e);
			return false;
		}
		finally
		{
			if(inputStream != null)try{inputStream.close();}catch(Exception ignore){}
		}
	}

	//----------------------------------------------------------------------------------------------
	@Override
	protected void onPostExecute(Boolean success)
	{
		_cb.onPostExecute(success, _Feed);
	}
}

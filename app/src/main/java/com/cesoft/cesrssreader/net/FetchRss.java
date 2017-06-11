package com.cesoft.cesrssreader.net;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import com.cesoft.cesrssreader.model.RssModel;
import com.cesoft.cesrssreader.model.RssParser;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

////////////////////////////////////////////////////////////////////////////////////////////////////
// Created by Cesar Casanova on 11/06/2017.
//TODO: RxJava
public class FetchRss extends AsyncTask<String, Void, Boolean>
{
	private static final String TAG = FetchRss.class.getSimpleName();
	
	private Callback _cb = null;
	private List<RssModel> _FeedList = null;

	
	////////////////////////////////////////////////////////////////////////////////////////////////
	public interface Callback
	{
		void onPreExecute();
		void onPostExecute(Boolean success, List<RssModel> lista);
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
		try
		{
			if( !_sURL.startsWith("http://") && !_sURL.startsWith("https://"))
				_sURL = "http://" + _sURL;
			URL url = new URL(_sURL);
			InputStream inputStream = url.openConnection().getInputStream();
			_FeedList = RssParser.parseFeed(inputStream);
			return true;
		}
		catch(Exception e)
		{
			Log.e(TAG, "FetchRssTask:doInBackground:e: ", e);
			return false;
		}
	}

	//----------------------------------------------------------------------------------------------
	@Override
	protected void onPostExecute(Boolean success)
	{
		_cb.onPostExecute(success, _FeedList);
	}
}

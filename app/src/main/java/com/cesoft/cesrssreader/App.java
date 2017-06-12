package com.cesoft.cesrssreader;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.cesoft.cesrssreader.model.RssFeedModel;
import com.cesoft.cesrssreader.model.RssSource;


////////////////////////////////////////////////////////////////////////////////////////////////////
// Created by Cesar Casanova on 12/06/20167
public class App extends Application
{
	private RssFeedModel _rssFeed = new RssFeedModel(null);
		public RssFeedModel getRssFeed(){return _rssFeed;}
		public void setRssFeed(@NonNull RssFeedModel v){_rssFeed=v;}
	@Override public void onCreate()
	{
		super.onCreate();
	}
}

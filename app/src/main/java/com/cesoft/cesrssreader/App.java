package com.cesoft.cesrssreader;

import android.app.Application;
import android.support.annotation.NonNull;

import com.cesoft.cesrssreader.model.RssFeedModel;

////////////////////////////////////////////////////////////////////////////////////////////////////
// Created by Cesar Casanova on 12/06/20167
public class App extends Application
{
	private static App _this;
		public static App getInstance(){return _this;}

	//"http://www.xatakandroid.com/tag/feeds/rss2.xml"
	//"https://www.nasa.gov/rss/dyn/breaking_news.rss"
	//"https://actualidad.rt.com/feeds/all.rss"
	private RssFeedModel _rssFeed = new RssFeedModel("https://www.nasa.gov/rss/dyn/breaking_news.rss");
		public RssFeedModel getRssFeed(){return _rssFeed;}
		public void setRssFeed(@NonNull RssFeedModel v){_rssFeed=v;}

	@Override public void onCreate()
	{
		super.onCreate();
		_this = this;
	}
}

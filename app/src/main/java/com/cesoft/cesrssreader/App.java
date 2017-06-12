package com.cesoft.cesrssreader;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.cesoft.cesrssreader.model.RssSource;


////////////////////////////////////////////////////////////////////////////////////////////////////
// Created by Cesar Casanova on 12/06/20167
public class App extends Application
{
	private RssSource _rssSource = new RssSource();
		public RssSource getRssSource(){return _rssSource;}
		public void setRssSource(@NonNull RssSource v){_rssSource=v;}
	@Override public void onCreate()
	{
		super.onCreate();
	}
}

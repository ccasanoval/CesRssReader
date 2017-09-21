package com.cesoft.cesrssreader

import android.app.Application
import com.cesoft.cesrssreader.model.RssFeedModel

////////////////////////////////////////////////////////////////////////////////////////////////////
// Created by Cesar Casanova on 12/06/20167
class App : Application()
{
	//"http://www.xatakandroid.com/tag/feeds/rss2.xml"
	//"https://www.nasa.gov/rss/dyn/breaking_news.rss"
	//"https://actualidad.rt.com/feeds/all.rss"

	override fun onCreate()
	{
		super.onCreate()
		instance = this
	}

	companion object
	{
		var rssFeed = RssFeedModel("https://www.nasa.gov/rss/dyn/breaking_news.rss")
		lateinit var instance: App
			private set
	}
}

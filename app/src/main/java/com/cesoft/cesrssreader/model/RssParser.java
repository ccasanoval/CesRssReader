package com.cesoft.cesrssreader.model;

import android.util.Log;
import android.util.Xml;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

////////////////////////////////////////////////////////////////////////////////////////////////////
// Created by Cesar Casanova on 11/06/2017.
public class RssParser
{
	private static final String TAG = RssParser.class.getSimpleName();
	
	private static Date str2date(String str)
	{
		if(str == null)return null;
		try
		{
			//SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm zzz");//Wed, 07 Jun 2017 16:00 EDT
			SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy HH:mm");
			return format.parse(str.substring(5, 22));
		}
		catch(ParseException e)
		{
			Log.e(TAG, "str2date:e: "+str, e);
			return null;
		}
	}
	
	//----------------------------------------------------------------------------------------------
	// Parsea el inputStream desde la red o archivo a una lista de RssFeeds
	public static ArrayList<RssModel> parseFeed(InputStream inputStream) throws XmlPullParserException, IOException
	{
		String title = null;
		String description = null;
		String link = null;
		String img = null;
		Date fecha = null;
		boolean isItem = false;
		ArrayList<RssModel> items = new ArrayList<>();
		
		try
		{
			XmlPullParser xmlPullParser = Xml.newPullParser();
			xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			xmlPullParser.setInput(inputStream, null);
			
			while(xmlPullParser.next() != XmlPullParser.END_DOCUMENT)
			{
				String name = xmlPullParser.getName();
				if(name == null) continue;
				
				int eventType = xmlPullParser.getEventType();
				if(!isItem && eventType == XmlPullParser.START_TAG && name.equalsIgnoreCase("item"))
				{
				    isItem = true;
				    continue;
				}
				if(!isItem)continue;
				if(eventType == XmlPullParser.END_TAG && name.equalsIgnoreCase("item"))
				{
					//if(name.equalsIgnoreCase("item")) isItem = false;
					//Log.e(TAG, "NEW ITEM------------------------"+title+" -"+img+"- "+link);
					RssModel item = new RssModel(title, description, link, img, fecha);
					items.add(item);
					
					title = null;
				    description = null;
				    link = null;
				    img = null;
				    isItem = false;
					
					continue;
				}
				
				String result = "";
				if(xmlPullParser.next() == XmlPullParser.TEXT)
				{
				    result = xmlPullParser.getText();
				    xmlPullParser.nextTag();
				}
				
				if(name.equalsIgnoreCase("title"))	title = result;
				else if(name.equalsIgnoreCase("link")) link = result;
				else if(name.equalsIgnoreCase("description")) description = result;
				else if(name.equalsIgnoreCase("pubdate")) fecha = str2date(result);//Wed, 07 Jun 2017 16:00 EDT
				
				if(img == null)
				{
					if(name.equalsIgnoreCase("enclosure")
						&& xmlPullParser.getAttributeValue(null, "type") != null
						&& xmlPullParser.getAttributeValue(null, "type").startsWith("image/"))
					{
						img = xmlPullParser.getAttributeValue(null, "url");
					}
					else if(description != null)
					{
						//TODO: Con pull parser
						Document doc = Jsoup.parse(description);
						Elements images = doc.select("img");
						if(images.size() > 0)
						{
							img = images.get(0).attr("src");
							if(img != null && img.length() > 3 && img.startsWith("//")) img = "http://"+img.substring(2);
						}
					}
				}
		    }
		    
		    /// Ordenar los elementos por fecha
			Collections.sort(items, new Comparator<RssModel>()
			{
				public int compare(RssModel o1, RssModel o2)
				{
					if(o1.getFecha() == null || o2.getFecha() == null) return 0;
					return o2.getFecha().compareTo(o1.getFecha());
				}
			});
			
		    return items;
		}
		finally
		{
			inputStream.close();
		}
    }
}

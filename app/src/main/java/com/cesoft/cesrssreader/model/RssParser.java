package com.cesoft.cesrssreader.model;

import android.util.Xml;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

////////////////////////////////////////////////////////////////////////////////////////////////////
// Created by Cesar Casanova on 11/06/2017.
public class RssParser
{
	private static final String TAG = RssParser.class.getSimpleName();
	
	public static List<RssModel> parseFeed(InputStream inputStream) throws XmlPullParserException, IOException
	{
		String title = null;
		String description = null;
		String link = null;
		String img = null;
		boolean isItem = false;
		List<RssModel> items = new ArrayList<>();
		
		try
		{
			XmlPullParser xmlPullParser = Xml.newPullParser();
			xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			xmlPullParser.setInput(inputStream, null);
			
			//xmlPullParser.nextTag();
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
					RssModel item = new RssModel(title, description, link, img);
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
				
				//Log.e(TAG, "---------------------------------------val ==> " + title + " : "+link+" : : "+img);
		    }
		
		    return items;
		}
		finally
		{
			inputStream.close();
		}
    }
}

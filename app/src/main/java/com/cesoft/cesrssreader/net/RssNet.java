package com.cesoft.cesrssreader.net;

import android.util.Log;

import com.cesoft.cesrssreader.Util;
import com.cesoft.cesrssreader.model.RssFeedModel;
import com.cesoft.cesrssreader.model.RssItemModel;
import com.cesoft.cesrssreader.model.RssSourceModel;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

////////////////////////////////////////////////////////////////////////////////////////////////////
// Created by Cesar Casanova on 21/06/2017.
public class RssNet
{
	private static final String TAG = RssNet.class.getSimpleName();
	//private String _link;
	//public RssNet(String link) { _link = link; }

	////////////////////////////////////////////////////////////////////////////////////////////////
	public interface Callback
	{
		void onData(RssFeedModel data);
		void onError(Throwable err);
	}

	//----------------------------------------------------------------------------------------------
	public static void fetch(String link, final Callback cb)//"http://www.feedforall.com/sample.xml"
	{
		if(link == null || link.isEmpty())
		{
			Log.e(TAG, "fetch:e: Rss Feed Url is wrong : "+link);
			cb.onError(new Exception("fetch:e: Rss Feed Url is wrong : "+link));
			return;
		}
		//final List<RssModel> items_ = new ArrayList<>();
		//RssNet reader = new RssNet();
		RssNet.getItems(link)
				.subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Action1<List<RssFeedModel>>()
				{
					@Override
					public void call(List<RssFeedModel> items)
					{
						cb.onData(items.get(0));
						//items_.addAll(items);
					}
				},
				new Action1<Throwable>()
				{
					@Override
					public void call(Throwable e)
					{
						cb.onError(e);
						Log.e("App", "Failed to download RSS Items:"+e);
					}
				});
	}


	//----------------------------------------------------------------------------------------------
	private static Observable<List<RssFeedModel>> getItems(final String link)
	{
		return Observable.create(new Observable.OnSubscribe<List<RssFeedModel>>()
		{
			@Override
			public void call(Subscriber<? super List<RssFeedModel>> subscriber)
			{
				try
				{
					SAXParserFactory factory = SAXParserFactory.newInstance();
					SAXParser saxParser = factory.newSAXParser();
					//Creates a new RssHandler which will do all the parsing.
					RssHandler handler = new RssHandler();
					//Pass SaxParser the RssHandler that was created.
					saxParser.parse(link, handler);
					RssFeedModel rssFeed = handler.getRssFeed(link);
					ArrayList<RssFeedModel> list = new ArrayList<>();
					list.add(rssFeed);
					subscriber.onNext(list);
					subscriber.onCompleted();
				}
				catch(Exception e)
				{
					subscriber.onError(e);
				}
			}
		});
	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	private static class RssHandler extends DefaultHandler
	{
		private List<RssItemModel> rssItemList;
		private RssItemModel rssItem;
		private RssSourceModel rssSource;
		private boolean parsingTitle;
		private boolean parsingLink;
		private boolean parsingDescription;
		private boolean parsingFecha;
		private String fecha;

		//------------------------------------------------------------------------------------------
		RssHandler()
		{
			rssItemList = new ArrayList<>();
			rssSource = new RssSourceModel();
		}
		//------------------------------------------------------------------------------------------
		RssFeedModel getRssFeed(String link)
		{
			rssSource.setLink(link);
			return new RssFeedModel(rssSource, rssItemList);
		}

		//------------------------------------------------------------------------------------------
		//Called when an opening tag is reached, such as <item> or <title>
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
		{
			//Log.e(TAG, "startElement: ---------------------------"+rssItemList.size()+"-----------------------------"+qName);
			switch(qName.toLowerCase())
			{
			case "item":
				rssItem = new RssItemModel();
				fecha = "";
				break;
			case "title":
				parsingTitle = true;
				break;
			case "link":
				parsingLink = true;
				break;
			case "description":
				parsingDescription = true;
				break;
			case "pubdate":
				parsingFecha = true;
				break;
			case "enclosure":
				if(attributes.getValue("type") == null || !attributes.getValue("type").contains("image"))break;
			case "media:thumbnail":
			case "media:content":
			case "image":
				if(attributes.getValue("url") != null)
				{
					if(rssItem != null)
						rssItem.setImg(attributes.getValue("url"));
					else
						rssSource.setImg(attributes.getValue("url"));
				}
				break;
			}
		}

		//------------------------------------------------------------------------------------------
		//Called when a closing tag is reached, such as </item> or </title>
		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException
		{
			switch(qName.toLowerCase())
			{
			case "item":
				//End of an item so add the currentItem to the list of items.
				rssItemList.add(rssItem);
				rssItem = null;
				break;
			case "title":
				parsingTitle = false;
				break;
			case "link":
				parsingLink = false;
				break;
			case "description":
				parsingDescription = false;
				break;
			case "pubdate":
				parsingFecha = false;
				fecha = "";
				break;
			}
		}

		//------------------------------------------------------------------------------------------
		//Goes through character by character when parsing whats inside of a tag.
		@Override
		public void characters(char[] ch, int start, int length) throws SAXException
		{
			if(rssItem != null)
			{
				//If parsingTitle is true, then that means we are inside a <title> tag so the text is the title of an item.
				if(parsingTitle)
				{
					String s = "";
					if(rssItem.getTitulo() != null)s=rssItem.getTitulo();
					rssItem.setTitulo(s + new String(ch, start, length));
					Log.e(TAG, "TITULO: ------------- "+new String(ch, start, length));
				}
				else if(parsingDescription)
				{
					String s = "";
					if(rssItem.getDescripcion() != null)s=rssItem.getDescripcion();
					rssItem.setDescripcion(s + new String(ch, start, length));
				}
				else if(parsingLink)
				{
					String s = "";
					if(rssItem.getLink() != null)s=rssItem.getLink();
					rssItem.setLink(s + new String(ch, start, length));
				}
				else if(parsingFecha)
				{
					fecha += new String(ch, start, length);
					rssItem.setFecha(Util.str2date(fecha));
				}
			}
			else
			{
				if(parsingTitle)
					rssSource.setTitulo(new String(ch, start, length));
				else if(parsingDescription)
					rssSource.setDescripcion(new String(ch, start, length));
				else if(parsingFecha)
					rssSource.setFecha(Util.str2date(new String(ch, start, length)));
				else if(parsingLink)
					Log.e(TAG, "-------- FEED LINK : "+new String(ch, start, length));
				//rssSource.setLink(new String(ch, start, length));
			}
		}
	}
}

package com.cesoft.cesrssreader.net

import com.cesoft.cesrssreader.Util
import com.cesoft.cesrssreader.model.RssFeedModel
import com.cesoft.cesrssreader.model.RssItemModel
import com.cesoft.cesrssreader.model.RssSourceModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

import org.xml.sax.Attributes
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler

import java.util.ArrayList

import javax.xml.parsers.SAXParserFactory



////////////////////////////////////////////////////////////////////////////////////////////////////
// Created by Cesar Casanova on 21/06/2017.
class RssNet
{
	////////////////////////////////////////////////////////////////////////////////////////////////
	interface Callback
	{
		fun onData(data: RssFeedModel)
		fun onError(err: Throwable)
	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	companion object
	{
		val TAG = RssNet::class.java.simpleName

		//----------------------------------------------------------------------------------------------
		fun fetch(link: String?, cb: Callback)//"http://www.feedforall.com/sample.xml"
		{
			if(link == null || link.isEmpty())
			{
				Util.log(TAG, "fetch:e: Rss Feed Url is wrong : " + link!!)
				cb.onError(Exception("fetch:e: Rss Feed Url is wrong : " + link))
				return
			}
			//final List<RssModel> items_ = new ArrayList<>();
			//RssNet reader = new RssNet();
			getItems(link)
				.subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(
						{
							items : List<RssFeedModel> -> cb.onData(items[0])
						},
						{
							e : Throwable ->
								cb.onError(e)
								Util.log("App", "Failed to download RSS Items:" + e)
						})
		}

		//----------------------------------------------------------------------------------------------
		private fun getItems(link: String): Observable<List<RssFeedModel>>
		{
			return Observable.create { subscriber ->
				try
				{
					val factory = SAXParserFactory.newInstance()
					val saxParser = factory.newSAXParser()
					//Creates a new RssHandler which will do all the parsing.
					val handler = RssHandler()
					//Pass SaxParser the RssHandler that was created.
					saxParser.parse(link, handler)
					val rssFeed = handler.getRssFeed(link)
					val list = ArrayList<RssFeedModel>()
					list.add(rssFeed)
					subscriber.onNext(list)
					subscriber.onComplete()// .onCompleted()
				}
				catch(e: Exception)
				{
					subscriber.onError(e)
				}
			}
		}
	}



	////////////////////////////////////////////////////////////////////////////////////////////////
	class RssHandler : DefaultHandler()
	{
		private var rssItemList: MutableList<RssItemModel> = ArrayList()
		private var rssItem: RssItemModel? = null
		private var rssSource: RssSourceModel = RssSourceModel()
		private var parsingTitle: Boolean = false
		private var parsingLink: Boolean = false
		private var parsingDescription: Boolean = false
		private var parsingFecha: Boolean = false
		private var fecha: String? = null


		//------------------------------------------------------------------------------------------
		internal fun getRssFeed(link: String): RssFeedModel
		{
			rssSource.link = link
			return RssFeedModel(rssSource, rssItemList)
		}

		//------------------------------------------------------------------------------------------
		//Called when an opening tag is reached, such as <item> or <title>
		@Throws(SAXException::class)
		override fun startElement(uri: String, localName: String, qName: String, attributes: Attributes)
		{
			//Log.e(TAG, "startElement: ---------------------------"+rssItemList.size()+"-----------------------------"+qName);
			when(qName.toLowerCase())
			{
				"item" ->
				{
					rssItem = RssItemModel()
					fecha = ""
				}
				"title" -> parsingTitle = true
				"link" -> parsingLink = true
				"description" -> parsingDescription = true
				"pubdate" -> parsingFecha = true
				"enclosure" ->
				{
					if(attributes.getValue("type") != null
						&& attributes.getValue("type")!!.contains("image")
						&& attributes.getValue("url") != null)
					{
						if(rssItem != null)
							rssItem!!.img = attributes.getValue("url")
						else
							rssSource.img = attributes.getValue("url")
					}
				}
				"media:thumbnail", "media:content", "image" -> if(attributes.getValue("url") != null)
				{
					if(rssItem != null)
						rssItem!!.img = attributes.getValue("url")
					else
						rssSource.img = attributes.getValue("url")
				}
			}
		}

		//------------------------------------------------------------------------------------------
		//Called when a closing tag is reached, such as </item> or </title>
		@Throws(SAXException::class)
		override fun endElement(uri: String, localName: String, qName: String)
		{
			when(qName.toLowerCase())
			{
				"item" -> {
					//End of an item so add the currentItem to the list of items.
					//if(rssItem!=null)
						rssItemList.add(rssItem!!)
					rssItem = null
				}
				"title" -> parsingTitle = false
				"link" -> parsingLink = false
				"description" -> parsingDescription = false
				"pubdate" -> {
					parsingFecha = false
					fecha = ""
				}
			}
		}

		//------------------------------------------------------------------------------------------
		//Goes through character by character when parsing whats inside of a tag.
		@Throws(SAXException::class)
		override fun characters(ch: CharArray, start: Int, length: Int)
		{
			if(rssItem != null)
			{
				//If parsingTitle is true, then that means we are inside a <title> tag so the text is the title of an item.
				if(parsingTitle)
				{
					var s = ""
					if(rssItem!!.titulo != null) s = rssItem!!.titulo
					rssItem!!.titulo = s + String(ch, start, length)
					Util.log(TAG, "TITULO: ------------- " + String(ch, start, length))
				}
				else if(parsingDescription)
				{
					var s = ""
					if(rssItem!!.descripcion != null) s = rssItem!!.descripcion
					rssItem!!.descripcion = s + String(ch, start, length)
				}
				else if(parsingLink)
				{
					var s = ""
					if(rssItem!!.link != null) s = rssItem!!.link
					rssItem!!.link = s + String(ch, start, length)
				}
				else if(parsingFecha)
				{
					fecha += String(ch, start, length)
					rssItem!!.fecha = Util.str2date(fecha)
				}
			}
			else
			{
				when
				{
					parsingTitle -> rssSource.titulo = String(ch, start, length)
					parsingDescription -> rssSource.descripcion = String(ch, start, length)
					parsingFecha -> rssSource.fecha = Util.str2date(String(ch, start, length))
					parsingLink -> Util.log(TAG, "-------- FEED LINK : " + String(ch, start, length))
				}
			}
		}
	}
}
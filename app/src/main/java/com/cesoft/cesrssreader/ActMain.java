package com.cesoft.cesrssreader;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

////////////////////////////////////////////////////////////////////////////////////////////////////
// Created by Cesar Casanova on 10/06/2017.
public class ActMain extends AppCompatActivity
{
	private static final String TAG = "ActMAin";

	private RecyclerView _lista;
	private SwipeRefreshLayout _SwipeLayout;
	private List<RssModel> _FeedList = new ArrayList<>();

	//----------------------------------------------------------------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		//---

		_lista = (RecyclerView)findViewById(R.id.rss_list);
		_lista.setLayoutManager(new LinearLayoutManager(this));
		_lista.setAdapter(new RssListAdapter(this, _FeedList));

		_SwipeLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
		_SwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
		{
			@Override
			public void onRefresh()
			{
				new FetchRssTask().execute((Void)null);
			}
		});
		new FetchRssTask().execute((Void)null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings)
		{
			return true;
		}

		return super.onOptionsItemSelected(item);
	}



	//----------------------------------------------------------------------------------------------
	/*public List<RssModel> parseFeed(InputStream inputStream) throws XmlPullParserException, IOException
	{
		String title = null;
		String link = null;
		String description = null;
		String img = null;
		boolean isItem = false;
		List<RssModel> items = new ArrayList<>();

		try
		{
			XmlPullParser xmlPullParser = Xml.newPullParser();
			xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			xmlPullParser.setInput(inputStream, null);

			xmlPullParser.nextTag();
			while(xmlPullParser.next() != XmlPullParser.END_DOCUMENT)
			{
Log.e(TAG, "------------ LOOP ---------------");
				String name = xmlPullParser.getName();
				if(name == null)continue;
				
				int eventType = xmlPullParser.getEventType();
				if(eventType == XmlPullParser.END_TAG && name.equalsIgnoreCase("item"))
				{
	Log.e(TAG, "************************ "+title+" ------ "+link+" --- "+img+"****************");
					RssModel item = new RssModel(title, description, link, img);
					items.add(item);
					
					title = null;
					description = null;
					link = null;
					img = null;
					isItem = false;
				}
				
				
	Log.e(TAG, "-----------------------------Parsing name ==> " + name);
				
				String result = "";
                if (xmlPullParser.next() == XmlPullParser.TEXT) {
                    result = xmlPullParser.getText();
                    xmlPullParser.nextTag();
                }

                if (name.equalsIgnoreCase("title")) {
                    title = result;
                } else if (name.equalsIgnoreCase("link")) {
                    link = result;
                } else if (name.equalsIgnoreCase("description")) {
                    description = result;
                }
	
				String result = "";
				if(xmlPullParser.next() == XmlPullParser.TEXT)
				{
					result = xmlPullParser.getText();
					xmlPullParser.nextTag();
				}

				if(name.equalsIgnoreCase("title"))title = result;
				else if(name.equalsIgnoreCase("link"))link = result;
				else if(name.equalsIgnoreCase("description"))description = result;

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
		if(description.length() > 10)Log.e(TAG, "DESC: -------------- "+description.substring(0, 10));
						Document doc = Jsoup.parse(description);
						Elements images = doc.select("img");
						if(images.size() > 0)img = images.get(0).attr("src");
					}
				}

Log.e(TAG, "----------------a-------------Parsing name ==> " + name);
				
				Log.e(TAG, "-----"+title+" ------- "+link+" --- "+img);
			}
			return items;
		}
		finally
		{
			inputStream.close();
		}
	}*/
	public List<RssModel> parseFeed(InputStream inputStream) throws XmlPullParserException, IOException
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
				Log.e(TAG, "name -------------------------------------"+name);
				
				int eventType = xmlPullParser.getEventType();
				if(!isItem && eventType == XmlPullParser.START_TAG && name.equalsIgnoreCase("item"))
				{
					Log.e(TAG, "BEG ITEM -------------------------------------");
				    isItem = true;
				    continue;
				}
				if(!isItem)continue;
				if(eventType == XmlPullParser.END_TAG && name.equalsIgnoreCase("item"))
				{
					//if(name.equalsIgnoreCase("item")) isItem = false;
					Log.e(TAG, "NEW ITEM------------------------"+title+" -"+img+"- "+link);
					RssModel item = new RssModel(title, description, link, img);
					items.add(item);
					
					title = null;
				    description = null;
				    link = null;
				    img = null;
				    isItem = false;
					
					continue;
				}
				
				
				Log.e(TAG, "---------------------------------------Parsing name ==> " + name);
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
							//if(img != null && img.length() > 3 && img.startsWith("//")) img = "http://"+img.substring(2);
						}
					}
				}
				
				Log.e(TAG, "---------------------------------------val ==> " + title + " : "+link+" : : "+img);
		    }
		
		    return items;
		}
		finally
		{
			Log.e(TAG, "*************************************** "+items.size());
			inputStream.close();
		}
    }
    

	////////////////////////////////////////////////////////////////////////////////////////////////
	private class FetchRssTask extends AsyncTask<Void, Void, Boolean>
	{
		//private String urlLink = "http://www.xatakandroid.com/tag/feeds/rss2.xml";
		private String urlLink = "https://www.nasa.gov/rss/dyn/breaking_news.rss";

		@Override
		protected void onPreExecute()
		{
			_SwipeLayout.setRefreshing(true);
		}

		@Override
		protected Boolean doInBackground(Void... voids)
		{
			if(TextUtils.isEmpty(urlLink))return false;

			try
			{
				if( !urlLink.startsWith("http://") && !urlLink.startsWith("https://"))
					urlLink = "http://" + urlLink;

				URL url = new URL(urlLink);
				InputStream inputStream = url.openConnection().getInputStream();
				_FeedList = parseFeed(inputStream);
				return true;
			}
			catch(Exception e)
			{
				Log.e(TAG, "Error IO: ", e);
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean success)
		{
			_SwipeLayout.setRefreshing(false);
			if(success)
			{
				/*mFeedTitleTextView.setText("Feed Title: " + mFeedTitle);
				mFeedDescriptionTextView.setText("Feed Description: " + mFeedDescription);
				mFeedLinkTextView.setText("Feed Link: " + mFeedLink);
				// Fill RecyclerView*/
				_lista.setAdapter(new RssListAdapter(ActMain.this, _FeedList));
			}
			else
			{
				Toast.makeText(ActMain.this, "Enter a valid Rss feed url", Toast.LENGTH_LONG).show();
			}
		}
	}
}

package com.cesoft.cesrssreader;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.cesoft.cesrssreader.model.RssModel;
import com.cesoft.cesrssreader.model.RssParser;

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
		handleIntent(getIntent());
	}
	@Override
	public void onNewIntent(Intent i)
	{
		handleIntent(getIntent());
	}
	private void handleIntent(Intent intent)
	{
		if(Intent.ACTION_SEARCH.equals(intent.getAction()))
		{
			String query = intent.getStringExtra(SearchManager.QUERY);
			Log.e(TAG, "---------------BUSCAR-----------"+query);
			//use the query to search your data somehow
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		
		// Associate searchable configuration with the SearchView
		SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView)menu.findItem(R.id.buscar).getActionView();
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
    searchView.setSubmitButtonEnabled(true);
    //searchView.setOnQueryTextListener(this);

		return true;
	}
//https://coderwall.com/p/zpwrsg/add-search-function-to-list-view-in-android TODO
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if(id == R.id.configuracion)
		{
			return true;
		}

		return super.onOptionsItemSelected(item);
	}



	//----------------------------------------------------------------------------------------------
	
    

	////////////////////////////////////////////////////////////////////////////////////////////////
	private class FetchRssTask extends AsyncTask<Void, Void, Boolean>
	{
		private String urlLink = "http://www.xatakandroid.com/tag/feeds/rss2.xml";
		//private String urlLink = "https://www.nasa.gov/rss/dyn/breaking_news.rss";

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
				_FeedList = RssParser.parseFeed(inputStream);
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

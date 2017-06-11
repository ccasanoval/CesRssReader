package com.cesoft.cesrssreader;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.cesoft.cesrssreader.model.RssModel;
import com.cesoft.cesrssreader.net.FetchRss;

import java.util.ArrayList;
import java.util.List;

////////////////////////////////////////////////////////////////////////////////////////////////////
// Created by Cesar Casanova on 10/06/2017.
public class ActMain extends AppCompatActivity implements FetchRss.Callback
{
	private static final String TAG = "ActMAin";
	private static final String _url = "http://www.xatakandroid.com/tag/feeds/rss2.xml";
	//private static final String _url = "https://www.nasa.gov/rss/dyn/breaking_news.rss";

	private RecyclerView _lista;
	private SwipeRefreshLayout _SwipeLayout;

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
		_lista.setAdapter(new RssListAdapter(this, new ArrayList<RssModel>()));

		_SwipeLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
		_SwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
		{
			@Override
			public void onRefresh()
			{
				new FetchRss(ActMain.this).execute(_url);
			}
		});
		new FetchRss(ActMain.this).execute(_url);
		handleIntent(getIntent());
	}
	//----------------------------------------------------------------------------------------------
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

	//----------------------------------------------------------------------------------------------
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
	//----------------------------------------------------------------------------------------------
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will automatically handle clicks
		// on the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if(id == R.id.configuracion)
		{
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
	
	///////////////// FetchRss.Callback
	//
	//----------------------------------------------------------------------------------------------
	@Override
	public void onPreExecute()
	{
		_SwipeLayout.setRefreshing(true);
	}
	//----------------------------------------------------------------------------------------------
	@Override
	public void onPostExecute(Boolean success, List<RssModel> lista)
	{
		_SwipeLayout.setRefreshing(false);
		if(success)
		{
			_lista.setAdapter(new RssListAdapter(this, lista));
		}
		else
		{
			Toast.makeText(this, "Enter a valid Rss feed url", Toast.LENGTH_LONG).show();
		}
	}
	
}

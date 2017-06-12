package com.cesoft.cesrssreader.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SearchViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cesoft.cesrssreader.App;
import com.cesoft.cesrssreader.R;
import com.cesoft.cesrssreader.adapter.RssListAdapter;
import com.cesoft.cesrssreader.model.RssModel;
import com.cesoft.cesrssreader.presenter.PreMain;

import java.util.List;

////////////////////////////////////////////////////////////////////////////////////////////////////
// Created by Cesar Casanova on 10/06/2017.
public class ActMain extends AppCompatActivity implements PreMain.IntVista
{
	//private static final String TAG = "ActMAin";

	private PreMain _presenter = new PreMain();
	
	private RecyclerView _lista;
	private SwipeRefreshLayout _SwipeLayout;
	private TextView _lblFeedTitle;

	//----------------------------------------------------------------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_main);
		
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		
		_lblFeedTitle = (TextView)findViewById(R.id.lblFeedTitle);

		_lista = (RecyclerView)findViewById(R.id.rss_list);
		_lista.setLayoutManager(new LinearLayoutManager(this));
		//_lista.setAdapter(new RssListAdapter(this, new ArrayList<RssModel>()));

		_SwipeLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
		_SwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
		{
			@Override
			public void onRefresh()
			{
				_presenter.cargarDatos();
			}
		});
		
		//handleIntent(getIntent());
	}
	//----------------------------------------------------------------------------------------------
	/*@Override
	public void onNewIntent(Intent i)
	{
		handleIntent(getIntent());
	}
	//----------------------------------------------------------------------------------------------
	private void handleIntent(Intent intent)
	{
		//BUSQUEDAS :
		if(Intent.ACTION_SEARCH.equals(intent.getAction()))
		{
			String query = intent.getStringExtra(SearchManager.QUERY);
			Log.e(TAG, "---------------BUSCAR-----------"+query);
			//use the query to search your data somehow
		}
	}*/
	@Override
	public void onResume()
	{
		super.onResume();
		_presenter.setVista(this);
		_presenter.cargarDatos();
		//com.google.firebase.crash.FirebaseCrash.report(new Exception("Crash reporting test"));
	}
	//----------------------------------------------------------------------------------------------
	@Override public void onStop()
	{
		super.onStop();
		_presenter.clear();
	}

	//----------------------------------------------------------------------------------------------
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		
		MenuItem item = menu.add("Search");
		item.setIcon(android.R.drawable.ic_menu_search);
		MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_ALWAYS | MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		View searchView = SearchViewCompat.newSearchView(this);
		if(searchView != null)
		{
			SearchViewCompat.setIconified(searchView, false);
			SearchViewCompat.setOnQueryTextListener(searchView, new SearchViewCompat.OnQueryTextListener()
			{
				@Override
				public boolean onQueryTextSubmit(String query)
				{
					//Log.e(TAG, "-------------- SEARCH ----------------"+query);
					if( ! query.isEmpty())
					{
						_lista.setAdapter(new RssListAdapter(ActMain.this, _presenter.getDatosFiltrados(query)));
					}
					return true;
				}
				@Override
				public boolean onQueryTextChange(String newText)
				{
					if(newText.isEmpty())//Cancelar busqueda
						_lista.setAdapter(new RssListAdapter(ActMain.this, _presenter.getDatos()));
					return true;
				}
			});
			MenuItemCompat.setActionView(item, searchView);
		}
		return true;
	}
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
			Intent intent = new Intent(this, ActSource.class);
			startActivityForResult(intent, ActSource.ID);
			//startActivity(intent);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
	// Activity Result from ActSource : cambiar fuente del Rss
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch(requestCode)
		{
		case ActSource.ID:
			if(resultCode == RESULT_OK)
			{
				String url = data.getStringExtra(ActSource.SOURCE_URL);
				_presenter.setFuente(url);
			}
			break;
		}
	}
	
	// Implements PreMain.IntVista
	//
	@Override
	public void setRefreshing(boolean b)
	{
		_SwipeLayout.setRefreshing(b);
	}
	@Override
	public void showEntradas(List<RssModel> items)
	{
		_lista.setAdapter(new RssListAdapter(this, items));
	}
	@Override
	public void showTitulo(String titulo)
	{
		_lblFeedTitle.setText(titulo);
	}
	@Override
	public void showError(int error)
	{
		Toast.makeText(this,error, Toast.LENGTH_LONG).show();
	}
}

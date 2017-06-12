package com.cesoft.cesrssreader.presenter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.cesoft.cesrssreader.App;
import com.cesoft.cesrssreader.R;
import com.cesoft.cesrssreader.db.DbOpenHelper;
import com.cesoft.cesrssreader.db.DbRssItem;
import com.cesoft.cesrssreader.model.RssFeedModel;
import com.cesoft.cesrssreader.model.RssModel;
import com.cesoft.cesrssreader.net.FetchRss;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.ArrayList;
import java.util.List;

import rx.schedulers.Schedulers;

////////////////////////////////////////////////////////////////////////////////////////////////////
// Created by Cesar Casanova on 12/06/2017.
public class PreMain implements FetchRss.Callback
{
	private static String TAG = PreMain.class.getSimpleName();
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	public interface IntVista
	{
		void setRefreshing(boolean b);
		void showTitulo(String titulo);
		void showEntradas(List<RssModel> items);
		void showError(int error);
	}
	
	private IntVista _vista;
	
	public void setVista(IntVista vista)
	{
		_vista = vista;
	}
	public void clear()
	{
		_vista = null;
	}
	
	public void setFuente(String url)
	{
		if(url != null && url.length() > 0)//TODO: check url?
		{
			App.getInstance().setRssFeed(new RssFeedModel(url));
			cargarDatos();
		}
		//else error
	}
	public void cargarDatos()
	{
		new FetchRss(this).execute(App.getInstance().getRssFeed().getFuente().getUrl());
	}
	public List<RssModel> getDatos()
	{
		return App.getInstance().getRssFeed().getEntradas();
	}
	public List<RssModel> getDatosFiltrados(String query)
	{
		query = query.toLowerCase();
		List<RssModel> itemsFiltered = new ArrayList<>();
		for(RssModel item : App.getInstance().getRssFeed().getEntradas())
		{
			if(item.getTitulo().toLowerCase().contains(query))
				itemsFiltered.add(item);
		}
		return itemsFiltered;
	}
	
	
	///////////////// FetchRss.Callback
	//
	//----------------------------------------------------------------------------------------------
	@Override
	public void onPreExecute()
	{
		if(_vista != null)_vista.setRefreshing(true);
	}
	//----------------------------------------------------------------------------------------------
	@Override
	public void onPostExecute(Boolean success, RssFeedModel feed)
	{
		if(_vista == null)return;
		_vista.setRefreshing(false);
		if(success)
		{
			// Mostrar RSS Feed en vista
			App.getInstance().setRssFeed(feed);
			_vista.showEntradas(feed.getEntradas());
			_vista.showTitulo(feed.getFuente().getTitulo());
			//
			//Guardar RSS Feed en BBDD
			DbOpenHelper helper = new DbOpenHelper(App.getInstance());
			SqlBrite sqlBrite = new SqlBrite.Builder().build();
			BriteDatabase db = sqlBrite.wrapDatabaseHelper(helper, Schedulers.io());
			DbRssItem.saveAll(db, feed.getEntradas());
		}
		else
		{
			if( ! isOnline())
			{
				DbOpenHelper helper = new DbOpenHelper(App.getInstance());
				SqlBrite sqlBrite = new SqlBrite.Builder().build();
				BriteDatabase db = sqlBrite.wrapDatabaseHelper(helper, Schedulers.io());
				DbRssItem.getLista(db, new DbRssItem.Listener<RssModel>()
				{
					@Override
					public void onError(Throwable t)
					{
						_vista.showError(R.string.error_load_rss);
					}
					@Override
					public void onDatos(List<RssModel> lista)
					{
						//Guardar y cargar la fuente de RSS Feed...
						App.getInstance().getRssFeed().setEntradas(lista);
						if(_vista != null)
						{
							_vista.showTitulo(App.getInstance().getString(R.string.elementos_cache));
							_vista.showEntradas(lista);
						}
					}
				});
			}
			else
				_vista.showError(R.string.error_load_rss);
		}
	}
	
	//----------------------------------------------------------------------------------------------
	private boolean isOnline()
	{
		ConnectivityManager cm = (ConnectivityManager)App.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		return netInfo != null && netInfo.isConnectedOrConnecting();
	}
}

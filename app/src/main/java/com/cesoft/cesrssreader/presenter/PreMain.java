package com.cesoft.cesrssreader.presenter;

import android.util.Log;

import com.cesoft.cesrssreader.App;
import com.cesoft.cesrssreader.R;
import com.cesoft.cesrssreader.Util;
import com.cesoft.cesrssreader.db.DbGlobal;
import com.cesoft.cesrssreader.db.DbOpenHelper;
import com.cesoft.cesrssreader.db.DbRssItem;
import com.cesoft.cesrssreader.db.DbRssSource;
import com.cesoft.cesrssreader.model.RssFeedModel;
import com.cesoft.cesrssreader.model.RssItemModel;
import com.cesoft.cesrssreader.net.RssNet;
import com.squareup.sqlbrite.BriteDatabase;

import java.util.ArrayList;
import java.util.List;

////////////////////////////////////////////////////////////////////////////////////////////////////
// Created by Cesar Casanova on 12/06/2017.
public class PreMain //implements FetchRss.Callback
{
	private static String TAG = PreMain.class.getSimpleName();
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	public interface IntVista
	{
		void setRefreshing(boolean b);
		void showTitulo(String titulo);
		void showEntradas(List<RssItemModel> items);
		void showError(int error);
	}
	private IntVista _vista;
	public void setVista(IntVista vista) { _vista = vista; }
	public void clear() { _vista = null; }
	public void onStart()
	{
	}

	//----------------------------------------------------------------------------------------------
	public void setFuente(String url)
	{
Log.e(TAG, "setFuente----------------------------------------------------------------"+url);
		if(url != null && url.length() > 0)//TODO: check url?
		{
			App.getInstance().setRssFeed(new RssFeedModel(url));
			getNetData();
		}
		else
		{
			Log.e(TAG, "setFuente:e:----------------------------------------------------------------url:"+url);
		}
		//else error
	}
	public void cargarDatos()
	{
Log.e(TAG, "cargarDatos----------------------------------------------------------------");
		//Si hay datos en la bbdd cargalos, si no buscamos en internet
		BriteDatabase db = DbOpenHelper.getDB();
		DbGlobal.getDatos(db, new DbGlobal.Listener<String>()
		{
			@Override
			public void onError(Throwable t)
			{
				Log.e(TAG, "cargarDatos:onError:e:--------------------------------------------------", t);
				getNetData();
			}
			@Override
			public void onDatos(List<String> lista)
			{
				Log.e(TAG, "cargarDatos:onDatos:--------------------------------------------------"+lista.size());
				if(lista.size() > 0)
				{
					Log.e(TAG, "cargarDatos:onDatos:--------------------------------------------------"+lista.get(0));
					//TODO: decidir si cargar de datos: es muy reciente? fecha
					App.getInstance().getRssFeed().setLink(lista.get(0));
					getNetData();
				}
				else
					getNetData();
				//DbRssSource feed = DbRssSource.get();
			}
		});
		db.close();
	}
	//----------------------------------------------------------------------------------------------
	private void getNetData()
	{
Log.e(TAG, "getNetData--------------------------------------------------");
		//new FetchRss(this).execute(App.getInstance().getRssFeed().getFuente().getLink());
		//---
		if(_vista != null) _vista.setRefreshing(true);
		RssNet.fetch(App.getInstance().getRssFeed().getLink(), new RssNet.Callback()
		{
			@Override public void onData(RssFeedModel data) { PreMain.this.onData(data); }
			@Override public void onError(Throwable err) { PreMain.this.onError(err); }
		});
	}
	//----------------------------------------------------------------------------------------------
	private void getDbData()
	{
		BriteDatabase db = DbOpenHelper.getDB();
		DbRssItem.getLista(db, new DbRssItem.Listener<RssItemModel>()
		{
			@Override
			public void onError(Throwable e)
			{
				Log.e(TAG, "getDbData:DbRssItem:e:--------------------------------------------------",e);
				_vista.showError(R.string.error_load_rss_db);
			}
			@Override
			public void onDatos(List<RssItemModel> lista)
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
		db.close();
	}
	//----------------------------------------------------------------------------------------------
	public List<RssItemModel> getData()
	{
		return App.getInstance().getRssFeed().getEntradas();
	}
	public List<RssItemModel> getDataFiltered(String query)
	{
		query = query.toLowerCase();
		List<RssItemModel> itemsFiltered = new ArrayList<>();
		for(RssItemModel item : App.getInstance().getRssFeed().getEntradas())
		{
			if(item.getTitulo().toLowerCase().contains(query))
				itemsFiltered.add(item);
		}
		return itemsFiltered;
	}


	///////////////// NET CALLBACK
	private void onData(RssFeedModel feed)
	{
		App.getInstance().setRssFeed(feed);
		if(_vista != null)
		{
			_vista.setRefreshing(false);
			// Mostrar RSS Feed en vista
			_vista.showEntradas(feed.getEntradas());
			_vista.showTitulo(feed.getFuente().getTitulo());
		}
		//
		//Guardar RSS Feed en BBDD
		BriteDatabase db = DbOpenHelper.getDB();
		// Save Current Source
		DbGlobal.save(db, App.getInstance().getRssFeed().getLink());
		// Save Source
		long id = DbRssSource.save(db, feed.getFuente());
Log.e(TAG, "onData-------------------------------------------------"+id+" .... "+feed.getFuente().getId());
		// Save Items
		DbRssItem.saveAll(db, feed.getEntradas());//, feed.getFuente().getId());
		db.close();
	}
	private void onError(Throwable e)
	{
		_vista.setRefreshing(false);
		Log.e(TAG, "NETCB:onError:e:----------------------------------------------------------------", e);
		if( ! Util.isOnline())
		{
			getDbData();
		}
		else
			_vista.showError(R.string.error_load_rss_net);
	}
	/*
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
			//DbOpenHelper helper = new DbOpenHelper(App.getInstance());
			//SqlBrite sqlBrite = new SqlBrite.Builder().build();
			//BriteDatabase db = sqlBrite.wrapDatabaseHelper(helper, Schedulers.io());
			BriteDatabase db = DbOpenHelper.getDB();
			// Save Source
			long id = DbRssSource.save(db, feed.getFuente());
Log.e(TAG, "onPostExecute----------------------------------"+id+" . "+feed.getFuente().getId());
			// Save Item
			DbRssItem.saveAll(db, feed.getEntradas(), feed.getFuente().getId());
		}
		else
		{
			if( ! isOnline())
			{
				//DbOpenHelper helper = new DbOpenHelper(App.getInstance());
				//SqlBrite sqlBrite = new SqlBrite.Builder().build();
				//BriteDatabase db = sqlBrite.wrapDatabaseHelper(helper, Schedulers.io());
				BriteDatabase db = DbOpenHelper.getDB();
				DbRssItem.getLista(db, new DbRssItem.Listener<RssItemModel>()
				{
					@Override
					public void onError(Throwable t)
					{
						_vista.showError(R.string.error_load_rss);
					}
					@Override
					public void onDatos(List<RssItemModel> lista)
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
	*/
}

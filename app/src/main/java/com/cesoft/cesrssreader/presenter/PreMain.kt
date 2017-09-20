package com.cesoft.cesrssreader.presenter

import com.cesoft.cesrssreader.App
import com.cesoft.cesrssreader.R
import com.cesoft.cesrssreader.Util
import com.cesoft.cesrssreader.db.DbGlobal
import com.cesoft.cesrssreader.db.DbOpenHelper
import com.cesoft.cesrssreader.db.DbRssItem
import com.cesoft.cesrssreader.db.DbRssSource
import com.cesoft.cesrssreader.model.RssFeedModel
import com.cesoft.cesrssreader.model.RssItemModel
import com.cesoft.cesrssreader.net.RssNet


////////////////////////////////////////////////////////////////////////////////////////////////////
// Created by Cesar Casanova on 12/06/2017.
class PreMain
{
	////////////////////////////////////////////////////////////////////////////////////////////////
	interface IntVista
	{
		fun setRefreshing(b: Boolean)
		fun showTitulo(titulo: String)
		fun showEntradas(items: List<RssItemModel>)
		fun showError(error: Int)
	}

	var vista: IntVista? = null

	fun clear() { vista = null }

	fun onStart() {}

	//----------------------------------------------------------------------------------------------
	fun setFuente(url: String?)
	{
		Util.log(TAG, "setFuente----------------------------------------------------------------" + url!!)
		if(url.isNotEmpty())
		//TODO: check url?
		{
			App.rssFeed = RssFeedModel(url)
			getNetData()
		}
		else {
			Util.log(TAG, "setFuente:e:----------------------------------------------------------------url:" + url)
		}
		//else error
	}

	//----------------------------------------------------------------------------------------------
	fun cargarDatos()
	{
		Util.log(TAG, "cargarDatos----------------------------------------------------------------")
		//Si hay datos en la bbdd cargalos, si no buscamos en internet
		val db = DbOpenHelper.db
		DbGlobal.getDatos(db, object : DbGlobal.Listener<String>
		{
			override fun onError(t: Throwable)
			{
				Util.log(TAG, "cargarDatos:onError:e:--------------------------------------------------", t)
				getNetData()
			}

			override fun onDatos(lista: List<String>)
			{
				Util.log(TAG, "cargarDatos:onDatos:--------------------------------------------------" + lista.size)
				if(lista.isNotEmpty())
				{
					Util.log(TAG, "cargarDatos:onDatos:--------------------------------------------------" + lista[0])
					//TODO: decidir si cargar de datos: es muy reciente? fecha
					App.rssFeed.link = lista[0]
					getNetData()
				}
				else
					getNetData()
				//DbRssSource feed = DbRssSource.get();
			}
		})
		db.close()
	}

	//----------------------------------------------------------------------------------------------
	private fun getNetData()
	{
		Util.log(TAG, "getNetData--------------------------------------------------")
		//---
		if(vista != null) vista!!.setRefreshing(true)
		RssNet.fetch(App.rssFeed.link, object : RssNet.Callback
		{
			override fun onData(data: RssFeedModel)
			{
				this@PreMain.onData(data)
			}
			override fun onError(err: Throwable)
			{
				this@PreMain.onError(err)
			}
		})
	}

	//----------------------------------------------------------------------------------------------
	private fun getDbData()
	{
		val db = DbOpenHelper.db
		DbRssItem.getLista(db, object : DbRssItem.Listener<RssItemModel>
		{
			override fun onError(t: Throwable)
			{
				Util.log(TAG, "getDbData:DbRssItem:e:--------------------------------------------------", t)
				vista!!.showError(R.string.error_load_rss_db)
			}
			override fun onDatos(lista: List<RssItemModel>)
			{
				//Guardar y cargar la fuente de RSS Feed...
				App.rssFeed.entradas = lista
				if(vista != null)
				{
					vista!!.showTitulo(App.instance.getString(R.string.elementos_cache))
					vista!!.showEntradas(lista)
				}
			}
		})
		db.close()
	}

	//----------------------------------------------------------------------------------------------
	val data: List<RssItemModel>
		get() = App.rssFeed.entradas

	//----------------------------------------------------------------------------------------------
	fun getDataFiltered(query: String): List<RssItemModel>
		= App.rssFeed.entradas.filter { it.titulo.toLowerCase().contains(query.toLowerCase()) }
//	{
//		val itemsFiltered = ArrayList<RssItemModel>()
//		for(item in App.rssFeed.entradas)
//		{
//			if(item.titulo.toLowerCase().contains(query.toLowerCase()))
//				itemsFiltered.add(item)
//		}
//		return itemsFiltered
//	}


	///////////////// NET CALLBACK
	private fun onData(feed: RssFeedModel)
	{
		App.rssFeed = feed
		if(vista != null)
		{
			vista!!.setRefreshing(false)
			// Mostrar RSS Feed en vista
			vista!!.showEntradas(feed.entradas)
			vista!!.showTitulo(feed.fuente.titulo)
		}
		//
		//Guardar RSS Feed en BBDD
		val db = DbOpenHelper.db
		// Save Current Source
		DbGlobal.save(db, App.rssFeed.link)
		// Save Source
		val id = DbRssSource.save(db, feed.fuente)
		Util.log(TAG, "onData-------------------------------------------------" + id + " .... " + feed.fuente.id)
		// Save Items
		DbRssItem.saveAll(db, feed.entradas)//, feed.getFuente().getId());
		db.close()
	}

	private fun onError(e: Throwable)
	{
		vista!!.setRefreshing(false)
		Util.log(TAG, "NETCB:onError:e:----------------------------------------------------------------", e)
		if( ! Util.isOnline)
			getDbData()
		else
			vista!!.showError(R.string.error_load_rss_net)
	}

	companion object
	{
		private val TAG = PreMain::class.java.simpleName
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

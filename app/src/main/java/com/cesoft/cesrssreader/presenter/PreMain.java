package com.cesoft.cesrssreader.presenter;

import com.cesoft.cesrssreader.App;
import com.cesoft.cesrssreader.R;
import com.cesoft.cesrssreader.model.RssFeedModel;
import com.cesoft.cesrssreader.model.RssModel;
import com.cesoft.cesrssreader.net.FetchRss;

import java.util.ArrayList;
import java.util.List;

////////////////////////////////////////////////////////////////////////////////////////////////////
// Created by Cesar Casanova on 12/06/2017.
public class PreMain implements FetchRss.Callback
{
	////////////////////////////////////////////////////////////////////////////////////////////////
	public interface IntVista
	{
		void setRefreshing(boolean b);
		App getApp();
		void showTitulo(String titulo);
		void showEntradas(List<RssModel> items);
		void showError(int error);
	}
	
	private IntVista _vista;
	
	public void setVista(IntVista vista)
	{
		_vista = vista;
	}
	
	public void setFuente(String url)
	{
		if(url != null && url.length() > 0)//TODO: check url?
		{
			_vista.getApp().setRssFeed(new RssFeedModel(url));
			cargarDatos();
		}
		//else error
	}
	public void cargarDatos()
	{
		new FetchRss(this).execute(_vista.getApp().getRssFeed().getFuente().getUrl());
	}
	public List<RssModel> getDatos()
	{
		return _vista.getApp().getRssFeed().getEntradas();
	}
	public List<RssModel> getDatosFiltrados(String query)
	{
		query = query.toLowerCase();
		List<RssModel> itemsFiltered = new ArrayList<>();
		for(RssModel item : _vista.getApp().getRssFeed().getEntradas())
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
		_vista.setRefreshing(true);
	}
	//----------------------------------------------------------------------------------------------
	@Override
	public void onPostExecute(Boolean success, RssFeedModel feed)
	{
		_vista.setRefreshing(false);
		if(success)
		{
			_vista.getApp().setRssFeed(feed);
			_vista.showEntradas(feed.getEntradas());
			_vista.showTitulo(feed.getFuente().getTitulo());
		}
		else
		{
			_vista.showError(R.string.error_load_rss);
		}
	}
}

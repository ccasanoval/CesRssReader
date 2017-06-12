package com.cesoft.cesrssreader.model;

import java.util.List;

////////////////////////////////////////////////////////////////////////////////////////////////////
// Created by Cesar Casanova on 12/06/2017.
public class RssFeedModel
{
	protected RssSource _source;
	protected List<RssModel> _items;
	
	public RssFeedModel(String url)
	{
		_source = new RssSource(url, "", "");
	}
	
	public RssSource getFuente(){return _source;}
	public void setFuente(RssSource v){_source=v;}
	public List<RssModel> getEntradas(){return _items;}
	public void setEntradas(List<RssModel> v){_items=v;}
}

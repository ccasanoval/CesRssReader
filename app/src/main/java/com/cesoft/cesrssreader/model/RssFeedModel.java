package com.cesoft.cesrssreader.model;

import java.util.List;

////////////////////////////////////////////////////////////////////////////////////////////////////
// Created by Cesar Casanova on 12/06/2017.
public class RssFeedModel
{
	private RssSourceModel _source;
	private List<RssItemModel> _items;

	public RssFeedModel(String link)
	{
		_source = new RssSourceModel(link);
	}
	public RssFeedModel(RssSourceModel source, List<RssItemModel> items)
	{
		_source = source;
		_items = items;
	}
	
	public RssSourceModel getFuente(){return _source;}
	public void setFuente(RssSourceModel v){_source=v;}
	public List<RssItemModel> getEntradas(){return _items;}
	public void setEntradas(List<RssItemModel> v){_items=v;}

	public String getLink()
	{
		if(_source != null)return _source.getLink();
		return null;
	}
	public void setLink(String link)
	{
		if(_source == null)_source = new RssSourceModel();
		_source.setLink(link);
	}
}

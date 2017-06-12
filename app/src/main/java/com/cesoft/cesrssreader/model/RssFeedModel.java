package com.cesoft.cesrssreader.model;

import java.util.List;

////////////////////////////////////////////////////////////////////////////////////////////////////
// Created by sandra on 12/06/2017.
public class RssFeedModel
{
	protected RssSource _source;
	protected List<RssModel> _items;
	
	public RssFeedModel()
	{
		_source = new RssSource();
	}
	
	public RssSource getSource(){return _source;}
	public void setSource(RssSource v){_source=v;}
	public List<RssModel> getEntradas(){return _items;}
	public void setEntradas(List<RssModel> v){_items=v;}
}

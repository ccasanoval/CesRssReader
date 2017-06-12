package com.cesoft.cesrssreader.model;

////////////////////////////////////////////////////////////////////////////////////////////////////
// Created by Cesar Casanova on 12/06/2017.
public class RssSource
{
	private String _url =
			"http://www.xatakandroid.com/tag/feeds/rss2.xml";
		//  "https://www.nasa.gov/rss/dyn/breaking_news.rss";
	private String _titulo =
			"XAtaka Android";
		//	"Nasa";
	private String _descripcion = "";
	
	public RssSource(){}
	public RssSource(String url, String titulo, String descripcion)
	{
		_url = url;
		_titulo = titulo;
		_descripcion = descripcion;
	}
	
	public String getUrl(){return _url;}
	public String getTitulo(){return _titulo;}
	public String getDescripcion(){return _descripcion;}
	
}

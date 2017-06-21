package com.cesoft.cesrssreader.model;

import java.util.Date;

////////////////////////////////////////////////////////////////////////////////////////////////////
// Created by Cesar Casanova on 12/06/2017.
public class RssSourceModel
{
	private String _id;
	private String _link =
			//"http://www.xatakandroid.com/tag/feeds/rss2.xml";
			//"https://www.nasa.gov/rss/dyn/breaking_news.rss";
			"https://actualidad.rt.com/feeds/all.rss";
	private String _titulo = "";
	private String _descripcion = "";
	private String _img;
	private Date _fecha;
	
	public RssSourceModel(){}
	public RssSourceModel(String link){_link=link;}
	public RssSourceModel(String id, String titulo, String descripcion, String link, String img, Date fecha)
	{
		_id = id;
		if(link != null && !link.isEmpty())
		_link = link;
		_titulo = titulo;
		_descripcion = descripcion;
		_img = img;
		_fecha = fecha;
	}

	public String getId(){return _id;}
	public String getLink(){return _link;}
	public String getTitulo(){return _titulo;}
	public String getDescripcion(){return _descripcion;}
	public String getImg(){return _img;}
	public Date getFecha(){return _fecha;}

	public void setId(String v){_id=v;}
	public void setLink(String v){_link=v;}
	public void setTitulo(String v){_titulo=v;}
	public void setDescripcion(String v){_descripcion=v;}
	public void setImg(String v){_img=v;}
	public void setFecha(Date v){_fecha=v;}
	
}

package com.cesoft.cesrssreader.model;

import java.util.Date;

////////////////////////////////////////////////////////////////////////////////////////////////////
// Created by Cesar Casanova on 10/06/2017.
public class RssModel //implements Comparable<RssModel>
{
	//public static final String TAG = RssModel.class.getSimpleName();
	private String titulo;
	private String descripcion;
	private String link;
	private String img;
	private Date fecha;

	public RssModel(String titulo, String descripcion, String link, String img, Date fecha)
	{
	    this.titulo = titulo;
		this.descripcion = descripcion;
	    this.link = link;
	    this.img = img;
		this.fecha = fecha;
	}

	public String getTitulo()
	{
		return titulo;
	}
	/*public RssModel setTitulo(String titulo)
	{
		this.titulo = titulo;
		return this;
	}*/
	public String getDescripcion()
	{
		return descripcion;
	}
	/*public RssModel setDescripcion(String descripcion)
	{
		this.descripcion = descripcion;
		return this;
	}*/
	public String getLink()
	{
		return link;
	}
	/*public RssModel setLink(String link)
	{
		this.link = link;
		return this;
	}*/
	public String getImg()
	{
		return img;
	}
	/*public RssModel setImg(String img)
	{
		this.img = img;
		return this;
	}*/
	public Date getFecha()
	{
		return fecha;
	}
	/*public RssModel setFecha(Date fecha)
	{
		this.fecha = fecha;
		return this;
	}*/
	
	//----------------------------------------------------------------------------------------------
	/// Implements Comparable
	/*@Override
	public int compareTo(@NonNull RssModel o)
	{
		return fecha.compareTo(o.getFecha());
	}*/
}

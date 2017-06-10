package com.cesoft.cesrssreader;

////////////////////////////////////////////////////////////////////////////////////////////////////
// Created by Cesar Casanova on 10/06/2017.
public class RssModel
{
	private String titulo;
	private String descripcion;
	private String link;
	private String img;

	public RssModel(String title, String descripcion, String link, String img)
	{
	    this.titulo = title;
		this.descripcion = descripcion;
	    this.link = link;
	    this.img = img;
	}

	public String getTitulo()
	{
		return titulo;
	}
	public RssModel setTitulo(String titulo)
	{
		this.titulo = titulo;
		return this;
	}
	public String getDescripcion()
	{
		return descripcion;
	}
	public RssModel setDescripcion(String descripcion)
	{
		this.descripcion = descripcion;
		return this;
	}
	public String getLink()
	{
		return link;
	}
	public RssModel setLink(String link)
	{
		this.link = link;
		return this;
	}
	public String getImg()
	{
		return img;
	}
	public RssModel setImg(String img)
	{
		this.img = img;
		return this;
	}
}

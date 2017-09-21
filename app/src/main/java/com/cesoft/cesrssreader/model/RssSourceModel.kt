package com.cesoft.cesrssreader.model

import java.util.Date

////////////////////////////////////////////////////////////////////////////////////////////////////
// Created by Cesar Casanova on 12/06/2017.
data class RssSourceModel(
	var link: String? = null,
	var id: String? = null,
	var titulo: String? = null,
	var descripcion: String? = null,
	var img: String? = null,
	var fecha: Date? = null)
{
	/*var id: String? = null
	//"http://www.xatakandroid.com/tag/feeds/rss2.xml";
	//"https://www.nasa.gov/rss/dyn/breaking_news.rss";
	var link = "https://actualidad.rt.com/feeds/all.rss"
	var titulo = ""
	var descripcion = ""
	var img: String? = null
	var fecha: Date? = null

	constructor() {}
	constructor(link: String)
	{
		this.link = link
	}*/

	/*constructor(id: String, titulo: String, descripcion: String, link: String?, img: String?, fecha: Date)
	{
		this.id = id
		if(link != null && !link.isEmpty())
			this.link = link
		this.titulo = titulo
		this.descripcion = descripcion
		this.img = img
		this.fecha = fecha
	}*/
}

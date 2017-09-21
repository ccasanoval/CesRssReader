package com.cesoft.cesrssreader.model

////////////////////////////////////////////////////////////////////////////////////////////////////
// Created by Cesar Casanova on 12/06/2017.
data class RssFeedModel(var fuente: RssSourceModel?, var entradas: List<RssItemModel>?)
{
	constructor(link: String) : this(RssSourceModel(link), null)

	var link: String?
		get() = fuente?.link
		set(link)
		{
			if(fuente == null) fuente = RssSourceModel(link)
			else fuente!!.link = link
		}
}

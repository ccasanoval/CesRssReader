package com.cesoft.cesrssreader.model

import java.util.Date

////////////////////////////////////////////////////////////////////////////////////////////////////
// Created by Cesar Casanova on 10/06/2017.
data class RssItemModel(
	var titulo: String = "",
	var descripcion: String = "",
	var link: String = "",
	var img: String = "",
	var fecha: Date? = null)


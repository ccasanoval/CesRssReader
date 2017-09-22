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

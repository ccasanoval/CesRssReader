package com.cesoft.cesrssreader.model

import android.os.Parcel
import android.os.Parcelable

import java.util.Date

////////////////////////////////////////////////////////////////////////////////////////////////////
// Created by Cesar Casanova on 11/06/2017.
data class RssItemParcelable(var rssModel: RssItemModel) : Parcelable
{
	//----------------------------------------------------------------------------------------------
	private constructor(obj: Parcel)
		: this(RssItemModel(
			obj.readString(),
			obj.readString(),
			obj.readString(),
			obj.readString(),
			Date(obj.readLong())))

	//----------------------------------------------------------------------------------------------
	override fun writeToParcel(dest: Parcel, flags: Int)
	{
		dest.writeString(rssModel.titulo)
		dest.writeString(rssModel.descripcion)
		dest.writeString(rssModel.link)
		dest.writeString(rssModel.img)
		dest.writeLong(rssModel.fecha?.time ?: 0)
	}

	//----------------------------------------------------------------------------------------------
	override fun describeContents(): Int = 0

	//----------------------------------------------------------------------------------------------
	companion object
	{
		@JvmField @Suppress("unused")
		val CREATOR = object : Parcelable.Creator<RssItemParcelable>
		{
			override fun createFromParcel(obj: Parcel): RssItemParcelable = RssItemParcelable(obj)
			override fun newArray(size: Int): Array<RssItemParcelable?> =  arrayOfNulls(size)
		}
	}
}

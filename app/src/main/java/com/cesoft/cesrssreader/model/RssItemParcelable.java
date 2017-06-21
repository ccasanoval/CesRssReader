package com.cesoft.cesrssreader.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

////////////////////////////////////////////////////////////////////////////////////////////////////
// Created by Cesar Casanova on 11/06/2017.
public class RssItemParcelable implements Parcelable
{
	private RssItemModel _data;
	
	public RssItemParcelable(RssItemModel data){_data=data;}
	public RssItemModel getRssModel(){return _data;}

	private RssItemParcelable(Parcel in)
	{
		_data = new RssItemModel(in.readString(), in.readString(), in.readString(), in.readString(), new Date(in.readLong()));
		/*_data.setTitulo(in.readString());
		_data.setDescripcion(in.readString());
		_data.setLink(in.readString());
		_data.setImg(in.readString());*/
	}
	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeString(_data.getTitulo());
		dest.writeString(_data.getDescripcion());
		dest.writeString(_data.getLink());
		dest.writeString(_data.getImg());
		dest.writeLong(_data.getFecha().getTime());
	}
	
	@Override
	public int describeContents()
	{
		return 0;
	}
	
	public static final Parcelable.Creator<RssItemParcelable> CREATOR = new Parcelable.Creator<RssItemParcelable>()
	{
		@Override
		public RssItemParcelable createFromParcel(Parcel in)
		{
			return new RssItemParcelable(in);
		}
		@Override
		public RssItemParcelable[] newArray(int size)
		{
			return new RssItemParcelable[size];
		}
	};
}

package com.cesoft.cesrssreader.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

////////////////////////////////////////////////////////////////////////////////////////////////////
// Created by sandra on 11/06/2017.
public class RssParcelable implements Parcelable
{
	private RssModel _data;
	
	public RssParcelable(RssModel data){_data=data;}
	public RssModel getRssModel(){return _data;}

	private RssParcelable(Parcel in)
	{
		_data = new RssModel(in.readString(), in.readString(), in.readString(), in.readString(), new Date(in.readLong()));
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
	
	public static final Parcelable.Creator<RssParcelable> CREATOR = new Parcelable.Creator<RssParcelable>()
	{
		@Override
		public RssParcelable createFromParcel(Parcel in)
		{
			return new RssParcelable(in);
		}
		@Override
		public RssParcelable[] newArray(int size)
		{
			return new RssParcelable[size];
		}
	};
}

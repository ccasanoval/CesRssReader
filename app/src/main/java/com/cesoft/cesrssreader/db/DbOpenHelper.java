package com.cesoft.cesrssreader.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

////////////////////////////////////////////////////////////////////////////////////////////////////
// Created by Cesar Casanova on 12/06/2017.
public final class DbOpenHelper extends SQLiteOpenHelper
{
	private static final int VERSION = 1;

	public DbOpenHelper(Context context)
	{
		super(context, "organizate.db", null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		//Log.e("DbOpenHelper", "onCreate---------------------------------------------"+DbObjeto.SQL_CREATE_TABLE);
		db.execSQL(DbRssItem.SQL_CREATE_TABLE);
		//db.execSQL(DbRssItem.SQL_CREATE_INDEX1);
		//
		//db.execSQL(DbRssSource.SQL_CREATE_TABLE);
		//db.execSQL(DbRssSource.SQL_CREATE_INDEX);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
}

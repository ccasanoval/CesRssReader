package com.cesoft.cesrssreader.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.cesoft.cesrssreader.App;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import rx.schedulers.Schedulers;

////////////////////////////////////////////////////////////////////////////////////////////////////
// Created by Cesar Casanova on 12/06/2017.
public final class DbOpenHelper extends SQLiteOpenHelper
{
	private static final int VERSION = 1;

	private DbOpenHelper(Context context)
	{
		super(context, "cesrrs.db", null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		//Log.e("DbOpenHelper", "onCreate---------------------------------------------"+DbObjeto.SQL_CREATE_TABLE);
		db.execSQL(DbRssItem.SQL_CREATE_TABLE);
		db.execSQL(DbRssItem.SQL_CREATE_INDEX);
		//
		db.execSQL(DbRssSource.SQL_CREATE_TABLE);
		db.execSQL(DbRssSource.SQL_CREATE_INDEX);
		//
		db.execSQL(DbGlobal.SQL_CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }

	public static BriteDatabase getDB()
	{
		DbOpenHelper helper = new DbOpenHelper(App.getInstance());
		SqlBrite sqlBrite = new SqlBrite.Builder().build();
		return sqlBrite.wrapDatabaseHelper(helper, Schedulers.io());
	}
}

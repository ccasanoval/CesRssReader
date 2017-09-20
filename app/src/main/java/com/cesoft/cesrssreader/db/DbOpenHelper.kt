package com.cesoft.cesrssreader.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

import com.cesoft.cesrssreader.App
import com.squareup.sqlbrite2.BriteDatabase
import com.squareup.sqlbrite2.SqlBrite
import io.reactivex.schedulers.Schedulers


////////////////////////////////////////////////////////////////////////////////////////////////////
// Created by Cesar Casanova on 12/06/2017.
class DbOpenHelper private constructor(context: Context) : SQLiteOpenHelper(context, "cesrrs.db", null, VERSION)
{
	override fun onCreate(db: SQLiteDatabase)
	{
		db.execSQL(DbRssItem.SQL_CREATE_TABLE)
		db.execSQL(DbRssItem.SQL_CREATE_INDEX)
		//
		db.execSQL(DbRssSource.SQL_CREATE_TABLE)
		db.execSQL(DbRssSource.SQL_CREATE_INDEX)
		//
		db.execSQL(DbGlobal.SQL_CREATE_TABLE)
	}

	override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

	companion object
	{
		private val VERSION = 1

		val db: BriteDatabase
			get()
			{
				val helper = DbOpenHelper(App.instance)
				val sqlBrite = SqlBrite.Builder().build()
				return sqlBrite.wrapDatabaseHelper(helper, Schedulers.io())
			}
	}
}

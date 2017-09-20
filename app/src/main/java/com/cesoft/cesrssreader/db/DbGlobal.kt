package com.cesoft.cesrssreader.db

import android.content.ContentValues
import android.database.Cursor
import android.util.Log

import com.squareup.sqlbrite2.BriteDatabase
import java.util.UUID


import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers


////////////////////////////////////////////////////////////////////////////////////////////////////
object DbGlobal
{
	private val TAG = DbGlobal::class.java.simpleName

	private val ID = "_id"
	//private static final String ID_FEED = "id_feed";
	private val FEED_LINK = "feedlink"

	private val TABLE = "cesglobal"
	private val QUERY = "SELECT * FROM " + TABLE

	//CREATE TABLE IF NOT EXISTS
	internal val SQL_CREATE_TABLE =
			"CREATE TABLE $TABLE ( " + DbGlobal.ID + " TEXT   NOT NULL   PRIMARY KEY," + DbGlobal.FEED_LINK + " TEXT" + " )"

	//----------------------------------------------------------------------------------------------
	private val MAPPER = Function<Cursor, String>
	{ cursor ->
		var link: String? = null
		try
		{
			var i = -1
			cursor.getString(++i)
			link = cursor.getString(++i)
		}
		catch(e: Exception)
		{
			Log.e(TAG, "Func1:e:----------------------------------------------------------------", e)
		}

		link
	}

	//----------------------------------------------------------------------------------------------
	private fun code(link: String): ContentValues {
		val cv = ContentValues()
		cv.put(DbGlobal.ID, UUID.randomUUID().toString())
		cv.put(DbGlobal.FEED_LINK, link)
		return cv
	}

	//______________________________________________________________________________________________
	fun save(db: BriteDatabase?, link: String) {
		try {
			if(db == null) {
				Log.e(TAG, "save:e:------------------------------------------------------------------ DB == NULL")
				return
			}
			db.delete(DbGlobal.TABLE, null)
			db.insert(DbGlobal.TABLE, code(link))
		}
		catch(e: Exception) {
			Log.e(TAG, "save:e:------------------------------------------------------------------", e)
		}

	}
	//----------------------------------------------------------------------------------------------
	/*public static void delete(BriteDatabase db, RssItemModel o)
	{
		db.delete(TABLE, TITULO+" LIKE ?", o.getTitulo());
	}*/


	//----------------------------------------------------------------------------------------------
	interface Listener<T>
	{
		fun onError(t: Throwable)
		fun onDatos(lista: List<T>)
	}

	fun getDatos(db: BriteDatabase, listener: Listener<String>) =
		db.createQuery(DbGlobal.TABLE, DbGlobal.QUERY)
			.mapToList(DbGlobal.MAPPER)
			.observeOn(AndroidSchedulers.mainThread())
			.subscribeOn(Schedulers.io())
			.doOnError { err -> listener.onError(err) }
			.subscribe { lista -> listener.onDatos(lista) }
}
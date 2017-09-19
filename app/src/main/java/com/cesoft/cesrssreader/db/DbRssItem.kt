package com.cesoft.cesrssreader.db

import com.cesoft.cesrssreader.model.RssItemModel
import com.squareup.sqlbrite.BriteDatabase

import android.content.ContentValues
import android.database.Cursor
import android.util.Log

import java.util.Date
import java.util.UUID

import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Func1
import rx.schedulers.Schedulers

////////////////////////////////////////////////////////////////////////////////////////////////////
object DbRssItem {
	private val TAG = DbRssItem::class.java.simpleName

	private val ID = "_id"
	private val TITULO = "titulo"
	private val DESCRIPCION = "descripcion"
	private val LINK = "link"
	private val IMG = "img"
	private val FECHA = "fecha"

	private val TABLE = "rssitem"

	private val QUERY = "SELECT * FROM " + DbRssItem.TABLE

	//CREATE TABLE IF NOT EXISTS
	internal val SQL_CREATE_TABLE =
		"CREATE TABLE $TABLE ( "+
			DbRssItem.ID + " TEXT   NOT NULL   PRIMARY KEY,"+
			DbRssItem.TITULO + " TEXT,"+
			DbRssItem.DESCRIPCION + " TEXT,"+
			DbRssItem.LINK + " TEXT,"+
			DbRssItem.IMG + " TEXT,"+
			DbRssItem.FECHA + " INTEGER"+
		" )"
	internal val SQL_CREATE_INDEX = "CREATE UNIQUE INDEX idx_id_$TABLE ON $TABLE ($ID)"

	//----------------------------------------------------------------------------------------------
	private val MAPPER = Func1<Cursor, RssItemModel> { cursor ->
		var i = -1
		//
		cursor.getString(++i)//id
		val titulo = cursor.getString(++i)
		val descripcion = cursor.getString(++i)
		val link = cursor.getString(++i)
		val img = cursor.getString(++i)
		val fecha = Date(cursor.getLong(++i))
		//
		RssItemModel(titulo, descripcion, link, img, fecha)
	}

	//----------------------------------------------------------------------------------------------
	private fun code(o: RssItemModel) : ContentValues
	{
		val cv = ContentValues()
		cv.put(DbRssItem.ID, UUID.randomUUID().toString())
		cv.put(DbRssItem.TITULO, o.titulo)
		cv.put(DbRssItem.DESCRIPCION, o.descripcion)
		cv.put(DbRssItem.LINK, o.link)
		cv.put(DbRssItem.IMG, o.img)
		cv.put(DbRssItem.FECHA, o.fecha.time)
		return cv
	}

	//______________________________________________________________________________________________
	fun saveAll(db: BriteDatabase?, lista: List<RssItemModel>)//, String id_feed)
	{
		try
		{
			if(db == null)
			{
				Log.e(TAG, "saveAll:e:------------------------------------------------------------------ DB == NULL")
				return
			}
			db.delete(DbRssItem.TABLE, null)
			for(o in lista)
			{
				db.insert(DbRssItem.TABLE, code(o))
			}
		}
		catch(e: Exception)
		{
			Log.e(TAG, "saveAll:e:------------------------------------------------------------------", e)
		}

	}


	//----------------------------------------------------------------------------------------------
	interface Listener<T>
	{
		fun onError(t: Throwable)
		fun onDatos(lista: List<T>)
	}

	fun getLista(db: BriteDatabase, listener: Listener<RssItemModel>): Subscription
	{
		return db.createQuery(DbRssItem.TABLE, DbRssItem.QUERY)
			.mapToList(DbRssItem.MAPPER)
			.observeOn(AndroidSchedulers.mainThread())
			.subscribeOn(Schedulers.io())
			.doOnError { err -> listener.onError(err) }
			.subscribe { lista -> listener.onDatos(lista) }
	}
}
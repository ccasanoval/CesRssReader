package com.cesoft.cesrssreader.db

import android.content.ContentValues
import android.database.Cursor
import com.cesoft.cesrssreader.Util

import com.cesoft.cesrssreader.model.RssSourceModel
import com.squareup.sqlbrite2.BriteDatabase

import java.util.Date
import java.util.UUID

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers


////////////////////////////////////////////////////////////////////////////////////////////////////
object DbRssSource
{
	private val TAG = "DbRssSource"//::class.java.simpleName

	private val ID = "_id"
	private val TITULO = "titulo"
	private val DESCRIPCION = "descripcion"
	private val LINK = "link"
	private val IMG = "img"
	private val FECHA = "fecha"

	private val TABLE = "rsssource"
	private val QUERY =	"SELECT * FROM $TABLE"

	//CREATE TABLE IF NOT EXISTS
	internal val SQL_CREATE_TABLE =
		"CREATE TABLE $TABLE ( "+
			ID + " TEXT   NOT NULL   PRIMARY KEY,"+
			TITULO + " TEXT,"+
			DESCRIPCION + " TEXT,"+
			LINK + " TEXT,"+
			IMG + " TEXT,"+
			FECHA + " INTEGER"+
		" )"
	internal val SQL_CREATE_INDEX = "CREATE UNIQUE INDEX idx_id_" + TABLE +
		" ON " + DbRssSource.TABLE + " (" + DbRssSource.ID + ")"

	//----------------------------------------------------------------------------------------------
	private val MAPPER = Function<Cursor, RssSourceModel>
	{
		cursor ->
			var i = -1
			//
			val id = cursor.getString(++i)
			val titulo = cursor.getString(++i)
			val descripcion = cursor.getString(++i)
			val link = cursor.getString(++i)
			val img = cursor.getString(++i)
			val fecha = Date(cursor.getLong(++i))
			//
			RssSourceModel(link, id, titulo, descripcion, img, fecha)
	}

	//----------------------------------------------------------------------------------------------
	private fun code(o: RssSourceModel): ContentValues
	{
		val cv = ContentValues()
		o.id = UUID.randomUUID().toString()
		cv.put(DbRssSource.ID, o.id)
		cv.put(DbRssSource.TITULO, o.titulo)
		cv.put(DbRssSource.DESCRIPCION, o.descripcion)
		cv.put(DbRssSource.LINK, o.link)
		cv.put(DbRssSource.IMG, o.img)
		cv.put(DbRssSource.FECHA, o.fecha?.time ?: 0)
		return cv
	}

	//______________________________________________________________________________________________
	fun save(db: BriteDatabase?, rssSourceModel: RssSourceModel): Long
	{
		try
		{
			if(db == null)
			{
				Util.log(TAG, "save:e:------------------------------------------------------------------ DB == NULL")
				return -1
			}
			//db.delete(DbRssSource.TABLE, null);//TODO: opcion de borrar...
			db.delete(TABLE, " link like ?", rssSourceModel.link)
			return db.insert(TABLE, code(rssSourceModel))
		}
		catch(e: Exception)
		{
			Util.log(TAG, "save:e:------------------------------------------------------------------", e)
			return -1
		}
	}

	//______________________________________________________________________________________________
	fun delete(db: BriteDatabase, rssSourceModel: RssSourceModel)
	{
		try
		{
			//if(db == null)	Util.log(TAG, "save:e:------------------------------------------------------------------ DB == NULL")
			//else
				db.delete(TABLE, " link like ?", rssSourceModel.link)
		}
		catch(e: Exception)
		{
			Util.log(TAG, "save:e:------------------------------------------------------------------", e)
		}
	}

	//----------------------------------------------------------------------------------------------
	interface Listener<in T>
	{
		fun onError(t: Throwable)
		fun onDatos(lista: List<T>)
	}

	//----------------------------------------------------------------------------------------------
	fun getLista(db: BriteDatabase, listener: Listener<RssSourceModel>) : Disposable =
		db.createQuery(DbRssSource.TABLE, DbRssSource.QUERY)
			.mapToList(DbRssSource.MAPPER)
			.observeOn(AndroidSchedulers.mainThread())
			.subscribeOn(Schedulers.io())
			.doOnError { err -> listener.onError(err) }
			.subscribe { lista -> listener.onDatos(lista) }	//!
}
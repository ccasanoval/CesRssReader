package com.cesoft.cesrssreader.db

import android.content.ContentValues
import android.database.Cursor
import android.util.Log

import com.cesoft.cesrssreader.model.RssSourceModel
import com.squareup.sqlbrite2.BriteDatabase

import java.util.Date
import java.util.UUID

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers


////////////////////////////////////////////////////////////////////////////////////////////////////
object DbRssSource {
	private val TAG = DbRssSource::class.java.simpleName

	private val ID = "_id"
	private val TITULO = "titulo"
	private val DESCRIPCION = "descripcion"
	private val LINK = "link"
	private val IMG = "img"
	private val FECHA = "fecha"

	private val TABLE = "rsssource"
	private val QUERY =	"SELECT * FROM " + DbRssSource.TABLE

	//CREATE TABLE IF NOT EXISTS
	internal val SQL_CREATE_TABLE =
		"CREATE TABLE $TABLE ( "+
			DbRssSource.ID + " TEXT   NOT NULL   PRIMARY KEY,"+
			DbRssSource.TITULO + " TEXT,"+
			DbRssSource.DESCRIPCION + " TEXT,"+
			DbRssSource.LINK + " TEXT,"+
			DbRssSource.IMG + " TEXT,"+
			DbRssSource.FECHA + " INTEGER"+
		" )"
	internal val SQL_CREATE_INDEX = "CREATE UNIQUE INDEX idx_id_" + TABLE +
		" ON " + DbRssSource.TABLE + " (" + DbRssSource.ID + ")"

	//----------------------------------------------------------------------------------------------
	private val MAPPER = Function<Cursor, RssSourceModel> { cursor ->
		var i = -1
		//
		val id = cursor.getString(++i)
		val titulo = cursor.getString(++i)
		val descripcion = cursor.getString(++i)
		val link = cursor.getString(++i)
		val img = cursor.getString(++i)
		val fecha = Date(cursor.getLong(++i))
		//
		RssSourceModel(id, titulo, descripcion, link, img, fecha)
	}

	//----------------------------------------------------------------------------------------------
	private fun code(o: RssSourceModel): ContentValues {
		val cv = ContentValues()
		o.id = UUID.randomUUID().toString()
		cv.put(DbRssSource.ID, o.id)
		cv.put(DbRssSource.TITULO, o.titulo)
		cv.put(DbRssSource.DESCRIPCION, o.descripcion)
		cv.put(DbRssSource.LINK, o.link)
		cv.put(DbRssSource.IMG, o.img)
		cv.put(DbRssSource.FECHA, if(o.fecha == null) 0 else o.fecha.time)
		return cv
	}

	//______________________________________________________________________________________________
	fun save(db: BriteDatabase?, rssSourceModel: RssSourceModel): Long {
		try {
			if(db == null) {
				Log.e(TAG, "saveAll:e:------------------------------------------------------------------ DB == NULL")
				return -1
			}
			//db.delete(DbRssSource.TABLE, null);//TODO: opcion de borrar...

			db.delete(TABLE, " link like ?", rssSourceModel.link)

			return db.insert(TABLE, code(rssSourceModel))
		}
		catch(e: Exception) {
			Log.e(TAG, "saveAll:e:------------------------------------------------------------------", e)
			return -1
		}

	}
	//----------------------------------------------------------------------------------------------
	/*public static void delete(BriteDatabase db, RssItemModel o)
	{
		db.delete(TABLE, TITULO+" LIKE ?", o.getTitulo());
	}*/


	//----------------------------------------------------------------------------------------------
	interface Listener<T> {
		fun onError(t: Throwable)
		fun onDatos(lista: List<T>)
	}
	//----------------------------------------------------------------------------------------------
	/*public static void getListaSync(BriteDatabase db, final Listener<RssItemModel> listener)
	{
		String LISTA2 = QUERY+" order by "+FECHA+" desc";

		db.createQuery(DbRssItem.TABLE, LISTA2)
			.mapToList(DbRssItem.MAPPER)
			.observeOn(Schedulers.immediate())
			.subscribeOn(Schedulers.immediate())
			.doOnError(new Action1<Throwable>()
			{
				@Override
				public void call(Throwable err)
				{
					listener.onError(err);
				}
			})
			.subscribe(new Action1<List<RssItemModel>>()
			{
				@Override
				public void call(List<RssItemModel> l)
				{
					listener.onDatos(l);
				}
			});
	}*/

	fun getLista(db: BriteDatabase, listener: Listener<RssSourceModel>) =
		db.createQuery(DbRssSource.TABLE, DbRssSource.QUERY)
			.mapToList(DbRssSource.MAPPER)
			.observeOn(AndroidSchedulers.mainThread())
			.subscribeOn(Schedulers.io())
			.doOnError { err -> listener.onError(err) }
			.subscribe { lista -> listener.onDatos(lista) }
}
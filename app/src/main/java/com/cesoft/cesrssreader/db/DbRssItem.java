package com.cesoft.cesrssreader.db;

import com.cesoft.cesrssreader.model.RssModel;
import com.squareup.sqlbrite.BriteDatabase;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

////////////////////////////////////////////////////////////////////////////////////////////////////
public class DbRssItem
{
	private static final String TAG = DbRssItem.class.getSimpleName();

	private static final String ID = "_id";
	private static final String TITULO = "titulo";
	private static final String DESCRIPCION = "descripcion";
	private static final String LINK = "link";
	private static final String IMG = "img";
	private static final String FECHA = "fecha";

	private static final String TABLE = "rssitem";
	private static final String QUERY =
			"SELECT * "
				//+DbRssItem.TABLE+".*, "
				+" FROM "+DbRssItem.TABLE
				;

	//CREATE TABLE IF NOT EXISTS
	static final String SQL_CREATE_TABLE =
			"CREATE TABLE "+ TABLE+" ( "
					+ DbRssItem.ID			+ " TEXT   NOT NULL   PRIMARY KEY,"
					+ DbRssItem.TITULO		+ " TEXT,"
					+ DbRssItem.DESCRIPCION	+ " TEXT,"
					+ DbRssItem.LINK	    + " TEXT,"
					+ DbRssItem.IMG	        + " TEXT,"
					+ DbRssItem.FECHA	    + " INTEGER"
					+" )";
	//static final String SQL_CREATE_INDEX1 = "CREATE UNIQUE INDEX idx_id_"+TABLE+" ON "+Db.TABLE+" ("+Db.ID+")";

	//----------------------------------------------------------------------------------------------
	private static Func1<Cursor, RssModel> MAPPER = new Func1<Cursor, RssModel>()
	{
		@Override public RssModel call(final Cursor cursor)
		{
			int i = -1;
			//
			String id = cursor.getString(++i);
			String titulo = cursor.getString(++i);
			String descripcion = cursor.getString(++i);
			String link = cursor.getString(++i);
			String img = cursor.getString(++i);
			Date fecha = new Date(cursor.getLong(++i));
			//
			return new RssModel(titulo, descripcion, link, img, fecha);
		}
	};

	//----------------------------------------------------------------------------------------------
	private static ContentValues code(RssModel o)
	{
		ContentValues cv = new ContentValues();
		cv.put(DbRssItem.ID, UUID.randomUUID().toString());
		cv.put(DbRssItem.TITULO, o.getTitulo());
		cv.put(DbRssItem.DESCRIPCION, o.getDescripcion());
		cv.put(DbRssItem.LINK, o.getLink());
		cv.put(DbRssItem.IMG, o.getImg());
		cv.put(DbRssItem.FECHA, o.getFecha().getTime());
		return cv;
	}
	//______________________________________________________________________________________________
	public static void saveAll(BriteDatabase db, List<RssModel> lista)
	{
		try
		{
			if(db == null)
			{
				Log.e(TAG, "saveAll:e:------------------------------------------------------------------ DB == NULL");
				return;
			}
			db.delete(DbRssItem.TABLE, null);
			for(RssModel o : lista)
			{
				db.insert(DbRssItem.TABLE, code(o));
			}
		}
		catch(Exception e)
		{
			Log.e(TAG, "saveAll:e:------------------------------------------------------------------", e);
		}
	}
	//----------------------------------------------------------------------------------------------
	/*public static void delete(BriteDatabase db, RssModel o)
	{
		db.delete(TABLE, TITULO+" LIKE ?", o.getTitulo());
	}*/


	//----------------------------------------------------------------------------------------------
	public interface Listener<T>
	{
		void onError(Throwable t);
		void onDatos(List<T> lista);
	}
	//----------------------------------------------------------------------------------------------
	/*public static void getListaSync(BriteDatabase db, final Listener<RssModel> listener)
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
			.subscribe(new Action1<List<RssModel>>()
			{
				@Override
				public void call(List<RssModel> l)
				{
					listener.onDatos(l);
				}
			});
	}*/

	public static Subscription getLista(BriteDatabase db, final Listener<RssModel> listener)
	{
		return db.createQuery(DbRssItem.TABLE, DbRssItem.QUERY)
				.mapToList(DbRssItem.MAPPER)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeOn(Schedulers.io())
				.doOnError(new Action1<Throwable>()
				{
					@Override
					public void call(Throwable err)
					{
						listener.onError(err);
					}
				})
				.subscribe(new Action1<List<RssModel>>()
				{
					@Override
					public void call(List<RssModel> lista)
					{
						listener.onDatos(lista);
					}
				});
	}
}
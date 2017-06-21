package com.cesoft.cesrssreader.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.cesoft.cesrssreader.model.RssSourceModel;
import com.squareup.sqlbrite.BriteDatabase;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

////////////////////////////////////////////////////////////////////////////////////////////////////
public class DbRssSource
{
	private static final String TAG = DbRssSource.class.getSimpleName();

	private static final String ID = "_id";
	private static final String TITULO = "titulo";
	private static final String DESCRIPCION = "descripcion";
	private static final String LINK = "link";
	private static final String IMG = "img";
	private static final String FECHA = "fecha";

	private static final String TABLE = "rsssource";
	private static final String QUERY =
			"SELECT * "
				//+DbRssItem.TABLE+".*, "
				+" FROM "+ DbRssSource.TABLE
				//+" LEFT OUTER JOIN "+DbRssItem.TABLE+" ON "+DbObjeto.TABLE+"."+ID+" = "+DbAvisoTem.TABLE+"."+DbAvisoTem.ID
				;

	//CREATE TABLE IF NOT EXISTS
	static final String SQL_CREATE_TABLE =
			"CREATE TABLE "+ TABLE+" ( "
					+ DbRssSource.ID			+ " TEXT   NOT NULL   PRIMARY KEY,"
					+ DbRssSource.TITULO		+ " TEXT,"
					+ DbRssSource.DESCRIPCION	+ " TEXT,"
					+ DbRssSource.LINK          + " TEXT,"
					+ DbRssSource.IMG			+ " TEXT,"
					+ DbRssSource.FECHA			+ " INTEGER"
					+" )";
	static final String SQL_CREATE_INDEX = "CREATE UNIQUE INDEX idx_id_"+TABLE+" ON "+DbRssSource.TABLE+" ("+DbRssSource.ID+")";

	//----------------------------------------------------------------------------------------------
	private static Func1<Cursor, RssSourceModel> MAPPER = new Func1<Cursor, RssSourceModel>()
	{
		@Override public RssSourceModel call(final Cursor cursor)
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
			return new RssSourceModel(id, titulo, descripcion, link, img, fecha);
		}
	};

	//----------------------------------------------------------------------------------------------
	private static ContentValues code(RssSourceModel o)
	{
		ContentValues cv = new ContentValues();
		o.setId(UUID.randomUUID().toString());
		cv.put(DbRssSource.ID, o.getId());
		cv.put(DbRssSource.TITULO, o.getTitulo());
		cv.put(DbRssSource.DESCRIPCION, o.getDescripcion());
		cv.put(DbRssSource.LINK, o.getLink());
		cv.put(DbRssSource.IMG, o.getImg());
		cv.put(DbRssSource.FECHA, o.getFecha()==null?0:o.getFecha().getTime());
		return cv;
	}
	//______________________________________________________________________________________________
	public static long save(BriteDatabase db, RssSourceModel rssSourceModel)
	{
		try
		{
			if(db == null)
			{
				Log.e(TAG, "saveAll:e:------------------------------------------------------------------ DB == NULL");
				return -1;
			}
			//db.delete(DbRssSource.TABLE, null);//TODO: opcion de borrar...

			db.delete(TABLE, " link like ?", rssSourceModel.getLink());

			return db.insert(TABLE, code(rssSourceModel));
		}
		catch(Exception e)
		{
			Log.e(TAG, "saveAll:e:------------------------------------------------------------------", e);
			return -1;
		}
	}
	//----------------------------------------------------------------------------------------------
	/*public static void delete(BriteDatabase db, RssItemModel o)
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

	public static Subscription getLista(BriteDatabase db, final Listener<RssSourceModel> listener)
	{
		return db.createQuery(DbRssSource.TABLE, DbRssSource.QUERY)
				.mapToList(DbRssSource.MAPPER)
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
				.subscribe(new Action1<List<RssSourceModel>>()
				{
					@Override
					public void call(List<RssSourceModel> lista)
					{
						listener.onDatos(lista);
					}
				});
	}
}
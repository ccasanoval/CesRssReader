package com.cesoft.cesrssreader.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.squareup.sqlbrite.BriteDatabase;

import java.util.List;
import java.util.UUID;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

////////////////////////////////////////////////////////////////////////////////////////////////////
public class DbGlobal
{
	private static final String TAG = DbGlobal.class.getSimpleName();

	private static final String ID = "_id";
	//private static final String ID_FEED = "id_feed";
	private static final String FEED_LINK = "feedlink";

	private static final String TABLE = "cesglobal";
	private static final String QUERY = "SELECT * FROM "+ TABLE;

	//CREATE TABLE IF NOT EXISTS
	static final String SQL_CREATE_TABLE =
			"CREATE TABLE "+TABLE+" ( "
					+ DbGlobal.ID			+ " TEXT   NOT NULL   PRIMARY KEY,"
					+ DbGlobal.FEED_LINK	+ " TEXT"
					+" )";
	
	//----------------------------------------------------------------------------------------------
	private static Func1<Cursor, String> MAPPER = new Func1<Cursor, String>()
	{
		@Override public String call(final Cursor cursor)
		{
			String link = null;
			try
			{
				int i = -1;
				cursor.getString(++i);
				link = cursor.getString(++i);
			}
			catch(Exception e)
			{
				Log.e(TAG, "Func1:e:----------------------------------------------------------------",e);
			}
			return link;
		}
	};

	//----------------------------------------------------------------------------------------------
	private static ContentValues code(String link)
	{
		ContentValues cv = new ContentValues();
		cv.put(DbGlobal.ID, UUID.randomUUID().toString());
		cv.put(DbGlobal.FEED_LINK, link);
		return cv;
	}
	//______________________________________________________________________________________________
	public static void save(BriteDatabase db, String link)
	{
		try
		{
			if(db == null)
			{
				Log.e(TAG, "save:e:------------------------------------------------------------------ DB == NULL");
				return;
			}
			db.delete(DbGlobal.TABLE, null);
			db.insert(DbGlobal.TABLE, code(link));
		}
		catch(Exception e)
		{
			Log.e(TAG, "save:e:------------------------------------------------------------------", e);
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

	public static Subscription getDatos(BriteDatabase db, final Listener<String> listener)
	{
		return db.createQuery(DbGlobal.TABLE, DbGlobal.QUERY)
				.mapToList(DbGlobal.MAPPER)
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
				.subscribe(new Action1<List<String>>()
				{
					@Override
					public void call(List<String> lista)
					{
						listener.onDatos(lista);
					}
				});
	}
}
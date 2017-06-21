package com.cesoft.cesrssreader.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.cesoft.cesrssreader.R;
import com.cesoft.cesrssreader.db.DbOpenHelper;
import com.cesoft.cesrssreader.db.DbRssSource;
import com.cesoft.cesrssreader.model.RssSourceModel;
import com.squareup.sqlbrite.BriteDatabase;

import java.util.List;

////////////////////////////////////////////////////////////////////////////////////////////////////
// Created by Cesar Casanova on 12/06/2017.
public class ActSource extends AppCompatActivity
{
	private static final String TAG = ActSource.class.getSimpleName();
	public static final String SOURCE_URL = "sourceURL";
	public static final int ID = 6969;

	private List<RssSourceModel> _lista;
	
	//----------------------------------------------------------------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_source);
		setTitle(R.string.escoge_feed);
		
		final EditText txtURL = (EditText)findViewById(R.id.txtURL);
		findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				if(txtURL.getText().length() < 1)//TODO: Validar Rss Feed
				{
					Toast.makeText(ActSource.this, R.string.url_incorrecta, Toast.LENGTH_LONG).show();
				}
				else
				{
					Intent i = new Intent();
					i.putExtra(SOURCE_URL, txtURL.getText().toString());
					setResult(Activity.RESULT_OK, i);
				}
				finish();
			}
		});

		final ListView lv = (ListView) findViewById(R.id.lstURL);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
			{
				txtURL.setText(_lista.get(i).getLink());
			}
		});
		iniListaFeeds(lv);
	}

	private void iniListaFeeds(final ListView lv)
	{
		BriteDatabase db = DbOpenHelper.getDB();
		DbRssSource.getLista(db, new DbRssSource.Listener<RssSourceModel>()
		{
			@Override
			public void onError(Throwable e)
			{
				Log.e(TAG, "getDbData:DbRssSource:e:------------------------------------------------",e);
			}
			@Override
			public void onDatos(List<RssSourceModel> lista)
			{
				_lista = lista;

				int i = 0;
				String[] a = new String[lista.size()];
				for(RssSourceModel feed : lista)
				{
					//TextView tv = new TextView(lv.getContext());
					String titulo = feed.getTitulo();
					final int maxLength = 25;
					if(titulo.length() > maxLength)titulo = titulo.substring(0, maxLength)+"...";
					a[i++] = titulo;
				}
				ArrayAdapter<String> adapter = new ArrayAdapter<>(lv.getContext(), android.R.layout.simple_list_item_1, android.R.id.text1, a);
    			lv.setAdapter(adapter);
			}
		});
	}
}

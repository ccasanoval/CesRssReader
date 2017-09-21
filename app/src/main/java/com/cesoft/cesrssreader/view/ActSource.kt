package com.cesoft.cesrssreader.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.*
import com.cesoft.cesrssreader.R
import com.cesoft.cesrssreader.Util
import com.cesoft.cesrssreader.db.DbOpenHelper
import com.cesoft.cesrssreader.db.DbRssSource
import com.cesoft.cesrssreader.model.RssSourceModel
import kotlinx.android.synthetic.main.act_source.*


////////////////////////////////////////////////////////////////////////////////////////////////////
// Created by Cesar Casanova on 12/06/2017.
class ActSource : AppCompatActivity()
{
	private var _lista: List<RssSourceModel>? = null

	//----------------------------------------------------------------------------------------------
	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.act_source)
		setTitle(R.string.escoge_feed)

		//val txtURL = findViewById(R.id.txtURL) as EditText
		btnOK.setOnClickListener {
			if(txtURL.text.isEmpty())
			//TODO: Validar Rss Feed
			{
				Toast.makeText(this@ActSource, R.string.url_incorrecta, Toast.LENGTH_LONG).show()
			}
			else
			{
				val i = Intent()
				i.putExtra(SOURCE_URL, txtURL.text.toString())
				setResult(Activity.RESULT_OK, i)
				finish()
			}
		}
		btnCancel.setOnClickListener {
			finish()
		}

		val lv = findViewById(R.id.lstURL) as ListView
		lv.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l -> txtURL.setText(_lista!![i].link) }
		iniListaFeeds(lv)
	}

	private fun iniListaFeeds(lv: ListView)
	{
		val db = DbOpenHelper.db
		Util.log(TAG, "----------------------iniListaFeeds----------------------------------------")

		DbRssSource.getLista(db, object : DbRssSource.Listener<RssSourceModel>
		{
			override fun onError(t: Throwable)
			{
				Util.log(TAG, "getDbData:DbRssSource:e:------------------------------------------------", t)
			}

			override fun onDatos(lista: List<RssSourceModel>)
			{
				_lista = lista
				var i = 0
				val a = arrayOfNulls<String>(lista.size)
				for(feed in lista)
				{
					//TextView tv = new TextView(lv.getContext());
					var titulo = feed.titulo
					val maxLength = 25
					if(titulo.length > maxLength) titulo = titulo.substring(0, maxLength) + "..."
					a[i++] = titulo
				}
				//_lista = lista
				/*val a = arrayOfNulls<String>(lista.size)
				_lista!!.withIndex().forEach {
					(i, feed) ->
					//TextView tv = new TextView(lv.getContext());
					var titulo = feed.titulo
					val maxLength = 25
					if(titulo.length > maxLength) titulo = titulo.substring(0, maxLength) + "..."
					a[i] = titulo
				}*/
				val adapter = ArrayAdapter<String>(lv.context, android.R.layout.simple_list_item_1, android.R.id.text1, a)
				lv.adapter = adapter
			}
		})
	}

	companion object
	{
		private val TAG = ActSource::class.java.simpleName
		val SOURCE_URL = "sourceURL"
		val ID = 6969
	}
}

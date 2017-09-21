package com.cesoft.cesrssreader.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import com.cesoft.cesrssreader.R
import com.cesoft.cesrssreader.Util
import com.cesoft.cesrssreader.db.DbOpenHelper
import com.cesoft.cesrssreader.db.DbRssSource
import com.cesoft.cesrssreader.model.RssSourceModel
import kotlinx.android.synthetic.main.act_source.*


////////////////////////////////////////////////////////////////////////////////////////////////////
// Created by Cesar Casanova on 12/06/2017.
//TODO: MVP + Borrar sources!
class ActSource : AppCompatActivity()
{
	private var _lista: List<RssSourceModel> = listOf()

	//----------------------------------------------------------------------------------------------
	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.act_source)
		setSupportActionBar(toolbar)

		btnOK.setOnClickListener {
			if(txtURL.text.isEmpty())//TODO: Validar Rss Feed
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
		//btnCancel.setOnClickListener { finish() }

		//val lv = findViewById(R.id.lstURL) as ListView
		lstURL.onItemClickListener = AdapterView.OnItemClickListener { _, _, i, _ -> txtURL.setText(_lista[i].link) }
		iniListaFeeds(lstURL)
	}

	//----------------------------------------------------------------------------------------------
	private fun iniListaFeeds(lv: ListView)
	{
		val db = DbOpenHelper.db

		DbRssSource.getLista(db, object : DbRssSource.Listener<RssSourceModel>
		{
			override fun onError(t: Throwable)
			{
				Util.log(TAG, "getDbData:DbRssSource:e:------------------------------------------------", t)
			}

			override fun onDatos(lista: List<RssSourceModel>)
			{
				_lista = lista
				val a = arrayOfNulls<String>(lista.size)
				val maxLength = 25
				for((i, feed) in lista.withIndex())
				{
					var titulo = feed.titulo
					if(titulo!=null && titulo.length > maxLength) titulo = titulo.substring(0, maxLength) + "..."
					a[i] = titulo
				}
				val adapter = ArrayAdapter<String>(lv.context, android.R.layout.simple_list_item_1, android.R.id.text1, a)
				lv.adapter = adapter
			}
		})
	}

	//----------------------------------------------------------------------------------------------
	override fun onCreateOptionsMenu(menu: Menu): Boolean
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		menuInflater.inflate(R.menu.menu_source, menu)
		return true
	}
	//----------------------------------------------------------------------------------------------
	override fun onOptionsItemSelected(item: MenuItem): Boolean
	{
		// Handle action bar item clicks here. The action bar will automatically handle clicks
		// on the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
		if(item.itemId == R.id.cancel_action)
		{
			finish()
			return true
		}
		return super.onOptionsItemSelected(item)
	}

	//----------------------------------------------------------------------------------------------
	companion object
	{
		private val TAG = ActSource::class.java.simpleName
		val SOURCE_URL = "sourceURL"
		val ID = 6969
	}
}

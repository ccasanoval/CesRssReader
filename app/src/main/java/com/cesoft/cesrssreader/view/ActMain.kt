package com.cesoft.cesrssreader.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast

import com.cesoft.cesrssreader.R
import com.cesoft.cesrssreader.adapter.RssListAdapter
import com.cesoft.cesrssreader.model.RssItemModel
import com.cesoft.cesrssreader.presenter.PreMain
import kotlinx.android.synthetic.main.act_main.*


////////////////////////////////////////////////////////////////////////////////////////////////////
// Created by Cesar Casanova on 10/06/2017.
class ActMain : AppCompatActivity(), PreMain.IntVista
{
	private val _presenter = PreMain()

	//----------------------------------------------------------------------------------------------
	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.act_main)

		//val toolbar = findViewById(R.id.toolbar) as Toolbar
		setSupportActionBar(toolbar)

		rssList.layoutManager = LinearLayoutManager(this)

		swipeRefreshLayout.setOnRefreshListener {
			_presenter.cargarDatos()
		}
	}

	//----------------------------------------------------------------------------------------------
	public override fun onStart()
	{
		super.onStart()
		_presenter.vista = this
		_presenter.cargarDatos()
	}

	//----------------------------------------------------------------------------------------------
	public override fun onResume()
	{
		super.onResume()
		_presenter.vista = this
		_presenter.onStart()
	}

	//----------------------------------------------------------------------------------------------
	public override fun onStop()
	{
		super.onStop()
		_presenter.clear()
	}

	//----------------------------------------------------------------------------------------------
	override fun onCreateOptionsMenu(menu: Menu): Boolean
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		menuInflater.inflate(R.menu.menu_main, menu)

		searchView.setIconifiedByDefault(true)
		searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener
		{
			override fun onQueryTextSubmit(query: String): Boolean
			{
				if( ! query.isEmpty())
					rssList.adapter = RssListAdapter(this@ActMain, _presenter.getDataFiltered(query))
				return true
			}
			override fun onQueryTextChange(newText: String): Boolean
			{
				if(newText.isEmpty())
					rssList.adapter = RssListAdapter(this@ActMain, _presenter.data)
				return true
			}
		})
		return true
	}

	//----------------------------------------------------------------------------------------------
	override fun onOptionsItemSelected(item: MenuItem): Boolean
	{
		// Handle action bar item clicks here. The action bar will automatically handle clicks
		// on the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.

		if(item.itemId == R.id.configuracion)
		{
			val intent = Intent(this, ActSource::class.java)
			startActivityForResult(intent, ActSource.ID)
			return true
		}

		return super.onOptionsItemSelected(item)
	}

	//----------------------------------------------------------------------------------------------
	// Activity Result from ActSource : cambiar fuente del Rss
	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
	{
		when(requestCode)
		{
			ActSource.ID ->
				if(resultCode == Activity.RESULT_OK)
				{
					val url = data?.getStringExtra(ActSource.SOURCE_URL)
					if(url != null)
						_presenter.setFuente(url)
				}
		}
	}

	// Implements PreMain.IntVista
	//
	override fun setRefreshing(b: Boolean) {
		swipeRefreshLayout.isRefreshing = b
	}

	override fun showEntradas(items: List<RssItemModel>) {
		rssList.adapter = RssListAdapter(this, items)
	}

	override fun showTitulo(titulo: String) {
		lblFeedTitle.text = titulo
	}

	override fun showError(error: Int) {
		Toast.makeText(this, error, Toast.LENGTH_LONG).show()
	}

	companion object {
		private val TAG = "ActMAin"
	}
}

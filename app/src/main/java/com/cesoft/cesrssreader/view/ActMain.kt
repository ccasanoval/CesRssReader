package com.cesoft.cesrssreader.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.MenuItemCompat
import android.support.v4.widget.SearchViewCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import com.cesoft.cesrssreader.R
import com.cesoft.cesrssreader.adapter.RssListAdapter
import com.cesoft.cesrssreader.model.RssItemModel
import com.cesoft.cesrssreader.presenter.PreMain


////////////////////////////////////////////////////////////////////////////////////////////////////
// Created by Cesar Casanova on 10/06/2017.
class ActMain : AppCompatActivity(), PreMain.IntVista
{
	private val _presenter = PreMain()

	private var _lista: RecyclerView? = null
	private var _SwipeLayout: SwipeRefreshLayout? = null
	private var _lblFeedTitle: TextView? = null

	//----------------------------------------------------------------------------------------------
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.act_main)

		val toolbar = findViewById(R.id.toolbar) as Toolbar
		setSupportActionBar(toolbar)

		_lblFeedTitle = findViewById(R.id.lblFeedTitle) as TextView

		_lista = findViewById(R.id.rss_list) as RecyclerView
		_lista!!.layoutManager = LinearLayoutManager(this)
		//_lista.setAdapter(new RssListAdapter(this, new ArrayList<RssItemModel>()));

		_SwipeLayout = findViewById(R.id.swipeRefreshLayout) as SwipeRefreshLayout
		_SwipeLayout!!.setOnRefreshListener {
			Log.e(TAG, "REFRESH:----------------------------------------------------------------")
			_presenter.cargarDatos()
		}

		//handleIntent(getIntent());
	}

	//----------------------------------------------------------------------------------------------
	/*@Override
	public void onNewIntent(Intent i)
	{
		handleIntent(getIntent());
	}
	//----------------------------------------------------------------------------------------------
	private void handleIntent(Intent intent)
	{
		//BUSQUEDAS :
		if(Intent.ACTION_SEARCH.equals(intent.getAction()))
		{
			String query = intent.getStringExtra(SearchManager.QUERY);
			Log.e(TAG, "---------------BUSCAR-----------"+query);
			//use the query to search your data somehow
		}
	}*/
	public override fun onStart()
	{
		super.onStart()
		_presenter.vista = this
		_presenter.cargarDatos()
	}

	public override fun onResume()
	{
		super.onResume()
		_presenter.vista = this
		_presenter.onStart()
		Log.e(TAG, "RESUME:----------------------------------------------------------------")
		//com.google.firebase.crash.FirebaseCrash.report(new Exception("Crash reporting test"));

		/*int h = getWindow().getDecorView().getHeight();//=>0
		h = this.getWindow().getDecorView().getBottom();
		Log.e(TAG, "onResume:------------------HEIGHT:"+h);
		findViewById(R.id.swipeRefreshLayout).setMinimumHeight(h);*/
	}

	//----------------------------------------------------------------------------------------------
	public override fun onStop() {
		super.onStop()
		_presenter.clear()
	}

	//----------------------------------------------------------------------------------------------
	override fun onCreateOptionsMenu(menu: Menu): Boolean
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		menuInflater.inflate(R.menu.menu_main, menu)

		val item = menu.add("Search")
		item.setIcon(android.R.drawable.ic_menu_search)
		MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_ALWAYS or MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW)
		val searchView = SearchViewCompat.newSearchView(this)
		if(searchView != null) {
			SearchViewCompat.setIconified(searchView, false)
			SearchViewCompat.setOnQueryTextListener(searchView, object : SearchViewCompat.OnQueryTextListener {
				override fun onQueryTextSubmit(query: String): Boolean {
					//Log.e(TAG, "-------------- SEARCH ----------------"+query);
					if(!query.isEmpty()) {
						_lista!!.adapter = RssListAdapter(this@ActMain, _presenter.getDataFiltered(query))
					}
					return true
				}

				override fun onQueryTextChange(newText: String): Boolean {
					if(newText.isEmpty())
					//Cancelar busqueda
						_lista!!.adapter = RssListAdapter(this@ActMain, _presenter.data)
					return true
				}
			})
			MenuItemCompat.setActionView(item, searchView)
		}
		return true
	}

	//----------------------------------------------------------------------------------------------
	override fun onOptionsItemSelected(item: MenuItem): Boolean
	{
		// Handle action bar item clicks here. The action bar will automatically handle clicks
		// on the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
		val id = item.itemId


		if(id == R.id.configuracion)
		{
			val intent = Intent(this, ActSource::class.java)
			startActivityForResult(intent, ActSource.ID)
			//startActivity(intent);
			return true
		}

		return super.onOptionsItemSelected(item)
	}

	// Activity Result from ActSource : cambiar fuente del Rss
	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent)
	{
		when(requestCode)
		{
			ActSource.ID -> if(resultCode == Activity.RESULT_OK)
			{
				val url = data.getStringExtra(ActSource.SOURCE_URL)
				_presenter.setFuente(url)
			}
		}
	}

	// Implements PreMain.IntVista
	//
	override fun setRefreshing(b: Boolean) {
		_SwipeLayout!!.isRefreshing = b
	}

	override fun showEntradas(items: List<RssItemModel>) {
		_lista!!.adapter = RssListAdapter(this, items)
	}

	override fun showTitulo(titulo: String) {
		_lblFeedTitle!!.text = titulo
	}

	override fun showError(error: Int) {
		Toast.makeText(this, error, Toast.LENGTH_LONG).show()
	}

	companion object {
		private val TAG = "ActMAin"
	}
}

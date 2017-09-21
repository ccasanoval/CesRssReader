package com.cesoft.cesrssreader.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.cesoft.cesrssreader.R
import com.cesoft.cesrssreader.Util
import com.cesoft.cesrssreader.model.RssItemModel
import com.cesoft.cesrssreader.model.RssItemParcelable
import kotlinx.android.synthetic.main.act_detail.*

////////////////////////////////////////////////////////////////////////////////////////////////////
// Created by Cesar Casanova on 11/06/2017.
class ActDetail : AppCompatActivity()
{
	//----------------------------------------------------------------------------------------------
	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.act_detail)

		// Back arrow on action bar
		supportActionBar!!.setDisplayHomeAsUpEnabled(true)
		supportActionBar!!.setDisplayShowHomeEnabled(true)

		// NO NEEDED WITH: import kotlinx.android.synthetic.main.act_detail.*
		//val txtTitulo = findViewById(R.id.txtTitulo) as TextView
		//val btnLink = findViewById(R.id.btnLink) as Button
		//val img = findViewById(R.id.img) as ImageView
		//val wvDescripcion = findViewById(R.id.wvDescripcion) as WebView

		try
		{
			val data = intent.getParcelableExtra<RssItemParcelable>(RssItemModel::class.java.simpleName)
			if(data == null)
			{
				Util.log(TAG, "onCreate:e:--------------------------------- sin datos del feed")
				finish()
				return
			}
			txtTitulo.text = data.rssModel?.titulo
			//txtDescripcion.setText(data.getRssModel().getDescripcion());
			wvDescripcion.loadData(data.rssModel?.descripcion, "text/html; charset=utf-8", "utf-8")

			if(data.rssModel?.img != null)
			{
				Glide.with(this)
					.load(data.rssModel!!.img)
					.into(img)
			}
			btnLink.setOnClickListener {
				val intent = Intent(Intent.ACTION_VIEW, Uri.parse(data.rssModel?.link))
				this@ActDetail.startActivity(intent)
			}

		}
		catch(e: Exception)
		{
			Util.log(TAG, "onCreate:e:--------------------------------- sin datos del feed ", e)
		}

	}

	//----------------------------------------------------------------------------------------------
	override fun onOptionsItemSelected(item: MenuItem): Boolean
		= when(item.itemId)
		{
			android.R.id.home ->
			{
				finish()
				true
			}
			else -> super.onOptionsItemSelected(item)
		}

	companion object
	{
		private val TAG = ActDetail::class.java.simpleName
	}
}

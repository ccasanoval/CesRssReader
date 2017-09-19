package com.cesoft.cesrssreader.adapter


import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.cesoft.cesrssreader.R
import com.cesoft.cesrssreader.model.RssItemModel
import com.cesoft.cesrssreader.model.RssItemParcelable
import com.cesoft.cesrssreader.view.ActDetail

import org.jsoup.Jsoup

////////////////////////////////////////////////////////////////////////////////////////////////////
// Created by Cesar Casanova on 10/06/2017.
class RssListAdapter(private val _context: Context, private val _RssItemModels: List<RssItemModel>)
	: RecyclerView.Adapter<RssListAdapter.RssModelViewHolder>()
{

	////////////////////////////////////////////////////////////////////////////////////////////////
	class RssModelViewHolder(val rssFeedView: View) : RecyclerView.ViewHolder(rssFeedView)

	override fun onCreateViewHolder(parent: ViewGroup, type: Int): RssModelViewHolder
	{
		val v = LayoutInflater.from(parent.context).inflate(R.layout.item_rss, parent, false)
		return RssModelViewHolder(v)
	}

	override fun onBindViewHolder(holder: RssModelViewHolder, position: Int) {
		val rssItemModel = _RssItemModels[position]

		//-------------------
		// Iniciamos campos del elemento Rss
		(holder.rssFeedView.findViewById<View>(R.id.txtTitulo) as TextView).text = rssItemModel.titulo
		(holder.rssFeedView.findViewById<View>(R.id.txtDescripcion) as TextView).text = Jsoup.parse(rssItemModel.descripcion).text()
		//((WebView)holder.rssFeedView.findViewById(R.id.txtDescripcion)).loadData(rssItemModel.getDescripcion(), "text/html; charset=utf-8", "utf-8");
		// Cargo imagen desde URL con Glide
		if(rssItemModel.img != null) {
			Glide
				.with(_context)
				.load(rssItemModel.img)
				.apply(RequestOptions
					.diskCacheStrategyOf(DiskCacheStrategy.ALL)
					//.decode(RawDataDecoder.class)
					.dontAnimate()
					.dontTransform())
				.into(holder.rssFeedView.findViewById<View>(R.id.img) as ImageView)
		}

		//-------------------
		// Boton que abre el navegador con la URL del Rss
		/*holder.rssFeedView.findViewById(R.id.btnLink).setOnClickListener(
			new View.OnClickListener()
			{
				@Override
				public void onClick(View view)
				{
					Intent intent= new Intent(Intent.ACTION_VIEW, Uri.parse(rssItemModel.getLink()));
					_context.startActivity(intent);
				}
			});*/
		//-------------------
		// Mostrar en Detalle
		val onClick = View.OnClickListener {
			val intent = Intent(_context, ActDetail::class.java)
			intent.putExtra(RssItemModel::class.java.simpleName, RssItemParcelable(rssItemModel))
			_context.startActivity(intent)
		}
		holder.rssFeedView.findViewById<View>(R.id.img).setOnClickListener(onClick)
		holder.rssFeedView.findViewById<View>(R.id.txtTitulo).setOnClickListener(onClick)
		holder.rssFeedView.findViewById<View>(R.id.txtDescripcion).setOnClickListener(onClick)
	}

	//----------------------------------------------------------------------------------------------
	override fun getItemCount(): Int =  _RssItemModels.size

}

package com.cesoft.cesrssreader.adapter


import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.cesoft.cesrssreader.R
import com.cesoft.cesrssreader.model.RssItemModel
import com.cesoft.cesrssreader.model.RssItemParcelable
import com.cesoft.cesrssreader.view.ActDetail
import kotlinx.android.synthetic.main.item_rss.view.*

import org.jsoup.Jsoup

////////////////////////////////////////////////////////////////////////////////////////////////////
// Created by Cesar Casanova on 10/06/2017.
class RssListAdapter(private val _context: Context, private val _RssItemModels: List<RssItemModel>)
	: RecyclerView.Adapter<RssListAdapter.RssModelViewHolder>()
{
	class RssModelViewHolder(val rssFeedView: View) : RecyclerView.ViewHolder(rssFeedView)

	//----------------------------------------------------------------------------------------------
	override fun onCreateViewHolder(parent: ViewGroup, type: Int): RssModelViewHolder
	{
		val v = LayoutInflater.from(parent.context).inflate(R.layout.item_rss, parent, false)
		return RssModelViewHolder(v)
	}

	//----------------------------------------------------------------------------------------------
	override fun onBindViewHolder(holder: RssModelViewHolder, position: Int)
	{
		val rssItemModel = _RssItemModels[position]

		//-------------------
		// Iniciamos campos del elemento Rss
		holder.rssFeedView.txtTitulo.text = rssItemModel.titulo
		holder.rssFeedView.txtDescripcion.text = Jsoup.parse(rssItemModel.descripcion ?: "").text()
		// Cargo imagen desde URL con Glide
		if(rssItemModel.img != null)
		{
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
		// Mostrar en Detalle
		val onClick = View.OnClickListener {
			val intent = Intent(_context, ActDetail::class.java)
			intent.putExtra(RssItemModel::class.java.simpleName, RssItemParcelable(rssItemModel))
			_context.startActivity(intent)
		}
		holder.rssFeedView.img.setOnClickListener(onClick)
		holder.rssFeedView.txtTitulo.setOnClickListener(onClick)
		holder.rssFeedView.txtDescripcion.setOnClickListener(onClick)
	}

	//----------------------------------------------------------------------------------------------
	override fun getItemCount(): Int =  _RssItemModels.size

}

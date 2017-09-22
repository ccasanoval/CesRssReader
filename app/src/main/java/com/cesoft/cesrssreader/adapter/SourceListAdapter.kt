package com.cesoft.cesrssreader.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cesoft.cesrssreader.R
import com.cesoft.cesrssreader.Util
import com.cesoft.cesrssreader.model.RssSourceModel
import kotlinx.android.synthetic.main.item_source.view.*

////////////////////////////////////////////////////////////////////////////////////////////////////
// Created by Cesar Casanova on 10/06/2017.
class SourceListAdapter(private val _context: Context, private val _RssSourceModels: List<RssSourceModel>)
	: RecyclerView.Adapter<SourceListAdapter.RssModelViewHolder>()
{
	class RssModelViewHolder(val rssFeedView: View) : RecyclerView.ViewHolder(rssFeedView)

	//----------------------------------------------------------------------------------------------
	override fun onCreateViewHolder(parent: ViewGroup, type: Int): RssModelViewHolder
	{
		val v = LayoutInflater.from(parent.context).inflate(R.layout.item_source, null)
		return RssModelViewHolder(v)
	}

	//----------------------------------------------------------------------------------------------
	override fun onBindViewHolder(holder: RssModelViewHolder, position: Int)
	{
		val rssSourceModel = _RssSourceModels[position]
		//holder.rssFeedView.txtTitulo.text = rssSourceModel.titulo
		var titulo = rssSourceModel.titulo ?: ""
		val first = 22
		val last = 7
		val max = first + last + 2
		titulo = if(titulo.length > max) titulo.substring(0, first)+ "..."+titulo.substring(titulo.length-last) else titulo
		holder.rssFeedView.txtTitulo.text = titulo

		holder.rssFeedView.setOnClickListener { funItemSelected?.invoke(rssSourceModel.link) }
		holder.rssFeedView.imgEliminar.setOnClickListener {
			AlertDialog.Builder(_context)
				.setMessage(R.string.seguro_eliminar)
				.setPositiveButton(R.string.eliminar,
					{
						dialogInterface: DialogInterface, i: Int ->
							funItemDelete?.invoke(rssSourceModel)
					})
				.setNegativeButton(R.string.cancelar,
					{
						dialogInterface: DialogInterface, i: Int ->

					})
			.create().show()
		}
	}

	//----------------------------------------------------------------------------------------------
	override fun getItemCount(): Int =  _RssSourceModels.size

	//----------------------------------------------------------------------------------------------
	private var funItemSelected: ((String?) -> Unit)? = null
	fun setOnItemSelected(callback : (String?) -> Unit)
	{
		funItemSelected = callback
	}
	//----------------------------------------------------------------------------------------------
	private var funItemDelete: ((RssSourceModel) -> Unit)? = null
	fun setOnItemDelete(callback : (RssSourceModel) -> Unit)
	{
		funItemDelete = callback
	}
}
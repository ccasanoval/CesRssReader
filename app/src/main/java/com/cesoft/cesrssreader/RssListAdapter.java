package com.cesoft.cesrssreader;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cesoft.cesrssreader.model.RssModel;
import com.cesoft.cesrssreader.model.RssParcelable;

import org.jsoup.Jsoup;

import java.util.List;

////////////////////////////////////////////////////////////////////////////////////////////////////
// Created by Cesar Casanova on 10/06/2017.
public class RssListAdapter extends RecyclerView.Adapter<RssListAdapter.RssModelViewHolder>
{
	private Context _context;
	private List<RssModel> _RssModels;

	////////////////////////////////////////////////////////////////////////////////////////////////
	public static class RssModelViewHolder extends RecyclerView.ViewHolder
	{
	    private View rssFeedView;
	    public RssModelViewHolder(View v)
	    {
	        super(v);
	        rssFeedView = v;
	    }
	}

	public RssListAdapter(Context context, List<RssModel> rssModels)
	{
		_context = context;
	    _RssModels = rssModels;
	}

	@Override
	public RssModelViewHolder onCreateViewHolder(ViewGroup parent, int type)
	{
	    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rss, parent, false);
	    return new RssModelViewHolder(v);
	}

	
	@Override
	public void onBindViewHolder(RssModelViewHolder holder, int position)
	{
	    final RssModel rssModel = _RssModels.get(position);

		//-------------------
		// Iniciamos campos del elemento Rss
	    ((TextView)holder.rssFeedView.findViewById(R.id.txtTitulo)).setText(rssModel.getTitulo());
	    ((TextView)holder.rssFeedView.findViewById(R.id.txtDescripcion)).setText(Jsoup.parse(rssModel.getDescripcion()).text());
		//((WebView)holder.rssFeedView.findViewById(R.id.txtDescripcion)).loadData(rssModel.getDescripcion(), "text/html; charset=utf-8", "utf-8");
		// Cargo imagen desde URL con Glide
		if(rssModel.getImg() != null)
		{
			Glide.with(_context)
	            .load(rssModel.getImg())
	            .into(((ImageView)holder.rssFeedView.findViewById(R.id.img)));
		}
		
		//-------------------
		// Boton que abre el navegador con la URL del Rss
		holder.rssFeedView.findViewById(R.id.btnLink).setOnClickListener(
			new View.OnClickListener()
			{
				@Override
				public void onClick(View view)
				{
					Intent intent= new Intent(Intent.ACTION_VIEW, Uri.parse(rssModel.getLink()));
					_context.startActivity(intent);
				}
			});
		//-------------------
		// Mostrar en Detalle
		View.OnClickListener onClick = new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Intent intent= new Intent(_context, ActDetail.class);
				intent.putExtra(RssModel.class.getSimpleName(), new RssParcelable(rssModel));
				_context.startActivity(intent);
			}
		};
		holder.rssFeedView.findViewById(R.id.img).setOnClickListener(onClick);
		holder.rssFeedView.findViewById(R.id.txtTitulo).setOnClickListener(onClick);
	    holder.rssFeedView.findViewById(R.id.txtDescripcion).setOnClickListener(onClick);
	}

	//----------------------------------------------------------------------------------------------
	@Override
	public int getItemCount()
	{
	    return _RssModels.size();
	}
}

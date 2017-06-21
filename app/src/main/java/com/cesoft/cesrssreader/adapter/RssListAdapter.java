package com.cesoft.cesrssreader.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.cesoft.cesrssreader.R;
import com.cesoft.cesrssreader.model.RssItemModel;
import com.cesoft.cesrssreader.model.RssItemParcelable;
import com.cesoft.cesrssreader.view.ActDetail;

import org.jsoup.Jsoup;

import java.util.List;

////////////////////////////////////////////////////////////////////////////////////////////////////
// Created by Cesar Casanova on 10/06/2017.
public class RssListAdapter extends RecyclerView.Adapter<RssListAdapter.RssModelViewHolder>
{
	private Context _context;
	private List<RssItemModel> _RssItemModels;

	////////////////////////////////////////////////////////////////////////////////////////////////
	static class RssModelViewHolder extends RecyclerView.ViewHolder
	{
	    private View rssFeedView;
	    RssModelViewHolder(View v)
	    {
	        super(v);
	        rssFeedView = v;
	    }
	}

	public RssListAdapter(Context context, List<RssItemModel> rssItemModels)
	{
		_context = context;
	    _RssItemModels = rssItemModels;
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
	    final RssItemModel rssItemModel = _RssItemModels.get(position);

		//-------------------
		// Iniciamos campos del elemento Rss
	    ((TextView)holder.rssFeedView.findViewById(R.id.txtTitulo)).setText(rssItemModel.getTitulo());
	    ((TextView)holder.rssFeedView.findViewById(R.id.txtDescripcion)).setText(Jsoup.parse(rssItemModel.getDescripcion()).text());
		//((WebView)holder.rssFeedView.findViewById(R.id.txtDescripcion)).loadData(rssItemModel.getDescripcion(), "text/html; charset=utf-8", "utf-8");
		// Cargo imagen desde URL con Glide
		if(rssItemModel.getImg() != null)
		{
			Glide
				.with(_context)
	            .load(rssItemModel.getImg())
				.apply(RequestOptions
					.diskCacheStrategyOf(DiskCacheStrategy.ALL)
					//.decode(RawDataDecoder.class)
					.dontAnimate()
					.dontTransform())
	            .into(((ImageView)holder.rssFeedView.findViewById(R.id.img)));
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
		View.OnClickListener onClick = new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Intent intent = new Intent(_context, ActDetail.class);
				intent.putExtra(RssItemModel.class.getSimpleName(), new RssItemParcelable(rssItemModel));
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
	    return _RssItemModels.size();
	}
}

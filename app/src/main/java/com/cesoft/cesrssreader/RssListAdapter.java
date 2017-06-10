package com.cesoft.cesrssreader;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

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

	    ((TextView)holder.rssFeedView.findViewById(R.id.txtTitulo)).setText(rssModel.getTitulo());
	    ((TextView)holder.rssFeedView.findViewById(R.id.txtDescripcion)).setText(rssModel.getDescripcion());

		((Button)holder.rssFeedView.findViewById(R.id.btnLink)).setTag(rssModel.getLink());

		/*if(_RssFeedModels.get(position).getImg() != null) {
        ParseFile image = (ParseFile) parseList.get(position).get("logo");
        String url = image.getUrl();
        Glide.with(context)
                .load(url)
                .placeholder(R.drawable.piwo_48)
                .transform(new CircleTransform(context))
                .into(holder.imageView);
    } else {
        // make sure Glide doesn't load anything into this view until told otherwise
        Glide.clear(holder.imageView);
        // remove the placeholder (optional); read comments below
        holder.imageView.setImageDrawable(null);
    }*/

		if(rssModel.getImg() != null)
		{
			Glide.with(_context)
	            .load(rssModel.getImg())
	            .into(((ImageView)holder.rssFeedView.findViewById(R.id.img)));
		}
	}

	//----------------------------------------------------------------------------------------------
	@Override
	public int getItemCount()
	{
		Log.e("Adapter", "getCount : ------------------------"+_RssModels.size());
	    return _RssModels.size();
	}
}

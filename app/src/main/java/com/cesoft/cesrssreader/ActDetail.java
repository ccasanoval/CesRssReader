package com.cesoft.cesrssreader;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cesoft.cesrssreader.model.RssModel;
import com.cesoft.cesrssreader.model.RssParcelable;

////////////////////////////////////////////////////////////////////////////////////////////////////
// Created by Cesar Casanova on 11/06/2017.
public class ActDetail extends AppCompatActivity
{
	private static final String TAG = ActDetail.class.getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_detail);
		
		TextView txtTitulo = ((TextView)findViewById(R.id.txtTitulo));
	    TextView txtDescripcion = ((TextView)findViewById(R.id.txtDescripcion));
		Button btnLink = (Button)findViewById(R.id.btnLink);
		ImageView img = (ImageView)findViewById(R.id.img);
		// Cargo imagen desde URL con Glide
		
		try
		{
			final RssParcelable data = getIntent().getParcelableExtra(RssModel.class.getSimpleName());
			if(data == null)
			{
				Log.e(TAG, "onCreate:e:--------------------------------- sin datos del feed");
				finish();
				return;
			}
			txtTitulo.setText(data.getRssModel().getTitulo());
			txtDescripcion.setText(data.getRssModel().getDescripcion());
			if(data.getRssModel().getImg() != null)
			{
				Glide.with(this)
		            .load(data.getRssModel().getImg())
		            .into(img);
			}
			btnLink.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View view)
				{
					Intent intent= new Intent(Intent.ACTION_VIEW, Uri.parse(data.getRssModel().getLink()));
					ActDetail.this.startActivity(intent);
				}
			});
			
		}
		catch(Exception e)
		{
			Log.e(TAG, "onCreate:e:--------------------------------- sin datos del feed ", e);
		}
	}
}

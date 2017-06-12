package com.cesoft.cesrssreader.view;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.cesoft.cesrssreader.R;

////////////////////////////////////////////////////////////////////////////////////////////////////
// Created by Cesar Casanova on 12/06/2017.
public class ActSource extends AppCompatActivity
{
	//private static final String TAG = ActSource.class.getSimpleName();
	public static final String SOURCE_URL = "sourceURL";
	public static final int ID = 6969;
	
	//----------------------------------------------------------------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_source);
		
		final EditText txtURL = (EditText)findViewById(R.id.txtURL);
		findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				if(txtURL.getText().length() < 1)//TODO: Validar Rss Feed
				{
					Toast.makeText(ActSource.this, R.string.url_incorrecta, Toast.LENGTH_LONG).show();
				}
				else
				{
					Intent i = new Intent();
					i.putExtra(SOURCE_URL, txtURL.getText().toString());
					setResult(Activity.RESULT_OK, i);
				}
				finish();
			}
		});
	}
}

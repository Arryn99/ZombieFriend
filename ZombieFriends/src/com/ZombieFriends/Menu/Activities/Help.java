package com.ZombieFriends.Menu.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.ZombieFriends.R;

public class Help extends Activity
{

	private Button mButtonReturnMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);

		mButtonReturnMenu = (Button) findViewById(R.id.returnMenu);

		mButtonReturnMenu.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				Intent returnMenu = new Intent(Help.this, Menu.class);

				startActivity(returnMenu);

			}
		});
	}

}

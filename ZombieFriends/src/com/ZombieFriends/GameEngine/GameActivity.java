package com.ZombieFriends.GameEngine;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.ZombieFriends.Menu.Activities.GameOver;

public class GameActivity extends Activity
{
	/** Called when the activity is first created. */
	private BroadcastReceiver mReceiver;

	private static final String TAG = GameActivity.class.getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// requesting to turn the title OFF
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// making it full screen
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		RelativeLayout RL = new RelativeLayout(this);
		RL.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
		RL.addView(new GameView(this, RL));
		// set our MainGamePanel as the View
		setContentView(RL);
		Log.d(TAG, "View added");
	}

	@Override
	protected void onDestroy()
	{
		Log.d(TAG, "Destroying...");
		super.onDestroy();
	}

	@Override
	protected void onStop()
	{
		Log.d(TAG, "Stopping...");
		super.onStop();
	}

	@Override
	protected void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		this.unregisterReceiver(this.mReceiver);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		IntentFilter intentFilter = new IntentFilter("endgame");

		mReceiver = new BroadcastReceiver()
		{
			@Override
			public void onReceive(Context context, Intent intent)
			{
				//extract our message from intent
				finish();

				Intent gameEnded = new Intent(GameActivity.this, GameOver.class);

				startActivity(gameEnded);

			}
		};
		//registering our receiver
		this.registerReceiver(mReceiver, intentFilter);
	}


}
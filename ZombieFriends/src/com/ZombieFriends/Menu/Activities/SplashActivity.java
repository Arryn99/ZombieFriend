package com.ZombieFriends.Menu.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.ZombieFriends.R;

public class SplashActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);
		Thread pause = new Thread()
		{
			public void run()
			{
				try
				{
					sleep(2000);
				}
				catch (InterruptedException e)	//if there is an error, take it in and do not crash the program
				{
					e.printStackTrace();
				}
				finally
				{
					Intent beginLogin = new Intent(SplashActivity.this, Login.class);
					startActivity(beginLogin);
				}
			}
		};
		pause.start();
	}
	
	  public void onActivityResult(int requestCode, int resultCode, Intent data) {
	       
	    }
}



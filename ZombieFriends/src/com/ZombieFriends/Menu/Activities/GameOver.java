package com.ZombieFriends.Menu.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.ZombieFriends.R;
import com.ZombieFriends.GameEngine.Tools.HighScoresUtility;

public class GameOver extends Activity
{

	private Button mButtonReturn;
	public static int score = 0; 
	EditText nameEntry;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_over);
		mButtonReturn = (Button) findViewById(R.id.returnMenu);
		nameEntry = (EditText)findViewById(R.id.NameEntry);

		mButtonReturn.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				Intent returnToMenu = new Intent(GameOver.this, Menu.class);

				startActivity(returnToMenu);

			}
		});
	}

	protected void onPause() {
		// TODO Auto-generated method stub
		if(nameEntry.getText().length()>0){
			HighScoresUtility mHighScoresUtility = new HighScoresUtility(this);
			mHighScoresUtility.addHighScore("" + nameEntry.getText(), score);
		}
		super.onPause();
	}
	@Override
	public void finish() {

		// TODO Auto-generated method stub
		super.finish();

	}

}

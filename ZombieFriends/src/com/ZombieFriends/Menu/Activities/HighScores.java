package com.ZombieFriends.Menu.Activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.ZombieFriends.R;
import com.ZombieFriends.GameEngine.Tools.HighScoresUtility;
import com.ZombieFriends.Menu.Activities.Adapters.HighScoreAdapter;

public class HighScores extends Activity
{
	ListView mListview;


	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.high_scores);
		List<HighScoresUtility.HighScore> list = new ArrayList<HighScoresUtility.HighScore>();
		mListview = (ListView) findViewById(R.id.highScore);
		HighScoreAdapter adapter = new HighScoreAdapter(this, list);		 
		mListview.setAdapter(adapter);
	}

}
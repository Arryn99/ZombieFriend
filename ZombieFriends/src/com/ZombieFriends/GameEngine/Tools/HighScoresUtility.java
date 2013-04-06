package com.ZombieFriends.GameEngine.Tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;

public class HighScoresUtility
{
	public String SharedPreferencesName = "TeachAttackName";	//identifies preferences
	public String HighScoresString = "HighScoresScore";	//to save the score
	public String HighScoresNameString = "HighScoresName";	//to save the name
	public String HighScoreCountString = "NumberKeyStringName";	//identifies number of scores added so far

	SharedPreferences mSharedPreferences;
	int mCount = 0;

	public HighScoresUtility(Context context)
	{
		super();
		this.mSharedPreferences = context.getSharedPreferences(SharedPreferencesName, 0);
		//get name of highscores added
		mCount = mSharedPreferences.getInt(HighScoreCountString, 0);

	}

	public void addHighScore(String name, int highscore)
	{		
		SharedPreferences.Editor editor = mSharedPreferences.edit(); //get the editor so we can add things to shared preferences for the score we are adding
		editor.putString(HighScoresNameString + mCount, name); //save every score using HighScoresNameString plus a number
		editor.putInt(HighScoresString + mCount, highscore); //same for score
		mCount++;	//iterate the total number of scores we have saved 
		editor.putInt(HighScoreCountString, mCount);	//save to sharedPreferences to get number of previously saved high scores
		editor.commit(); //save everything
	}

	//highscore model
	public class HighScore
	{
		public String name;
		public int score;
	}

	public List<HighScore> getHighScore()
	{
		List<HighScore> list = new ArrayList<HighScore>();
		for(int i = 0 ; i < mCount; i++ )
		{
			HighScore high = new HighScore();
			high.name = mSharedPreferences.getString(HighScoresNameString+i, "");
			high.score = mSharedPreferences.getInt(HighScoresString+i, 0);
			list.add(high);
		}
		Collections.sort(list, new CompareHighScores());	//this sorts the list
		return list;
	}

	//this class is used to sort through the high scores
	public class CompareHighScores implements Comparator<HighScore>
	{

		@Override
		public int compare(HighScore scoreOne, HighScore ScoreTwo)
		{

			double score1 = scoreOne.score;
			double score2 = ScoreTwo.score;

			if (score1 > score2)
			{
				return -1;		//move item down in the list
			}
			else if (score1 < score2)
			{
				return 1;		//move item up in the list
			}
			else			
				return 0;
		}

	}

}
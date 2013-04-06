package com.ZombieFriends.Menu.Activities.Adapters;

import java.util.List;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ZombieFriends.R;
import com.ZombieFriends.GameEngine.Tools.HighScoresUtility;
import com.ZombieFriends.Mechanics.Game;

public class HighScoreAdapter extends ArrayAdapter<HighScoresUtility.HighScore>
{
	private final List<HighScoresUtility.HighScore> mList;	//data for listview
	private final Activity mContext; //this is needed to create a layout inflator
	HighScoresUtility mHighScoresUtility;

	public int lastScore = 0;
	
	public HighScoreAdapter(Activity context, List<HighScoresUtility.HighScore> list)
	{
		super(context, 0, list);
		//load list of previously saved scores
		mHighScoresUtility = new HighScoresUtility(context);
		mList = mHighScoresUtility.getHighScore();
		mContext = context;
		SharedPreferences sharedPrefs = mContext.getSharedPreferences(Game.PREFS_NAME, 0);
		lastScore = sharedPrefs.getInt(Game.HIGHSCORE_NAME, 1001);
	}

	@Override
	public int getCount()
	{
		if(mList.size()>0)
			return mList.size();	//how many rows does the listview need
		else
			return 1;
	}

	static class ViewHolder
	{
		protected TextView score;
		protected TextView name;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View view = null;		//view represents cell
		if (convertView == null)
		{
			LayoutInflater inflator = mContext.getLayoutInflater();
			view = inflator.inflate(R.layout.scores_row, null);
			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.score = (TextView) view.findViewById(R.id.score);
			viewHolder.name = (TextView) view.findViewById(R.id.name);
			view.setTag(viewHolder);
		}
		else
		{
			view = convertView;
		}
		ViewHolder holder = (ViewHolder) view.getTag();	//sets tag to be the view holder
		if(mList.size()>0){
			holder.name.setText(mList.get(position).name);
			holder.score.setText("" + mList.get(position).score);
		}else{
			holder.name.setText("No highscores saved!");
			holder.score.setText("" );
		}
		return view;
	}

}
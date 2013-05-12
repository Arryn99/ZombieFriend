package com.ZombieFriends.Mechanics;


import java.util.LinkedList;
import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;

import com.ZombieFriends.GameEngine.GameThread.Finger_Action;
import com.ZombieFriends.GameEngine.Tools.Vector;
import com.ZombieFriends.GameObjects.Debri;

public class DebriController extends Mechanics
{
	LinkedList<Debri> mCloudsInPlay = new LinkedList<Debri>();

	float mFrequency = 5;		//distance in time between balls appearing on screen
	Context mContext;
	float mTimeSinceLastLaunch = 0;

	public DebriController(Context context, float frequency)
	{
		super(context);
		mContext = context;
		mFrequency = frequency;
	}

	@Override
	public void onDraw(Canvas canvas)
	{
		for (Debri cloud : mCloudsInPlay) // for each ball in the list
		{
			cloud.onDraw(canvas);
		}

	}

	public void update(float dt)
	{
		if (canLaunch(dt))
		{
			addCloud();
		}
		for (int i = 0; i< mCloudsInPlay.size(); i++)		//loop through all balls that have been launched
		{
			Debri cloud = mCloudsInPlay.get(i);			//get ball at position i
			cloud.update(dt);								//updates ball position


			if (!cloud.isOnScreen())							//if the ball is not on the screen
			{
				if(cloud.isAppeared()){ // only remove balls that have appeared
					mCloudsInPlay.remove(i);						//remove from update list
					cloud.cleanUp();								//free up resources for ball
					cloud = null;								//make sure garbage collector cleans up ball
				}
			}else{
				if(!cloud.isAppeared()) // if the ball has not appeared on screen yet
					cloud.setAppeared(true); // it has now
			}
		}


	}

	public void addCloud()
	{
		Random generator = new Random(System.currentTimeMillis());
		Debri cloud = new Debri(mContext, new Vector(-500,generator.nextInt((int) Game.ScreenSize.getY())));
		cloud.setmSpeed(new Vector(50,0));
		mCloudsInPlay.add(cloud);
	}

	public boolean canLaunch(float dt)
	{
		mTimeSinceLastLaunch += dt;
		if (mTimeSinceLastLaunch > mFrequency)
		{
			mTimeSinceLastLaunch = 0;
			return true;
		}
		else return false;
	}

	@Override
	public void onTouchEvent(Finger_Action action, Vector position, long eventTime)
	{
		for (int i = 0; i <mCloudsInPlay.size(); i++)
		{
			Debri cloud = mCloudsInPlay.get(i);			//get ball at position i
			cloud.onTouchEvent(action, position, eventTime);
		}

	}

}
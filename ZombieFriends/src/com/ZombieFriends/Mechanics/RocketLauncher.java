package com.ZombieFriends.Mechanics;


import java.util.LinkedList;
import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;

import com.ZombieFriends.GameEngine.GameThread.Finger_Action;
import com.ZombieFriends.GameEngine.Tools.Vector;
import com.ZombieFriends.GameObjects.GameObject;
import com.ZombieFriends.GameObjects.Player;
import com.ZombieFriends.GameObjects.Rockets;

public class RocketLauncher extends Mechanics
{
	LinkedList<Rockets> mRocketsInPlay = new LinkedList<Rockets>();

	float mFrequency = 5;		//distance in time between balls appearing on screen
	Context mContext;
	float mTimeSinceLastLaunch = 0;

	public RocketLauncher(Context context, float frequency)
	{
		super(context);
		mContext = context;
		mFrequency = frequency;
	}

	@Override
	public void onDraw(Canvas canvas)
	{
		for (Rockets ball : mRocketsInPlay) // for each ball in the list
		{
			ball.onDraw(canvas);
		}

	}

	public void update(float dt, Player player)
	{
		if (canLaunch(dt))
		{
			launch();
		}
		for (int i = 0; i< mRocketsInPlay.size(); i++)		//loop through all balls that have been launched
		{
			Rockets rocket = mRocketsInPlay.get(i);			//get ball at position i
			rocket.update(dt);								//updates ball position

			if (GameObject.checkForCollision(player, rocket))
			{
				mRocketsInPlay.remove(i);						//remove from update list
				rocket.cleanUp();								//free up resources for ball
				rocket = null;								//make sure garbage collector cleans up ball
				player.decrementHealth();
				return;
			}
			
			if (!rocket.isOnScreen())							//if the ball is not on the screen
			{
				if(rocket.isAppeared()){ // only remove balls that have appeared
					mRocketsInPlay.remove(i);						//remove from update list
					rocket.cleanUp();								//free up resources for ball
					rocket = null;								//make sure garbage collector cleans up ball
				}
			}else{
				if(!rocket.isAppeared()) // if the ball has not appeared on screen yet
					rocket.setAppeared(true); // it has now
			}
		}


	}

	public void launch()
	{
		Random generator = new Random(System.currentTimeMillis());
		Rockets ballToLaunch = new Rockets(mContext, new Vector(-100,generator.nextInt((int) Game.ScreenSize.getY())));
		ballToLaunch.setmSpeed(new Vector(50,0));
		mRocketsInPlay.add(ballToLaunch);
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
		for (int i = 0; i <mRocketsInPlay.size(); i++)
		{
			Rockets ball = mRocketsInPlay.get(i);			//get ball at position i
			ball.onTouchEvent(action, position, eventTime);
		}

	}

	@Override
	public void update(float dt)
	{

	}
}
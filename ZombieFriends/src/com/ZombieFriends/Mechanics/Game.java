package com.ZombieFriends.Mechanics;

import android.content.Intent;
import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ZombieFriends.GameEngine.GameThread;
import com.ZombieFriends.GameEngine.GameView;
import com.ZombieFriends.GameEngine.Tools.Vector;
import com.ZombieFriends.GameObjects.Background;
import com.ZombieFriends.GameObjects.Player;
import com.ZombieFriends.Menu.Activities.GameOver;

public class Game extends GameThread
{
	public static final String PREFS_NAME = "TeachAttackPreferences";
	public static final String HIGHSCORE_NAME = "HighScore";
	
	static int score = 0;
	static TextView scoreTextView;
	protected RelativeLayout mRoot;	
	
	Background   mBackground;
	Player       mPlayer;
	RocketLauncher mBallLauncher;
	CloudController        mCloudController;
	
	
	public Game(SurfaceHolder surfaceHolder, GameView gamePanel, RelativeLayout root)
	{
		super(surfaceHolder, gamePanel);

		mBackground = new Background(mContext);
		mPlayer = new Player (mContext);
		mBallLauncher = new RocketLauncher(mContext, 2);
		mCloudController = new CloudController(mContext, 1);
		
		
	}
	
	static public void endGame()
	{
	      Intent i = new Intent("endgame");
	      mContext.sendBroadcast(i);
	      scoreTextView = null;
	      //add high score
	     GameOver.score = score;
	      score = 0;
	      
	}
	
	static public void incrementScore()
	{
		score++;	//add 1 to the score
		if(scoreTextView != null)
			scoreTextView.setText("Score: " + score);	//display the score
	}

	@Override
	public void onDraw(Canvas canvas)
	{
		mBackground.onDraw(canvas);
		mPlayer.onDraw(canvas);
		mBallLauncher.onDraw(canvas);
		mCloudController.onDraw(canvas);
	}

	/* (non-Javadoc)
	 * @see com.TeachAttack.GameEngine.GameThread#update(float)
	 * 
	 * runs at CPU speed
	 */
	protected void update(float dt)
	{
		super.update(dt);
		mPlayer.update(dt);
		mBallLauncher.update(dt, mPlayer);
		mCloudController.update(dt);
	}

	@Override
	public void onTouchEvent(Finger_Action action, Vector position, long eventTime)
	{
		super.onTouchEvent(action, position, eventTime);
		mPlayer.onTouchEvent(action, position, eventTime);
		mBallLauncher.onTouchEvent(action, position, eventTime);
	}

	
	/**
	  *this function is called when a surface is created after the views have been inflated
	  *we can add any addition views or widgets we need here
	  * @param root
	  */
	public void addScreens(RelativeLayout root)
	{
		scoreTextView = new TextView(mContext);
		scoreTextView.setText("Score: ");
		root.addView(scoreTextView);
		
	}
	
}

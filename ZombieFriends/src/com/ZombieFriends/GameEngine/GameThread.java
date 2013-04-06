package com.ZombieFriends.GameEngine;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import com.ZombieFriends.GameEngine.Tools.Vector;


/**
 * The Main thread which contains the game loop. The thread must have access to 
 * the surface view and holder to trigger events every game tick.
 */
public class GameThread extends Thread
{
	static protected Context mContext;
	private static final String TAG = GameThread.class.getSimpleName();
	long lastGameTime = System.currentTimeMillis();

	// Surface holder that can access the physical surface
	private SurfaceHolder surfaceHolder;
	// The actual view that handles inputs
	// and draws to the surface
	private GameView gamePanel;

	// flag to hold game state 
	private boolean running;
	public static Vector ScreenSize;

	public void setRunning(boolean running)
	{
		this.running = running;
	}

	public GameThread(SurfaceHolder surfaceHolder, GameView gamePanel)
	{
		super();
		mContext = gamePanel.getContext();
		this.surfaceHolder = surfaceHolder;
		this.gamePanel = gamePanel;

		WindowManager wm = (WindowManager) gamePanel.getContext().getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();

		GameThread.ScreenSize = new Vector(display.getWidth(),display.getHeight());
	}

	@Override
	public void run()
	{
		long tickCount = 0L;
		Canvas canvas = null;
		Log.d(TAG, "Starting game loop");
		while (running)
		{
			tickCount++;
			// update game state 
			// render state to the screen
			canvas = surfaceHolder.lockCanvas();
			if (canvas != null) {
				// work out the time between frames
				long dt =  (System.currentTimeMillis() - lastGameTime);
				lastGameTime = System.currentTimeMillis();
				// update is called before draw
				update(dt/1000.0f);                
				gamePanel.onDraw(canvas);  // tell the game to redraw
				surfaceHolder.unlockCanvasAndPost(canvas);
			}
		}
		Log.d(TAG, "Game loop executed " + tickCount + " times");
		mContext = null;
	}

	protected void onDraw(Canvas canvas)
	{

	}

	protected void update(float dt)
	{

	}

	public void onTouchEvent(Finger_Action action, Vector position,  long eventTime)
	{
		
	}
	
	public enum Finger_Action {
	    UP,
	    DOWN,
	    MOVE
	}
}

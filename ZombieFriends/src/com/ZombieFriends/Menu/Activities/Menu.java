package com.ZombieFriends.Menu.Activities;

import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ZombieFriends.R;
import com.ZombieFriends.GameEngine.GameActivity;
import com.ZombieFriends.GameEngine.GameThread;
import com.ZombieFriends.GameEngine.Tools.Vector;

public class Menu extends Activity
{

	private ImageButton mButtonStartGame;
	private Button mButtonHelpScreen;
	private Button mButtonHighScores;
	private RelativeLayout mMainLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu);

		mMainLayout = (RelativeLayout)findViewById(R.id.mainLayout); // get a layout we can add screens too
		mButtonStartGame = (ImageButton) findViewById(R.id.playGame);
		mButtonHighScores = (Button) findViewById(R.id.highScores);
		mButtonHelpScreen = (Button) findViewById(R.id.helpGame);

		//add 20 clouds
	//	for(int i =0; i < 20; i++)
		//	newCloudAnimation();

		mButtonStartGame.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				Intent beginGame = new Intent(Menu.this, GameActivity.class);

				startActivity(beginGame);
			}
		});

		mButtonHelpScreen.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				Intent helpGame = new Intent(Menu.this, Help.class);

				startActivity(helpGame);
			}
		});

		mButtonHighScores.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{				
				Intent gameScores = new Intent(Menu.this, HighScores.class);

				startActivity(gameScores);
			}
		});
	}

	public void newCloudAnimation()
	{
		Random r = new Random(); // create a random number generator

		int sizeX       = 100 + r.nextInt(300); // pick a random size for the cloud
		int sizeY       = 200 - r.nextInt(150);

		final ImageView cloudView = new ImageView(this); // create an image view to represent the cloud
		cloudView.setLayoutParams( new RelativeLayout.LayoutParams(sizeX, sizeY)); // set its size
		cloudView.setImageResource(R.drawable.rock);//set its image
		mMainLayout.addView(cloudView, 1); // add at 1 so just above background very back

		// get screen size so we know how far to move our clouds
		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		GameThread.ScreenSize = new Vector(display.getWidth(),display.getHeight());

		int time            = 30000 - r.nextInt(20000); // get a random time between 10 and 30 seconds 
		int randomYPosition = r.nextInt(display.getHeight()); // get a random y position on screen

		//create a new animation 
		Animation animationcloud = new TranslateAnimation(-sizeX, display.getWidth()+sizeX, // move from offscreen left (- size of image) to offscreen right (screen size + image size)
															randomYPosition, randomYPosition); // keep the same y position
		animationcloud.setDuration(time);

		cloudView.setAnimation(animationcloud); // set the image views animation
		animationcloud.start(); // start the animation

		animationcloud.setAnimationListener(new AnimationListener()
		{
			ImageView tempImage = cloudView; // keep a reference to the cloud
			@Override
			public void onAnimationStart(Animation animation)
			{
				// do nothing

			}

			@Override
			public void onAnimationRepeat(Animation animation)
			{
				// do nothing
			}

			@Override
			public void onAnimationEnd(Animation animation)
			{
				mMainLayout.removeView(tempImage); // remove the cloud this animation is attached to
				newCloudAnimation(); // add a new cloud
			}
		});
	}

	public void animateMenuButtons(){
		// this function just animates the menu buttons left to right to simulate wind

		// animations to simulate wind
		Animation animation = new TranslateAnimation(-20, 20,0, 0);
		animation.setDuration(3000);
		animation.setRepeatCount(Animation.INFINITE);
		animation.setRepeatMode(Animation.REVERSE);
		mButtonStartGame.setAnimation(animation);
		animation.start();

		// animations to simulate wind
		Animation animation2 = new TranslateAnimation(-10,10,0, 0);
		animation2.setDuration(3500);
		animation2.setRepeatCount(Animation.INFINITE);
		animation2.setRepeatMode(Animation.REVERSE);
		mButtonHelpScreen.setAnimation(animation2);
		animation2.start();

		// animations to simulate wind
		Animation animation3 = new TranslateAnimation(-20, 20,0, 0);
		animation3.setDuration(4000);
		animation3.setRepeatCount(Animation.INFINITE);
		animation3.setRepeatMode(Animation.REVERSE);
		mButtonHighScores.setAnimation(animation3);
		animation3.start();
	}

}

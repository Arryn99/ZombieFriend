package com.ZombieFriends.Menu.Activities;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.ZombieFriends.R;
import com.ZombieFriends.ZombieApplication;
import com.ZombieFriends.ServerRequests.FacebookFriend;
import com.ZombieFriends.ServerRequests.Facebook.FacebookActivity;

public class Login extends FacebookActivity
{

	private static final String TAG = "FacebookActivity";
	ImageButton Login;
	ProgressDialog mDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		Login = (ImageButton)findViewById(R.id.Login);
		Login.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				//login to facebook
				Login();		
			}
		});
	}

	@Override
	public void signInSuccess()
	{
		Log.d(TAG, "signInSuccess");
		// TODO Auto-generated method stub
		super.signInSuccess();
		// download all of the friend images
		//getAllFriends();
		// show a loading dialog when while we fetch all of the users friends
		mDialog = ProgressDialog.show(this, "", "Please wait while we infect your friends...", true);
	}

	@Override
	public void signInFailed(Exception exception)
	{
		Log.d(TAG, "signInFailed");
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.login_failed_dialog_title)
		.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});
		// Create the AlertDialog object and return it
		builder.create().show();
	}

	@Override
	public void gotAllFriends(FacebookFriend[] friendsArray)
	{
		Log.d(TAG, "gotAllFriends");
		super.gotAllFriends(friendsArray);
		ZombieApplication.getApplication(this).getImageManager().setFriends(friendsArray);
		//dismiss loading dialog
		mDialog.dismiss();
		// move on to the next screen
		Intent beginMenu = new Intent(this, Menu.class);
		startActivity(beginMenu);
	}

	@Override
	public void gotAllFriendsFailed(String error)
	{
		Log.d(TAG, "gotAllFriendsFailed");
		// TODO Auto-generated method stub
		super.gotAllFriendsFailed(error);
		//dismiss loading dialog
		mDialog.dismiss();
		//failed to get all your friends
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.login_failed_dialog_title)
		.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
	//			finish();
			}
		});
	}

	@Override
	public void finish()
	{
		// TODO Auto-generated method stub
		//super.finish();
	}
}

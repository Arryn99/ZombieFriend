package com.ZombieFriends.Menu.Activities;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
	/**
	 * login buton
	 */
	ImageButton Login;
	/**
	 * dialog that is shown when the user is downloading friends
	 */
	ProgressDialog mDialog;
	
	/* (non-Javadoc)
	 * @see com.ZombieFriends.ServerRequests.Facebook.FacebookActivity#onCreate(android.os.Bundle)
	 */
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
	/* (non-Javadoc)
	 * @see com.ZombieFriends.ServerRequests.Facebook.FacebookActivity#signInSuccess()
	 */
	@Override
	public void signInSuccess() {
		// TODO Auto-generated method stub
		super.signInSuccess();
		getProfileID();
		mDialog = ProgressDialog.show(this, "", "Loading. Please wait...", true);
	}
	
	/* (non-Javadoc)
	 * @see com.ZombieFriends.ServerRequests.Facebook.FacebookActivity#gotAllFriends(com.ZombieFriends.ServerRequests.FacebookFriend[])
	 */
	@Override
	public void gotAllFriends(FacebookFriend[] friendsArray) {
		// TODO Auto-generated method stub
		super.gotAllFriends(friendsArray);
		//save a reference of all of our friends
		ZombieApplication.getApplication(this).getImageManager().setFriends(friendsArray);
		mDialog.dismiss();
		//load main menu
		Intent loadMainMenu = new Intent(this, Menu.class);
		startActivity(loadMainMenu);
	}
	
	/* (non-Javadoc)
	 * @see com.ZombieFriends.ServerRequests.Facebook.FacebookActivity#gotAllFriendsFailed(java.lang.String)
	 */
	@Override
	public void gotAllFriendsFailed(String error) {
		// TODO Auto-generated method stub
		super.gotAllFriendsFailed(error);
		mDialog.dismiss();
	}
	
	/* (non-Javadoc)
	 * @see com.ZombieFriends.ServerRequests.Facebook.FacebookActivity#signInFailed(java.lang.Exception)
	 */
	@Override
	public void signInFailed(Exception exception) {
		// TODO Auto-generated method stub
		super.signInFailed(exception);
		finish();
	}
	
	/* (non-Javadoc)
	 * @see com.ZombieFriends.ServerRequests.Facebook.FacebookActivity#gotProfileID(java.lang.String)
	 */
	@Override
	public void gotProfileID(String ID) {
		// TODO Auto-generated method stub
		super.gotProfileID(ID);
		getAllFriends();
		ZombieApplication.getApplication(this).getImageManager().setProfileID(ID);
	}
	
}

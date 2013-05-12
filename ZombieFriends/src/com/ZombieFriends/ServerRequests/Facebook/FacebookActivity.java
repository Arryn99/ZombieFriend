package com.ZombieFriends.ServerRequests.Facebook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.ZombieFriends.ServerRequests.FacebookFriend;

/**
 * A utility class that any activity that logs into Facebook must implement.
 * @author Steph
 */
public class FacebookActivity extends Activity implements FacebookInterface
{
	/**
	 * maintains a connection to facebook
	 */
	FacebookConnection mFacebookConnection;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		mFacebookConnection= new FacebookConnection(this,savedInstanceState);
		mFacebookConnection.mFacebookInterface = this;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		mFacebookConnection.onActivityResult(this, requestCode, resultCode, data);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onRestoreInstanceState(android.os.Bundle)
	 */
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mFacebookConnection.onRestoreInstanceState(savedInstanceState);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mFacebookConnection.onSaveInstanceState(outState);
	}
	
	/**
	 * perform a login to facebook
	 */
	public void Login(){
		mFacebookConnection.signIn(this);
	}
	
	/**
	 * retrieve the complete list of all facebook friends
	 */
	public void getAllFriends(){
		mFacebookConnection.getFriends();
	}
	
	/**
	 * retrieves the facebook users profile ID
	 */
	public void getProfileID(){
		mFacebookConnection.getloggedInUsersID();
	}

	/* (non-Javadoc)
	 * @see com.ZombieFriends.ServerRequests.Facebook.FacebookInterface#signInSuccess()
	 */
	@Override
	public void signInSuccess()
	{
		
	}

	/* (non-Javadoc)
	 * @see com.ZombieFriends.ServerRequests.Facebook.FacebookInterface#signInFailed(java.lang.Exception)
	 */
	@Override
	public void signInFailed(Exception exception)
	{
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.ZombieFriends.ServerRequests.Facebook.FacebookInterface#gotAllFriends(com.ZombieFriends.ServerRequests.FacebookFriend[])
	 */
	@Override
	public void gotAllFriends(FacebookFriend[] friendsArray)
	{
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.ZombieFriends.ServerRequests.Facebook.FacebookInterface#gotAllFriendsFailed(java.lang.String)
	 */
	@Override
	public void gotAllFriendsFailed(String error)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gotProfileID(String ID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gotProfileIDFailed(String string) {
		// TODO Auto-generated method stub
		
	}
	
}

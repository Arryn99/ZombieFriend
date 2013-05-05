package com.ZombieFriends.ServerRequests.Facebook;

import com.ZombieFriends.ServerRequests.FacebookFriend;

public interface FacebookInterface
{
	/**
	 * called when the user has logged in successfully
	 */
	public void signInSuccess();
	/**
	 * called when the user has failed to login
	 * @param exception the reason why the login has failed
	 */
	public void signInFailed(Exception exception);
	
	/**
	 * retruns a list of all of the users facebook friends
	 * @param friendsArray
	 */
	public void gotAllFriends(FacebookFriend[] friendsArray);
	/**
	 * Called when the request to fetch all friends fails
	 * @param error a string representing the reason the request failed
	 */
	public void gotAllFriendsFailed(String error);
}

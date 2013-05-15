package com.ZombieFriends.ServerRequests.ImageDownloading;

import java.util.Random;

import android.content.Context;

import com.ImageHandler.utils.ImageLoading.Downloading.IDownloadBitmapListener;
import com.ZombieFriends.ServerRequests.FacebookFriend;

/**
 * Class that manages all of the image retrival within out application
 * 
 * @author Steph
 *
 */
public class ImageManager
{
	/**
	 * a complete list of all of our facebook friends
	 */
	FacebookFriend[] friends = null;
	String           ID;

	public void setFriends(FacebookFriend[] friends)
	{
		this.friends = friends;
	}
	
	public void getRandomFriendImage(Context context, IDownloadBitmapListener listener ){
		Random r = new Random();
		int index = r.nextInt(friends.length-1);
		friends[index].downloadAndrespondTo(context, listener);
	}

	public void getProfileImage(Context context, IDownloadBitmapListener listener) {
			DownloadFacebookProfileImage image = new DownloadFacebookProfileImage(ID);
			image.downloadAndrespondTo(context, listener);
	}

	public void setProfileID(String iD) {
		ID =iD;
	}
	

}

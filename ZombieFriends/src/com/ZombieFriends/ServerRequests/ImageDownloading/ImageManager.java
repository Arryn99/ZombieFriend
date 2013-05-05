package com.ZombieFriends.ServerRequests.ImageDownloading;

import java.util.Random;

import android.content.Context;

import com.ZombieFriends.ServerRequests.FacebookFriend;
import com.rockspin.utils.ImageLoading.Downloading.IDownloadBitmapListener;

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

	public void setFriends(FacebookFriend[] friends)
	{
		this.friends = friends;
	}
	
	public void getRandomFriendImage(Context context, IDownloadBitmapListener listener ){
		Random r = new Random();
		int index = r.nextInt(friends.length-1);
		friends[index].downloadAndrespondTo(context, listener);
	}
	

}

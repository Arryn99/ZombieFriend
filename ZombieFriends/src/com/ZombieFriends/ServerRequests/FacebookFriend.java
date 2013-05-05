package com.ZombieFriends.ServerRequests;

import android.content.Context;
import android.graphics.Bitmap;

import com.ZombieFriends.ServerRequests.ImageDownloading.DownloadFacebookProfileImage;
import com.rockspin.utils.ImageLoading.Downloading.IDownloadBitmapListener;
import com.rockspin.utils.ImageLoading.Downloading.ImageToCache;

/**
 * A POJO that holds details about all of my facebook friends
 * @author Steph
 *
 */
public class FacebookFriend{ 
	String name;
	String id;
	String url;
	/**
	 * whether the image for this contact has finished downloading
	 */
	boolean loaded;
	
	/**
	 * A POJO that holds details about all of my facebook friends
	 * @param name name of facebook friend
	 * @param id   the face book id of this person
	 * @param url  the url of the persons profile picture
	 */
	public FacebookFriend(String name, String id, String url)
	{
		super();
		this.name = name;
		this.id = id;
		this.url = url;
	}
	
	public String getName()
	{
		return name;
	}

	public String getId()
	{
		return id;
	}

	public String getUrl()
	{
		return url;
	}

	public void downloadAndrespondTo(Context context, IDownloadBitmapListener listener ) 
	{
		DownloadFacebookProfileImage imageToDownload = new DownloadFacebookProfileImage(this);
		imageToDownload.downloadAndrespondTo(context, listener);
		
	}
	
}

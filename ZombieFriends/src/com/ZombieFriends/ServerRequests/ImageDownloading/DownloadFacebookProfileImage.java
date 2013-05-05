package com.ZombieFriends.ServerRequests.ImageDownloading;

import android.content.Context;

import com.ZombieFriends.ServerRequests.FacebookFriend;
import com.rockspin.utils.ImageLoading.Downloading.FromWeb.ImageToDownload;


public class DownloadFacebookProfileImage extends ImageToDownload{

	public DownloadFacebookProfileImage(FacebookFriend friend) {		
		super("https://graph.facebook.com/" + friend.getId() + "/picture?type=large", friend.getId());
	}

}

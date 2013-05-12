package com.ZombieFriends.ServerRequests.ImageDownloading;

import com.ZombieFriends.ServerRequests.FacebookFriend;
import com.rockspin.utils.ImageLoading.Downloading.FromWeb.ImageToDownload;


public class DownloadFacebookProfileImage extends ImageToDownload{

	public DownloadFacebookProfileImage(FacebookFriend friend) {		
		super("https://graph.facebook.com/" + friend.getId() + "/picture?type=normal", friend.getId());
	}
	public DownloadFacebookProfileImage(String ID) {		
		super("https://graph.facebook.com/" + ID + "/picture?type=normal", ID);
	}

}

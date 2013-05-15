package com.ImageHandler.utils.ImageLoading.Downloading.FromWeb;

import android.content.Context;
import android.widget.ImageView;

import com.ImageHandler.utils.ImageLoading.Downloading.IDownloadBitmapListener;
import com.ImageHandler.utils.ImageLoading.Downloading.ImageToCache;


public class ImageToDownload extends ImageToCache {
	String mUrl;
	String mKey;
	

	public ImageToDownload(String Url, String uniqueKey) {
		this.mUrl = Url;
		mKey      = uniqueKey;
	}
	
	public String getUrl() {
		return mUrl;
	}
	
	@Override
	public String getKey() {
		// TODO Auto-generated method stub
		return mKey;
	}

	@Override
	public void downloadAsync(ImageView pView) {
		ImageDownloader d = new ImageDownloader(pView.getContext());
		d.loadImage(this, pView);
	}
	
	public void downloadAndrespondTo(Context context, IDownloadBitmapListener listener ) {
		ImageDownloader d = new ImageDownloader(context);
		d.loadImage( this , listener );
	}

}

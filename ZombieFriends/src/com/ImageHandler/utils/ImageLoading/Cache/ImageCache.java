/*
 * Copyright (C) 2012 The Android Open Source Project
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ImageHandler.utils.ImageLoading.Cache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StatFs;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.ImageHandler.utils.Utils;
import com.ImageHandler.utils.ImageLoading.BackwardsCompatibility.DiskLruCache;

/**
 * This class holds our bitmap caches (memory and disk).
 */
public class ImageCache {
	/**
	 * A simple non-UI Fragment that stores a single Object and is retained over
	 * configuration changes. It will be used to retain the ImageCache object.
	 */
	public static class RetainFragment extends Fragment {
		private Object	mObject;

		/**
		 * Empty constructor as per the Fragment documentation
		 */
		public RetainFragment() {}

		/**
		 * Get the stored object.
		 * 
		 * @return The stored object
		 */
		public Object getObject() {
			return mObject;
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			// Make sure this Fragment is retained over a configuration change
			setRetainInstance(true);
		}

		/**
		 * Store a single object in this Fragment.
		 * 
		 * @param object
		 *            The object to store
		 */
		public void setObject(Object object) {
			mObject = object;
		}
	}

	private static final String	TAG					= "ImageCache";
	private static final int	DISK_CACHE_INDEX	= 0;

	private static final String	IMAGE_CACHE_DIR		= "ContactsImages";

	private static String bytesToHexString(byte[] bytes) {
		// http://stackoverflow.com/questions/332079
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			final String hex = Integer.toHexString(0xFF & bytes[i]);
			if (hex.length() == 1) {
				sb.append('0');
			}
			sb.append(hex);
		}
		return sb.toString();
	}

	/**
	 * Find and return an existing ImageCache stored in a {@link RetainFragment} , if not found a new one is created using the supplied params and saved
	 * to a {@link RetainFragment}.
	 * 
	 * @param fragmentManager
	 *            The fragment manager to use when dealing with the retained
	 *            fragment.
	 * @param cacheParams
	 *            The cache parameters to use if creating the ImageCache
	 * @return An existing retained ImageCache object or a new one if one did
	 *         not exist
	 */
	public static ImageCache findOrCreateCache(FragmentManager fragmentManager, ImageCacheParams cacheParams) {

		// Search for, or create an instance of the non-UI RetainFragment
		final RetainFragment mRetainFragment = ImageCache.findOrCreateRetainFragment(fragmentManager);

		// See if we already have an ImageCache stored in RetainFragment
		final ImageCache imageCache = (ImageCache) mRetainFragment.getObject();

		// No existing ImageCache, create one and store it in RetainFragment
		if (imageCache == null) {
			// imageCache = new ImageCache(cacheParams);
			// mRetainFragment.setObject(imageCache);
		}

		return imageCache;
	}

	/**
	 * Locate an existing instance of this Fragment or if not found, create and
	 * add it using FragmentManager.
	 * 
	 * @param fm
	 *            The FragmentManager manager to use.
	 * @return The existing instance of the Fragment or the new instance if just
	 *         created.
	 */
	public static RetainFragment findOrCreateRetainFragment(FragmentManager fm) {
		// Check to see if we have retained the worker fragment.
		RetainFragment mRetainFragment = (RetainFragment) fm.findFragmentByTag(ImageCache.TAG);

		// If not retained (or first time running), we need to create and add
		// it.
		if (mRetainFragment == null) {
			mRetainFragment = new RetainFragment();
			fm.beginTransaction().add(mRetainFragment, ImageCache.TAG).commitAllowingStateLoss();
		}

		return mRetainFragment;
	}

	/**
	 * Get the size in bytes of a bitmap.
	 * 
	 * @param bitmap
	 * @return size in bytes
	 */
	@TargetApi(12)
	public static int getBitmapSize(Bitmap bitmap) {
		if (Utils.hasHoneycombMR1()) {
			return bitmap.getByteCount();
		}
		// Pre HC-MR1
		return bitmap.getRowBytes() * bitmap.getHeight();
	}

	/**
	 * Check how much usable space is available at a given path.
	 * 
	 * @param path
	 *            The path to check
	 * @return The space available in bytes
	 */
	@TargetApi(9)
	public static long getUsableSpace(File path) {
		if (Utils.hasGingerbread()) {
			return path.getUsableSpace();
		}
		final StatFs stats = new StatFs(path.getPath());
		return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
	}

	/**
	 * A hashing method that changes a string (like a URL) into a hash suitable
	 * for using as a disk filename.
	 */
	public static String hashKeyForDisk(String key) {
		String cacheKey;
		try {
			final MessageDigest mDigest = MessageDigest.getInstance("MD5");
			mDigest.update(key.getBytes());
			cacheKey = ImageCache.bytesToHexString(mDigest.digest());
		} catch (final NoSuchAlgorithmException e) {
			cacheKey = String.valueOf(key.hashCode());
		}
		return cacheKey;
	}

	private DiskLruCache				mDiskLruCache;

	private LruCache<String, Bitmap>	mMemoryCache;

	private ImageCacheParams			mCacheParams;

	private final Object				mDiskCacheLock		= new Object();

	private boolean						mDiskCacheStarting	= true;

	/**
	 * Creating a new ImageCache object using the specified parameters.
	 * 
	 * @param cacheParams
	 *            The cache parameters to use to initialize the cache
	 */
	public ImageCache(Context pContext) {
		final ImageCacheParams cacheParams = new ImageCacheParams(pContext, ImageCache.IMAGE_CACHE_DIR);
		cacheParams.setMemCacheSizePercent(0.1f); // Set memory cache to 10% of app memory approx 4 mb
		cacheParams.diskCacheEnabled = true;	// load contacts from disc
		cacheParams.memoryCacheEnabled = false;
		init(cacheParams);
	}

	/**
	 * Creating a new ImageCache object using the default parameters.
	 * 
	 * @param context
	 *            The context to use
	 * @param uniqueName
	 *            A unique name that will be appended to the cache directory
	 */
	public ImageCache(Context context, String uniqueName) {
		init(new ImageCacheParams(context, uniqueName));
	}

	/**
	 * Adds a bitmap to both memory and disk cache.
	 * 
	 * @param data
	 *            Unique identifier for the bitmap to store
	 * @param bitmap
	 *            The bitmap to store
	 */
	public void addBitmapToCache(String data, Bitmap bitmap) {
		if (data == null || bitmap == null) {
			return;
		}

		// Add to memory cache
		if (mMemoryCache != null && mMemoryCache.get(data) == null) {
			mMemoryCache.put(data, bitmap);
		}

		synchronized (mDiskCacheLock) {
			// Add to disk cache
			if (mDiskLruCache != null) {
				final String key = data;
				OutputStream out = null;
				try {
					final DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
					if (snapshot == null) {
						final DiskLruCache.Editor editor = mDiskLruCache.edit(key);
						if (editor != null) {
							out = editor.newOutputStream(ImageCache.DISK_CACHE_INDEX);
							bitmap.compress(mCacheParams.compressFormat, mCacheParams.compressQuality, out);
							editor.commit();
							out.close();
						}
					} else {
						snapshot.getInputStream(ImageCache.DISK_CACHE_INDEX).close();
					}
				} catch (final IOException e) {
					Log.e(ImageCache.TAG, "addBitmapToCache - " + e);
				} catch (final Exception e) {
					Log.e(ImageCache.TAG, "addBitmapToCache - " + e);
				} finally {
					try {
						if (out != null) {
							out.close();
						}
					} catch (final IOException e) {}
				}
			}
		}
	}

	/**
	 * Clears both the memory and disk cache associated with this ImageCache
	 * object. Note that this includes disk access so this should not be
	 * executed on the main/UI thread.
	 */
	public void clearCache() {
		if (mMemoryCache != null) {
			mMemoryCache.evictAll();
		}

		synchronized (mDiskCacheLock) {
			mDiskCacheStarting = true;
			if (mDiskLruCache != null && !mDiskLruCache.isClosed()) {
				try {
					mDiskLruCache.delete();
				} catch (final IOException e) {
					Log.e(ImageCache.TAG, "clearCache - " + e);
				}
				mDiskLruCache = null;
				initDiskCache();
			}
		}
	}

	/**
	 * Closes the disk cache associated with this ImageCache object. Note that
	 * this includes disk access so this should not be executed on the main/UI
	 * thread.
	 */
	public void close() {
		synchronized (mDiskCacheLock) {
			if (mDiskLruCache != null) {
				try {
					if (!mDiskLruCache.isClosed()) {
						mDiskLruCache.close();
						mDiskLruCache = null;
					}
				} catch (final IOException e) {
					Log.e(ImageCache.TAG, "close - " + e);
				}
			}
		}
	}

	/**
	 * Flushes the disk cache associated with this ImageCache object. Note that
	 * this includes disk access so this should not be executed on the main/UI
	 * thread.
	 */
	public void flush() {
		synchronized (mDiskCacheLock) {
			if (mDiskLruCache != null) {
				try {
					mDiskLruCache.flush();
				} catch (final IOException e) {
					Log.e(ImageCache.TAG, "flush - " + e);
				}
			}
		}
	}

	/**
	 * Get from disk cache.
	 * 
	 * @param data
	 *            Unique identifier for which item to get
	 * @return The bitmap if found in cache, null otherwise
	 */
	public Bitmap getBitmapFromDiskCache(String data) {
		final String key = data;
		synchronized (mDiskCacheLock) {
			while (mDiskCacheStarting) {
				try {
					mDiskCacheLock.wait();
				} catch (final InterruptedException e) {}
			}
			if (mDiskLruCache != null) {
				InputStream inputStream = null;
				try {
					final DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
					if (snapshot != null) {
						inputStream = snapshot.getInputStream(ImageCache.DISK_CACHE_INDEX);
						if (inputStream != null) {
							final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
							return bitmap;
						}
					}
				} catch (final IOException e) {
					Log.e(ImageCache.TAG, "getBitmapFromDiskCache - " + e);
				} finally {
					try {
						if (inputStream != null) {
							inputStream.close();
						}
					} catch (final IOException e) {}
				}
			}
			return null;
		}
	}

	/**
	 * Get from memory cache.
	 * 
	 * @param data
	 *            Unique identifier for which item to get
	 * @return The bitmap if found in cache, null otherwise
	 */
	public Bitmap getBitmapFromMemCache(String data) {
		if (mMemoryCache != null) {
			final Bitmap memBitmap = mMemoryCache.get(data);
			if (memBitmap != null) {
				return memBitmap;
			}
		}
		return null;
	}

	/**
	 * Initialize the cache, providing all parameters.
	 * 
	 * @param cacheParams
	 *            The cache parameters to initialize the cache
	 */
	private void init(ImageCacheParams cacheParams) {
		mCacheParams = cacheParams;

		// Set up memory cache
		if (mCacheParams.memoryCacheEnabled) {
			mMemoryCache = new LruCache<String, Bitmap>(mCacheParams.memCacheSize) {
				/**
				 * Measure item size in kilobytes rather than units which is
				 * more practical for a bitmap cache
				 */
				@Override
				protected int sizeOf(String key, Bitmap bitmap) {
					final int bitmapSize = ImageCache.getBitmapSize(bitmap) / 1024;
					return bitmapSize == 0 ? 1 : bitmapSize;
				}
			};
		}

		// By default the disk cache is not initialized here as it should be
		// initialized
		// on a separate thread due to disk access.
		if (cacheParams.initDiskCacheOnCreate) {
			// Set up disk cache
			initDiskCache();
		}
	}

	/**
	 * Initializes the disk cache. Note that this includes disk access so this
	 * should not be executed on the main/UI thread. By default an ImageCache
	 * does not initialize the disk cache when it is created, instead you should
	 * call initDiskCache() to initialize it on a background thread.
	 */
	public void initDiskCache() {
		// Set up disk cache
		synchronized (mDiskCacheLock) {
			if (mDiskLruCache == null || mDiskLruCache.isClosed()) {
				final File diskCacheDir = mCacheParams.diskCacheDir;
				if (mCacheParams.diskCacheEnabled && diskCacheDir != null) {
					if (!diskCacheDir.exists()) {
						diskCacheDir.mkdirs();
					}
					if (ImageCache.getUsableSpace(diskCacheDir) > mCacheParams.diskCacheSize) {
						try {
							mDiskLruCache = DiskLruCache.open(diskCacheDir, 1, 1, mCacheParams.diskCacheSize);
						} catch (final IOException e) {
							mCacheParams.diskCacheDir = null;
							Log.e(ImageCache.TAG, "initDiskCache - " + e);
						}
					}
				}
			}
			mDiskCacheStarting = false;
			mDiskCacheLock.notifyAll();
		}
	}

	/**
	 * Deletes an key from the disc and image cache.
	 * 
	 * @param pKey the key you want to delete form the cache. Does a check to remove invalid file name characters from string
	 */
	public void deleteFromCache(final String pKey) {
		// make sure that the key is a valid file name
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					if(mDiskLruCache != null && pKey != null) {
						mDiskLruCache.remove(pKey.replaceAll("\\W", "").replaceAll("\\s", ""));	
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
		mMemoryCache.remove(pKey.replaceAll("\\W", "").replaceAll("\\s", ""));
	}


}

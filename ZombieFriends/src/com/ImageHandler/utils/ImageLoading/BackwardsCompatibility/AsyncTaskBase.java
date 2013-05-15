package com.ImageHandler.utils.ImageLoading.BackwardsCompatibility;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.ImageHandler.utils.Utils;

public class AsyncTaskBase<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

	/*
	 * (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Result doInBackground(Params... pParams) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressLint("NewApi")
	public final AsyncTask<Params, Progress, Result> executeOnExecutorBase(Params... params) throws Exception {

		if (Utils.hasHoneycomb()) {
			return executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
		} else {
			return execute(params);
		}

	}

}
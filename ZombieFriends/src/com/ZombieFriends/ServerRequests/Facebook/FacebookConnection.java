package com.ZombieFriends.ServerRequests.Facebook;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.ZombieFriends.R;
import com.ZombieFriends.ServerRequests.FacebookFriend;
import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.facebook.model.GraphObject;

/**
 * Manages signing in to facebook and requesting a connection
 *
 * @author Steph
 *
 */
class FacebookConnection
{
	/**
	 * A key that is used to store and restore the state of any pending requests
	 */
	static final String PENDING_REQUEST_BUNDLE_KEY = "com.ZombieFriends.Facebook:PendingRequest";
	/**
	 * a unique ID assigned by facebook which represents the ZombieFriends application
	 */
	static final String applicationId = "272006619601451";
	/**
	 * if a loging request is currently being processes
	 */
	boolean pendingRequest = false;
	/**
	 * class that is notified by the facebook interface it is a weak
	 */
	FacebookInterface mFacebookInterface;
	Session session;

	FacebookConnection(Activity activity , Bundle savedInstanceState)
	{
		super();
		session =  createSession(activity);
	}

	/**
	 * creates a session with facebook
	 */
	void signIn(final Activity activity){
		//check if we have previously logged in
		if (session.isOpened()) {
			mFacebookInterface.signInSuccess();
		} else {
			StatusCallback callback = new StatusCallback() {
				public void call(Session session, SessionState state, Exception exception) {
					if (exception != null) {
						FacebookConnection.this.session = createSession(activity);
					}
					mFacebookInterface.signInSuccess();
				}
			};
			pendingRequest = true;
			this.session.openForRead(new Session.OpenRequest(activity).setCallback(callback));
		}
	}

	/**
	 * sends a graph request to facebook looking for a list of your friends
	 */
	void getFriends(){
		String requestIdsText = "/me/friends";

		Request r = new Request(session, requestIdsText, null, null, new Request.Callback() {
			public void onCompleted(Response response) {
				GraphObject graphObject = response.getGraphObject();
				FacebookRequestError error = response.getError();
				FacebookFriend friends[] = null;
				String errorString = "";
				if (graphObject != null) {
					JSONObject object = graphObject.getInnerJSONObject();
					try
					{
						JSONArray array = object.getJSONArray("data");
						friends = new FacebookFriend[array.length()]; 
						for (int i=0; i<array.length(); i++)
						{

							JSONObject friendJSON = array.getJSONObject(i);
							String name = friendJSON.getString("name");
							String id = friendJSON.getString("id");

							//gets the URL from which to get friends
							FacebookFriend friend = new FacebookFriend(name, id, "https://graph.facebook.com/" +
									id + "/picture?type=small");
							friends[i] = friend;

							Log.d("Graph API sample activity", name + (" ") + id);
						}
					} catch (JSONException e)
					{
						e.printStackTrace();
						mFacebookInterface.gotAllFriendsFailed("json error : " + e.getLocalizedMessage());
						return;
					}
					graphObject.asMap();
					mFacebookInterface.gotAllFriends(friends);
				} else if (error != null) {
					errorString = errorString + String.format("Error: %s", error.getErrorMessage());
					mFacebookInterface.gotAllFriendsFailed(errorString);
				}

			}
		});
		pendingRequest = false;
		r.executeAsync();
	}

	void getloggedInUsersID(){
		String requestIdsText = "/me";
		Request r = new Request(session, requestIdsText, null, null, new Request.Callback() {
			String id;
			public void onCompleted(Response response) {
				GraphObject graphObject = response.getGraphObject();
				FacebookRequestError error = response.getError();
				String errorString = "";
				if (graphObject != null) {
					JSONObject object = graphObject.getInnerJSONObject();
					try
					{
						String name = object.getString("name");
						id = object.getString("id");
						Log.d("Graph API sample activity", name + (" ") + id);
				} catch (JSONException e)
				{
					e.printStackTrace();
					mFacebookInterface.gotProfileIDFailed("json error : " + e.getLocalizedMessage());
					return;
				}
				mFacebookInterface.gotProfileID(id);
			} else if (error != null) {
				errorString = errorString + String.format("Error: %s", error.getErrorMessage());
				mFacebookInterface.gotProfileIDFailed(errorString);
			}

		}
	});
		pendingRequest = false;
		r.executeAsync();
} 

/**
 * creates a facebook session object if none exists already
 * @param context A android context
 * @return a valid session
 */
private Session createSession(Context context) {
	Session activeSession = Session.getActiveSession();
	if (activeSession == null || activeSession.getState().isClosed()) {
		activeSession = new Session.Builder(context).setApplicationId(applicationId).build();
		Session.setActiveSession(activeSession);
	}
	return activeSession;
}

//************************************** activity call backs *************************************//
/**
 * Call this function from onActivityResult in an activity to manage logins
 * @param activity
 * @param requestCode
 * @param resultCode
 * @param data
 */
void onActivityResult(Activity activity ,int requestCode, int resultCode, Intent data) {
	if (Session.getActiveSession().onActivityResult(activity, requestCode, resultCode, data) && pendingRequest && session.getState().isOpened()) {
		mFacebookInterface.signInSuccess();
	}
}

/**
 * call this function form onRestoreInstanceState to restore any pending requests
 * @param savedInstanceState
 */
void onRestoreInstanceState(Bundle savedInstanceState) {
	pendingRequest = savedInstanceState.getBoolean(PENDING_REQUEST_BUNDLE_KEY, pendingRequest);
}

/**
 * Called when a activity is recreated.
 * @param outState
 */
void onSaveInstanceState(Bundle outState) {
	outState.putBoolean(PENDING_REQUEST_BUNDLE_KEY, pendingRequest);
}

//************************************** /activity call backs *************************************//
}

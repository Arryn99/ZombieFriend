package com.ZombieFriends.Menu.Activities;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.ZombieFriends.R;
import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;

public class Login extends Activity
{
	 static final String PENDING_REQUEST_BUNDLE_KEY = "com.facebook.a.graphapi:PendingRequest";
	  static final String applicationId = "272006619601451";
	private static final String TAG = "FacebookActivity";
	ImageButton Login;
	ProgressDialog mDialog;

	Session session;
	boolean pendingRequest;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		Login = (ImageButton)findViewById(R.id.Login);
		Login.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				//login to facebook
				onClickRequest();	
			}
		});
		this.session = createSession();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (this.session.onActivityResult(this, requestCode, resultCode, data) && pendingRequest && this.session.getState().isOpened()) {
            sendRequests();
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        pendingRequest = savedInstanceState.getBoolean(PENDING_REQUEST_BUNDLE_KEY, pendingRequest);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(PENDING_REQUEST_BUNDLE_KEY, pendingRequest);
    }

    private void onClickRequest() {
        if (this.session.isOpened()) {
            sendRequests();
        } else {
            StatusCallback callback = new StatusCallback() {
                public void call(Session session, SessionState state, Exception exception) {
                    if (exception != null) {
                        new AlertDialog.Builder(Login.this)
                                .setTitle(R.string.login_failed_dialog_title)
                                .setMessage(exception.getMessage())
                                .setPositiveButton(R.string.ok_button, null)
                                .show();
                        Login.this.session = createSession();
                    }
                }
            };
            pendingRequest = true;
            this.session.openForRead(new Session.OpenRequest(this).setCallback(callback));
        }
    }

    private void sendRequests() {
   /*     textViewResults.setText("");

        String requestIdsText = editRequests.getText().toString();
        String[] requestIds = requestIdsText.split(",");

        List<Request> requests = new ArrayList<Request>();
        for (final String requestId : requestIds) {
            requests.add(new Request(session, requestId, null, null, new Request.Callback() {
                public void onCompleted(Response response) {
                    GraphObject graphObject = response.getGraphObject();
                    FacebookRequestError error = response.getError();
                    String s = textViewResults.getText().toString();
                    if (graphObject != null) {
                        if (graphObject.getProperty("id") != null) {
                            s = s + String.format("%s: %s\n", graphObject.getProperty("id"), graphObject.getProperty(
                                    "name"));
                        } else {
                            s = s + String.format("%s: <no such id>\n", requestId);
                        }
                    } else if (error != null) {
                        s = s + String.format("Error: %s", error.getErrorMessage());
                    }
                    textViewResults.setText(s);
                }
            }));
        }
        pendingRequest = false;
        Request.executeBatchAndWait(requests);*/
    }

    private Session createSession() {
        Session activeSession = Session.getActiveSession();
        if (activeSession == null || activeSession.getState().isClosed()) {
            activeSession = new Session.Builder(this).setApplicationId(applicationId).build();
            Session.setActiveSession(activeSession);
        }
        return activeSession;
    }
}

package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.SampleModel;
import com.codepath.apps.restclienttemplate.models.SampleModelDao;
import com.codepath.oauth.OAuthLoginActionBarActivity;

public class LoginActivity extends OAuthLoginActionBarActivity<TwitterClient> {

    private ConnectivityManager connectivityManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		// TODO - use connectivityManager to check if network is connected before attempting login
		connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

//		if ( connectedToNetwork(connectivityManager) ) {
		    Log.d("LoginActivity", "Detected internet connection");
            // do something
            final SampleModel sampleModel = new SampleModel();
            sampleModel.setName("CodePath");
            final SampleModelDao sampleModelDao = ((TwitterApp) getApplicationContext()).getMyDatabase().sampleModelDao();
            AsyncTask<SampleModel, Void, Void> task = new AsyncTask<SampleModel, Void, Void>() {
                @Override
                protected Void doInBackground(SampleModel... sampleModels) {
                    sampleModelDao.insertModel(sampleModels);
                    return null;
                }

                ;
            };
            task.execute(sampleModel);

//        } else Toast.makeText(this, "Error! Not connected to the internet", Toast.LENGTH_LONG).show();

	}



    // Inflate the menu; this adds items to the action bar if it is present.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	// OAuth authenticated successfully, launch primary authenticated activity
	// i.e Display application "homepage"
	@Override
	public void onLoginSuccess() {
        Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, TimelineActivity.class);
        startActivity(i);
	}

	// OAuth authentication flow failed, handle the error
	// i.e Display an error dialog or toast
	@Override
	public void onLoginFailure(Exception e) {
		e.printStackTrace();
	}

	// Click handler method for the button used to start OAuth flow
	// Uses the client to initiate OAuth authorization
	// This should be tied to a button used to login
	public void loginToRest(View view) {
		getClient().connect();
	}

//    // check if the device is connected to network before attempting a login
//    private boolean connectedToNetwork(ConnectivityManager connectivityManager) {
//        Log.d("LoginActivity", "Checking if network is connected");
//        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
//        return networkInfo != null && networkInfo.isConnected();
//    }

}

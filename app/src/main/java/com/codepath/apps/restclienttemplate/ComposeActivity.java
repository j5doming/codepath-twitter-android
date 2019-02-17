package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {

    private static final String TAG = ComposeActivity.class.getName();
    private static final int MAX_TWEET_LEN = 140;

    private TextInputEditText etCompose;
    private Button btnTweet;
    private TwitterClient twitterClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        twitterClient = TwitterApp.getRestClient(this);
        etCompose = findViewById(R.id.etCompose);
        btnTweet = findViewById(R.id.buttonTweet);

        // set click listener for the button
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweetContent = etCompose.getText().toString();
                // TODO: do better error handling (remove toasts eventually)
                if ( tweetContent.isEmpty() ) {

                    Toast.makeText(ComposeActivity.this,"Tweet cannot be empty", Toast.LENGTH_LONG).show();

                } else if ( tweetContent.length() > MAX_TWEET_LEN ) {

                    Toast.makeText(ComposeActivity.this, "Tweet is too long", Toast.LENGTH_LONG).show();

                } else {
                    twitterClient.composeTweet(tweetContent, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Log.d(TAG, "onSuccess(int statusCode, Header[] headers, JSONObject response):" +
                                    "successful tweet posted: " + response.toString());

                            try {
                                Tweet tweet = Tweet.fromJSON(response);
                                Intent data = new Intent();
                                data.putExtra("tweet", Parcels.wrap(tweet));
                                setResult(RESULT_OK, data);

                                // close this activity, passing data to parent activity
                                Log.d(TAG, "Returning to parent activity, tweet posted successfully");
                                finish();

                            } catch (JSONException e) {
                                Log.e(TAG, "Failed to to get tweet from JSON object");
                                e.printStackTrace();
                            }
                            super.onSuccess(statusCode, headers, response);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Log.e(TAG, "onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable): " +
                                    "Failed to post tweet: " + responseString, throwable);
                            super.onFailure(statusCode, headers, responseString, throwable);
                        }
                    });
                }
            }
        });
    }


}

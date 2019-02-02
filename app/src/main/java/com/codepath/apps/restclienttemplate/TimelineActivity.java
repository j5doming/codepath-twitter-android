package com.codepath.apps.restclienttemplate;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    private TwitterClient client;
    private RecyclerView rvTweets;
    private TweetAdapter adapter;
    private List<Tweet> tweets;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        client = TwitterApp.getRestClient(this);
        swipeContainer = findViewById(R.id.swipeContainer);
        // find recycler view
        rvTweets = findViewById(R.id.rvTweets);
        // init list of tweets and adapter
        tweets = new ArrayList<>();
        adapter = new TweetAdapter(this, tweets);
        // set up Recycler View
        rvTweets.setLayoutManager(new LinearLayoutManager(this));
        rvTweets.setAdapter(adapter);

        populateHomeTimeline();

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("TwitterClient", "Content is being refreshed");
                populateHomeTimeline();
            }
        });

        swipeContainer.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_red_light)
        );
    }

    // set up r view: set layout manager and adapater
    private void populateHomeTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("TwitterClient", response.toString());

                JSONObject jsonTweetObject;
                Tweet tweet;
                List<Tweet> tweetsToAdd = new ArrayList<>();
                // iterate through list of tweets
                for (int i = 0; i < response.length(); i++) {
                    try {
                        // convert each JSONObj to tweet obj
                        jsonTweetObject = response.getJSONObject(i);
                        tweet = Tweet.fromJSON(jsonTweetObject);
                        tweetsToAdd.add(tweet);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                adapter.clear();
                adapter.addTweets(tweetsToAdd);

                // stop showing refreshing icon
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                swipeContainer.setRefreshing(false);
                Log.e("TwitterClient", responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                swipeContainer.setRefreshing(false);
                Log.e("TwitterClient", errorResponse.toString());
            }
        });
    }





}

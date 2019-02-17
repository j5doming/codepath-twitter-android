package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    private static final String TAG = TimelineActivity.class.getName();
    private static final int REQUEST_CODE_COMPOSE_ACTIVITY = 666;

    private TwitterClient twitterClient;
    private RecyclerView rvTweets;
    private RelativeLayout itemTweet;
    private TweetAdapter adapter;
    private List<Tweet> tweets;

    private SwipeRefreshLayout swipeContainer;
    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        twitterClient = TwitterApp.getRestClient(this);
        swipeContainer = findViewById(R.id.swipeContainer);

        rvTweets = findViewById(R.id.rvTweets);

        // init list of tweets and adapter
        tweets = new ArrayList<>();
        adapter = new TweetAdapter(this, tweets);

        // set up Recycler View
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.setAdapter(adapter);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.d(TAG, "Going to load more data onLoadMore(int page, int totalItemsCount, RecyclerView view)");
                loadMoreData();
            }
        };

        rvTweets.addOnScrollListener(scrollListener);
        rvTweets.addItemDecoration(new DividerItemDecoration(rvTweets.getContext(), DividerItemDecoration.VERTICAL));

        populateHomeTimeline();

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "SwipeContainer refreshing content");
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // adds items to the action bar if it is present
        Log.d(TAG, "onCreateOptionsMenu(Menu menu): Creating options menu and inflating content");
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if ( item.getItemId() == R.id.compose ) {
            Log.d(TAG, "onOptionsItemSelection(MenuItem item): clicked on compose button, starting new intent");
            Intent i = new Intent(this, ComposeActivity.class);
            startActivityForResult(i, REQUEST_CODE_COMPOSE_ACTIVITY);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_COMPOSE_ACTIVITY && resultCode == RESULT_OK) {
            Log.d(TAG, "onActivityResult(int requestCode, int resultCode, Intent data): Successfully returned from ComposeActivity");
            // pull data from Intent data (pull the Tweet object) and manually place it into this activity
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));

            // update the recycler view to see the newly posted tweet
            tweets.add(0, tweet);
            adapter.notifyItemInserted(0);

            // scroll to the top TODO - do we want this? Possibly no
            rvTweets.smoothScrollToPosition(0);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // set up r view: set layout manager and adapter
    private void populateHomeTimeline() {
        twitterClient.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                Log.i(TAG, "Success populating home time line.");
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
                Log.e(TAG, "error populating home timeline with status code " + statusCode + " and response string \"" + responseString +"\"");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                swipeContainer.setRefreshing(false);
                Log.e(TAG, "error populating home timeline with status code " + statusCode, throwable);
            }
        });
    }

    // this is where android docs say to perform heavy-load shutdown operations such as save
    @Override
    protected void onStop() {
        super.onStop();
        // TODO - add database features to persist when offline
    }

    public void loadMoreData() {
        Log.d(TAG, "loadMoreData(): going to load more data");
        // 1. Send an API request to retrieve appropriate paginated data
        // 2. Deserialize and construct new model objects from the API response
        // 3. Append the new data objects to the existing set of items inside the array of items
        // 4. Notify the adapter of the new items made with `notifyItemRangeInserted()`
    }

}

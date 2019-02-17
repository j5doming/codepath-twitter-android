package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import com.codepath.apps.restclienttemplate.models.Tweet;

import com.loopj.android.http.JsonHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

import org.json.JSONObject;

import java.util.List;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.TweetViewHolder> {
    private static final String TAG = TweetAdapter.class.getName();

    private Context context;
    private List<Tweet> tweets;
    private TwitterClient twitterClient;

    public TweetAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
        twitterClient = TwitterApp.getRestClient(context);
    }

    class TweetViewHolder extends RecyclerView.ViewHolder {

        ImageView ivProfileImage;
        TextView tvScreenName;
        TextView tvBody;
        TextView tvName;
        TextView tvTime;
        ImageButton retweetButton;


        public TweetViewHolder(View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvName = itemView.findViewById(R.id.tvName);
            tvTime = itemView.findViewById(R.id.tvTime);
            retweetButton = itemView.findViewById(R.id.buttonRetweet);

        }
    }

    @NonNull
    @Override
    public TweetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate layout for a tweet for each tweet
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new TweetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final TweetViewHolder holder, int position) {
        // bind values based on position of the elements
        final Tweet tweet = tweets.get(position);

        holder.retweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( !tweet.isRetweeted() ) {
                    onClickRetweet(tweet, holder);
                } else {
                    onClickUnretweet(tweet, holder);
                }
            }
        });

        holder.tvName.setText(tweet.getUser().getName());
        holder.tvScreenName.setText(String.format("@%s", tweet.getUser().getScreenName()));
        holder.tvTime.setText(tweet.getCreatedAt());
        holder.tvBody.setText(tweet.getBody());
        Glide.with(context).load(tweet.getUser().getProfileImageURL())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.ivProfileImage);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }


    // clear current data from Recycler View
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // add a list of items
    public void addTweets(List<Tweet> tweets) {
        this.tweets.addAll(tweets);
        notifyDataSetChanged();
    }

    // uses client to request retweet
    private void onClickRetweet(final Tweet tweet, final TweetViewHolder holder) {
        final long id = tweet.getId();
        twitterClient.retweet(id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i(TAG, "Retweet request successful with id " + id);
                tweet.setRetweeted(true);
                changeRetweetButton(holder, tweet.isRetweeted());
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e(TAG, "retweet request failure with status code: " + statusCode + " response: " + responseString, throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e(TAG, "retweet request failure with status code: " + statusCode, throwable);
            }
        });
    }

    // use client to request undo retweet
    private void onClickUnretweet(final Tweet tweet, final TweetViewHolder holder) {
        final long id = tweet.getId();
        twitterClient.unretweet(id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i(TAG, "Unretweet request sucessful with id " + id);
                tweet.setRetweeted(false);
                changeRetweetButton(holder, tweet.isRetweeted());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e(TAG, "Unretweet reqeuest failure with status code: " + statusCode + " response: " + responseString, throwable);
            }
        });
    }

    // change the appearance of the retweet button if a tweet is retweeted.
    private void changeRetweetButton(TweetViewHolder holder, boolean retweeted) {
        if ( retweeted ) {
            holder.retweetButton.setColorFilter(ResourcesCompat.getColor(context.getResources(), R.color.medium_green, null));
        } else {
            holder.retweetButton.setColorFilter(ResourcesCompat.getColor(context.getResources(), R.color.medium_gray, null));
        }
    }
}

package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    private static final int RADIUS = 30;
    private static final int MARGIN = 10;

    private Context context;
    private List<Tweet> tweets;

    public TweetAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate layout for a tweet for each tweet
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // bind values based on position of the elements
        Tweet tweet = tweets.get(position);
        holder.tvBody.setText(tweet.getBody());
        holder.tvScreenName.setText("@" + tweet.getUser().getScreenName());
        holder.tvName.setText(tweet.getUser().getName());
        holder.tvTime.setText(tweet.getCreatedAt());
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

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivProfileImage;
        TextView tvScreenName;
        TextView tvBody;
        TextView tvName;
        TextView tvTime;


        public ViewHolder(View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvName = itemView.findViewById(R.id.tvName);
            tvTime = itemView.findViewById(R.id.tvTime);

        }
    }
}

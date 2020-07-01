package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.util.List;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder>{
    // Pass in context and List of tweets
    Context context;
    List<Tweet> tweets;
    public TweetsAdapter(Context context, List<Tweet> tweets){
        this.context = context;
        this.tweets = tweets;

    }
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }
    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tweet tweet = tweets.get(position);
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView ivProfileImage;
        TextView tvBody;
        TextView tvScreenName;
        TextView tvUsername;
        TextView tvTimestamp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
        }

        public void bind(Tweet tweet) {
            tvBody.setText(tweet.getBody());
            tvScreenName.setText(tweet.getUser().getName());
            tvUsername.setText("@" + tweet.getUser().screenName);
            tvTimestamp.setText(tweet.getTimeAgo());
            Glide.with(context).load(tweet.user.publicImageUrl).into(ivProfileImage);
        }
    }
}

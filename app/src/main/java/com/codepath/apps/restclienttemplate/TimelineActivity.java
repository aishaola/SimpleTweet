package com.codepath.apps.restclienttemplate;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.databinding.ActivityTimelineBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.TweetDao;
import com.codepath.apps.restclienttemplate.models.TweetWithUser;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {
    TwitterClient client;
    RecyclerView rvTweets;
    public static final String TAG = "TimelineActivity";
    private final int REQUEST_CODE = 20;

    List<Tweet> tweets;
    TweetsAdapter adapter;
    RecyclerView.OnScrollListener scrollListener;
    private SwipeRefreshLayout swipeContainer;
    private TweetDao TweetDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_timeline);
        ActivityTimelineBinding binding = ActivityTimelineBinding.inflate(getLayoutInflater());

        // layout of activity is stored in a special property called root
        View view = binding.getRoot();
        setContentView(view);

        client = TwitterApp.getRestClient(this);
        TweetDao = ((TwitterApp) getApplicationContext()).getMyDatabase().tweetDao();

        rvTweets = findViewById(R.id.rvTweets);

        tweets = new ArrayList<Tweet>();
        adapter = new TweetsAdapter(this, tweets);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.setAdapter(adapter);

        // Retain an instance so that you can call `resetState()` for fresh searches

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                Log.i(TAG, "onLoadMore: " + page);
                loadMoreData();
            }
        };
        // Adds the scroll listener to RecyclerView
        rvTweets.addOnScrollListener(scrollListener);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "run: Getting tweets from database...");
                List<TweetWithUser> tweetWithUsers = TweetDao.recentItems();
                List<Tweet> tweetsFromDB = TweetWithUser.getTweetList(tweetWithUsers);
                adapter.clear();
                adapter.addAll(tweetsFromDB);
            }
        });

        // Setup refresh listener which triggers new data loading
        populateHomeTimeline();
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully
                populateHomeTimeline();
            }
        });



    }

    private void loadMoreData() {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
        long maxId = tweets.get(tweets.size() - 1).id;
        client.loadMoreTweets(maxId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    Log.i(TAG, "onSuccess: Loading more tweets!!");
                    adapter.addAll(Tweet.fromJsonArray(json.jsonArray));
                } catch (JSONException e) {
                    Log.e(TAG, "Error with loading tweets!!", e);
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure: Couldn't reach API", throwable);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu, this add items to the action bar if it is present
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.compose){
            // Compose item has been selected
            Intent i = new Intent(this, ComposeActivity.class);
            startActivityForResult(i, REQUEST_CODE);
            // Navigate to the compose activity

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            // Get data from the intent
            Tweet tweet = (Tweet) Parcels.unwrap(data.getParcelableExtra("tweet"));
            // Update the recyclerView with new tweet
            tweets.add(0, tweet);
            // Modify data source to include tweet
            // Update the adapter
            adapter.notifyItemInserted(0);
            rvTweets.smoothScrollToPosition(0);
        }
    }

    private void populateHomeTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess!!" + json.toString());
                try {
                    final List<Tweet> tweetsFromNetwork = Tweet.fromJsonArray(json.jsonArray);

                    adapter.clear();
                    adapter.addAll(tweetsFromNetwork);
                    Log.i(TAG, "onSuccess: Tweets added to tweet model");
                    swipeContainer.setRefreshing(false);
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(TAG, "run: Saving tweets from database...");
                            //insert users into database first so foreign key can be linked
                            List<User> users = User.fromJsonTweetList(tweetsFromNetwork);
                            TweetDao.insertModel(users.toArray(new User[0]));
                            //insert tweets
                            TweetDao.insertModel(tweetsFromNetwork.toArray(new Tweet[0]));
                        }
                    });
                } catch (JSONException e) {
                    Log.e(TAG, "Error: Tweets couldn't be added", e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure!!!", throwable);
            }
        });
    }
}
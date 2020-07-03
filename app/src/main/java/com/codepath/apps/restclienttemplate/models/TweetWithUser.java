package com.codepath.apps.restclienttemplate.models;

import androidx.room.Embedded;

import java.util.ArrayList;
import java.util.List;

public class TweetWithUser {

    //Embedded notation flattens the properties of the User object into the object
    //preserving encapsulation.

    //This class allows for query to be put in with all fields of
    // User and all field of tweet(with tweet_ prefix)
    @Embedded
    User user;

    @Embedded(prefix = "tweet_")
    Tweet tweet;

    public static List<Tweet> getTweetList(List<TweetWithUser> tweetWithUsers) {
        List<Tweet> tweets = new ArrayList<Tweet>();
        for (int i = 0; i < tweetWithUsers.size() ; i++) {
            Tweet tweet = tweetWithUsers.get(i).tweet;
            tweet.user = tweetWithUsers.get(i).user;

            tweets.add(tweet);
        }
        return tweets;
    }
}

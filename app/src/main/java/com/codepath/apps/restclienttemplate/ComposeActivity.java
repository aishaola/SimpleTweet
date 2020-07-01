package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ComposeActivity extends AppCompatActivity {
    EditText etCompose;
    Button btnTweet;
    public static final int MAX_TWEET_LENGTH = 140;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        etCompose = findViewById(R.id.etCompose);
        btnTweet = findViewById(R.id.btnTweet);

        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Make an API call to twitter to publish the tweet.
                String tweetContent = etCompose.getText().toString();
                if(tweetContent.isEmpty()){
                    Toast.makeText(ComposeActivity.this, "Sorry, tweet is too short", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(tweetContent.length()> MAX_TWEET_LENGTH){
                    Toast.makeText(ComposeActivity.this, "Sorry, tweet is too long", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(ComposeActivity.this, tweetContent, Toast.LENGTH_LONG).show();
            }
        });
    }
}
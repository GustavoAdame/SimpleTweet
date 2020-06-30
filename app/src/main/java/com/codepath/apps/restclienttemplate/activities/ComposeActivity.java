package com.codepath.apps.restclienttemplate.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.other.TwitterApp;
import com.codepath.apps.restclienttemplate.other.TwitterClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcel;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {

    /* Global Variables */
    public static final int MAX_TWEET_LENGTH = 280;
    public EditText etCompose;
    public Button btnTweet;
    public TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        client = TwitterApp.getRestClient(this);
        etCompose = findViewById(R.id.etCompose);
        btnTweet = findViewById(R.id.btnTweet);

        /* Set a button onClick listener */
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweetContent = etCompose.getText().toString();
                if(tweetContent.isEmpty()){
                    Toast.makeText(ComposeActivity.this, "Sorry, no empty tweet allowed", Toast.LENGTH_LONG).show();
                    return;
                }

                if(tweetContent.length() > MAX_TWEET_LENGTH){
                    Toast.makeText(ComposeActivity.this, "Sorry, tweet to long", Toast.LENGTH_LONG).show();
                    return;
                }

                /* Make a API call to Twitter to publish Tweet */
                client.publishTweet(new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Toast.makeText(ComposeActivity.this, "Tweet Posted!", Toast.LENGTH_LONG).show();
                            Intent i = new Intent();
                            i.putExtra("tweet", Parcels.wrap(tweet));
                            /* Set result code and bundle data for response */
                            setResult(RESULT_OK, i);
                            /* Closes the activity, pass data to parent */
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                    }
                }, tweetContent);
            }
        });



    }
}
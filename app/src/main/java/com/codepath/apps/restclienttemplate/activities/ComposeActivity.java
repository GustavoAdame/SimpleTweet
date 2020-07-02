package com.codepath.apps.restclienttemplate.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.databinding.ActivityComposeBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.app.TwitterApp;
import com.codepath.apps.restclienttemplate.app.TwitterClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {
    /* Global variables */
    public static final int MAX_TWEET_LENGTH = 280;
    /* Views references */
    public EditText etCompose;
    public Button btnTweet;
    /* Class references */
    public TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        /* Initalizing Global Variables */
        client = TwitterApp.getRestClient(this);
        etCompose = findViewById(R.id.etCompose);
        btnTweet = findViewById(R.id.btnTweet);

        /* Button: Tweet -> onClickListener */
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweetContent = etCompose.getText().toString();
                if(tweetContent.isEmpty()){
                    Toast.makeText(ComposeActivity.this, "Can't Send Empty Tweet", Toast.LENGTH_LONG).show();
                    return;
                }

                if(tweetContent.length() > MAX_TWEET_LENGTH){
                    Toast.makeText(ComposeActivity.this, "Exceed Character Limit", Toast.LENGTH_LONG).show();
                    return;
                }

                /* Make a API call to Twitter to publish Tweet */
                client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
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
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {}
                });
            }
        });
    }
}
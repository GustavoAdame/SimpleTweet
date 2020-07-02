package com.codepath.apps.restclienttemplate.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.target.Target;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.app.TwitterClient;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import okhttp3.Headers;

public class TweetDetailsActivity extends AppCompatActivity {
    public static Tweet tweet;
    public ImageView ivProfileImage2;
    public TextView tvBody2;
    public TextView tvScreenName2;
    public ImageView ivImage2;
    public TextView tvHandle2;
    public TextView tvRetweetCount2;
    public TextView tvLikeCount2;
    public TextView tvRetweetText2;
    public TextView tvLikeText2;
    public ImageView ivComment2;
    public ImageView ivLike2;
    public ImageView ivRetweet2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);
        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra("tweet"));
        ivProfileImage2 = findViewById(R.id.ivProfileImage2);
        tvScreenName2 = findViewById(R.id.tvScreenName2);
        tvBody2 = findViewById(R.id.tvBody2);
        ivImage2 = findViewById(R.id.ivImage2);
        tvHandle2 = findViewById(R.id.tvHandle2);
        tvRetweetCount2 = findViewById(R.id.tvRetweetCount2);
        tvLikeCount2 = findViewById(R.id.tvLikeCount2);
        tvRetweetText2 = findViewById(R.id.tvRetweetText2);
        tvLikeText2 = findViewById(R.id.tvLikeText2);
        ivComment2 = findViewById(R.id.ivComment2);
        ivLike2 = findViewById(R.id.ivLike2);
        ivRetweet2 = findViewById(R.id.ivRetweet2);
        bindAssets();

    }

    public void bindAssets(){
        tvBody2.setText(tweet.body);
        tvScreenName2.setText(tweet.user.screenName);
        tvHandle2.setText("@" + tweet.user.screenName);

        tvRetweetCount2.setText(String.valueOf(tweet.retweetCount));
        tvLikeCount2.setText(String.valueOf(tweet.favoriteCount));


        ivComment2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(TweetDetailsActivity.this, ReplyActivity.class);
                i.putExtra("handle", "@"+tweet.user.screenName);
                startActivity(i);
            }
        });


        if(tweet.isFavorite == true){
            ivLike2.setImageResource(R.drawable.ic_vector_heart);
        } else{
            ivLike2.setImageResource(R.drawable.ic_vector_heart_stroke);
        }

        if(tweet.isRetweet == true){
            ivRetweet2.setImageResource(R.drawable.ic_vector_retweet);
        } else{
            ivRetweet2.setImageResource(R.drawable.ic_vector_retweet_stroke);
        }


        final TwitterClient client = new TwitterClient(TweetDetailsActivity.this);
        ivRetweet2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tweet.isRetweet){
                    client.retweetTweet(new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            ivRetweet2.setImageResource(R.drawable.ic_vector_retweet_stroke);
                            tweet.isRetweet = false;
                            tvRetweetCount2.setText(String.valueOf(tweet.retweetCount));
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                        }
                    }, tweet.id, "unretweet");
                } else{
                    client.retweetTweet(new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            ivRetweet2.setImageResource(R.drawable.ic_vector_retweet);
                            tweet.isRetweet = true;
                            tvRetweetCount2.setText(String.valueOf(tweet.retweetCount+1));
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                        }
                    }, tweet.id, "retweet");

                }

            }
        });

        ivLike2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tweet.isFavorite){
                    client.favoriteTweet(new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            ivLike2.setImageResource(R.drawable.ic_vector_heart_stroke);
                            tweet.isFavorite = false;
                            tvLikeCount2.setText(String.valueOf(tweet.favoriteCount));
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                        }
                    }, tweet.id, "destroy");
                } else{
                    client.favoriteTweet(new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            ivLike2.setImageResource(R.drawable.ic_vector_heart);
                            tweet.isFavorite = true;
                            tvLikeCount2.setText(String.valueOf(tweet.favoriteCount+1));
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                        }
                    }, tweet.id, "create");

                }

            }
        });

        int radius = 100; // corner radius, higher value = more rounded
        Glide.with(TweetDetailsActivity.this).load(tweet.user.profileImageURL).transform(new RoundedCorners(radius)).override(Target.SIZE_ORIGINAL, 120).into(ivProfileImage2);

        if(!tweet.ImageURL.equals("No Image")){
            ivImage2.setVisibility(View.VISIBLE);
            Glide.with(TweetDetailsActivity.this).load(tweet.ImageURL).into(ivImage2);
        } else{
            ivImage2.setVisibility(View.GONE);
        }

    }
}
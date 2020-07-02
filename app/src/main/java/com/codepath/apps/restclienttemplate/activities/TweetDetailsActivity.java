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
    public ImageView ivProfileImage;
    public TextView tvBody;
    public TextView tvScreenName;
    public ImageView ivImage;
    public TextView tvHandle;
    public TextView tvRetweetCount;
    public TextView tvLikeCount;
    public TextView tvRetweetText;
    public TextView tvLikeText;
    public ImageView ivComment;
    public ImageView ivLike;
    public ImageView ivRetweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);
        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra("tweet"));
        ivProfileImage = findViewById(R.id.ivProfileImage);
        tvScreenName = findViewById(R.id.tvScreenName);
        tvBody = findViewById(R.id.tvBody);
        ivImage = findViewById(R.id.ivImage);
        tvHandle = findViewById(R.id.tvHandle);
        tvRetweetCount = findViewById(R.id.tvRetweetCount);
        tvLikeCount = findViewById(R.id.tvLikeCount);
        tvRetweetText = findViewById(R.id.tvRetweetText);
        tvLikeText = findViewById(R.id.tvLikeText);
        ivComment = findViewById(R.id.ivComment);
        ivLike = findViewById(R.id.ivLike);
        ivRetweet = findViewById(R.id.ivRetweet);
        bindAssets();

    }

    public void bindAssets(){
        tvBody.setText(tweet.body);
        tvScreenName.setText(tweet.user.screenName);
        tvHandle.setText("@" + tweet.user.screenName);

        tvRetweetCount.setText(String.valueOf(tweet.retweetCount));
        tvLikeCount.setText(String.valueOf(tweet.favoriteCount));


        ivComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(TweetDetailsActivity.this, ReplyActivity.class);
                i.putExtra("handle", "@"+tweet.user.screenName);
                startActivity(i);
            }
        });


        if(tweet.isFavorite == true){
            ivLike.setImageResource(R.drawable.ic_vector_heart);
        } else{
            ivLike.setImageResource(R.drawable.ic_vector_heart_stroke);
        }

        if(tweet.isRetweet == true){
            ivRetweet.setImageResource(R.drawable.ic_vector_retweet);
        } else{
            ivRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);
        }


        final TwitterClient client = new TwitterClient(TweetDetailsActivity.this);
        ivRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tweet.isRetweet){
                    client.retweetTweet(new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            ivRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);
                            tweet.isRetweet = false;
                            tvRetweetCount.setText(String.valueOf(tweet.retweetCount));
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                        }
                    }, tweet.id, "unretweet");
                } else{
                    client.retweetTweet(new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            ivRetweet.setImageResource(R.drawable.ic_vector_retweet);
                            tweet.isRetweet = true;
                            tvRetweetCount.setText(String.valueOf(tweet.retweetCount+1));
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                        }
                    }, tweet.id, "retweet");

                }

            }
        });

        ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tweet.isFavorite){
                    client.favoriteTweet(new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            ivLike.setImageResource(R.drawable.ic_vector_heart_stroke);
                            tweet.isFavorite = false;
                            tvLikeCount.setText(String.valueOf(tweet.favoriteCount));
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                        }
                    }, tweet.id, "destroy");
                } else{
                    client.favoriteTweet(new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            ivLike.setImageResource(R.drawable.ic_vector_heart);
                            tweet.isFavorite = true;
                            tvLikeCount.setText(String.valueOf(tweet.favoriteCount+1));
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                        }
                    }, tweet.id, "create");

                }

            }
        });

        int radius = 100; // corner radius, higher value = more rounded
        Glide.with(TweetDetailsActivity.this).load(tweet.user.profileImageURL).transform(new RoundedCorners(radius)).override(Target.SIZE_ORIGINAL, 120).into(ivProfileImage);

        if(!tweet.ImageURL.equals("No Image")){
            ivImage.setVisibility(View.VISIBLE);
            Glide.with(TweetDetailsActivity.this).load(tweet.ImageURL).into(ivImage);
        } else{
            ivImage.setVisibility(View.GONE);
        }

    }
}
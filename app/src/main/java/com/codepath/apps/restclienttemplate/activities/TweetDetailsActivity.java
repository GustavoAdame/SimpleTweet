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
    /* Class references */
    public static Tweet tweet;

    /* Views references */
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

        /* Getting revelant data from a tweet */
        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

        /* Inflate our Views */
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

        /* This method binds the necessary data and setup the UI functionality */
        bindAssets();
    }

    public void bindAssets(){
        /* Binding data into our views */
        tvBody2.setText(tweet.body);
        tvScreenName2.setText(tweet.user.screenName);
        tvHandle2.setText("@" + tweet.user.screenName);
        tvRetweetCount2.setText(String.valueOf(tweet.retweetCount));
        tvLikeCount2.setText(String.valueOf(tweet.favoriteCount));

        /* Icon: Comment --> onClick to ReplyActivity */
        ivComment2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(TweetDetailsActivity.this, ReplyActivity.class);
                i.putExtra("handle", "@"+tweet.user.screenName);
                startActivity(i);
            }
        });

        /* Detemine Icon depending on different states to get UI feedback */
        if(tweet.isFavorite == true){
            ivLike2.setImageResource(R.drawable.ic_vector_heart);
        } else{
            ivLike2.setImageResource(R.drawable.ic_vector_heart_stroke);
        }

        /* Detemine Icon depending on different states to get UI feedback */
        if(tweet.isRetweet == true){
            ivRetweet2.setImageResource(R.drawable.ic_vector_retweet);
        } else{
            ivRetweet2.setImageResource(R.drawable.ic_vector_retweet_stroke);
        }

        /* Set up client */
        final TwitterClient client = new TwitterClient(TweetDetailsActivity.this);

        ivRetweet2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tweet.isRetweet){
                    /* Operation string - unretweet is based on Twitter API parameter */
                    client.retweetTweet(tweet.id, "unretweet", new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            ivRetweet2.setImageResource(R.drawable.ic_vector_retweet_stroke);
                            tweet.isRetweet = false;
                            tvRetweetCount2.setText(String.valueOf(tweet.retweetCount));
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {}
                    });
                } else{
                    /* Operation string - retweet is based on Twitter API parameter */
                    client.retweetTweet(tweet.id, "retweet", new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            ivRetweet2.setImageResource(R.drawable.ic_vector_retweet);
                            tweet.isRetweet = true;
                            tvRetweetCount2.setText(String.valueOf(tweet.retweetCount+1));
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {}
                    });
                }
            }
        });

        ivLike2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tweet.isFavorite){
                    /* Operation string - destroy is based on Twitter API parameter */
                    client.favoriteTweet(tweet.id, "destroy", new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            ivLike2.setImageResource(R.drawable.ic_vector_heart_stroke);
                            tweet.isFavorite = false;
                            tvLikeCount2.setText(String.valueOf(tweet.favoriteCount));
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {}
                    });
                } else{
                    /* Operation string - create is based on Twitter API parameter */
                    client.favoriteTweet(tweet.id, "create", new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            ivLike2.setImageResource(R.drawable.ic_vector_heart);
                            tweet.isFavorite = true;
                            tvLikeCount2.setText(String.valueOf(tweet.favoriteCount+1));
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {}
                    });
                }
            }
        });

        int radius = 100;
        /* Get Profile image to display on TweetDetails */
        Glide.with(TweetDetailsActivity.this).load(tweet.user.profileImageURL).
                transform(new RoundedCorners(radius)).override(Target.SIZE_ORIGINAL, 120).into(ivProfileImage2);

        /* If tweet have a image --> get display ImageView with data */
        if(!tweet.ImageURL.equals("No Image")){
            ivImage2.setVisibility(View.VISIBLE);
            /* Get tweet media image to display on TweetDetails */
            Glide.with(TweetDetailsActivity.this).load(tweet.ImageURL).into(ivImage2);
        }
        /* Else remove Imageview */
        else{
            ivImage2.setVisibility(View.GONE);
        }

    }
}
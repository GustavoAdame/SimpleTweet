package com.codepath.apps.restclienttemplate.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.restclienttemplate.databinding.ActivityTimelineBinding;
import com.codepath.apps.restclienttemplate.models.TweetDao;
import com.codepath.apps.restclienttemplate.models.TweetWithUser;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.apps.restclienttemplate.listener.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.app.TwitterApp;
import com.codepath.apps.restclienttemplate.app.TwitterClient;
import com.codepath.apps.restclienttemplate.adapters.TweetsAdapter;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {
    /* Listener reference variables */
    public SwipeRefreshLayout swipeContainer;
    public EndlessRecyclerViewScrollListener scrollListener;

    /* Data structures reference variables */
    public List<Tweet> tweets;
    public RecyclerView rvTweets;
    public TweetsAdapter tweetsAdapter;
    private List<Tweet> tweetsFromDB;

    /* Class references */
    public TwitterClient client;
    public TweetDao tweetDao;

    /* REQUEST_CODE */
    private final int REQUEST_CODE = 200;  /* User sending a tweet */
    private final int REQUEST_CODE2 = 201; /* User replying to  a tweet */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        /* Initalizing Global Variables */
        client = TwitterApp.getRestClient(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        swipeContainer = findViewById(R.id.swipeContainer);
        rvTweets = findViewById(R.id.rvTweets);
        tweetDao = ((TwitterApp) getApplicationContext()).getMyDatabase().tweetModelDao();

        /* Configure the refreshing colors */
        swipeContainer.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() { populateHomeTimeline();}
        });

        /* Initialize the list of tweets and adapter */
        tweets = new ArrayList<>();
        tweetsAdapter = new TweetsAdapter(this, tweets);

        /* Sets the Toolbar to act as the ActionBar for this Activity window. */
        setSupportActionBar(toolbar);

        /* Setup: layout manager and the adapter */
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(layoutManager);
        rvTweets.setAdapter(tweetsAdapter);

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) { loadMoreData();}
        };

        /* Adds the scroll listener to RecyclerView */
        rvTweets.addOnScrollListener(scrollListener);

        /* Update timelines with lastest tweets on a seperate thread */
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                /* Query for existing tweets in the local database */
                List<TweetWithUser> tweetWithUsers = tweetDao.recentItems();
                tweetsFromDB = TweetWithUser.getTweetList(tweetWithUsers);
                tweetsAdapter.clear();
                tweetsAdapter.addAll(tweetsFromDB);
            }
        });

        /* Network request for user timeline */
        populateHomeTimeline();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Inflate the menu; this adds items to the action bar if it is present */
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*  Handle presses on the action bar items */
        if (item.getItemId() == R.id.compose) {
            Intent i = new Intent(TimelineActivity.this, ComposeActivity.class);
            startActivityForResult(i, REQUEST_CODE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /* Handle the result of the child-activity */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        /* REQUEST_CODE is defined above */
        super.onActivityResult(requestCode, resultCode, data);

        /* Tweeting */
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            /* Get data from intent (Compose) */
            Tweet tweetStatus = Parcels.unwrap(data.getParcelableExtra("tweet"));

            /* Update RecyclerView with new tweet - Modify data source */
            tweets.add(0, tweetStatus);

            /* Update the adapter */
            tweetsAdapter.notifyItemInserted(0);
            rvTweets.smoothScrollToPosition(0);
        }

        /* Replying */
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE2) {
            /* Get data from intent (Compose) */
            Tweet tweetStatus = Parcels.unwrap(data.getParcelableExtra("reply"));

            /* Update RecyclerView with new tweet - Modify data source */
            tweets.add(0, tweetStatus);

            /* Update the adapter */
            tweetsAdapter.notifyItemInserted(0);
            rvTweets.smoothScrollToPosition(0);
        }
    }

    /* Uses a certain endpoint from the API */
    private void loadMoreData() {
        /* Send an API request to retrieve appropriate paginated data */
        client.getNextPageOfTweets(tweets.get(tweets.size()-1).id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                /* Deserialize and construct new model objects from the API response */
                JSONArray jsonArray = json.jsonArray;
                try {
                    List<Tweet> tweets = Tweet.fromJsonArray(jsonArray);

                    /* Append the new data objects to the existing set of items inside the array of items */
                    tweetsAdapter.addAll(tweets);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {}
        });
    }

    /* Uses a certain endpoint from the API */
    private void populateHomeTimeline() {
        client.getHomeTimelne(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONArray jsonArray = json.jsonArray;
                try {

                    final List<Tweet> tweetsFromNetwork = Tweet.fromJsonArray(jsonArray);
                    tweetsAdapter.clear();
                    tweetsAdapter.addAll(tweetsFromNetwork);

                    /* Now we call setRefreshing(false) to signal refresh has finished */
                    swipeContainer.setRefreshing(false);

                    /* Update timelines with lastest tweets on a seperate thread */
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            List<User> usersFromNetwork = User.fromJsonTweetArray(tweetsFromNetwork);
                            tweetDao.insertModel(usersFromNetwork.toArray(new User[0]));
                            tweetDao.insertModel(tweetsFromNetwork.toArray(new Tweet[0]));
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
            }
        });
    }
}
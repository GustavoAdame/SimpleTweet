package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.provider.ContactsContract;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.target.Target;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.activities.ReplyActivity;
import com.codepath.apps.restclienttemplate.activities.TweetDetailsActivity;
import com.codepath.apps.restclienttemplate.app.TwitterClient;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.TweetDao;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Headers;

import static androidx.core.content.ContextCompat.startActivity;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder>{

    public Context context;
    public List<Tweet> tweets;

    /* Pass in context and list of tweets */
    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    /* For each row, inflate a layout */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    /* Bind values to the most current position */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        /* Get data at current position */
        Tweet tweet = tweets.get(position);
        /* Bind the tweet with view holder */
        holder.bind(tweet);

    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }

    /* Define a viewholder */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView ivProfileImage;
        public TextView tvBody;
        public TextView tvScreenName;
        public ImageView ivImage;
        public TextView tvTimeStamp;
        public TextView tvHandle;
        public TextView tvRetweetCount;
        public TextView tvLikeCount;
        public ImageView ivComment;
        public ImageView ivLike;
        public ImageView ivRetweet;

        /* itemView is aka a single tweet */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvTimeStamp = itemView.findViewById(R.id.tvTimeStamp);
            tvHandle = itemView.findViewById(R.id.tvHandle);
            tvRetweetCount = itemView.findViewById(R.id.tvRetweetCount);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
            ivComment = itemView.findViewById(R.id.ivComment);
            ivRetweet = itemView.findViewById(R.id.ivRetweet);
            ivLike = itemView.findViewById(R.id.ivLike);
            itemView.setOnClickListener(this);
        }

        public void bind(final Tweet tweet) {
            tvBody.setText(tweet.body);
            tvScreenName.setText(tweet.user.screenName);
            tvHandle.setText("@" + tweet.user.screenName);
            tvTimeStamp.setText("· " + getRelativeTimeAgo(tweet.createdAt));

            tvRetweetCount.setText(String.valueOf(tweet.retweetCount));
            tvLikeCount.setText(String.valueOf(tweet.favoriteCount));


            ivComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, ReplyActivity.class);
                    i.putExtra("handle", "@"+tweet.user.screenName);
                    context.startActivity(i);
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


            final TwitterClient client = new TwitterClient(context);
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
            Glide.with(context).load(tweet.user.profileImageURL).transform(new RoundedCorners(radius)).override(Target.SIZE_ORIGINAL, 120).into(ivProfileImage);

            if(!tweet.ImageURL.equals("No Image")){
                ivImage.setVisibility(View.VISIBLE);
                Glide.with(context).load(tweet.ImageURL).into(ivImage);
            } else{
                ivImage.setVisibility(View.GONE);
            }

        }

        @Override
        public void onClick(View view) {

            int position = getAdapterPosition();
            // make sure the position is valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION) {
                Tweet tweet = tweets.get(position);
                Intent intent = new Intent(context, TweetDetailsActivity.class);
                intent.putExtra("tweet", Parcels.wrap(tweet));
                context.startActivity(intent);
            }

        }
    }

    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }
}

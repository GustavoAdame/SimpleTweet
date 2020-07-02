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
    /* Data structure references */
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
        /* XML View references */
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

            /* Inflate our Views*/
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

            /* Click on row to launch intent */
            itemView.setOnClickListener(this);
        }

        public void bind(final Tweet tweet) {
            /* Binding data into our views */
            tvBody.setText(tweet.body);
            tvScreenName.setText(tweet.user.screenName);
            tvHandle.setText("@" + tweet.user.screenName);
            tvTimeStamp.setText("· " + getRelativeTimeAgo(tweet.createdAt));
            tvRetweetCount.setText(String.valueOf(tweet.retweetCount));
            tvLikeCount.setText(String.valueOf(tweet.favoriteCount));

            /* Icon: Comment --> onClick to ReplyActivity */
            ivComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, ReplyActivity.class);
                    i.putExtra("handle", "@"+tweet.user.screenName);
                    context.startActivity(i);
                }
            });

            /* Detemine Icon depending on different states to get UI feedback */
            if(tweet.isFavorite == true){
                ivLike.setImageResource(R.drawable.ic_vector_heart);
            } else{
                ivLike.setImageResource(R.drawable.ic_vector_heart_stroke);
            }

            /* Detemine Icon depending on different states to get UI feedback */
            if(tweet.isRetweet == true){
                ivRetweet.setImageResource(R.drawable.ic_vector_retweet);
            } else{
                ivRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);
            }

            /* Set up client */
            final TwitterClient client = new TwitterClient(context);

            ivRetweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(tweet.isRetweet){
                        /* Operation string - unretweet is based on Twitter API parameter */
                        client.retweetTweet(tweet.id, "unretweet", new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                ivRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);
                                tweet.isRetweet = false;
                                tvRetweetCount.setText(String.valueOf(tweet.retweetCount));
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {}
                        });
                    } else{
                        /* Operation string - retweet is based on Twitter API parameter */
                        client.retweetTweet(tweet.id, "retweet", new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                ivRetweet.setImageResource(R.drawable.ic_vector_retweet);
                                tweet.isRetweet = true;
                                tvRetweetCount.setText(String.valueOf(tweet.retweetCount+1));
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {}
                        });
                    }
                }
            });

            ivLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(tweet.isFavorite){
                        /* Operation string - destroy is based on Twitter API parameter */
                        client.favoriteTweet(tweet.id, "destroy", new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                ivLike.setImageResource(R.drawable.ic_vector_heart_stroke);
                                tweet.isFavorite = false;
                                tvLikeCount.setText(String.valueOf(tweet.favoriteCount));
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                            }
                        });
                    } else{
                        client.favoriteTweet(tweet.id, "create", new JsonHttpResponseHandler() {
                            /* Operation string - create is based on Twitter API parameter */
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                ivLike.setImageResource(R.drawable.ic_vector_heart);
                                tweet.isFavorite = true;
                                tvLikeCount.setText(String.valueOf(tweet.favoriteCount+1));
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                            }
                        });
                    }
                }
            });

            int radius = 100;
            /* Get Profile image to display on Timeline */
            Glide.with(context).load(tweet.user.profileImageURL).
                    transform(new RoundedCorners(radius)).override(Target.SIZE_ORIGINAL, 120).into(ivProfileImage);

            /* If tweet have a image --> get display ImageView with data */
            if(!tweet.ImageURL.equals("No Image")){
                ivImage.setVisibility(View.VISIBLE);
                /* Get tweet media image to display on TweetDetails */
                Glide.with(context).load(tweet.ImageURL).into(ivImage);
            }
            /* Else remove Imageview */
            else{
                ivImage.setVisibility(View.GONE);
            }

        }

        /* Actually implementation of the onClick for recycler view */
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Tweet tweet = tweets.get(position);
                Intent intent = new Intent(context, TweetDetailsActivity.class);
                intent.putExtra("tweet", Parcels.wrap(tweet));
                context.startActivity(intent);
            }

        }
    }

    /* Helper method to format the timestamp */
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

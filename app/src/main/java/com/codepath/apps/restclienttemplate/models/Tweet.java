package com.codepath.apps.restclienttemplate.models;

import android.text.format.DateUtils;
import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Parcel
@Entity(foreignKeys = @ForeignKey(entity=User.class, parentColumns="id", childColumns="userID"))
public class Tweet {
    @ColumnInfo
    @PrimaryKey
    public long id;

    @ColumnInfo
    public String body;

    @ColumnInfo
    public String createdAt;

    @ColumnInfo
    public long userID;

    @ColumnInfo
    public String ImageURL;

    @ColumnInfo
    public int retweetCount;

    @ColumnInfo
    public int favoriteCount;

    @Ignore /* Foreign Key to User Table */
    public User user;


    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.id = jsonObject.getLong("id");
        tweet.retweetCount = jsonObject.getInt("retweet_count");
        Log.d("Gustavo", "retweet_count: " + tweet.retweetCount);
        tweet.favoriteCount = jsonObject.getInt("favorite_count");
        Log.d("Gustavo", "favorite_count: " + tweet.favoriteCount);

        try{
            JSONObject entities = jsonObject.getJSONObject("entities");
            JSONArray media = entities.getJSONArray("media");
            JSONObject media_First = media.getJSONObject(0);
            tweet.ImageURL = media_First.getString("media_url_https");
        } catch (JSONException e){
            tweet.ImageURL = "No Image";
        }

        User user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.user = user;
        tweet.userID = user.id;
        return tweet;
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }

}

package com.codepath.apps.restclienttemplate.models;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

/* Implementation of Room : Google's persistence library - querying */
@Dao
public interface TweetDao {
    @Query("SELECT Tweet.body AS tweet_body, Tweet.ImageURL AS tweet_ImageURL, Tweet.createdAt AS tweet_createdAt, Tweet.id AS tweet_id, User.* " +
            "FROM Tweet INNER JOIN User ON Tweet.userID = User.id ORDER BY Tweet.createdAt DESC LIMIT 50")
    List<TweetWithUser> recentItems();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(Tweet... tweets);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(User... users);
}

package com.codepath.apps.restclienttemplate.app;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.codepath.apps.restclienttemplate.models.sample.SampleModel;
import com.codepath.apps.restclienttemplate.models.sample.SampleModelDao;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.TweetDao;
import com.codepath.apps.restclienttemplate.models.User;

@Database(entities={SampleModel.class, Tweet.class, User.class}, version=4)
public abstract class MyDatabase extends RoomDatabase {
    public abstract SampleModelDao sampleModelDao();
    public abstract TweetDao tweetModelDao();

    // Database name to be used
    public static final String NAME = "MyDataBase";
}

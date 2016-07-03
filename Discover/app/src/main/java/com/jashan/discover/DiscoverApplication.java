package com.jashan.discover;

import android.app.Application;

import com.parse.Parse;


/**
 * Created by Jashan on 16/05/2015.
 */

public class DiscoverApplication extends Application{

// Enable Local Datastore.

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "PARSE_API_KEY", "PARSE_API_KEY");
    }
}

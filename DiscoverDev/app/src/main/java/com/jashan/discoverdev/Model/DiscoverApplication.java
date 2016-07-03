package com.jashan.discoverdev.Model;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by Jashan on 11/05/2015.
 */

public class DiscoverApplication extends Application {
    // Enable Local Datastore.

    @Override
    public void onCreate() {
        // Enable Local Datastore.
        super.onCreate();
        Parse.enableLocalDatastore(this);
        ParseObject.registerSubclass(Museum.class);
        Parse.initialize(this, "PARSE_API_KEY",
                "PARSE_API_KEY");
    }
}

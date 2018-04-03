package com.androidchatapp;

import com.firebase.client.Firebase;

/**
 * Created by grishma on 06-01-2017.
 */
public class ChatApplication extends android.app.Application {
    @Override
    public void onCreate()
    {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}

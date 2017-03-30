package com.plusonesoftwares.plusonesoftwares.wchingtech;

import android.app.Application;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Plus 3 on 30-03-2017.
 */

public class GcmMessageHandler extends IntentService {

    public GcmMessageHandler(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
       Intent mIntent = intent;

        Application application = (Application) getApplication();


    }

    /* ... */



}
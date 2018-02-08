package com.example.mudit.ass2test;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by Mudit on 07-02-2018.
 */

public class ActivityRecognizedService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public ActivityRecognizedService() {
        super("ActivityRecognizedService");
    }

    public ActivityRecognizedService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }
}

package com.example.mudit.ass2test;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.media.Image;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.widget.ImageView;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

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
        if (ActivityRecognitionResult.hasResult(intent))
        {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            System.out.println("insideOnhandlemethod");
            handleactivity(result.getProbableActivities());
        }

    }

    private void handleactivity(List<DetectedActivity> probableActivities) {
        for (DetectedActivity activity : probableActivities)
        {
            switch (activity.getType())
            {
                case DetectedActivity.WALKING:
                    break;

                case DetectedActivity.STILL:
                    MainActivity.ActivityStill();
                    break;
            }
        }
    }
}

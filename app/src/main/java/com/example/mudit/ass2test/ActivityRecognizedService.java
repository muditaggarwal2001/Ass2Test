package com.example.mudit.ass2test;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.media.Image;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
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
    Intent actionIntent;
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
            actionIntent = new Intent();
            actionIntent.setAction("ACTION");
            handleactivity(result.getProbableActivities());
        }

    }

    private void handleactivity(List<DetectedActivity> probableActivities) {
            for (DetectedActivity activity : probableActivities) {
                if (activity.getConfidence() > 70) {
                    switch (activity.getType()) {
                        case DetectedActivity.WALKING:
                            Log.d("Activityservice", "Walking");
                            actionIntent.putExtra("activity", "WALK");
                            break;

                        case DetectedActivity.STILL:
                            Log.d("Activityservice", "Sitting Still");
                            actionIntent.putExtra("activity", "STILL");
                            break;

                        case DetectedActivity.RUNNING:
                            Log.d("Activityservice", "Running");
                            actionIntent.putExtra("activity", "RUN");
                            break;

                        case DetectedActivity.IN_VEHICLE:
                            Log.d("Activityservice", "In Vehicle");
                            actionIntent.putExtra("activity", "IN VEHICLE");
                            break;

                        default:
                            Log.d("Activityservice", "Unknown");
                            actionIntent.putExtra("activity", "UNKNOWN");
                            break;

                    }
                }
            }
        LocalBroadcastManager.getInstance(this).sendBroadcast(actionIntent);
    }
}

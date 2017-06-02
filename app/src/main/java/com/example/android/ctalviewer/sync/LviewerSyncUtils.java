package com.example.android.ctalviewer.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.example.android.ctalviewer.Utilities.TrainUtils;
import com.example.android.ctalviewer.data.CTAContract;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

/**
 * Created by Ryan on 5/4/2017.
 */

public class LviewerSyncUtils {

    private static final int SYNC_HOURS = 0;
    private static final int SYNC_INTERVAL_SECONDS = (int)(60);
    private static final int SYNC_FLEXTIME_SECONDS = 60;
    private static final String SYNC_JOB_TAG = "trains_sync_tag";

    private static boolean sInitialized;

    synchronized public static void scheduleSync(@NonNull final Context context){


        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        Job constraintSyncJob = dispatcher.newJobBuilder()
                .setService(LviewerFirebaseJobService.class)
                .setTag(SYNC_JOB_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        30,90
                ))
                .setReplaceCurrent(true)
                .build();

        dispatcher.schedule(constraintSyncJob);
    }
    public static void inialize(final Context context){


            scheduleSync(context);

            Thread checkForUpdateNeeded = new Thread(new Runnable() {
                @Override
                public void run() {
                    Uri uri = CTAContract.TrainsEntry.CONTENT_URI;

                    String[] projection = {CTAContract.TrainsEntry._ID, CTAContract.TrainsEntry.TIMESTAMP, CTAContract.TrainsEntry.ARRIVAL_TIME};

                    Cursor cursor = context.getContentResolver().query(uri,projection,null,null,null);

                    int trainsPassed = 0;

                    if(cursor.moveToFirst()){
                        while(!cursor.isAfterLast()){
                            String time = TrainUtils.formatTimeString(cursor.getString(2),cursor.getString(1));

                            if(time.equals("passed"))
                                trainsPassed++;

                            cursor.moveToNext();
                        }
                    }


                    if(cursor == null || trainsPassed > 0 ){
                        startImmediateSync(context);
                    }

                    cursor.close();

                }
            });

            checkForUpdateNeeded.start();


    }

    public static void startImmediateSync(Context context){
        Intent intent = new Intent(context,LviewerSyncIntentService.class);
        context.startService(intent);
    }
}

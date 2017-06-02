package com.example.android.ctalviewer.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.android.ctalviewer.R;
import com.example.android.ctalviewer.Utilities.NetworkUtils;
import com.example.android.ctalviewer.Utilities.TrainUtils;
import com.example.android.ctalviewer.data.CTAContract;

import java.net.URL;

/**
 * Created by Ryan on 5/4/2017.
 */

public class LviewereSyncTask {

    synchronized public static void syncTrains(Context context){
        try{
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

            String location = sharedPreferences.getString(context.getResources().getString(R.string.pref_location_key),
                    context.getResources().getString(R.string.pref_location_opeast_value));

            URL trainRequestURL = NetworkUtils.buildUrl(location);

            String jsonTrainResponse = NetworkUtils.getResponseFromHttpUrl(trainRequestURL);

            ContentValues[] trainValues = TrainUtils.parseJSON(jsonTrainResponse,context);

            if(trainValues != null && trainValues.length != 0){
                ContentResolver contentResolver = context.getContentResolver();

                contentResolver.delete(CTAContract.TrainsEntry.CONTENT_URI,null,null);

                contentResolver.bulkInsert(CTAContract.TrainsEntry.CONTENT_URI,trainValues);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}

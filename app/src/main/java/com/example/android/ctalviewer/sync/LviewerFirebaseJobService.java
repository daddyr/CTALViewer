package com.example.android.ctalviewer.sync;

import android.content.Context;
import android.os.AsyncTask;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * Created by Ryan on 5/4/2017.
 */

public class LviewerFirebaseJobService extends JobService {

    AsyncTask mFetchTrainsTask;

    @Override
    public boolean onStartJob(final JobParameters job) {
        mFetchTrainsTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                Context context = LviewerFirebaseJobService.this;
                LviewerSyncUtils.startImmediateSync(context);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                jobFinished(job,false);
            }
        };

        mFetchTrainsTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if(mFetchTrainsTask != null) mFetchTrainsTask.cancel(true);
        return true;
    }
}

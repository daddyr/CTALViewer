package com.example.android.ctalviewer.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by Ryan on 5/4/2017.
 */

public class LviewerSyncIntentService extends IntentService {

    public LviewerSyncIntentService() {
        super("CtaViewerIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        LviewereSyncTask.syncTrains(this);
    }
}

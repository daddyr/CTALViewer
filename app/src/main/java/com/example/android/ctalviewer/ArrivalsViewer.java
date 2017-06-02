package com.example.android.ctalviewer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.ctalviewer.Utilities.NetworkUtils;
import com.example.android.ctalviewer.Utilities.TrainUtils;
import com.example.android.ctalviewer.data.CTAContract;
import com.example.android.ctalviewer.data.CTADbHelper;
import com.example.android.ctalviewer.data.CTAViewerPreferences;
import com.example.android.ctalviewer.data.DatabaseAccess;
import com.example.android.ctalviewer.sync.LviewerSyncUtils;
import com.example.android.ctalviewer.sync.LviewereSyncTask;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.jar.Manifest;

public class ArrivalsViewer extends AppCompatActivity implements
        TrainAdapter.ListItemClickListener,
        LoaderManager.LoaderCallbacks<Cursor>,
        SharedPreferences.OnSharedPreferenceChangeListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private SQLiteDatabase mDb;

    private static final int TRAIN_LOADER = 0;
    private static final int STOPS_LOADER = 1;

    private ProgressBar mLoadingProgressBar;
    private RecyclerView mRecyclerView;
    private TrainAdapter mTrainAdapter;
    Toast mToast;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    private List<Lstop> stopsList;

    private int mPostion = RecyclerView.NO_POSITION;

    private static boolean flag = false;

    public static final String[] mProjection = new String[]
            {CTAContract.TrainsEntry._ID,
            CTAContract.TrainsEntry.TIMESTAMP,
                    CTAContract.TrainsEntry.STOP_ID,
                    CTAContract.TrainsEntry.STOP_NAME,
                    CTAContract.TrainsEntry.DESTINATION_NAME,
                    CTAContract.TrainsEntry.ROUTE,
                    CTAContract.TrainsEntry.ARRIVAL_TIME,
                    CTAContract.TrainsEntry.IS_DUE};

    public static final int ID = 0;
    public static final int TIMESTAMP = 1;
    public static final int STOPID = 2;
    public static final int STOPNAME = 3;
    public static final int DESTINATION = 4;
    public static final int ROUTE = 5;
    public static final int ARRIVALTIME = 6;
    public static final int ISDUE = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrivals_viewer);

        mRecyclerView = (RecyclerView)findViewById(R.id.rv_trains);
        mTrainAdapter = new TrainAdapter(this, this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mTrainAdapter);

        mLoadingProgressBar = (ProgressBar)findViewById(R.id.pb_loading_data);

        getSupportLoaderManager().initLoader(TRAIN_LOADER,null,this);

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        stopsList = databaseAccess.getStops();
        databaseAccess.close();


        LviewerSyncUtils.inialize(this);
        /*if(mGoogleApiClient == null){
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }*/
        //LviewereSyncTask.syncTrains(this);
    }

    @Override
    protected void onStop() {
        //mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onStart() {
        //mGoogleApiClient.connect();
        super.onStart();
        getSupportLoaderManager().restartLoader(TRAIN_LOADER,null,this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);

        if(permissionCheck != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)){

            } else {
                //ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    //debug method
    private void displayURL(String location){
        String trainURLString = NetworkUtils.buildUrl(location).toString();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.trains,menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_refresh){
            LviewerSyncUtils.inialize(this);
            return true;
        }

        if(id == R.id.action_map){
            String geoString = "geo:" + CTAViewerPreferences.stopCoordinates[0] + "," + CTAViewerPreferences.stopCoordinates[1];
            Uri geoUri = Uri.parse(geoString);

            Intent intent = new Intent(Intent.ACTION_VIEW);

            intent.setData(geoUri);

            if(intent.resolveActivity(getPackageManager()) != null)
                startActivity(intent);
        }

        if(id == R.id.action_settings){
            Intent settingsIntent = new Intent(this,SettingsActivity.class);
            startActivity(settingsIntent);
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListeItemClick(String clickedItem) {
        if(mToast != null)
            mToast.cancel();
        Intent intent = new Intent(this,DetailActivity.class);
        intent.putExtra(CTAViewerPreferences.KEY_TEXT, clickedItem);
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, CTAContract.TrainsEntry.CONTENT_URI,mProjection,null,null,null);
    }



    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mLoadingProgressBar.setVisibility(View.INVISIBLE);

        mTrainAdapter.swapCursor(data);

        if(mPostion == RecyclerView.NO_POSITION)
            mPostion = 0;

        mRecyclerView.smoothScrollToPosition(mPostion);


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mTrainAdapter.swapCursor(null);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        flag = true;

        String location = sharedPreferences.getString(key,getResources().getString(R.string.pref_location_opeast_value));

        LviewerSyncUtils.startImmediateSync(this);
        if(location == getString(R.string.pref_location_nearest_value)){

        }
        for(Lstop lstop:stopsList){
            String id = lstop.getStopID();
            if(id.equals(location)){
                CTAViewerPreferences.stopCoordinates = lstop.getLocation();
                break;
            }
        }



    }

    private String findNearestLocation(){
        String location = null;

        return location;
    }
}

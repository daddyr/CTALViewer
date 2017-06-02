package com.example.android.ctalviewer.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Ryan on 3/21/2017.
 */

public class TrainProvider extends ContentProvider {
    public static final int CODE_TRAINS = 100;
    public static final int CODE_TRAINS_WITH_ID = 101;
    public static final int CODE_STOPS = 200;
    public static final int CODE_STOPS_WITH_ID = 201;

    CTADbHelper mOpenHelper;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(CTAContract.CONTENT_AUTHORITY,CTAContract.PATH_TRAINS, CODE_TRAINS);
        uriMatcher.addURI(CTAContract.CONTENT_AUTHORITY,CTAContract.PATH_TRAINS + "/#",CODE_TRAINS_WITH_ID);
        uriMatcher.addURI(CTAContract.CONTENT_AUTHORITY,CTAContract.PATH_STOPS, CODE_STOPS);
        uriMatcher.addURI(CTAContract.CONTENT_AUTHORITY,CTAContract.PATH_STOPS + "/*",CODE_STOPS_WITH_ID);

        return uriMatcher;
    }
    @Override
    public boolean onCreate() {
        mOpenHelper = new CTADbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        int match = sUriMatcher.match(uri);
        SQLiteDatabase db;

        if(Math.floor(match/100%10) == 1){
            db = mOpenHelper.getReadableDatabase();
        }else{
            throw new RuntimeException("database not available");
        }

        Cursor cursor;
        switch (match){
            case CODE_TRAINS:
                cursor = db.query(CTAContract.TrainsEntry.TABLE_NAME,
                        projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case CODE_TRAINS_WITH_ID:
                String id = uri.getPathSegments().get(0);
                cursor = db.query(CTAContract.TrainsEntry.TABLE_NAME,
                        projection,
                        "_ID=?",new String[]{id},null,null,sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);

        }

        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)){
            case CODE_TRAINS:
                db.beginTransaction();
                int rowsInserted = 0;
                try{
                    for(ContentValues value:values){
                        long _id = db.insert(CTAContract.TrainsEntry.TABLE_NAME,null,value);
                        if(_id != -1){
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }

                if(rowsInserted > 0)
                    getContext().getContentResolver().notifyChange(uri,null);
            default:
                return super.bulkInsert(uri,values);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        int tasksDeleted;

        switch (match){
            case CODE_TRAINS:
                tasksDeleted = db.delete(CTAContract.TrainsEntry.TABLE_NAME,selection,selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        if(tasksDeleted != 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return tasksDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}

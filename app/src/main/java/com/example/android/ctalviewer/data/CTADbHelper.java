package com.example.android.ctalviewer.data;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.ctalviewer.data.CTAContract.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Ryan on 3/3/2017.
 */

public class CTADbHelper extends SQLiteOpenHelper{


    private static final String DATABASE_NAME = "trains.db";

    private static final int DATABASE_VERSION = 1;

    public CTADbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREAT_TRAINS_TABLE = "CREATE TABLE " + TrainsEntry.TABLE_NAME + " (" +
                TrainsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                TrainsEntry.TIMESTAMP + " TEXT NOT NULL," +
                TrainsEntry.STOP_ID + " TEXT NOT NULL," +
                TrainsEntry.STOP_NAME + " TEXT NOT NULL," +
                TrainsEntry.DESTINATION_NAME + " TEXT NOT NULL," +
                TrainsEntry.ROUTE + " TEXT NOT NULL," +
                TrainsEntry.ARRIVAL_TIME + " TEXT NOT NULL," +
                TrainsEntry.IS_DUE + " TEXT NOT NULL" +
                ");";

        db.execSQL(SQL_CREAT_TRAINS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXIST" + TrainsEntry.TABLE_NAME);
        onCreate(db);
    }



}

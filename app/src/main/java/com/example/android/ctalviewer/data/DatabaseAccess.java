package com.example.android.ctalviewer.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.example.android.ctalviewer.Lstop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Ryan on 3/13/2017.
 */

public class DatabaseAccess {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private static DatabaseAccess instance;

    public DatabaseAccess(Context context) {
        openHelper = new DatabaseOpenHelper(context);
    }

    public static DatabaseAccess getInstance(Context context){
        if(instance == null){
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    public void open(){
        this.database = openHelper.getWritableDatabase();
    }

    public void close(){
        if(database != null){
            this.database.close();
        }
    }

    public List<Lstop> getStops(){
        List<Lstop> list = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM stops",null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            list.add(new Lstop(cursor.getString(cursor.getColumnIndex("STOP_ID")),
                    cursor.getString(cursor.getColumnIndex("STOP_NAME")),
                    cursor.getString(cursor.getColumnIndex("Location"))));
            cursor.moveToNext();
        }


        cursor.close();
        return list;
    }
}

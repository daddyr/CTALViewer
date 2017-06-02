package com.example.android.ctalviewer.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Ryan on 3/3/2017.
 */

public class CTAContract {
    public static final String CONTENT_AUTHORITY = "com.example.android.ctalviewer";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_TRAINS = "trains";
    public static final String PATH_STOPS = "stops";

    public static final class TrainsEntry implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_TRAINS)
                .build();

        public static final String TABLE_NAME = "trains";
        public static final String TIMESTAMP = "timestamp";
        public static final String STOP_ID = "stop_id";
        public static final String STOP_NAME = "stop_name";
        public static final String DESTINATION_NAME = "destination_name";
        public static final String ROUTE = "route";
        public static final String ARRIVAL_TIME = "arrival_time";
        public static final String IS_DUE = "is_due";
    }

    public static final class StopsEntry implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_STOPS)
                .build();

        public static final String TABLE_NAME = "stops";
        public static final String COLUMN_STOP_ID = "STOP_ID";
        public static final String COLUMN_DIRECTION_ID = "DIRECTION_ID";
        public static final String COLUMN_STOP_NAME = "STOP_NAME";
        public static final String COLUMN_STATION_NAME= "STATION_NAME";
        public static final String COLUMN_STATION_DESCRIPTION_NAME = "STATION_DESCRIPTIVE_NAME";
        public static final String COLUMN_MAP_ID = "MAP_ID";
        public static final String COLUMN_ADA = "ADA";
        public static final String COLUMN_RED = "RED";
        public static final String COLUMN_BLUE = "BLUE";
        public static final String COLUMN_G = "G";
        public static final String COLUMN_BRN = "BRN";
        public static final String COLUMN_P = "P";
        public static final String COLUMN_PEXP = "Pexp";
        public static final String COLUMN_Y = "Y";
        public static final String COLUMN_PNK = "Pnk";
        public static final String COLUMN_O = "O";
        public static final String COLUMN_LOCATION = "Location";
    }
}

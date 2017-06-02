package com.example.android.ctalviewer.Utilities;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.android.ctalviewer.ArrivalsViewer;
import com.example.android.ctalviewer.R;
import com.example.android.ctalviewer.data.CTAContract;
import com.example.android.ctalviewer.data.CTADbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by Ryan on 2/14/2017.
 */

public class TrainUtils {
    public static final int HOURS = 0;
    public static final int MINUTES = 1;
    public static final int SECONDS = 2;

    public static final int STATION = 0;
    //public static final int CURRENT_TIME = 1;
    public static final int FIRST_TRAIN = 1;

    public static String formatTimeString(String s, String currentTime){
        String newTimeString = getTimePortion(s);

        int hours;
        int minutes;
        int seconds;

        Calendar calendar = Calendar.getInstance();

        int cur_hour = calendar.get(Calendar.HOUR_OF_DAY);
        int cur_min = calendar.get(Calendar.MINUTE);
        int cur_sec = calendar.get(Calendar.SECOND);


        int[] currentTimeArray = {calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),calendar.get(Calendar.SECOND)};
        int[] arrivalTimeArray = getTimeValues(newTimeString);

        if(compareTimes(arrivalTimeArray,currentTimeArray) < 0)
            return "passed";

        if(arrivalTimeArray[HOURS] > currentTimeArray[HOURS]){
            arrivalTimeArray[HOURS] += 24;
        }

        hours = arrivalTimeArray[HOURS] - currentTimeArray[HOURS];

        if(hours > 0){
            arrivalTimeArray[MINUTES] += 60;
        }

        minutes = arrivalTimeArray[MINUTES] - currentTimeArray[MINUTES];

        if(arrivalTimeArray[SECONDS] < currentTimeArray[SECONDS]){
            arrivalTimeArray[SECONDS] += 60;
        }

        seconds = arrivalTimeArray[SECONDS] - currentTimeArray[SECONDS];

        if(seconds >= 30)
            minutes++;

        if(minutes > 0)
            newTimeString = String.valueOf(minutes) + " Minutes";
        else
            newTimeString = "DUE";


        return newTimeString;
    }

    private static int compareTimes(int[] time1, int[] time2){

        int t1 = getTimeInt(time1);
        int t2 = getTimeInt(time2);

        return t1-t2;

    }
    private static int getTimeInt(int[] time){
        return time[0]*3600+time[1]*60+time[2];
    }

    public static int[] getTimeValues(String timeString){
        String[] timeStringArray = timeString.split(":");

        int[] newTimeArray = new int[3];

        for(int i = 0; i < timeStringArray.length; i++){
            newTimeArray[i] = (int)Integer.valueOf(timeStringArray[i]);
        }

        return newTimeArray;
    }

    public static String getTimePortion(String time){
        String[] timeArrayString = time.split("T");

        return timeArrayString[timeArrayString.length-1];
    }

    public static ContentValues[] parseJSON(String s, Context context){

        String[] newString = null;

        JSONObject data = null;

        CTADbHelper dbHelper = new CTADbHelper(context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentResolver resolver = context.getContentResolver();

        resolver.delete(CTAContract.TrainsEntry.CONTENT_URI,null,null);

        resolver.notifyChange(CTAContract.TrainsEntry.CONTENT_URI,null);

        ContentValues[] trainValues = null;

        try {
            data = new JSONObject(s);
            JSONObject ctta = data.getJSONObject("ctatt");

            String station = null;

            String currentTime = TrainUtils.getTimePortion(ctta.getString("tmst"));
            JSONArray eta = ctta.getJSONArray("eta");
            trainValues = new ContentValues[eta.length()];

            //List<ContentValues> valuesList = new ArrayList<ContentValues>();
            for(int i = 0; i < eta.length(); i++) {
                JSONObject arrival = eta.getJSONObject(i);
                String stpId = arrival.getString("stpId");
                station = arrival.getString("staNm");
                String destination = arrival.getString("stpDe");
                String route = arrival.getString("rt");
                String arrivalTime = TrainUtils.getTimePortion(arrival.getString("arrT"));
                String isApp = arrival.getString("isApp");

                ContentValues cv = new ContentValues();
                cv.put(CTAContract.TrainsEntry.TIMESTAMP,currentTime);
                cv.put(CTAContract.TrainsEntry.STOP_ID,stpId);
                cv.put(CTAContract.TrainsEntry.STOP_NAME,station);
                cv.put(CTAContract.TrainsEntry.DESTINATION_NAME,destination);
                cv.put(CTAContract.TrainsEntry.ROUTE,route);
                cv.put(CTAContract.TrainsEntry.ARRIVAL_TIME,arrivalTime);
                cv.put(CTAContract.TrainsEntry.IS_DUE,isApp);
                trainValues[i] = cv;

                station = station + " " +destination;
                //ArrivalsViewer.stopCoordinates[0] = Double.valueOf(arrival.getString("lat"));
                //ArrivalsViewer.stopCoordinates[1] = Double.valueOf(arrival.getString("lon"));
            }

            //resolver.bulkInsert(CTAContract.TrainsEntry.CONTENT_URI,valuesList.toArray(new ContentValues[valuesList.size()]));


            //newString[CURRENT_TIME] = currentTime;

        }catch (JSONException e){
            e.printStackTrace();
        }

        return trainValues;
    }

    public static String getRouteString(String code){
        switch(code){
            case "Red":
                return "Red Line";
            case "Blue":
                return "Blue Line";
            case "Brn":
                return "Brown Line";
            case "G":
                return "Green Line";
            case "Org":
                return "Orange Line";
            case "P":
                return "Purple Line";
            case "Pink":
                return "Pink Line";
            case "Y":
                return "Yellow Line";
            default:
                return null;
        }

    }

    public static int getTrainColor(String code){
        switch(code){
            case "Red":
                return R.color.trainRed;
            case "Blue":
                return R.color.trainBlue;
            case "Brn":
                return R.color.trainBrown;
            case "G":
                return R.color.trainGreen;
            case "Org":
                return R.color.trainOrange;
            case "P":
                return R.color.trainPurple;
            case "Pink":
                return R.color.trainPink;
            case "Y":
                return R.color.trainYellow;
            default:
                return -1;
        }

    }

}

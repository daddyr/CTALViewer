package com.example.android.ctalviewer.data;

import android.content.SharedPreferences;
import android.content.Context;
import android.content.res.Resources;
import android.support.v7.preference.PreferenceManager;

import com.example.android.ctalviewer.R;

/**
 * Created by Ryan on 2/21/2017.
 */

public class CTAViewerPreferences {

    public static final String PREF_LOCATION = "30263";

    //(41.886988, -87.793783)
    public static double[] stopCoordinates = {41.886988, -87.793783};

    public static final String KEY_TEXT = "text";

    public static final String[][] routeCodes = new String[][]{
            {"Red", "Red"},
            {"Blue", "Blue"},
            {"Brn", "Brown"},
            {"G","Green"},
            {"Org","Orange"},
            {"P","Purple"},
            {"Pink","Pink"},
            {"Y","Yellow"}
    };

    public static String getPreferredStopLocation(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Resources res = context.getResources();
        return sharedPreferences.getString(
                res.getString(R.string.pref_location_key),
                res.getString(R.string.pref_location_opeast_value)
        );

    }

}

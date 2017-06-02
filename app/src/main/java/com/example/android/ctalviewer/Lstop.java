package com.example.android.ctalviewer;

/**
 * Created by Ryan on 3/14/2017.
 */

public class Lstop {
    private String stopID;
    private String stopName;
    private double[] location;

    public Lstop(String stop_id, String stop_name, String location_string) {
        stopID = stop_id;
        stopName = stop_name;
        location = parseLocation(location_string);
    }

    private double[] parseLocation (String location_string){
        location_string = location_string.substring(1,location_string.length()-2);
        String[] splitLocation = location_string.split(",");
        double[] loc = new double[2];
        for (int i=0;i<splitLocation.length;i++)
            loc[i] = Double.parseDouble(splitLocation[i]);

        return loc;
    }

    public String getStopID() {
        return stopID;
    }

    public String getStopName() {
        return stopName;
    }

    public double[] getLocation() {
        return location;
    }
}

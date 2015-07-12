package com.iprodev.spotifystreamer;

/**
 * Created by curtis on 7/12/15.
 */
public class Utils {

    //Time constants
    public static final int ONE_HOUR = 3600000;
    public static final int ONE_MINUTE = 60000;
    public static final int ONE_SECOND = 1000;

    /**
     * Converts relative milliseconds into a user friendly string.
     * Example 1hour 3minutes and 25seconds (3805000 milliseconds) returns 1:03:25
     * @param millis relative milliseconds.
     * @return user freindly string.
     */
    public static String convertMilliToFriendlyText(int millis) {
        String retVal = "";
        int hours = 0;
        int minutes = 0;
        int seconds = 0;

        //Extract hour
        if (millis > ONE_HOUR) {
            float f = millis / ONE_HOUR;
            hours = (int) Math.floor(f);
        }
        //Extract minutes
        if (hours > 0) {
            int tmp = millis - (hours * ONE_HOUR);
            float f = tmp / ONE_MINUTE;
            minutes = (int) Math.floor(f);
        } else {
            float f = millis / ONE_MINUTE;
            minutes = (int) Math.floor(f);
        }
        if (millis > ONE_MINUTE) {
        }
        //Extract seconds
        if (hours > 0) {
            int tmp = millis - (hours * ONE_HOUR);
            tmp = tmp - (minutes * ONE_MINUTE);
            seconds = (int) Math.floor(tmp / ONE_SECOND);
        } else if (minutes > 0) {
            int tmp = millis - (minutes * ONE_MINUTE);
            seconds = (int) Math.floor(tmp / ONE_SECOND);
        } else {
            seconds = (int) Math.floor(millis / ONE_SECOND);
        }
        if (seconds < 10) {
            retVal = (hours > 0) ? hours + ":" : "" + minutes + ":0" + seconds;
        } else {
            retVal = (hours > 0) ? hours + ":" : "";
            retVal = retVal + minutes + ":" + seconds;
        }

        return retVal;
    }
}

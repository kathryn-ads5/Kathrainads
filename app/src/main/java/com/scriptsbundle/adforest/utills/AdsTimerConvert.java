package com.kathryn.kathryn.utills;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class AdsTimerConvert {
    public static long adforest_bidTimer(JSONArray jsonArray) {
        String sday = null, shours = null, smintues = null, ssconds = null;
        try {
            sday = jsonArray.getString(0);
            shours = jsonArray.getString(1);
            smintues = jsonArray.getString(2);
            ssconds = jsonArray.getString(3);


        } catch (JSONException e) {
            e.printStackTrace();
        }

//                long time4 = (long)12*24*60*60*1000;
        String defaultDate = "1970-01-01 00:00:00";  // Start date
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        TimeZone tz = TimeZone.getDefault();
//        Log.d("info date   ", "" + tz.getDisplayName(false, TimeZone.SHORT) + " Timezon id :: " + tz.getID());
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        simpleDateFormat.setTimeZone(timeZone);
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(simpleDateFormat.parse(defaultDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.add(Calendar.DATE, Integer.parseInt(sday));
        calendar.add(Calendar.MONTH, 0);
        calendar.add(Calendar.HOUR_OF_DAY, Integer.parseInt(shours));
        calendar.add(Calendar.MINUTE, Integer.parseInt(smintues));
        calendar.add(Calendar.SECOND, Integer.parseInt(ssconds));

        String output = simpleDateFormat.format(calendar.getTime());
        simpleDateFormat.setLenient(false);
        Date oldDate = null;
        try {
            oldDate = simpleDateFormat.parse(output);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long timer = oldDate.getTime();
        return timer;
    }
}

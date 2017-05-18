package com.fypj.icreative.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

public class Utils {
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;


    public String fromMiliSecToDateString(long epochLong, String dateFormat) {
//      long epochLongTime = epochLong + ((60 * 60 * 12) * 1000);
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        return formatter.format(epochLong);
    }

    public String fromEpochToDateString(long epochLong, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        return formatter.format((epochLong*1000));
    }

    public long fromDateToUnixEpoch(Date currentDate) {
        return (currentDate.getTime());
    }

    public Date getTodayDateWithoutTime() {
        Calendar calendar = Calendar.getInstance();
        setTimeToBeginningOfDay(calendar);
        return calendar.getTime();
    }

    public Date getEndOfTodayDate() {
        Calendar calendar = Calendar.getInstance();
        setTimeToEndOfDay(calendar);
        return calendar.getTime();
    }

    public Date getFourDaysFromToday() {
        Calendar calendar = Calendar.getInstance();
        setTimeToEndOfDay(calendar);
        calendar.add(Calendar.DAY_OF_MONTH, -4);
        return calendar.getTime();
    }

    public Date getFourWeeksFromToday() {
        Calendar calendar = Calendar.getInstance();
        setTimeToEndOfDay(calendar);
        calendar.add(Calendar.WEEK_OF_YEAR, -4);
        return calendar.getTime();
    }

    public Date getFourMonthsFromToday() {
        Calendar calendar = Calendar.getInstance();
        setTimeToEndOfDay(calendar);
        calendar.add(Calendar.MONTH, -4);
        return calendar.getTime();
    }

    public Date[] getStartAndEndOfTheWeek() {
        Calendar cal = Calendar.getInstance();
        setTimeToBeginningOfDay(cal);
        Calendar beginning = (Calendar) cal.clone();
        beginning.set(Calendar.DAY_OF_WEEK, beginning.getFirstDayOfWeek());

        Calendar end = (Calendar) beginning.clone();
        end.add(Calendar.WEEK_OF_YEAR, 1);
        Date[] dateArr = new Date[]{beginning.getTime(), end.getTime()};
        return dateArr;
    }

    public Date[] getStartAndEndOfMonth() {
        Calendar cal = Calendar.getInstance();
        setTimeToBeginningOfDay(cal);
        Calendar beginning = (Calendar) cal.clone();
        beginning.set(Calendar.DAY_OF_MONTH, 1);

        Calendar end = (Calendar) beginning.clone();
        end.add(Calendar.MONTH, 1);
        Date[] dateArr = new Date[]{beginning.getTime(), end.getTime()};
        return dateArr;
    }

    public void setTimeToBeginningOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    public void setTimeToEndOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight, int postRotate) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);
        if (postRotate != 0) {
            matrix.postRotate(postRotate);
        }

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    public String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuilder sb = new StringBuilder();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            Log.d("downloadUrl", data);
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    public long getInternetTime() {
        long returnTime = 0;
        try {
            NTPUDPClient timeClient = new NTPUDPClient();
            InetAddress inetAddress = InetAddress.getByName("0.sg.pool.ntp.org");
            TimeInfo timeInfo = timeClient.getTime(inetAddress);
            returnTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return returnTime;
    }
}

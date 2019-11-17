package com.harshsharma.musify.utilites;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.harshsharma.musify.models.Track;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

//Utilitiy for Common Tasks in the App
public class MusifyUtil {
    public static long getTrackId(Track track) {
        String trackString = track.getSong() + track.getUrl() + track.getArtists() + track.getCover_image();
        long trackId = 0;
        char[] trackCharArray = trackString.toCharArray();
        for (char c : trackCharArray) {
            trackId = trackId + ((int) c);
        }
        return trackId;
    }

    public static Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}

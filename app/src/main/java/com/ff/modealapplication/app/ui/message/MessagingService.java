package com.ff.modealapplication.app.ui.message;

import android.util.Log;

import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by BIT on 2017-02-06.
 */

public class MessagingService {

    public static final String FCM_URL = "https://fcm.googleapis.com/fcm/send";
    public static final String FCM_SERVER_API_KEY = "AIzaSyDf1ekCTEBiFu049x5spOd0rZu4gSsQV4A";
    private static final String deviceRegistrationId = "909125768226";

    public static void send(String title, String body) {
        try {
            Log.d("----", "1111");
            URL url = new URL(FCM_URL);
            HttpsURLConnection httpURLConnection = (HttpsURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(10000);
            httpURLConnection.setReadTimeout(10000);
            Log.d("----", "2222");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);
            Log.d("----", "3333");
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Authorization", "key=" + FCM_SERVER_API_KEY);
            Log.d("----", "4444");
            JSONObject json = new JSONObject();
            json.put("to", "/topics/notice");
            JSONObject notification = new JSONObject();
            notification.put("title", "알람제목");
            notification.put("body", "알람내용");
            json.put("notification", notification);
            Log.d("----", "5555");
            Log.w("---------------->", json.toString());

            OutputStreamWriter wr = new OutputStreamWriter(httpURLConnection.getOutputStream());
            wr.write(json.toString());
            wr.flush();
            wr.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
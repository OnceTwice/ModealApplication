package com.ff.modealapplication.app.ui.message;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by BIT on 2017-02-06.
 */

public class MessagingService {

    public static final String FCM_URL = "https://fcm.googleapis.com/fcm/send";
    public static final String FCM_SERVER_API_KEY = "AIzaSyDf1ekCTEBiFu049x5spOd0rZu4gSsQV4A";

    public static void send(String title, String body) {
        try {
            URL url = new URL(FCM_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(10000);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "key=" + FCM_SERVER_API_KEY);

            JSONObject json = new JSONObject();
            json.put("to", "/topics/notice");
            JSONObject noti = new JSONObject();
            noti.put("title", title);
            noti.put("body", body);
            json.put("notification", noti);

            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.write(json.toString().getBytes()); // 한글깨짐방지
            wr.flush();
            wr.close();

            conn.getResponseCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
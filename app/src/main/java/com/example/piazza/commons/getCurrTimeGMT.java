package com.example.piazza.commons;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.piazza.controladores.employee.fragments.introduir_hores.IntroduirHoresFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.time.ZonedDateTime;

import javax.xml.transform.Result;


public class getCurrTimeGMT extends AsyncTask<String, Void, String> {

    private static final String LOG_TAG = "LOG";
    public static ZonedDateTime zdt;

    public static ZonedDateTime getZoneDateTime(String s) {

        if (s != null) {
            try {
                JSONObject jsonResponse = new JSONObject(s);
                ZonedDateTime jsonArray = ZonedDateTime.parse(jsonResponse.getString("datetime"));

                return jsonArray;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;

    }


    public ZonedDateTime getCurrTimeGMT() {
        return this.zdt;
    }

    public void setZdt(ZonedDateTime zdt) {
        this.zdt = zdt;
    }

    static String collectInfoGMT(){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String bookJSONString = null;

        try {

            URL requestURL = new URL("https://worldtimeapi.org/api/timezone/Europe/Madrid");

            urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            try {
                InputStream inputStream = urlConnection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder builder = new StringBuilder();

                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                    builder.append("\n");
                }
                if (builder.length() == 0) {
                    return null;
                }

                bookJSONString = builder.toString();
            } catch (Exception e) {
                Log.d(LOG_TAG, "CIUTAT NO TROBADA");
                return null;
            }





        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        Log.d(LOG_TAG, bookJSONString);
        return bookJSONString;
    }

    @Override
    protected String doInBackground(String... strings) {

        return collectInfoGMT();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if (s != null) {
            try {
                JSONObject jsonResponse = new JSONObject(s);
                ZonedDateTime jsonArray = ZonedDateTime.parse(jsonResponse.getString("datetime"));

                setZdt(jsonArray);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }



}
package com.example.mililivantilili.nfcsecurity;

import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;


import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class DBAccess
{
    private static final String URLwebApi = "http://www.penezniustavjakcyp.jecool.net/api/";

    private DbAccessListener activityContext;


    public DBAccess(Object context)
    {
        this.activityContext = (DbAccessListener)context;
    }

    void LogIN(String NFC_ID)
    {
        DownloadJSON getJSON = new DownloadJSON();
        getJSON.execute("logIN", "INP_NFC_ID", NFC_ID);
    }

    void LoadUser(int userID)
    {
        DownloadJSON getJSON = new DownloadJSON();
        getJSON.execute("loadUser", "INP_userID", String.valueOf(userID));
    }

    void TestConnection()
    {
        DownloadJSON getJSON = new DownloadJSON();
        getJSON.execute("testConn");
    }



    private class DownloadJSON extends AsyncTask<String, Void, JSONObject>
    {

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }


        @Override
        protected void onPostExecute(JSONObject s)
        {
            super.onPostExecute(s);

            activityContext.DbAccess_callback(s);
        }

        @Override
        protected JSONObject doInBackground(String... params)
        {
            try
            {
                URL url = new URL(URLwebApi + params[0] + ".php");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setReadTimeout(10000);
                con.setConnectTimeout(15000);

                if(params.length > 1)
                {
                    StringBuilder postData = new StringBuilder();

                    for (int i = 1; i < params.length; i += 2)
                    {
                        if (postData.length() != 0)
                        {
                            postData.append('&');
                        }
                        postData.append(URLEncoder.encode(params[i], "UTF-8"));
                        postData.append('=');
                        postData.append(URLEncoder.encode(params[i + 1], "UTF-8"));
                    }
                    byte[] postDataBytes = postData.toString().getBytes("UTF-8");

                    con.setRequestMethod("POST");
                    con.getOutputStream().write(postDataBytes);
                }

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuffer sb = new StringBuffer();

                String line = "";
                while ((line = bufferedReader.readLine()) != null)
                {
                    sb.append(line);
                }

                String finalJson = sb.toString();

                return new JSONObject(finalJson);
            }
            catch (Exception e)
            {
                return null;
            }
        }
    }
}

class User
{
    public int ID;
    public String NFC_ID;
    public String Jmeno;
    public String Prijmeni;
    public double Zustatek;

    public User()
    {

    }

    static User fromJson(JSONObject json)
    {
        return new Gson().fromJson(json.toString(), User.class);
    }
}

interface DbAccessListener
{
    void DbAccess_callback(JSONObject json);
}
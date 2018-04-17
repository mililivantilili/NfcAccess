package com.example.mililivantilili.nfcsecurity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.crypto.spec.GCMParameterSpec;


public class LogedUserActivity extends AppCompatActivity implements DbAccessListener {
    TextView TvName, TvSurname, TvZustatek;
    User logedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.JAKCYP);
        setContentView(R.layout.activity_loged_user);

        TvName = (TextView) findViewById(R.id.TvName);
        TvSurname = (TextView) findViewById(R.id.TvSurname);
        TvZustatek = (TextView) findViewById(R.id.TvZustatek);


        Intent intent = getIntent();
        int userID = intent.getIntExtra(MainActivity.INTENT_USER_ID, -1);

        DBAccess dbAccess = new DBAccess(this);
        dbAccess.LoadUser(userID);


        //TvUserID.setText(userID);

    }

    public void BtnLogOff(View view) {
        finish();
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void DbAccess_callback(JSONObject json) {
        if (json != null) {
            try
            {
                String cmd = json.getString("cmd");

                if (cmd.equals("userOK"))
                {
                    logedUser = User.fromJson(json.getJSONObject("data"));
                    showUser();
                }
                else if (cmd.equals("userNOK"))
                {

                }
                else
                {

                }
            }
            catch (Exception e)
            {

            }
        }
    }

    private void showUser()
    {
        TvName.setText(logedUser.Jmeno);
        TvSurname.setText(logedUser.Prijmeni);
        TvZustatek.setText(String.valueOf(logedUser.Zustatek) + " kč");
    }

}

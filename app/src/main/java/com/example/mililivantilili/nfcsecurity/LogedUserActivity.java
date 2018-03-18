package com.example.mililivantilili.nfcsecurity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class LogedUserActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loged_user);

        TextView TvUserID = (TextView)findViewById(R.id.TvName);

        Intent intent = getIntent();
        String UserID = intent.getStringExtra(MainActivity.INTENT_USER_ID);

        TvUserID.setText(UserID);

    }

    public void BtnLogOff(View view)
    {
        finish();
    }

    @Override
    public void onBackPressed()
    {

    }
}

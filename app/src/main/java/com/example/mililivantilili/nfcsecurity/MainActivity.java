package com.example.mililivantilili.nfcsecurity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements DbAccessListener
{
    public static final String INTENT_USER_ID = "com.example.mililivantilili.nfcsecurity.UseID";

    PendingIntent pendingIntent;
    //IntentFilter writeTagFilters[];
    String TAG = "NfcMain";
    Tag myTag;
    NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter == null)
        {
            // NFC is not available on the device.
            Log.d(TAG, String.valueOf(R.string.NFC_NOT_AVAILABLE));
        }
        else
        {
            pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

            /*readFromIntent(getIntent());
            IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
            tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
            writeTagFilters = new IntentFilter[] { tagDetected };*/
            /* Nelze programově zapnout NFC takže tohle otevře nastavení NFC a napíše uživateli, ať ho zapne*/
            if (!nfcAdapter.isEnabled())
            {
                Toast.makeText(getApplicationContext(), String.valueOf(R.string.ACTIVE_NFC_AND_RETURN), Toast.LENGTH_LONG).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
                }
                else
                {
                    // Snad to jede, nemám to na čem ověřit
                    startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                }
            }
        }

    }

    private void readFromIntent(Intent intent)
    {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action) || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action) || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action))
        {
            Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            byte[] extraID = tagFromIntent.getId();
            DBAccess dbAccess = new DBAccess(this);

            StringBuilder sb = new StringBuilder();
            for (byte b : extraID)
            {
                sb.append(String.format("%02X", b));
            }
            String userID = sb.toString();
            Log.d(TAG,"ID: " + userID);
            dbAccess.LogIN(userID);
            //logIn(UserID);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    public void onBackPressed()
    {
        // Deaktivace tlačítka zpět
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        Log.d(TAG,"onNewIntent");
        setIntent(intent);
        readFromIntent(intent);
        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
            myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        }
    }

    private void logIn(int userID)
    {
        Intent intent = new Intent(MainActivity.this, LogedUserActivity.class);

        intent.putExtra(INTENT_USER_ID, userID);
        startActivity(intent);
    }

    public void BtnAdmin(View view)
    {
        logIn(1);
    }

    @Override
    public void DbAccess_callback(JSONObject json)
    {
        if (json != null)
        {
            try
            {
                String cmd = json.getString("cmd");
                if (cmd.equals("logOK"))
                {
                    int userID = json.getInt("data");
                    Intent intent = new Intent(MainActivity.this, LogedUserActivity.class);

                    intent.putExtra(INTENT_USER_ID, userID);
                    startActivity(intent);
                }
                else if (cmd.equals("logNOK"))
                {
                    Toast.makeText(this, R.string.UNKNOWN_USER, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(this, R.string.SERVER_CONN_ERROR, Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception e)
            {
                Toast.makeText(this, R.string.SERVER_CONN_ERROR, Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(this, R.string.SERVER_CONN_ERROR, Toast.LENGTH_SHORT).show();
        }
    }
}

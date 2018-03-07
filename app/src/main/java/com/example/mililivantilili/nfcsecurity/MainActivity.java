package com.example.mililivantilili.nfcsecurity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity
{

    PendingIntent pendingIntent;
    IntentFilter writeTagFilters[];
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
            Log.d(TAG,"NFC is not available, Use phone with NFC");
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
                Toast.makeText(getApplicationContext(), "Please activate NFC and press Back to return to the application!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
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

            StringBuilder sb = new StringBuilder();
            for (byte b : extraID) {
                sb.append(String.format("%02X", b));
            };
            Log.d(TAG,"ID: " + sb.toString());
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
}

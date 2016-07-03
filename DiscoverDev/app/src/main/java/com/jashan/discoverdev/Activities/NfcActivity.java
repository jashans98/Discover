package com.jashan.discoverdev.Activities;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.jashan.discoverdev.Model.ParseConstants;
import com.jashan.discoverdev.R;

import java.nio.charset.Charset;
import java.util.Locale;

public class NfcActivity extends Activity {

    public static final String TAG = NfcActivity.class.getSimpleName();

    String mSectionId;
    String mMuseumId;

    NfcAdapter mNfcAdapter;
    NdefMessage mNdefMessage;

    IntentFilter[] intentFiltersArray;

    String [] [] techListsArray;

    PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);

        Intent intent = getIntent();
        mMuseumId = intent.getStringExtra(ParseConstants.KEY_SECTION_PARENT);
        mSectionId = intent.getStringExtra(ParseConstants.KEY_SECTION_IDS);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter == null){
            Toast.makeText(this, getString(R.string.no_nfc_error), Toast.LENGTH_LONG).show();
        }




        pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndef.addDataType("*/*");    /* Handles all MIME based dispatches.
                                       You should specify only the ones that you need. */
        }
        catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }

        intentFiltersArray = new IntentFilter[] {ndef};

        techListsArray = new String[][] { new String[] { MifareUltralight.class.getName(),
        Ndef.class.getName()} };

        Locale locale = Locale.ENGLISH;

        NdefRecord r1 = NdefRecord.createApplicationRecord("com.jashan.discover");
        NdefRecord r2 = createTextRecord(mMuseumId, locale, true);
        NdefRecord r3 = createTextRecord(mSectionId, locale, true);
        NdefRecord [] records = {r1, r2, r3};

        mNdefMessage = new NdefMessage(records);

    }


    public NdefRecord createTextRecord(String payload, Locale locale, boolean encodeInUtf8) {
        byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));
        Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16");
        byte[] textBytes = payload.getBytes(utfEncoding);
        int utfBit = encodeInUtf8 ? 0 : (1 << 7);
        char status = (char) (utfBit + langBytes.length);
        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        data[0] = (byte) status;
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);
        NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
                NdefRecord.RTD_TEXT, new byte[0], data);
        return record;
    }

    public void onPause() {
        super.onPause();
        mNfcAdapter.disableForegroundDispatch(this);
    }

    public void onResume() {
        super.onResume();
        mNfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        write(tag);
    }

    private void write(Tag tag) {
        Ndef ndef = Ndef.get(tag);
        if (tag == null){
            Toast.makeText(this, getString(R.string.not_ndef_error), Toast.LENGTH_LONG).show();
        }
        else{
            try{
                ndef.connect();
                ndef.writeNdefMessage(mNdefMessage);
                Toast.makeText(this, "Data written", Toast.LENGTH_LONG).show();
            }
            catch (Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
            finally {
                try{
                    ndef.close();
                }
                catch (Exception e){
                    Log.e(TAG, e.getMessage());
                }
            }
        }

    }
}

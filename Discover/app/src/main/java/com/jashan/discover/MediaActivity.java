package com.jashan.discover;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Locale;


public class MediaActivity extends Activity {

    ImageView mImageView;
    Button mStoreButton;
    NfcAdapter mNfcAdapter;

    MediaPlayer mMediaPlayer;

    IntentFilter [] intentFiltersArray;

    String [] [] techListsArray;

    PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);

        mImageView = (ImageView) findViewById(R.id.playButton);
        mStoreButton = (Button) findViewById(R.id.storeButton);

        mStoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToStore();
            }
        });
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        Locale locale = new Locale("en");

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

        techListsArray = new String[][] { new String[] { MifareUltralight.class.getName() } };

    }

    private void navigateToStore() {
        Intent intent = new Intent(this, StoreActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        mMediaPlayer = new MediaPlayer();
        NdefRecord [] records;
        records = getRecords(tagFromIntent);
        String museumId = getTextFromNdefRecord(records[1]);
        String objectId = getTextFromNdefRecord(records[2]) + ".mp4";
        File file1 = new File (Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), "DiscoverAudio");
        File file2 = new File(file1, museumId);
        File file3 = new File(file2, objectId);

        Uri refUri = Uri.parse(file3.getAbsolutePath());
        Toast.makeText(this, file3.getAbsolutePath(), Toast.LENGTH_LONG).show();
        try {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setDataSource(getApplicationContext(), refUri);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        }
        catch (Exception e){
            Log.e("MediaActivity", e.toString());
        }
    }

    private NdefRecord[] getRecords(Tag tagFromIntent) {
        Ndef ndef = Ndef.get(tagFromIntent);

        try{
            ndef.connect();
            NdefRecord[] ndefRecords = ndef.getNdefMessage().getRecords();
            return ndefRecords;
        }
        catch (Exception e){

        }
        return null;
    }

    public String getTextFromNdefRecord(NdefRecord ndefRecord)
    {
        String tagContent = null;
        try {
            byte[] payload = ndefRecord.getPayload();
            String textEncoding = "UTF-8";
            int languageSize = payload[0] & 0063;
            tagContent = new String(payload, languageSize + 1,
                    payload.length - languageSize - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            Log.e("getTextFromNdefRecord", e.getMessage(), e);
        }
        return tagContent;


    }

    public void onPause() {
        super.onPause();
        mNfcAdapter.disableForegroundDispatch(this);
    }

    public void onResume() {
        super.onResume();
        mNfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray);
    }


}

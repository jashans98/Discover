package com.jashan.discoverdev.Activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jashan.discoverdev.Model.FileHelper;
import com.jashan.discoverdev.Model.ParseConstants;
import com.jashan.discoverdev.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.io.File;
import java.util.List;

public class EditSectionActivity extends Activity {

    public static final String TAG = EditSectionActivity.class.getSimpleName();

    private EditText mSectionNameText;
    private Button mBrowseButton;
    private Button mUploadButton;

    private Uri mMediaUri = null;

    private String mMuseumId;

    public static final int CHOOSE_FILE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_section);

        mSectionNameText = (EditText) findViewById(R.id.sectionNameText);
        mBrowseButton = (Button) findViewById(R.id.browseButton);
        mUploadButton = (Button) findViewById(R.id.uploadWriteButton);

        mMuseumId = getIntent().getStringExtra("museumId");

        Log.i(TAG, mMuseumId);

        mBrowseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("audio/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(intent, CHOOSE_FILE);
            }
        });

        mUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadToParse(mMediaUri);
            }
        });
    }

    private void uploadToParse(Uri mediaUri) {

            Toast.makeText(this, "Saving", Toast.LENGTH_LONG).show();
            byte[] audioFile = FileHelper.getByteArrayFromFile(this, mediaUri);
            ParseObject section = new ParseObject(ParseConstants.CLASS_SECTION);
            String fileName = FileHelper.getFileName(this, mMediaUri, "audio");
            ParseFile file = new ParseFile(fileName, audioFile);
            section.put(ParseConstants.KEY_SECTION_AUDIO, file);
            section.put(ParseConstants.KEY_SECTION_NAME, mSectionNameText.getText().toString());
            section.put(ParseConstants.KEY_SECTION_PARENT, mMuseumId);
            section.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(EditSectionActivity.this,
                                getString(R.string.upload_seccessful),
                                Toast.LENGTH_LONG).show();

                        runParseQuery();


                    } else {
                        Log.e(TAG, e.getMessage());
                    }
                }

            });
        }

    private void runParseQuery() {
        ParseQuery <ParseObject> query = ParseQuery.getQuery("Section");
        query.whereEqualTo(ParseConstants.KEY_SECTION_NAME, mSectionNameText.getText().toString());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {

                Log.e(TAG, list.toString());
                String id = list.get(0).getObjectId();

                goToNFCActivity(id);
            }
        });
    }

    private void goToNFCActivity(String id) {
        Intent intent = new Intent(this, NfcActivity.class);
        intent.putExtra(ParseConstants.KEY_SECTION_PARENT, mMuseumId);
        intent.putExtra(ParseConstants.KEY_SECTION_IDS, id);
        startActivity(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            //success
            mMediaUri = data.getData();
            Toast.makeText(this, mMediaUri.toString(), Toast.LENGTH_LONG).show();

        }
        else{
            Toast.makeText(this, "Error selecting file", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_section, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

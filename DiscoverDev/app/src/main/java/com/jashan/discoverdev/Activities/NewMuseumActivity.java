package com.jashan.discoverdev.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jashan.discoverdev.Model.Museum;
import com.jashan.discoverdev.Model.ParseConstants;
import com.jashan.discoverdev.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class NewMuseumActivity extends Activity {

    public static final String TAG = NewMuseumActivity.class.getSimpleName();

    private EditText mMuseumText;
    private ProgressBar mProgressBar;
    private Button mAddButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_museum);
        mProgressBar = (ProgressBar) findViewById(R.id.spinnerNewMuseum);
        mMuseumText = (EditText) findViewById(R.id.museumNameText);
        mAddButton = (Button) findViewById(R.id.addMuseumButton);

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewMuseum();
            }
        });
    }

    private void createNewMuseum() {
        final Museum newMuseum = new Museum();
        newMuseum.setMuseumName(mMuseumText.getText().toString());
        newMuseum.setMasterUser();
        toggleProgressBar();
        newMuseum.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    //success :D
                    toggleProgressBar();
                    Toast.makeText(NewMuseumActivity.this, "Museum Created", Toast.LENGTH_LONG).show();
                    Log.i(TAG, newMuseum.getObjectId());


                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(NewMuseumActivity.this);
                    builder.setTitle(R.string.general_error)
                            .setMessage(e.getMessage())
                            .setPositiveButton(android.R.string.ok, null)
                            .create()
                            .show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_museum, menu);
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

    private void toggleProgressBar() {
        if (mProgressBar.getVisibility() == View.INVISIBLE){
            mProgressBar.setVisibility(View.VISIBLE);
        }
        else{
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }
}

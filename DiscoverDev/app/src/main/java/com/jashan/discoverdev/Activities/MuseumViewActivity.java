package com.jashan.discoverdev.Activities;


import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.jashan.discoverdev.Model.Museum;
import com.jashan.discoverdev.Model.ParseConstants;
import com.jashan.discoverdev.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.util.List;


/**
 * Author: Jashan Shewakramani
 *
 * Description: Activity to list all the user's created Museums
 */
public class MuseumViewActivity extends ListActivity {

    private Button mCreateMuseumButton;

    private ParseUser mCurrentUser;

    String [] mMuseumNames = new String[550];

    String [] mMuseumIds;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_museum_view);

        // get the current parse user
        ParseUser mCurrentUser = ParseUser.getCurrentUser();

        // if there is no user logged in, take them to LoginActivity
        if (mCurrentUser == null){
            navigateToLogin();
        }

        // option to create new museums
        mCreateMuseumButton = (Button) findViewById(R.id.addMuseumButton);
        mCreateMuseumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToCreateMuseum();
            }
        });

        // if there is a user logged in, display the museums
        if (mCurrentUser != null) {
            ParseQuery<Museum> query = ParseQuery.getQuery("Museum");
            // query parse to figure out which museums are owned by this user
            query.whereEqualTo(ParseConstants.KEY_MASTER_USER, mCurrentUser.getUsername());
            query.findInBackground(new FindCallback<Museum>() {
                @Override
                public void done(List<Museum> list, ParseException e) {
                    String[] museumNames = new String[list.size()];
                    String[] objectName = new String[list.size()];
                    for (int i = 0; i < list.size(); i++) {
                        museumNames[i] = list.get(i).getMuseumName();
                        objectName[i] = list.get(i).getObjectId();
                    }

                    mMuseumNames = museumNames;
                    mMuseumIds = objectName;
                    if (mMuseumNames != null) {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(MuseumViewActivity.this,
                                android.R.layout.simple_list_item_1,
                                mMuseumNames);
                        setListAdapter(adapter);
                    }
                }
            });

        }

    }

    private void navigateToCreateMuseum() {
        Intent intent = new Intent(this, NewMuseumActivity.class);
        startActivity(intent);
    }

    private void navigateToLogin() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_museum_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            mCurrentUser.logOut();
            navigateToLogin();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    // take the user to see the sections within that museum
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        String finalMuseumName = mMuseumNames[position];
        String finalObjectId = mMuseumIds[position];


        Intent intent = new Intent(this, SectionViewActivity.class);
        intent.putExtra(ParseConstants.KEY_MUSEUM_NAME, finalMuseumName);
        intent.putExtra("museumId", finalObjectId);

        startActivity(intent);
    }
}
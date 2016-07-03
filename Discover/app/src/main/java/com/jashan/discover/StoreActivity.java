package com.jashan.discover;

import android.app.ListActivity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;


public class StoreActivity extends ListActivity {

    List<ParseObject> mMuseums;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Museum");
        query.setLimit(20);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                mMuseums = list;
                String [] names = new String[list.size()];

                for (int i = 0; i<list.size(); i++){
                    names[i] = list.get(i).getString(ParseConstants.KEY_MUSEUM_NAME);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        StoreActivity.this,
                        android.R.layout.simple_list_item_1,
                        names
                );

                setListAdapter(adapter);
            }
        });


    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        String finalId = mMuseums.get(position).getObjectId();
        String finalName = mMuseums.get(position).getString(ParseConstants.KEY_MUSEUM_NAME);
        Intent intent = new Intent(this, DownloadActivity.class);
        intent.putExtra("museumId", finalId);
        intent.putExtra(ParseConstants.KEY_MUSEUM_NAME, finalName);
        startActivity(intent);
    }
}

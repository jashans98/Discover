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
import android.widget.TextView;
import com.jashan.discoverdev.Model.ParseConstants;
import com.jashan.discoverdev.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class SectionViewActivity extends ListActivity {

    //layout components
    private TextView mMuseumView;
    private Button mNewSectionButton;
    private String[] mSectionName;
    private String [] mSectionIds;
    private String mMuseumId;
    private String mMuseumName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section_view);

        Intent intent = getIntent();
        mMuseumId = intent.getStringExtra("museumId");
        mMuseumName = intent.getStringExtra(ParseConstants.KEY_MUSEUM_NAME);
        mMuseumView = (TextView) findViewById(R.id.museumNameText);

        mNewSectionButton = (Button) findViewById(R.id.newSectionButton);
        mNewSectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToEditSection();
            }
        });

        setup();


    }

    private void setup() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Section");
        query.whereEqualTo(ParseConstants.KEY_SECTION_PARENT, mMuseumId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                String [] temp = new String[list.size()];
                String [] tt = new String[list.size()];
                for (int i = 0; i < list.size(); i++){
                    temp[i] = (String) list.get(i).get(ParseConstants.KEY_SECTION_NAME);
                    tt[i] = list.get(i).getObjectId();
                }
                mSectionName = temp;
                mSectionIds = tt;

                if (mSectionName != null){
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(SectionViewActivity.this,
                            android.R.layout.simple_list_item_1,
                            mSectionName);
                    setListAdapter(adapter);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setup();
    }

    private void navigateToEditSection() {
        Intent editSectionIntent = new Intent(this, EditSectionActivity.class);
        editSectionIntent.putExtra("museumId", mMuseumId);
        startActivity(editSectionIntent);
    }

    private void navigateToEditSection (String id, String name){
        Intent editSectionIntent = new Intent(this, EditSectionActivity.class);
        editSectionIntent.putExtra("museumId", mMuseumId);
        editSectionIntent.putExtra(ParseConstants.KEY_SECTION_NAME, name);
        editSectionIntent.putExtra(ParseConstants.KEY_SECTION_IDS, id);
        startActivity(editSectionIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_section_view, menu);
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

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        String finalId = mSectionIds[position];
        String finalName = mSectionName[position];
        navigateToEditSection(finalId, finalName);
    }
}

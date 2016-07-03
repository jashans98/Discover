package com.jashan.discover;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.snowdream.android.util.concurrent.AsyncTask;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;


public class DownloadActivity extends Activity {

    Button mDownloadButton;
    TextView mLabelName;
    TextView mDownloadLabel;
    String mMuseumId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        mDownloadLabel = (TextView) findViewById(R.id.downloadLabel);

        Intent intent = getIntent();
        mMuseumId = intent.getStringExtra("museumId");
        String museumName = intent.getStringExtra(ParseConstants.KEY_MUSEUM_NAME);

        mLabelName = (TextView) findViewById(R.id.museumName);
        mLabelName.setText(museumName);

        mDownloadButton = (Button) findViewById(R.id.downloadButton);

        mDownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadSections();
            }
        });

    }

    private void downloadSections() {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Section");
        query.whereEqualTo(ParseConstants.KEY_SECTION_PARENT, mMuseumId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                for (int i = 0; i < list.size(); i++) {
                    ParseFile file = list.get(i).getParseFile(ParseConstants.KEY_SECTION_AUDIO);
                    String url = file.getUrl().toString();
                    String objectId = list.get(i).getObjectId();
                    int k = i +1 ;
                    mDownloadLabel.setText("Downloading File " + k+ " of " + list.size());
                    getStuff(url, objectId);
                }
            }
        });
    }

    private void getStuff(String url, String objectId) {

        DownloadManager.Request request= new DownloadManager.Request(Uri.parse(url));
        request.setTitle("Audio Download");
        request.setDescription("File is Being Downloaded");
        String extension = url.substring(url.lastIndexOf("."));
        File fileDir = new File(Environment.DIRECTORY_DOWNLOADS, "DiscoverAudio");
        File file = new File(fileDir, mMuseumId);
        fileDir.mkdirs();
        request.setDestinationInExternalPublicDir(file.getAbsolutePath(), objectId+extension);
        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);

    }



}

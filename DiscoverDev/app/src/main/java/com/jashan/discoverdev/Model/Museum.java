package com.jashan.discoverdev.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jashan on 11/05/2015.
 */
@ParseClassName("Museum")
public class Museum extends ParseObject implements Parcelable{

    String mMuseumId;
    String mMuseumName;
    List<String> mSectionIds;
    String mMasterUser;


    public Museum (){

    }

    public String getMuseumId(){
        return getObjectId();
    }
    public String getMuseumName() {
        return getString(ParseConstants.KEY_MUSEUM_NAME);
    }
    public void setMuseumName(String name) {
        put(ParseConstants.KEY_MUSEUM_NAME, name);
    }
    public List<String> getSectionIds(){
        List<String> al= new ArrayList<>();
        al = getList(ParseConstants.KEY_SECTION_IDS);
        return al;
    }
    public void setMasterUser(){
        put(ParseConstants.KEY_MASTER_USER, ParseUser.getCurrentUser().getUsername());
    }
    public String getMasterUser(){
        return  getString(ParseConstants.KEY_MASTER_USER);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(getSectionIds());
        dest.writeString(getMasterUser());
        dest.writeString(getMuseumName());
        dest.writeString(getObjectId());
    }

}

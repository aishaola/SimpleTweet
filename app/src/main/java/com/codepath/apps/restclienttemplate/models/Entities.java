package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import androidx.room.Entity;

@Entity
@Parcel
public class Entities {
    public String mediaLink;
    public static final String TAG = "Entities";


    public Entities() { }
    public static Entities fromJson(JSONObject jsonObject){
        try {
            Entities entities = new Entities();
            JSONObject jsonMedia = (JSONObject) jsonObject.getJSONArray("media").get(0);
            entities.mediaLink = jsonMedia.getString("media_url_https");
            return entities;
        } catch (JSONException e) {
            Entities entities = new Entities();
            entities.mediaLink = "";
            Log.i(TAG, "fromJson: No media for this tweet!");
            return entities;
        }

    }
}


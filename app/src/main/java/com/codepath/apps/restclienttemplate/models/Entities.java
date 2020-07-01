package com.codepath.apps.restclienttemplate.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class Entities {
    public String mediaLink;

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
            e.printStackTrace();
            return entities;
        }

    }
}


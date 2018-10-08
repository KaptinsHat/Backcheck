package com.wsovine.backcheck;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Team {
    private static final String TAG = "Team";

    private int id = 0;
    private String name = "Error";
    private String abbreviation = "ERR";
    private String imageURL = "https://imgur.com/9alkqJd.png";

    //Constructors
    public Team(int id, String name, String abbreviation){
        setId(id);
        setName(name);
        setAbbreviation(abbreviation);
        setImage();
    }

    public Team(int id, final Context context){
        setId(id);
        setImage();

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = context.getString(R.string.api_url) + "teams/" + id;
        Log.d(TAG, url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //remove the progress bar

                try {
                    JSONArray jsonTeamArray = response.getJSONArray("teams");
                    JSONObject jsonTeam = jsonTeamArray.getJSONObject(0);
                    setName(jsonTeam.getString("name"));
                    setAbbreviation(jsonTeam.getString("abbreviation"));

                } catch (JSONException e) {
                    Log.d(TAG, "onResponse: unable to find 'teams' array within json response");
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error.getMessage());
            }
        });

        queue.add(request);
    }

    //SET METHODS
    public void setId(int id){
        this.id = id;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setAbbreviation(String abbreviation){
        this.abbreviation = abbreviation;
    }

    public void setImage() {
        switch (id){
            case 24:
                imageURL = "https://imgur.com/j5YANkX.png";
                break;
            case 53:
                imageURL = "https://imgur.com/yzlnzGq.png";
                break;
            case 6:
                imageURL = "https://imgur.com/HxqfJlL.png";
                break;
            case 7:
                imageURL = "https://imgur.com/lRk9TD4.png";
                break;
            case 20:
                imageURL = "https://imgur.com/glMQFLb.png";
                break;
            case 12:
                imageURL = "https://imgur.com/2pBl8mc.png";
                break;
            case 16:
                imageURL = "https://imgur.com/nTcGUjd.png";
                break;
            case 21:
                imageURL = "https://imgur.com/OdbSjBQ.png";
                break;
            case 29:
                imageURL = "https://imgur.com/hFE42Dw.png";
                break;
            case 25:
                imageURL = "https://imgur.com/sed5gKW.png";
                break;
            case 17:
                imageURL = "https://imgur.com/fgWaB4W.png";
                break;
            case 22:
                imageURL = "https://imgur.com/2leT7Hz.png";
                break;
            case 13:
                imageURL = "https://imgur.com/Z19zn7M.png";
                break;
            case 26:
                imageURL = "https://imgur.com/WMOQjlL.png";
                break;
            case 30:
                imageURL = "https://imgur.com/QTnU9r4.png";
                break;
            case 8:
                imageURL = "https://imgur.com/UnH9O9E.png";
                break;
            case 18:
                imageURL = "https://imgur.com/pbIGykC.png";
                break;
            case 1:
                imageURL = "https://imgur.com/Oqzy8t7.png";
                break;
            case 2:
                imageURL = "https://imgur.com/U6Dt2F6.png";
                break;
            case 3:
                imageURL = "https://imgur.com/uYu3JKa.png";
                break;
            case 9:
                imageURL = "https://imgur.com/Joadl3a.png";
                break;
            case 4:
                imageURL = "https://imgur.com/jRYjXr6.png";
                break;
            case 5:
                imageURL = "https://imgur.com/5Q92H8e.png";
                break;
            case 28:
                imageURL = "https://imgur.com/Sg7QNN9.png";
                break;
            case 19:
                imageURL = "https://imgur.com/cgPorBN.png";
                break;
            case 14:
                imageURL = "https://imgur.com/YKUU9MA.png";
                break;
            case 10:
                imageURL = "https://imgur.com/tTq2XuG.png";
                break;
            case 23:
                imageURL = "https://imgur.com/Yx011CV.png";
                break;
            case 54:
                imageURL = "https://imgur.com/qZSHbPG.png";
                break;
            case 15:
                imageURL = "https://imgur.com/eIi6lGB.png";
                break;
            case 52:
                imageURL = "https://imgur.com/TB1snGy.png";
                break;
        }
    }

    //GET METHODS
    public int getId(){
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public String getImageURL() {
        return imageURL;
    }

    @Override
    public String toString() {
        return getName();
    }
}

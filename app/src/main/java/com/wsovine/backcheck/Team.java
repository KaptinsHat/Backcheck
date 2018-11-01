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
            case 24: //Anaheim Ducks
                imageURL = "https://imgur.com/r8U3C27.png";
                break;
            case 53: //Arizona Coyotes
                imageURL = "https://imgur.com/M1EWVel.png";
                break;
            case 6: //Boston Bruins
                imageURL = "https://imgur.com/bZAWzyP.png";
                break;
            case 7: //Buffalo Sabres
                imageURL = "https://imgur.com/sMZE7qB.png";
                break;
            case 20: //Calgary Flames
                imageURL = "https://imgur.com/ujoajbs.png";
                break;
            case 12: //Carolina Hurricanes
                imageURL = "https://imgur.com/TL8X8ZV.png";
                break;
            case 16: //Chicago Blackhawks
                imageURL = "https://imgur.com/9LD0yH7.png";
                break;
            case 21:  //Colorado Avalanche
                imageURL = "https://imgur.com/HiXGurD.png";
                break;
            case 29: //Columbus Blue Jackets
                imageURL = "https://imgur.com/HV54zth.png";
                break;
            case 25: //Dallas Stars
                imageURL = "https://imgur.com/ShnqjZQ.png";
                break;
            case 17: //Detroit Red Wings
                imageURL = "https://imgur.com/9vnU40U.png";
                break;
            case 22: //Edmonton Oilers
                imageURL = "https://imgur.com/KPpbJhX.png";
                break;
            case 13: //Florida Panthers
                imageURL = "https://imgur.com/kQOtH9I.png";
                break;
            case 26: //Los Angeles Kings
                imageURL = "https://imgur.com/jR8IRSC.png";
                break;
            case 30: //Minnesota Wild
                imageURL = "https://imgur.com/O4wynF2.png";
                break;
            case 8: //Montreal Canadians
                imageURL = "https://imgur.com/KvaHPPw.png";
                break;
            case 18: //Nashville Predators
                imageURL = "https://imgur.com/ou7ZzeG.png";
                break;
            case 1: //New Jersey Devils
                imageURL = "https://imgur.com/X3VTvec.png";
                break;
            case 2: //New York Islanders
                imageURL = "https://imgur.com/eODeDFJ.png";
                break;
            case 3: //New York Rangers
                imageURL = "https://imgur.com/64vI2YE.png";
                break;
            case 9: //Ottawa Senators
                imageURL = "https://imgur.com/kXww0sF.png";
                break;
            case 4: //Philadelphia Flyers
                imageURL = "https://https://imgur.com/Wrltrcc.png";
                break;
            case 5: //Pittsburgh Penguins
                imageURL = "https://imgur.com/GYysxQw.png";
                break;
            case 28: //San Jose Sharks
                imageURL = "https://imgur.com/wOqJcUF.png";
                break;
            case 19: //St. Louis Blues
                imageURL = "https://imgur.com/vQReLim.png";
                break;
            case 14: //Tampa Bay Lightning
                imageURL = "https://imgur.com/UQx2zn3.png";
                break;
            case 10: //Toronto Maple Leafs
                imageURL = "https://imgur.com/qPlAOpu.png";
                break;
            case 23: //Vancouver Canucks
                imageURL = "https://imgur.com/wxysRFK.png";
                break;
            case 54: //Vegas Golden Knights
                imageURL = "https://imgur.com/h9SY0zZ.png";
                break;
            case 15: //Washington Capitals
                imageURL = "https://imgur.com/MFsWGNn.png";
                break;
            case 52: //Winnipeg Jets
                imageURL = "https://imgur.com/o2K4CNl.png";
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

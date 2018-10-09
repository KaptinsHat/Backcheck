package com.wsovine.backcheck;

import android.app.IntentService;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.service.wallpaper.WallpaperService;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SetupActivity extends AppCompatActivity {
    private static final String TAG = "SetupActivity";

    private ProgressBar progressBar;

    //OPTION VARIABLES
    //Team names
    ArrayList<Team> teamList = new ArrayList<Team>();

    //SCREEN / WALLPAPER VARIABLES


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        progressBar = findViewById(R.id.progressBar);

        setTeamNamesSelection();
    }

    private void setTeamNamesSelection(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.api_url) + "teams";
        Log.d(TAG, url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //remove the progress bar
                progressBar.setVisibility(View.GONE);

                try {
                    //get the teams from the response, create the java object, and add them to the ArrayList
                    JSONArray jsonTeams = response.getJSONArray("teams");
                    for (int i = 0; i < jsonTeams.length(); i++){
                        JSONObject jsonTeam = jsonTeams.getJSONObject(i);
                        int id = jsonTeam.getInt("id");
                        String name = jsonTeam.getString("name");
                        String abbreviation = jsonTeam.getString("abbreviation");
                        Team team = new Team(id, name, abbreviation);
                        teamList.add(team);
                    }

                    //sort the teamlist alphabetically
                    Collections.sort(teamList, new Comparator<Team>() {
                        @Override
                        public int compare(Team o1, Team o2) {
                            return o1.toString().compareTo(o2.toString());
                        }
                    });

                    //Find the recycler view and configure recycler
                    RecyclerView teamRecycler = (RecyclerView) findViewById(R.id.team_recycler);

                    //Create the arrayadapter
                    TeamCardAdapter teamCardAdapter = new TeamCardAdapter(teamList);

                    //Set what happens when we click an item (team card)
                    teamCardAdapter.setListener(new TeamCardAdapter.Listener() {
                        @Override
                        public void onClick(int position) {
                            Log.d(TAG, "Team: " + teamList.get(position).toString() + " selected");
                            SharedPreferences.Editor editor = getSharedPreferences(Constants.PREFS, MODE_PRIVATE).edit();
                            editor.putInt(Constants.TEAM_ID, teamList.get(position).getId());
                            editor.putString(Constants.IMAGE_URL, teamList.get(position).getImageURL());
                            editor.apply();
                            setTeamWallpaper();
                        }
                    });

                    //Attach the adapter to the recycler
                    teamRecycler.setAdapter(teamCardAdapter);

                    GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3);
                    teamRecycler.setLayoutManager(layoutManager);
                    teamRecycler.setHasFixedSize(true);
                    teamRecycler.setItemViewCacheSize(20);
                    teamRecycler.setDrawingCacheEnabled(true);
                    teamRecycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                    teamRecycler.setVisibility(View.VISIBLE);

                } catch (JSONException e) {
                    Log.d(TAG, "onResponse: unable to find 'teams' array within json response");
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error.getMessage());
                Toast.makeText(getApplicationContext(), "Unable to reach data source", Toast.LENGTH_SHORT);
            }
        });

        queue.add(request);
    }

    private void setTeamWallpaper(){
        Log.d(TAG, "setTeamWallpaper: running");
        Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(this, BackcheckWallpaperService.class));
        startActivity(intent);
    }

}

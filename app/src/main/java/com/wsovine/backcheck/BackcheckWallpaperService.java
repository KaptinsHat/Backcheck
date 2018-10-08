package com.wsovine.backcheck;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;


public class BackcheckWallpaperService extends WallpaperService {
    private static final String TAG = "BackcheckWallpaperService";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public Engine onCreateEngine() {
        Log.d(TAG, "onCreateEngine: creating engine");
        return new MyEngine();
    }







    //-------------------------------- INNER CLASSES---------------------------------
    private class MyEngine extends Engine{

        // Screen variables
        private int width;
        private int height;
        private boolean visible = true;

        // Image variables
        private Bitmap logoBitmap;
        private int bitmapHeight;
        private int bitmapWidth;

        // Settings variables
        SharedPreferences preferences;
        int teamID;
        Team team;

        // Text variables
        Game game = new Game();
        Paint textPaint = new Paint();

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            preferences = getSharedPreferences(Constants.PREFS, MODE_PRIVATE);


            //set the text color, font, etc
            textPaint.setColor(Color.WHITE);
            textPaint.setTextSize(50);
            textPaint.setTextAlign(Paint.Align.CENTER);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            this.visible = visible;
            if (visible){
                refreshAndDrawFrame();
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            Log.i(TAG, "onSurfaceChanged: Height = " + Integer.toString(height) + " Width = " + Integer.toString(width));
            this.height = height;
            this.width = width;
            refreshAndDrawFrame();
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            this.visible = false;
        }

        private void refreshAndDrawFrame(){
            if(teamID != preferences.getInt(Constants.TEAM_ID, 0)) {
                teamID = preferences.getInt(Constants.TEAM_ID, 0);
                team = new Team(teamID, getApplicationContext());
            }
            updateGameLink();
            new RefreshImageTask().execute();
        }

        private void drawFrame(){
            if(!visible){
                return;
            }

            final SurfaceHolder holder = getSurfaceHolder();

            Canvas c = null;
            try {
                c = holder.lockCanvas();
                if (c != null) {
                    // Draw to background and logo
                    c.drawColor(Color.BLACK);
                    int bitmapHorizontalCenterPlacement = (width/2) - (bitmapWidth/2);
                    int bitmapVerticalCenterPlacement = (height/2)-(bitmapHeight/2);
                    c.drawBitmap(logoBitmap, bitmapHorizontalCenterPlacement, bitmapVerticalCenterPlacement, null);

                    //draw the text
                    String text;
                    // text if the game is in the future
                    if(game.getGameDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() > System.currentTimeMillis()){
                        //line 1
                        text = team.getName() +" next game:";
                        c.drawText(text, width/2, bitmapVerticalCenterPlacement - textPaint.getTextSize() * 3, textPaint);
                        //line 2
                        text = game.getGameDateString();
                        c.drawText(text, width/2, bitmapVerticalCenterPlacement - textPaint.getTextSize() * 2, textPaint);
                        //line 3
                        if(teamID == game.getHomeTeamID()){
                            text = "vs " + game.getAwayTeamName();
                        } else {
                            text = "@ " + game.getHomeTeamName();
                        }
                        c.drawText(text, width/2, bitmapVerticalCenterPlacement - textPaint.getTextSize(), textPaint);
                    } else {
                        //line 1
                        text = game.getAwayTeamTriCode() + " " + game.getAwayTeamScore() +
                                " | " + game.getHomeTeamTriCode() + " " + game.getHomeTeamScore();
                        c.drawText(text, width/2, bitmapVerticalCenterPlacement - textPaint.getTextSize(), textPaint);
                    }

                }
            } finally {
                if (c != null) {
                    holder.unlockCanvasAndPost(c);
                }
            }
        }



        private void updateGameLink(){
            if(game.getLiveLink() == null) {
                String url = getString(R.string.api_url)
                        + "teams/" + teamID
                        + "?expand=team.schedule.next";
                Log.d(TAG, "Next Game: " + url);

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject nextGameJson = response
                                    .getJSONArray("teams")
                                    .getJSONObject(0)
                                    .getJSONObject("nextGameSchedule")
                                    .getJSONArray("dates")
                                    .getJSONObject(0)
                                    .getJSONArray("games")
                                    .getJSONObject(0);


                            //Store the game primary key
                            game.setGamePk(nextGameJson.getInt("gamePk"));
                            //Store the live link
                            game.setLiveLink(nextGameJson.getString("link"));

                            storeGameData();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                queue.add(request);

            } else {
                storeGameData();
            }
        }

        private void storeGameData(){
            String url = getString(R.string.short_api_url)
                    + game.getLiveLink();
            Log.d(TAG, "Live Link: " + url);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONObject gameData = response
                                .getJSONObject("gameData");
                        //Store the game date
                        game.setGameDate(gameData
                                .getJSONObject("datetime")
                                .getString("dateTime"));
                        Log.d(TAG, "gameDate: " + game.getGameDate());
                        //Store the team IDs
                        game.setAwayTeamID(gameData
                                .getJSONObject("teams")
                                .getJSONObject("away")
                                .getInt("id"));
                        Log.d(TAG, "awayTeamID: " + game.getAwayTeamID());
                        game.setHomeTeamID(gameData
                                .getJSONObject("teams")
                                .getJSONObject("home")
                                .getInt("id"));
                        //Store the team names
                        game.setAwayTeamName(gameData
                                .getJSONObject("teams")
                                .getJSONObject("away")
                                .getString("name"));
                        game.setHomeTeamName(gameData
                                .getJSONObject("teams")
                                .getJSONObject("home")
                                .getString("name"));

                        JSONObject liveData = response
                                .getJSONObject("liveData");
                        //Store the triCode for each team
                        game.setAwayTeamTriCode(liveData
                                .getJSONObject("teams")
                                .getJSONObject("away")
                                .getJSONObject("team")
                                .getString("triCode"));
                        game.setHomeTeamTriCode(liveData
                                .getJSONObject("teams")
                                .getJSONObject("home")
                                .getJSONObject("team")
                                .getString("triCode"));
                        //Store the scores for each team
                        game.setAwayTeamScore(liveData
                                .getJSONObject("linescore")
                                .getJSONObject("teams")
                                .getJSONObject("away")
                                .getInt("goals"));
                        game.setHomeTeamScore(liveData
                                .getJSONObject("linescore")
                                .getJSONObject("teams")
                                .getJSONObject("home")
                                .getInt("goals"));

                        refreshAndDrawFrame();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            queue.add(request);

        }








        //------------------------------------ INNER CLASSES------------------------------------

        private class RefreshImageTask extends AsyncTask<Void, Void, Boolean>{
            @Override
            protected void onPreExecute(){

            }

            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    String logoURL = preferences.getString(Constants.IMAGE_URL, "https://imgur.com/9alkqJd.png");
                    logoBitmap = Picasso.get().load(logoURL).centerInside().resize(width, height).get();
                    bitmapHeight = logoBitmap.getHeight();
                    bitmapWidth = logoBitmap.getWidth();
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if(!result){
                    Toast.makeText(BackcheckWallpaperService.this, "Unable to load image", Toast.LENGTH_SHORT).show();
                } else {
                    drawFrame();
                }
            }
        }
    }
}

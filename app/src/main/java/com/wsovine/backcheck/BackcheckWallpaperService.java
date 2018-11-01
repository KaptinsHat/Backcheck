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
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Switch;
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
        Game currentGame;
        Game lastGame;
        boolean gameInProgress = false;
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
            Log.i(TAG, "onSurfaceChanged: Height = " + Integer.toString(height) + " Width = " + Integer.toString(width));
            this.height = height;
            this.width = width;
            super.onSurfaceChanged(holder, format, width, height);
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
                currentGame = null;
            }
            updateGameLink();
            new RefreshImageTask().execute();
        }

        private void drawFrame(int frame){
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
                    // text to show if the current game is in the future
                    if(currentGame.getGameDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() > System.currentTimeMillis()){
                        gameInProgress = false;
                        switch (frame) {
                            case 0:
                                //line 1
                                text = "Next game:";
                                c.drawText(text, width / 2, bitmapVerticalCenterPlacement - textPaint.getTextSize() * 3, textPaint);
                                //line 2
                                text = currentGame.getGameDateString();
                                c.drawText(text, width / 2, bitmapVerticalCenterPlacement - textPaint.getTextSize() * 2, textPaint);
                                //line 3
                                if (teamID == currentGame.getHomeTeamID()) {
                                    text = "vs " + currentGame.getAwayTeamName();
                                } else {
                                    text = "@ " + currentGame.getHomeTeamName();
                                }
                                c.drawText(text, width / 2, bitmapVerticalCenterPlacement - textPaint.getTextSize(), textPaint);
                                break;
                            case 1:
                                //line 1
                                text = "Previous game:";
                                c.drawText(text, width / 2, bitmapVerticalCenterPlacement - textPaint.getTextSize() * 3, textPaint);
                                //line 2
                                text = lastGame.getAwayTeamTriCode() + " " + lastGame.getAwayTeamScore();
                                c.drawText(text, width/2, bitmapVerticalCenterPlacement - textPaint.getTextSize()* 2, textPaint);
                                //line 3
                                text = lastGame.getHomeTeamTriCode() + " " + lastGame.getHomeTeamScore();
                                c.drawText(text, width/2, bitmapVerticalCenterPlacement - textPaint.getTextSize(), textPaint);
                                break;
                        }
                    // text to show if there is a game in progress
                    } else {
                        gameInProgress = true;
                        //line 1
                        text = currentGame.getAwayTeamTriCode() + " " + currentGame.getAwayTeamScore();
                        c.drawText(text, width/2, bitmapVerticalCenterPlacement - textPaint.getTextSize()* 3, textPaint);
                        //line 2
                        text = currentGame.getHomeTeamTriCode() + " " + currentGame.getHomeTeamScore();
                        c.drawText(text, width/2, bitmapVerticalCenterPlacement - textPaint.getTextSize() * 2, textPaint);
                        //line 3
                        text = currentGame.getCurrentPeriodTimeRemaining() + " " + currentGame.getCurrentPeriodOrdinal();
                        c.drawText(text, width/2, bitmapVerticalCenterPlacement - textPaint.getTextSize(), textPaint);
                    }

                }
            } finally {
                if (c != null) {
                    holder.unlockCanvasAndPost(c);
                }
                // if the game is in progress then update every x milliseconds
                if(gameInProgress) {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            refreshAndDrawFrame();
                        }
                    }, 5000);
                }else if(frame==0){
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            drawFrame(1);
                        }
                    }, 10000);
                }else{
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            refreshAndDrawFrame();
                        }
                    }, 10000);
                }
            }
        }



        private void updateGameLink(){
            if(currentGame == null) {
                currentGame = new Game();
            }
            String urlCurrent = getString(R.string.api_url)
                    + "teams/" + teamID
                    + "?expand=team.schedule.next";
            Log.d(TAG, "Next Game: " + urlCurrent);

            JsonObjectRequest requestCurrent = new JsonObjectRequest(Request.Method.GET, urlCurrent, null, new Response.Listener<JSONObject>() {
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
                        currentGame.setGamePk(nextGameJson.getInt("gamePk"));
                        //Store the live link
                        currentGame.setLiveLink(nextGameJson.getString("link"));

                        storeCurrentGameData();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            queue.add(requestCurrent);

            if(!gameInProgress && lastGame == null) {
                lastGame = new Game();
                String urlPrevious = getString(R.string.api_url)
                        + "teams/" + teamID
                        + "?expand=team.schedule.previous";
                Log.d(TAG, "Previous Game: " + urlPrevious);

                JsonObjectRequest requestPrevious = new JsonObjectRequest(Request.Method.GET, urlPrevious, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject nextGameJson = response
                                    .getJSONArray("teams")
                                    .getJSONObject(0)
                                    .getJSONObject("previousGameSchedule")
                                    .getJSONArray("dates")
                                    .getJSONObject(0)
                                    .getJSONArray("games")
                                    .getJSONObject(0);


                            //Store the game primary key
                            lastGame.setGamePk(nextGameJson.getInt("gamePk"));
                            //Store the live link
                            lastGame.setLiveLink(nextGameJson.getString("link"));

                            storePreviousGameData();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

                queue.add(requestPrevious);
            }
        }

        private void storeCurrentGameData(){
            String url = getString(R.string.short_api_url)
                    + currentGame.getLiveLink();
            Log.d(TAG, "Current Live Link: " + url);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONObject gameData = response
                                .getJSONObject("gameData");
                        //Store the game date
                        currentGame.setGameDate(gameData
                                .getJSONObject("datetime")
                                .getString("dateTime"));
                        //Store the team IDs
                        currentGame.setAwayTeamID(gameData
                                .getJSONObject("teams")
                                .getJSONObject("away")
                                .getInt("id"));
                        currentGame.setHomeTeamID(gameData
                                .getJSONObject("teams")
                                .getJSONObject("home")
                                .getInt("id"));
                        //Store the team names
                        currentGame.setAwayTeamName(gameData
                                .getJSONObject("teams")
                                .getJSONObject("away")
                                .getString("name"));
                        currentGame.setHomeTeamName(gameData
                                .getJSONObject("teams")
                                .getJSONObject("home")
                                .getString("name"));

                        JSONObject liveData = response
                                .getJSONObject("liveData");
                        //Store the triCode for each team
                        currentGame.setAwayTeamTriCode(liveData
                                .getJSONObject("linescore")
                                .getJSONObject("teams")
                                .getJSONObject("away")
                                .getJSONObject("team")
                                .getString("triCode"));
                        currentGame.setHomeTeamTriCode(liveData
                                .getJSONObject("linescore")
                                .getJSONObject("teams")
                                .getJSONObject("home")
                                .getJSONObject("team")
                                .getString("triCode"));
                        //Store the scores for each team
                        currentGame.setAwayTeamScore(liveData
                                .getJSONObject("linescore")
                                .getJSONObject("teams")
                                .getJSONObject("away")
                                .getInt("goals"));
                        currentGame.setHomeTeamScore(liveData
                                .getJSONObject("linescore")
                                .getJSONObject("teams")
                                .getJSONObject("home")
                                .getInt("goals"));
                        currentGame.setCurrentPeriodOrdinal(liveData
                                .getJSONObject("linescore")
                                .getString("currentPeriodOrdinal"));
                        currentGame.setCurrentPeriodTimeRemaining(liveData
                                .getJSONObject("linescore")
                                .getString("currentPeriodTimeRemaining"));

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

        private void storePreviousGameData(){
            String url = getString(R.string.short_api_url)
                    + lastGame.getLiveLink();
            Log.d(TAG, "Last Live Link: " + url);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONObject gameData = response
                                .getJSONObject("gameData");
                        //Store the game date
                        lastGame.setGameDate(gameData
                                .getJSONObject("datetime")
                                .getString("dateTime"));
                        //Store the team IDs
                        lastGame.setAwayTeamID(gameData
                                .getJSONObject("teams")
                                .getJSONObject("away")
                                .getInt("id"));
                        lastGame.setHomeTeamID(gameData
                                .getJSONObject("teams")
                                .getJSONObject("home")
                                .getInt("id"));
                        //Store the team names
                        lastGame.setAwayTeamName(gameData
                                .getJSONObject("teams")
                                .getJSONObject("away")
                                .getString("name"));
                        lastGame.setHomeTeamName(gameData
                                .getJSONObject("teams")
                                .getJSONObject("home")
                                .getString("name"));

                        JSONObject liveData = response
                                .getJSONObject("liveData");
                        //Store the triCode for each team
                        lastGame.setAwayTeamTriCode(liveData
                                .getJSONObject("linescore")
                                .getJSONObject("teams")
                                .getJSONObject("away")
                                .getJSONObject("team")
                                .getString("triCode"));
                        lastGame.setHomeTeamTriCode(liveData
                                .getJSONObject("linescore")
                                .getJSONObject("teams")
                                .getJSONObject("home")
                                .getJSONObject("team")
                                .getString("triCode"));
                        //Store the scores for each team
                        lastGame.setAwayTeamScore(liveData
                                .getJSONObject("linescore")
                                .getJSONObject("teams")
                                .getJSONObject("away")
                                .getInt("goals"));
                        lastGame.setHomeTeamScore(liveData
                                .getJSONObject("linescore")
                                .getJSONObject("teams")
                                .getJSONObject("home")
                                .getInt("goals"));
                        lastGame.setCurrentPeriodOrdinal(liveData
                                .getJSONObject("linescore")
                                .getString("currentPeriodOrdinal"));
                        lastGame.setCurrentPeriodTimeRemaining(liveData
                                .getJSONObject("linescore")
                                .getString("currentPeriodTimeRemaining"));

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
                    drawFrame(0);
                }
            }
        }
    }
}

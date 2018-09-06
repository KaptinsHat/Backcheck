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

        // Text variables
        Game game = new Game();
        Paint textPaint = new Paint();

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            preferences = getSharedPreferences(Constants.PREFS, MODE_PRIVATE);

            //set the text color, font, etc
            textPaint.setColor(Color.WHITE);
            textPaint.setTextSize(100);
            textPaint.setTextAlign(Paint.Align.CENTER);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            updateGame();
            this.visible = visible;
            if (visible){
                refreshImageAndDrawFrame();
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            Log.i(TAG, "onSurfaceChanged: Height = " + Integer.toString(height) + " Width = " + Integer.toString(width));
            this.height = height;
            this.width = width;
            refreshImageAndDrawFrame();
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

        private void refreshImageAndDrawFrame(){
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
                    c.drawText(game.getGameDateString(), width/2, bitmapVerticalCenterPlacement - 50, textPaint);
                }
            } finally {
                if (c != null) {
                    holder.unlockCanvasAndPost(c);
                }
            }
        }



        private void updateGame(){
            //Create a game object to store all of the game details
            int teamID = preferences.getInt(Constants.TEAM_ID, 0);

            String url = getString(R.string.api_url)
                    + "teams/" + teamID
                    +"?expand=team.schedule.next";
            Log.d(TAG, "JSONURL: " + url);

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
                        //Store the game date
                        game.setGameDateString(nextGameJson.getString("gameDate"));
                        //Store the team IDs
                        game.setAwayTeamID(nextGameJson
                                .getJSONObject("teams")
                                .getJSONObject("away")
                                .getJSONObject("team")
                                .getInt("id"));
                        game.setHomeTeamID(nextGameJson
                                .getJSONObject("teams")
                                .getJSONObject("home")
                                .getJSONObject("team")
                                .getInt("id"));
                        //Store the team names
                        game.setAwayTeamName(nextGameJson
                                .getJSONObject("teams")
                                .getJSONObject("away")
                                .getJSONObject("team")
                                .getString("name"));
                        game.setHomeTeamName(nextGameJson
                                .getJSONObject("teams")
                                .getJSONObject("home")
                                .getJSONObject("team")
                                .getString("name"));
                        //Store the scores for each team
                        game.setAwayTeamScore(nextGameJson
                                .getJSONObject("teams")
                                .getJSONObject("away")
                                .getInt("score"));
                        game.setHomeTeamScore(nextGameJson
                                .getJSONObject("teams")
                                .getJSONObject("home")
                                .getInt("score"));

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
                updateGame();
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

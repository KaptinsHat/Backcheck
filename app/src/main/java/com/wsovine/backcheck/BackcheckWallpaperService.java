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
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;


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



    private class MyEngine extends Engine{

        private boolean visible = true;

        private String logoURL;
        private Bitmap logoBitmap;
        private int bitmapHeight;

        private int width;
        private int height;


        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            SharedPreferences preferences = getSharedPreferences(Constants.PREFS, MODE_PRIVATE);
            logoURL = preferences.getString(Constants.IMAGE_URL, "https://imgur.com/9alkqJd.png");
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
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
                    // Draw
                    //c.drawColor(Color.WHITE);
                    int bitmapCenterPlacement = (height/2)-(bitmapHeight/2);
                    c.drawBitmap(logoBitmap, 0, bitmapCenterPlacement, null);
                }
            } finally {
                if (c != null) {
                    holder.unlockCanvasAndPost(c);
                }
            }
        }

        private class RefreshImageTask extends AsyncTask<Void, Void, Boolean>{
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    logoBitmap = Picasso.get().load(logoURL).centerInside().resize(width, height).get();
                    bitmapHeight = logoBitmap.getHeight();
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

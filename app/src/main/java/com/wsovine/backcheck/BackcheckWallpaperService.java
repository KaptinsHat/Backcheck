package com.wsovine.backcheck;

import android.app.IntentService;
import android.content.Intent;
import android.service.wallpaper.WallpaperService;


public class BackcheckWallpaperService extends WallpaperService {

    @Override
    public void onCreate(){
        super.onCreate();
    }

    @Override
    public Engine onCreateEngine() {
        return new TeamWallEngine();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    class TeamWallEngine extends Engine {

    }
}

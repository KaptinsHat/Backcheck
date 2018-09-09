package com.wsovine.backcheck;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Game {
    private static final String TAG = "GAME";

    private int gamePk = 0;
    private LocalDateTime gameDate = LocalDateTime.now();
    private String gameDateString = "None";
    private int awayTeamID = 0;
    private String awayTeamName = "None";
    private int homeTeamID = 0;
    private String homeTeamName = "None";
    private int awayTeamScore = -1;
    private int homeTeamScore = -1;

    public int getGamePk() {
        return gamePk;
    }

    public void setGamePk(int gamePk) {
        this.gamePk = gamePk;
    }

    public String getGameDateString() {
        return gameDateString;
    }

    public void setGameDate(String gameDateString) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("M/d/yyyy h:mma", Locale.getDefault());

        gameDate = LocalDateTime.parse(gameDateString, inputFormatter);
        gameDate = gameDate.atZone(ZoneId.of("GMT"))
                .withZoneSameInstant(ZoneId.systemDefault())
                .toLocalDateTime();

        //DEBUG TESTING
        LocalDateTime currentLocalDateTime = LocalDateTime.now();
        long currentTimeInMillis = System.currentTimeMillis();
        Log.d(TAG, "currentLocalDateTime outputFormatter: " + outputFormatter.format(currentLocalDateTime));
        Log.d(TAG, "currentLocalDateTime in millis: " + currentLocalDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        Log.d(TAG, "currentTimeInMillis: " + currentTimeInMillis);


        //END DEBUG TESTING
        this.gameDateString = outputFormatter.format(gameDate);
    }

    public LocalDateTime getGameDate(){
        return gameDate;
    }

    public int getAwayTeamID() {
        return awayTeamID;
    }

    public void setAwayTeamID(int awayTeamID) {
        this.awayTeamID = awayTeamID;
    }

    public int getHomeTeamID() {
        return homeTeamID;
    }

    public void setHomeTeamID(int homeTeamID) {
        this.homeTeamID = homeTeamID;
    }

    public int getAwayTeamScore() {
        return awayTeamScore;
    }

    public void setAwayTeamScore(int awayTeamScore) {
        this.awayTeamScore = awayTeamScore;
    }

    public int getHomeTeamScore() {
        return homeTeamScore;
    }

    public void setHomeTeamScore(int homeTeamScore) {
        this.homeTeamScore = homeTeamScore;
    }

    public String getAwayTeamName() {
        return awayTeamName;
    }

    public void setAwayTeamName(String awayTeamName) {
        this.awayTeamName = awayTeamName;
    }

    public String getHomeTeamName() {
        return homeTeamName;
    }

    public void setHomeTeamName(String homeTeamName) {
        this.homeTeamName = homeTeamName;
    }
}

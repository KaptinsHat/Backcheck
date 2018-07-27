package com.wsovine.backcheck;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.sip.SipSession;
import android.service.wallpaper.WallpaperService;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

public class TeamCardAdapter extends RecyclerView.Adapter<TeamCardAdapter.ViewHolder> {
    ArrayList<Team> teamList;
    private Listener listener;

    interface Listener {
        void onClick(int position);
    }

    public TeamCardAdapter(ArrayList<Team> teamList){
        this.teamList = teamList;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }


    @NonNull
    @Override
    public TeamCardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.team_card, parent, false);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull TeamCardAdapter.ViewHolder holder, final int position) {
        CardView cardView = holder.cardView;

        ImageView imageView = (ImageView) cardView.findViewById(R.id.team_image);
        Picasso.get().load(teamList.get(position).getImage()).into(imageView);
        imageView.setContentDescription(teamList.get(position).getName());

        TextView textView = (TextView) cardView.findViewById(R.id.team_text);
        textView.setText(teamList.get(position).getName());

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.onClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return teamList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;

        public ViewHolder(CardView v){
            super(v);
            cardView = v;
        }

    }
}

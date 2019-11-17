package com.harshsharma.musify.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.harshsharma.musify.R;
import com.harshsharma.musify.customviews.TextViewCustom;
import com.harshsharma.musify.interfaces.MusifyConstants;
import com.harshsharma.musify.interfaces.OnTracksClickListener;
import com.harshsharma.musify.models.Track;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

// Adapter for Tracks which are being loaded from REST API in fist Tab of the App.
public class TracksAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements MusifyConstants {

    private ArrayList<Track> trackArrayList;
    private OnTracksClickListener tracksClickListener;

    public TracksAdapter(ArrayList<Track> trackArrayList,
                         OnTracksClickListener tracksClickListener) {
        this.trackArrayList = trackArrayList;
        this.tracksClickListener = tracksClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        View chaptersView
                = inflater.inflate(R.layout.item_tracks_list, viewGroup, false);
        viewHolder = new TracksViewHolder(chaptersView);
        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        return -1;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        TracksViewHolder tracksViewHolder = (TracksViewHolder) viewHolder;
        configureTracksViewHolder(tracksViewHolder, position);
    }

    private void configureTracksViewHolder(TracksViewHolder tracksViewHolder, int position) {
        Track track = trackArrayList.get(position);

        Picasso.get()
                .load(track.getCover_image())
                .placeholder(R.drawable.ic_music_darker_gray)
                .error(R.drawable.ic_music_darker_gray)
                .into(tracksViewHolder.trackCoverImageView);
        tracksViewHolder.textTrackArtist.setText(track.getArtists());
        tracksViewHolder.textTrackSong.setText(track.getSong());
        tracksViewHolder.textTrackArtist.setSelected(true);
        tracksViewHolder.textTrackSong.setSelected(true);

    }

    @Override
    public int getItemCount() {
        return trackArrayList.size();
    }

    private class TracksViewHolder
            extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextViewCustom textTrackSong, textTrackArtist;
        CardView rootLayout;
        ImageView trackCoverImageView, purchaseTrackImageView;

        TracksViewHolder(View view) {
            super(view);
            trackCoverImageView = view.findViewById(R.id.iv_track_cover);
            textTrackSong = view.findViewById(R.id.tv_track_song);
            textTrackArtist = view.findViewById(R.id.tv_track_artist);
            purchaseTrackImageView = view.findViewById(R.id.iv_track_purchase);
            rootLayout = view.findViewById(R.id.root_track);
            rootLayout.setOnClickListener(this);
            purchaseTrackImageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Track track = trackArrayList.get(getAdapterPosition());
            switch (view.getId()) {
                case R.id.root_track:
                    tracksClickListener.onTrackClick(getAdapterPosition(), track);
                    break;
                case R.id.iv_track_purchase:
                    tracksClickListener.onTrackPurchase(getAdapterPosition(), track);
                    break;
            }
        }
    }

}
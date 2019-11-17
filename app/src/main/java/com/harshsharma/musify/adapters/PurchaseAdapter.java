package com.harshsharma.musify.adapters;

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

import java.util.List;

// Adapter for Purchases in the Musify App. Individual item can be seen as a card in UI
public class PurchaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements MusifyConstants {

    private List<Track> purchasedTracks;
    private OnTracksClickListener tracksClickListener;

    public PurchaseAdapter(List<Track> purchasedTracks,
                           OnTracksClickListener tracksClickListener) {
        this.purchasedTracks = purchasedTracks;
        this.tracksClickListener = tracksClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        View chaptersView
                = inflater.inflate(R.layout.item_tracks_grid, viewGroup, false);

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
        Track track = purchasedTracks.get(position);

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
        return purchasedTracks.size();
    }

    private class TracksViewHolder
            extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextViewCustom textTrackSong, textTrackArtist;
        CardView rootLayout;
        ImageView trackCoverImageView;

        TracksViewHolder(View view) {
            super(view);
            trackCoverImageView = view.findViewById(R.id.iv_track_cover);
            textTrackSong = view.findViewById(R.id.tv_track_song);
            textTrackArtist = view.findViewById(R.id.tv_track_artist);
            rootLayout = view.findViewById(R.id.root_track);
            rootLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Track track = purchasedTracks.get(getAdapterPosition());
            if (view.getId() == R.id.root_track) {
                tracksClickListener.onTrackClick(getAdapterPosition(), track);
            }
        }
    }

}
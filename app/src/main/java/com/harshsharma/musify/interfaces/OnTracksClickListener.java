package com.harshsharma.musify.interfaces;

import com.harshsharma.musify.models.Track;

//Click Listener for Tracks
public interface OnTracksClickListener {
    void onTrackClick(int position, Track track);

    void onTrackPurchase(int position, Track track);
}

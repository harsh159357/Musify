package com.harshsharma.musify.interfaces;


import com.harshsharma.musify.models.TrackMetaData;

public interface MusifyServiceCallback {
    void updatePlaybackState(int state);

    void playSongComplete();

    void currentSeekBarPosition(int progress);

    void playCurrent(int indexP, TrackMetaData currentAudio);

    void playNext(int indexP, TrackMetaData currentAudio);

    void playPrevious(int indexP, TrackMetaData currentAudio);
}

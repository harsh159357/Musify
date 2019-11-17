package com.harshsharma.musify.interfaces;


import com.harshsharma.musify.models.TrackMetaData;

public interface PlaybackListener {
    void start();

    void stop(boolean notifyListeners);

    void setState(int state);

    int getState();

    boolean isConnected();

    boolean isPlaying();

    int getCurrentStreamPosition();

    void setCurrentStreamPosition(int pos);

    void updateLastKnownStreamPosition();

    void play(TrackMetaData item);

    void pause();

    void seekTo(int position);

    void setCurrentMediaId(String mediaId);

    String getCurrentMediaId();

    interface Callback {
        void onCompletion();

        void onPlaybackStatusChanged(int state);

        void onError(String error);

        void setCurrentMediaId(String mediaId);
    }

    void setCallback(Callback callback);
}

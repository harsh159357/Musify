package com.harshsharma.musify.controllers;


import com.harshsharma.musify.models.TrackMetaData;

public abstract class MusifyAbstractStreamingController {

    public abstract void onPlay(TrackMetaData infoData);

    public abstract void onPause();

    public abstract void onStop();

    public abstract void onSeekTo(long position);

    public abstract int lastSeekPosition();

    public abstract void onSkipToNext();

    public abstract void onSkipToPrevious();
}

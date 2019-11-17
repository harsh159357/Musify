package com.harshsharma.musify.models;


import android.os.Parcel;
import android.os.Parcelable;

// TrackMetaData Model for Service Usage
public class TrackMetaData implements Parcelable {

    private String trackId;
    private String trackUrl;
    private String trackTitle;
    private String trackArtist;
    private String trackArt;
    private int playState;

    public TrackMetaData() {
    }

    protected TrackMetaData(Parcel in) {
        trackId = in.readString();
        trackUrl = in.readString();
        trackTitle = in.readString();
        trackArtist = in.readString();
        trackArt = in.readString();
        playState = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(trackId);
        dest.writeString(trackUrl);
        dest.writeString(trackTitle);
        dest.writeString(trackArtist);
        dest.writeString(trackArt);
        dest.writeInt(playState);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TrackMetaData> CREATOR = new Creator<TrackMetaData>() {
        @Override
        public TrackMetaData createFromParcel(Parcel in) {
            return new TrackMetaData(in);
        }

        @Override
        public TrackMetaData[] newArray(int size) {
            return new TrackMetaData[size];
        }
    };

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public String getTrackUrl() {
        return trackUrl;
    }

    public void setTrackUrl(String trackUrl) {
        this.trackUrl = trackUrl;
    }

    public String getTrackTitle() {
        return trackTitle;
    }

    public void setTrackTitle(String trackTitle) {
        this.trackTitle = trackTitle;
    }

    public String getTrackArtist() {
        return trackArtist;
    }

    public void setTrackArtist(String trackArtist) {
        this.trackArtist = trackArtist;
    }

    public String getTrackArt() {
        return trackArt;
    }

    public void setTrackArt(String trackArt) {
        this.trackArt = trackArt;
    }

    public int getPlayState() {
        return playState;
    }

    public void setPlayState(int playState) {
        this.playState = playState;
    }
}

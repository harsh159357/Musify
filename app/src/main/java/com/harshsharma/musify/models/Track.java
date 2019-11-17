package com.harshsharma.musify.models;

import android.os.Parcel;
import android.os.Parcelable;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

// Track Data Model
@Entity
public class Track implements Parcelable {
    @Id(assignable = true)
    long trackId;
    private String song;
    private String url;
    private String artists;
    private String cover_image;

    public Track() {
    }

    public Track(long trackId, String song, String url, String artists, String cover_image) {
        this.trackId = trackId;
        this.song = song;
        this.url = url;
        this.artists = artists;
        this.cover_image = cover_image;
    }

    public long getTrackId() {
        return trackId;
    }

    public void setTrackId(long trackId) {
        this.trackId = trackId;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getArtists() {
        return artists;
    }

    public void setArtists(String artists) {
        this.artists = artists;
    }

    public String getCover_image() {
        return cover_image;
    }

    public void setCover_image(String cover_image) {
        this.cover_image = cover_image;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.trackId);
        dest.writeString(this.song);
        dest.writeString(this.url);
        dest.writeString(this.artists);
        dest.writeString(this.cover_image);
    }

    protected Track(Parcel in) {
        this.trackId = in.readLong();
        this.song = in.readString();
        this.url = in.readString();
        this.artists = in.readString();
        this.cover_image = in.readString();
    }

    public static final Parcelable.Creator<Track> CREATOR = new Parcelable.Creator<Track>() {
        @Override
        public Track createFromParcel(Parcel source) {
            return new Track(source);
        }

        @Override
        public Track[] newArray(int size) {
            return new Track[size];
        }
    };
}

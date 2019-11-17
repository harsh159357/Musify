package com.harshsharma.musify.interfaces;

import com.harshsharma.musify.models.Track;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;

//Interface Used by Retrofit for API interaction With App
public interface MusifyApiInterface {

    String MUSIFY_BASE_URL = "http://starlord.hackerearth.com/";

    @GET("studio")
    Call<ArrayList<Track>> getTracks();

}

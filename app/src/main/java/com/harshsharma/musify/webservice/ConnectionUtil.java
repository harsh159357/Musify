package com.harshsharma.musify.webservice;

import com.harshsharma.musify.utilites.Logger;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

//Used for making connection with APIs
public class ConnectionUtil {

    private static final String TAG = ConnectionUtil.class.getName();

    public static <T> ApiResponse execute(Call call) {
        try {
            Response<T> response = call.execute();
            return new ApiResponse(response.code(), response.body(), response.headers());
        } catch (IOException e) {
            Logger.d(TAG, "Error in execute api request");
        } catch (Exception ex) {
            Logger.d(TAG, "Error in execute api" + ex.getMessage());
        }
        return null;
    }
}

package com.harshsharma.musify.webservice;

import okhttp3.Headers;

//Used for mapping response fetched from server and other useful things
public class ApiResponse<T> {

    private int responseCode;
    private T response;
    private Headers headers;

    public ApiResponse(int responseCode, T response) {
        super();
        this.responseCode = responseCode;
        this.response = response;
    }

    public ApiResponse(int responseCode, T response, Headers headers) {
        super();
        this.responseCode = responseCode;
        this.response = response;
        this.headers = headers;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public T getResponse() {
        return response;
    }

    public void setResponse(T responseString) {
        this.response = responseString;
    }

    public Headers getHeaderParams() {
        return headers;
    }

    public void setHeaderParams(Headers header) {
        this.headers = header;
    }

}

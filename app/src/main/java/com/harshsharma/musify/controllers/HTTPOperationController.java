package com.harshsharma.musify.controllers;

import com.harshsharma.musify.MusifyApplication;
import com.harshsharma.musify.interfaces.MusifyApiInterface;
import com.harshsharma.musify.interfaces.MusifyConstants;
import com.harshsharma.musify.webservice.ApiResponse;
import com.harshsharma.musify.webservice.ConnectionUtil;

//Controller for API interfacing
public class HTTPOperationController implements MusifyConstants {

    private static MusifyApiInterface getApiInterface() {
        return MusifyApplication.getInstance().getMusifyApiInterface();
    }

    public static ApiResponse getTracks() {
        return ConnectionUtil.execute(getApiInterface().getTracks());
    }
}
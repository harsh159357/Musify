package com.harshsharma.musify.controllers;


import com.harshsharma.musify.MusifyApplication;
import com.harshsharma.musify.models.Track;

import io.objectbox.Box;
import io.objectbox.BoxStore;

// Object Box as a Database Provider in the App being used of persisting purchases.
public class ObjectBoxController {

    private static ObjectBoxController objectBoxController;
    private static Box<Track> trackBox;

    private ObjectBoxController() {
        objectBoxController = this;
    }

    public static synchronized ObjectBoxController getInstance() {
        if (objectBoxController == null
                || trackBox == null) {
            objectBoxController = new ObjectBoxController();

            BoxStore boxStore = MusifyApplication.getInstance().getBoxStore();

            trackBox = boxStore.boxFor(Track.class);
        }
        return objectBoxController;
    }

    public static Box<Track> getTrackBox() {
        return trackBox;
    }
}

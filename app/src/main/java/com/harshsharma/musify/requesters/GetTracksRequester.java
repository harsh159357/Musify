package com.harshsharma.musify.requesters;

import com.harshsharma.musify.MusifyApplication;
import com.harshsharma.musify.controllers.HTTPOperationController;
import com.harshsharma.musify.controllers.ObjectBoxController;
import com.harshsharma.musify.interfaces.BaseRequester;
import com.harshsharma.musify.models.Track;
import com.harshsharma.musify.models.Track_;
import com.harshsharma.musify.models.commons.EventObject;
import com.harshsharma.musify.utilites.MusifyUtil;
import com.harshsharma.musify.utilites.NetworkUtil;
import com.harshsharma.musify.webservice.ApiResponse;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import io.objectbox.query.Query;

// Runnable Operation for Fetching Tracks from Rest API
public class GetTracksRequester implements BaseRequester {

    @Override
    public void run() {
        if (NetworkUtil.isConnected(MusifyApplication.getInstance())) {
            ApiResponse<ArrayList<Track>> apiTracksResponse = HTTPOperationController.getTracks();

            if (apiTracksResponse != null) {
                if (!apiTracksResponse.getResponse().isEmpty()) {
                    ArrayList<Track> unfilteredTracks = apiTracksResponse.getResponse();
                    setTrackIds(unfilteredTracks);

                    EventBus.getDefault()
                            .post(new EventObject(EventCenter.SETUP_MUSIC,
                                    unfilteredTracks));

                    EventBus.getDefault().post(new EventObject(EventCenter.GET_ALL_TRACKS_SUCCESSFUL,
                            getFilteredTracks(unfilteredTracks)
                    ));
                } else {
                    EventBus
                            .getDefault()
                            .post(new EventObject(EventCenter.GET_ALL_TRACKS_UN_SUCCESSFUL, null));
                }
            } else {
                EventBus.getDefault().post(new EventObject(EventCenter.NO_INTERNET_CONNECTION, null));
            }
        } else {
            EventBus.getDefault().post(new EventObject(EventCenter.NO_INTERNET_CONNECTION, null));
        }
    }

    private ArrayList<Track> setTrackIds(ArrayList<Track> trackArrayList) {
        for (Track track : trackArrayList) {
            track.setTrackId(MusifyUtil.getTrackId(track));
        }
        return trackArrayList;
    }

    private ArrayList<Track> getFilteredTracks(ArrayList<Track> trackArrayList) {
        ArrayList<Track> filteredTracks = new ArrayList<>();

        for (Track track : trackArrayList) {
            if (!isTrackPurchased(MusifyUtil.getTrackId(track))) {
                filteredTracks.add(track);
            }
        }

        return filteredTracks;
    }

    private boolean isTrackPurchased(long id) {
        Query<Track> isTrackPurchased
                = ObjectBoxController
                .getTrackBox()
                .query().equal(Track_.__ID_PROPERTY, id).build();
        return !isTrackPurchased.find().isEmpty();
    }

}

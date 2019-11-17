package com.harshsharma.musify.requesters;

import com.harshsharma.musify.controllers.ObjectBoxController;
import com.harshsharma.musify.interfaces.BaseRequester;
import com.harshsharma.musify.models.Track;
import com.harshsharma.musify.models.Track_;
import com.harshsharma.musify.models.commons.EventObject;
import com.harshsharma.musify.utilites.MusifyUtil;

import org.greenrobot.eventbus.EventBus;

import io.objectbox.query.Query;

// Runnable Operation for Mimicking Tracks purchasing in the App
public class PurchaseRequester implements BaseRequester {

    private Track track;

    public PurchaseRequester(Track track) {
        this.track = track;
    }


    @Override
    public void run() {
        try {
            Track trackPurchased = new Track();
            long id = MusifyUtil.getTrackId(track);

            Query<Track> isAlreadyExist
                    = ObjectBoxController.getTrackBox().query().equal(Track_.__ID_PROPERTY, id).build();
            if (isAlreadyExist.find().isEmpty()) {
                trackPurchased.setTrackId(id);
                trackPurchased.setSong(track.getSong());
                trackPurchased.setArtists(track.getArtists());
                trackPurchased.setCover_image(track.getCover_image());
                trackPurchased.setUrl(track.getUrl());
                ObjectBoxController.getTrackBox().put(trackPurchased);
                EventBus.getDefault().post(new EventObject(EventCenter.TRACK_PURCHASED_SUCCESSFULLY, track));
            } else {
                EventBus.getDefault().post(new EventObject(EventCenter.TRACK_IS_ALREADY_PURCHASED, track));
            }
        } catch (Exception exception) {
            EventBus.getDefault()
                    .post(new EventObject(EventCenter.UNABLE_TO_PURCHASE_TRACK_OR_SOMETHING_WENT_WRONG, null));
        }
    }


}

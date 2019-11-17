package com.harshsharma.musify.controllers;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.media.session.PlaybackStateCompat;

import com.harshsharma.musify.interfaces.MusifyServiceCallback;
import com.harshsharma.musify.interfaces.PlaybackListener;
import com.harshsharma.musify.models.TrackMetaData;
import com.harshsharma.musify.service.MusifyStreamingService;
import com.harshsharma.musify.streaming.MusifyPlaybackListener;
import com.harshsharma.musify.utilites.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

// Streaming Controller in the App
public class MusifyStreamingController extends MusifyAbstractStreamingController {
    private static final String TAG = Logger.makeLogTag(MusifyStreamingController.class);

    private MusifyPlaybackListener audioPlayback;
    private MusifyServiceCallback musifyServiceCallback;
    private static volatile MusifyStreamingController instance = null;
    private Context context;
    private int index = 0;
    private boolean playMultiple = false;
    private boolean showPlayerNotification = false;
    public PendingIntent pendingIntent;
    private TrackMetaData currentAudio;
    private List<TrackMetaData> mediaList = new ArrayList<>();
    public static volatile Handler applicationHandler = null;


    public static MusifyStreamingController getInstance(Context context) {
        if (instance == null) {
            synchronized (MusifyStreamingController.class) {
                instance = new MusifyStreamingController();
                instance.context = context;
                instance.audioPlayback = new MusifyPlaybackListener(context);
                instance.audioPlayback.setCallback(new MyStatusCallback());
                applicationHandler = new Handler(context.getMainLooper());
            }
        }
        return instance;
    }

    public void subscribesCallBack(MusifyServiceCallback callback) {
        this.musifyServiceCallback = callback;
    }

    public int getCurrentIndex() {
        return this.index;
    }

    public void unSubscribeCallBack() {
        this.musifyServiceCallback = null;
    }

    public TrackMetaData getCurrentAudio() {
        return currentAudio;
    }

    public String getCurrentAudioId() {
        return currentAudio != null ? currentAudio.getTrackId() : "";
    }

    public boolean isPlayMultiple() {
        return playMultiple;
    }

    public void setPlayMultiple(boolean playMultiple) {
        this.playMultiple = playMultiple;
    }

    public boolean isPlaying() {
        return instance.audioPlayback.isPlaying();
    }

    public void setPendingIntentAct(PendingIntent mPendingIntent) {
        this.pendingIntent = mPendingIntent;
    }

    public void setShowPlayerNotification(boolean showPlayerNotification) {
        this.showPlayerNotification = showPlayerNotification;
    }

    public void setMediaList(List<TrackMetaData> currentAudioList) {
        if (this.mediaList != null) {
            this.mediaList.clear();
            this.mediaList.addAll(currentAudioList);
        }
    }

    public void clearList() {
        if (this.mediaList != null && mediaList.size() > 0) {
            this.mediaList.clear();
            this.index = 0;
            this.onPause();
        }
    }

    @Override
    public void onPlay(TrackMetaData infoData) {
        if (infoData == null) {
            return;
        }
        if (playMultiple && !isMediaListEmpty()) {
            index = mediaList.indexOf(infoData);
        }
        if (this.currentAudio != null
                && this.currentAudio.getTrackId().equalsIgnoreCase(infoData.getTrackId())
                && instance.audioPlayback != null && instance.audioPlayback.isPlaying()) {
            onPause();
        } else {
            this.currentAudio = infoData;
            handlePlayRequest();
            if (musifyServiceCallback != null)
                musifyServiceCallback.playCurrent(index, currentAudio);
        }
    }

    @Override
    public void onPause() {
        handlePauseRequest();
    }

    @Override
    public void onStop() {
        handleStopRequest(null);
    }

    @Override
    public void onSeekTo(long position) {
        audioPlayback.seekTo((int) position);
    }

    @Override
    public int lastSeekPosition() {
        return (audioPlayback == null) ? 0 : audioPlayback.getCurrentStreamPosition();
    }

    @Override
    public void onSkipToNext() {
        int nextIndex = index + 1;
        if (isValidIndex(true, nextIndex)) {
            TrackMetaData metaData = mediaList.get(nextIndex);
            onPlay(metaData);
            if (instance.musifyServiceCallback != null) {
                musifyServiceCallback.playNext(nextIndex, metaData);
            }
        }
    }


    @Override
    public void onSkipToPrevious() {
        int prvIndex = index - 1;
        if (isValidIndex(false, prvIndex)) {
            TrackMetaData metaData = mediaList.get(prvIndex);
            onPlay(metaData);
            if (instance.musifyServiceCallback != null) {
                musifyServiceCallback.playPrevious(prvIndex, metaData);
            }
        }
    }

    /**
     * @return
     */
    public boolean isMediaListEmpty() {
        return (mediaList == null || mediaList.size() == 0);
    }

    /**
     * @param isIncremental
     * @return
     */
    private boolean isValidIndex(boolean isIncremental, int index) {
        if (isIncremental) {
            return (playMultiple && !isMediaListEmpty() && mediaList.size() > index);
        } else {
            return (playMultiple && !isMediaListEmpty() && index >= 0);
        }
    }

    public void handlePlayRequest() {
        Logger.d(TAG, "handlePlayRequest: mState=" + audioPlayback.getState());
        if (audioPlayback != null && currentAudio != null) {
            audioPlayback.play(currentAudio);
            if (showPlayerNotification) {
                if (context != null) {
                    Intent intent = new Intent(context, MusifyStreamingService.class);
                    context.startService(intent);
                } else {
                    Intent intent = new Intent(context, MusifyStreamingService.class);
                    context.stopService(intent);
                }

                MusifyNotificationController
                        .getInstance()
                        .postNotificationName(
                                MusifyNotificationController.audioDidStarted,
                                currentAudio);

                MusifyNotificationController
                        .getInstance()
                        .postNotificationName(
                                MusifyNotificationController.audioPlayStateChanged,
                                getCurrentAudio().getTrackId());
                setPendingIntent();
            }
        }
    }

    private void setPendingIntent() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (pendingIntent != null) {
                    MusifyNotificationController
                            .getInstance()
                            .postNotificationName(
                                    MusifyNotificationController.setAnyPendingIntent,
                                    pendingIntent);
                }
            }
        }, 400);
    }

    public void handlePauseRequest() {
        Logger.d(TAG, "handlePauseRequest: mState=" + audioPlayback.getState());
        if (audioPlayback != null && audioPlayback.isPlaying()) {
            audioPlayback.pause();
            if (showPlayerNotification) {
                MusifyNotificationController
                        .getInstance()
                        .postNotificationName(
                                MusifyNotificationController.audioPlayStateChanged,
                                getCurrentAudio().getTrackId());
            }
        }
    }

    public void handleStopRequest(String withError) {
        Logger.d(TAG,
                "handleStopRequest: mState=" + audioPlayback.getState() + " error=", withError);
        audioPlayback.stop(true);
    }

    static class MyStatusCallback implements PlaybackListener.Callback {
        @Override
        public void onCompletion() {
            if (instance.playMultiple && !instance.isMediaListEmpty()) {
                if (instance.musifyServiceCallback != null) {
                    instance.musifyServiceCallback.playSongComplete();
                }
                instance.onSkipToNext();
            } else {
                instance.handleStopRequest(null);
            }
        }

        @Override
        public void onPlaybackStatusChanged(int state) {
            try {
                if (state == PlaybackStateCompat.STATE_PLAYING) {
                    instance.scheduleSeekBarUpdate();
                } else {
                    instance.stopSeekBarUpdate();
                }
                if (instance.musifyServiceCallback != null) {
                    instance.musifyServiceCallback.updatePlaybackState(state);
                }

                instance.mLastPlaybackState = state;
                if (instance.currentAudio != null) {
                    instance.currentAudio.setPlayState(state);
                }
                if (instance.showPlayerNotification) {
                    MusifyNotificationController
                            .getInstance()
                            .postNotificationName(
                                    MusifyNotificationController.audioPlayStateChanged,
                                    instance.getCurrentAudio().getTrackId());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(String error) {
            //TODO FOR ERROR
        }

        @Override
        public void setCurrentMediaId(String mediaId) {

        }

    }


    public void cleanupPlayer(Context context, boolean notify, boolean stopService) {
        cleanupPlayer(notify, stopService);
    }

    public void cleanupPlayer(boolean notify, boolean stopService) {
        handlePauseRequest();
        audioPlayback.stop(true);
        if (stopService) {
            Intent intent = new Intent(context, MusifyStreamingService.class);
            context.stopService(intent);
        }
    }

    private ScheduledFuture<?> mScheduleFuture;
    public int mLastPlaybackState;
    private long currentPosition = 0;
    private final Handler mHandler = new Handler();
    private static final long PROGRESS_UPDATE_INTERNAL = 1000;
    private static final long PROGRESS_UPDATE_INITIAL_INTERVAL = 100;
    private final ScheduledExecutorService mExecutorService = Executors.newSingleThreadScheduledExecutor();
    private final Runnable mUpdateProgressTask = new Runnable() {
        @Override
        public void run() {
            updateProgress();
        }
    };

    public void scheduleSeekBarUpdate() {
        stopSeekBarUpdate();
        if (!mExecutorService.isShutdown()) {
            mScheduleFuture = mExecutorService.scheduleAtFixedRate(
                    new Runnable() {
                        @Override
                        public void run() {
                            mHandler.post(mUpdateProgressTask);
                        }
                    }, PROGRESS_UPDATE_INITIAL_INTERVAL,
                    PROGRESS_UPDATE_INTERNAL, TimeUnit.MILLISECONDS);
        }
    }

    public void stopSeekBarUpdate() {
        if (mScheduleFuture != null) {
            mScheduleFuture.cancel(false);
        }
    }

    private void updateProgress() {
        if (instance.mLastPlaybackState == 0 || instance.mLastPlaybackState < 0) {
            return;
        }
        if (instance.mLastPlaybackState != PlaybackStateCompat.STATE_PAUSED && instance.musifyServiceCallback != null) {
            instance.musifyServiceCallback.currentSeekBarPosition(audioPlayback.getCurrentStreamPosition());
        }
    }

}

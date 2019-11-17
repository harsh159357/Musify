package com.harshsharma.musify.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.RemoteControlClient;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.harshsharma.musify.R;
import com.harshsharma.musify.controllers.MusifyNotificationController;
import com.harshsharma.musify.controllers.MusifyStreamingController;
import com.harshsharma.musify.interfaces.MusifyConstants;
import com.harshsharma.musify.models.TrackMetaData;
import com.harshsharma.musify.receivers.MusifyStreamingReceiver;
import com.harshsharma.musify.utilites.Logger;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Objects;


public class MusifyStreamingService
        extends Service
        implements MusifyNotificationController.NotificationCenterDelegate, MusifyConstants {

    private static final String TAG = Logger.makeLogTag(MusifyStreamingService.class);

    private static boolean supportBigNotifications = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    private static boolean supportLockScreenControls = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    private RemoteControlClient remoteControlClient;
    private AudioManager audioManager;
    private MusifyStreamingController musifyStreamingController;
    private PhoneStateListener phoneStateListener;
    public PendingIntent pendingIntent;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        musifyStreamingController = MusifyStreamingController.getInstance(MusifyStreamingService.this);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        MusifyNotificationController
                .getInstance()
                .addObserver(this, MusifyNotificationController.audioProgressDidChanged);

        MusifyNotificationController
                .getInstance()
                .addObserver(this, MusifyNotificationController.setAnyPendingIntent);

        MusifyNotificationController
                .getInstance()
                .addObserver(this, MusifyNotificationController.audioPlayStateChanged);
        try {
            phoneStateListener = new PhoneStateListener() {
                @Override
                public void onCallStateChanged(int state, String incomingNumber) {
                    if (state == TelephonyManager.CALL_STATE_RINGING) {
                        if (musifyStreamingController.isPlaying()) {
                            musifyStreamingController.handlePauseRequest();
                        }
                    } else if (state == TelephonyManager.CALL_STATE_IDLE) {

                    } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {

                    }
                    super.onCallStateChanged(state, incomingNumber);
                }
            };
            TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            if (mgr != null) {
                mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
            }
        } catch (Exception e) {
            Log.e("tmessages", e.toString());
        }
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent startIntent, int flags, int startId) {
        try {
            TrackMetaData messageObject
                    = MusifyStreamingController.getInstance(MusifyStreamingService.this).getCurrentAudio();
            if (messageObject == null) {
                Handler handler = new Handler(MusifyStreamingService.this.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        stopSelf();
                    }
                });
                return START_STICKY;
            }

            if (supportLockScreenControls) {
                ComponentName remoteComponentName
                        = new ComponentName(getApplicationContext(), MusifyStreamingReceiver.class.getName());
                try {
                    if (remoteControlClient == null) {
                        audioManager.registerMediaButtonEventReceiver(remoteComponentName);
                        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
                        mediaButtonIntent.setComponent(remoteComponentName);
                        PendingIntent mediaPendingIntent
                                = PendingIntent.getBroadcast(this, 0, mediaButtonIntent, 0);
                        remoteControlClient = new RemoteControlClient(mediaPendingIntent);
                        audioManager.registerRemoteControlClient(remoteControlClient);
                    }
                    remoteControlClient.setTransportControlFlags(RemoteControlClient.FLAG_KEY_MEDIA_PLAY | RemoteControlClient.FLAG_KEY_MEDIA_PAUSE
                            | RemoteControlClient.FLAG_KEY_MEDIA_PLAY_PAUSE | RemoteControlClient.FLAG_KEY_MEDIA_STOP
                            | RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS | RemoteControlClient.FLAG_KEY_MEDIA_NEXT);
                } catch (Exception e) {
                    Log.e("tmessages", e.toString());
                }
            }
            createNotification(messageObject);
        } catch (Exception e) {

        }
        if (startIntent != null) {
            String action = startIntent.getAction();
            String command = startIntent.getStringExtra(Service.COMMAND.CMD_NAME);
            if (Service.COMMAND.ACTION_CMD.equals(action)) {
                if (Service.COMMAND.CMD_PAUSE.equals(command)) {
                    musifyStreamingController.handlePauseRequest();
                } else if (Service.COMMAND.CMD_STOP_CASTING.equals(command)) {
                    //TODO FOR EXTERNAL DEVICE
                }
            }
        }
        return START_NOT_STICKY;
    }

    private void createNotification(TrackMetaData mSongDetail) {
        try {

            String channelId = "";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                channelId = getNotificationChannelId();
            }

            String songName = mSongDetail.getTrackTitle();
            String authorName = mSongDetail.getTrackArtist();
            TrackMetaData audioInfo
                    = MusifyStreamingController.getInstance(MusifyStreamingService.this).getCurrentAudio();

            RemoteViews simpleContentView
                    = new RemoteViews(getApplicationContext().getPackageName(), R.layout.notification_small);
            RemoteViews expandedView = null;
            if (supportBigNotifications) {
                expandedView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.notification_big);
            }


            Notification notification = null;
            if (pendingIntent != null) {
                notification = new NotificationCompat.Builder(getApplicationContext(), channelId)
                        .setSmallIcon(R.drawable.player)
                        .setContentIntent(pendingIntent)
                        .setContentTitle(songName)
                        .build();
            } else {
                notification = new NotificationCompat.Builder(getApplicationContext(), channelId)
                        .setSmallIcon(R.drawable.player)
                        .setContentTitle(songName)
                        .build();
            }

            notification.contentView = simpleContentView;
            if (supportBigNotifications) {
                notification.bigContentView = expandedView;
            }

            setListeners(simpleContentView);
            if (supportBigNotifications) {
                setListeners(expandedView);
            }

            Bitmap albumArt = null;
            try {
                ImageLoader imageLoader = ImageLoader.getInstance();
                albumArt = imageLoader.loadImageSync(audioInfo.getTrackArt());
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (albumArt != null) {
                notification.contentView.setImageViewBitmap(R.id.player_album_art, albumArt);
                if (supportBigNotifications) {
                    notification.bigContentView.setImageViewBitmap(R.id.player_album_art, albumArt);
                }
            } else {
                notification.contentView.setImageViewResource(R.id.player_album_art, R.drawable.bg_default_cover);
                if (supportBigNotifications) {
                    notification.bigContentView.setImageViewResource(R.id.player_album_art, R.drawable.bg_default_cover);
                }
            }
            notification.contentView.setViewVisibility(R.id.player_progress_bar, View.GONE);
            notification.contentView.setViewVisibility(R.id.player_next, View.VISIBLE);
            notification.contentView.setViewVisibility(R.id.player_previous, View.VISIBLE);
            if (supportBigNotifications) {
                notification.bigContentView.setViewVisibility(R.id.player_next, View.VISIBLE);
                notification.bigContentView.setViewVisibility(R.id.player_previous, View.VISIBLE);
                notification.bigContentView.setViewVisibility(R.id.player_progress_bar, View.GONE);
            }

            if (!MusifyStreamingController.getInstance(MusifyStreamingService.this).isPlaying()) {
                notification.contentView.setViewVisibility(R.id.player_pause, View.GONE);
                notification.contentView.setViewVisibility(R.id.player_play, View.VISIBLE);
                if (supportBigNotifications) {
                    notification.bigContentView.setViewVisibility(R.id.player_pause, View.GONE);
                    notification.bigContentView.setViewVisibility(R.id.player_play, View.VISIBLE);
                }
            } else {
                notification.contentView.setViewVisibility(R.id.player_pause, View.VISIBLE);
                notification.contentView.setViewVisibility(R.id.player_play, View.GONE);
                if (supportBigNotifications) {
                    notification.bigContentView.setViewVisibility(R.id.player_pause, View.VISIBLE);
                    notification.bigContentView.setViewVisibility(R.id.player_play, View.GONE);
                }
            }

            notification.contentView.setTextViewText(R.id.player_song_name, songName);
            notification.contentView.setTextViewText(R.id.player_author_name, authorName);
            if (supportBigNotifications) {
                notification.bigContentView.setTextViewText(R.id.player_song_name, songName);
                notification.bigContentView.setTextViewText(R.id.player_author_name, authorName);
//                notification.bigContentView.setTextViewText(R.id.player_albumname, albumName);
            }
            notification.flags |= Notification.FLAG_ONGOING_EVENT;
            startForeground(5, notification);

            if (remoteControlClient != null) {
                RemoteControlClient.MetadataEditor metadataEditor = remoteControlClient.editMetadata(true);
                metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_ARTIST, authorName);
                metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_TITLE, songName);
                if (albumArt != null) {
                    metadataEditor.putBitmap(RemoteControlClient.MetadataEditor.BITMAP_KEY_ARTWORK, albumArt);
                }
                metadataEditor.apply();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @NonNull
    private String getNotificationChannelId() {
        NotificationChannel channel = new NotificationChannel(TAG, getString(R.string.playback),
                android.app.NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(true);
        channel.setLightColor(Color.BLUE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        android.app.NotificationManager notificationManager =
                (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
        return TAG;
    }

    private void setListeners(RemoteViews view) {
        try {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0,
                    getIntentForNotification(Service.NOTIFY.PREVIOUS), PendingIntent.FLAG_UPDATE_CURRENT);
            view.setOnClickPendingIntent(R.id.player_previous, pendingIntent);
            pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0,
                    getIntentForNotification(Service.NOTIFY.CLOSE), PendingIntent.FLAG_UPDATE_CURRENT);
            view.setOnClickPendingIntent(R.id.player_close, pendingIntent);
            pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0,
                    getIntentForNotification(Service.NOTIFY.PAUSE), PendingIntent.FLAG_UPDATE_CURRENT);
            view.setOnClickPendingIntent(R.id.player_pause, pendingIntent);
            pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0,
                    getIntentForNotification(Service.NOTIFY.NEXT), PendingIntent.FLAG_UPDATE_CURRENT);
            view.setOnClickPendingIntent(R.id.player_next, pendingIntent);
            pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0,
                    getIntentForNotification(Service.NOTIFY.PLAY), PendingIntent.FLAG_UPDATE_CURRENT);
            view.setOnClickPendingIntent(R.id.player_play, pendingIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NonNull
    private Intent getIntentForNotification(@NonNull String action) {
        Intent intent = new Intent(action);
        intent.setClass(this, MusifyStreamingReceiver.class);
        return intent;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (remoteControlClient != null) {
            RemoteControlClient.MetadataEditor metadataEditor = remoteControlClient.editMetadata(true);
            metadataEditor.clear();
            metadataEditor.apply();
            audioManager.unregisterRemoteControlClient(remoteControlClient);
        }
        try {
            TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            if (mgr != null) {
                mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
            }
        } catch (Exception e) {
            Logger.e("tmessages", e.toString());
        }
        MusifyNotificationController
                .getInstance()
                .removeObserver(this, MusifyNotificationController.audioProgressDidChanged);
        MusifyNotificationController
                .getInstance()
                .removeObserver(this, MusifyNotificationController.audioPlayStateChanged);
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == MusifyNotificationController.setAnyPendingIntent) {
            PendingIntent pendingIntent = (PendingIntent) args[0];
            if (pendingIntent != null) {
                this.pendingIntent = pendingIntent;
            }
        } else if (id == MusifyNotificationController.audioPlayStateChanged) {
            TrackMetaData mSongDetail = MusifyStreamingController.getInstance(MusifyStreamingService.this).getCurrentAudio();
            if (mSongDetail != null) {
                createNotification(mSongDetail);
            } else {
                stopSelf();
            }
        }
    }

    @Override
    public void newSongLoaded(Object... args) {

    }
}

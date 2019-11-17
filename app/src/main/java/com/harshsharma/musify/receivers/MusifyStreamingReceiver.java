package com.harshsharma.musify.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

import com.harshsharma.musify.controllers.MusifyStreamingController;
import com.harshsharma.musify.interfaces.MusifyConstants;

// Receiver for Service Help
public class MusifyStreamingReceiver extends BroadcastReceiver implements MusifyConstants {

    private MusifyStreamingController musifyStreamingController;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.musifyStreamingController = MusifyStreamingController.getInstance(context);
        if (this.musifyStreamingController == null) {
            return;
        }
        if (intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)) {
            if (intent.getExtras() == null) {
                return;
            }
            KeyEvent keyEvent = (KeyEvent) intent.getExtras().get(Intent.EXTRA_KEY_EVENT);
            if (keyEvent == null) {
                return;
            }
            if (keyEvent.getAction() != KeyEvent.ACTION_DOWN)
                return;
            switch (keyEvent.getKeyCode()) {
                case KeyEvent.KEYCODE_HEADSETHOOK:
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                    if (this.musifyStreamingController.isPlaying()) {
                        this.musifyStreamingController.onPause();
                    } else {
                        this.musifyStreamingController.onPlay(this.musifyStreamingController.getCurrentAudio());
                    }
                    break;
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                    this.musifyStreamingController.onPlay(this.musifyStreamingController.getCurrentAudio());
                    break;
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                    this.musifyStreamingController.onPause();
                    break;
                case KeyEvent.KEYCODE_MEDIA_STOP:
                    break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    this.musifyStreamingController.onSkipToNext();
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    this.musifyStreamingController.onSkipToPrevious();
                    break;
            }
        } else {
            this.musifyStreamingController = MusifyStreamingController.getInstance(context);
            if (intent.getAction().equals(Service.NOTIFY.PLAY)) {
                this.musifyStreamingController.onPlay(this.musifyStreamingController.getCurrentAudio());
            } else if (intent.getAction().equals(Service.NOTIFY.PAUSE)
                    || intent.getAction().equals(android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
                this.musifyStreamingController.onPause();
            } else if (intent.getAction().equals(Service.NOTIFY.NEXT)) {
                this.musifyStreamingController.onSkipToNext();
            } else if (intent.getAction().equals(Service.NOTIFY.CLOSE)) {
                this.musifyStreamingController.cleanupPlayer(context, true, true);
            } else if (intent.getAction().equals(Service.NOTIFY.PREVIOUS)) {
                this.musifyStreamingController.onSkipToPrevious();
            }
        }
    }
}

package com.harshsharma.musify.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;

import com.harshsharma.musify.R;
import com.harshsharma.musify.interfaces.MusifyConstants;
import com.harshsharma.musify.models.commons.EventObject;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Vector;

import butterknife.ButterKnife;


/*
        All activities in the app must extend this Activity common Tasks Like
        Event Bus and ButterKnife Initialization Done Here.
*/

public abstract class BaseActivity extends AppCompatActivity implements MusifyConstants {
    public static final int SHOW_TOAST = 0;
    private PauseHandler pauseHandler = new PauseHandler();
    private ProgressDialog progressDialog;

    protected abstract
    @LayoutRes
    int getLayout();

    @Subscribe
    public abstract void onEvent(EventObject eventObject);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        injectViews();
        EventBus.getDefault().register(this);
        initProgressDialog(getString(R.string.progress_text_loading));
    }

    private void initProgressDialog(String msg) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage(msg);
    }

    public void showProgress(String msg) {
        progressDialog.setMessage(msg);
        progressDialog.show();
    }

    public void dismissProgress() {
        progressDialog.dismiss();
    }

    private void injectViews() {
        ButterKnife.bind(this);
    }

    public void sendMessageToHandler(int what, int arg1, int arg2, Object response) {
        Message message = pauseHandler.obtainMessage();
        message.obj = response;
        message.what = what;
        message.arg1 = arg1;
        message.arg2 = arg2;
        pauseHandler.sendMessage(message);
    }

    @Override
    protected void onResume() {
        super.onResume();
        pauseHandler.setBaseActivity(this);
        pauseHandler.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseHandler.pause();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        progressDialog.dismiss();
        super.onDestroy();
    }

    public boolean isActivityPaused() {
        return pauseHandler.isPaused();
    }

    public void processMessage(Message message) {
        switch (message.what) {
            case SHOW_TOAST:
                String msg = (String) message.obj;
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                break;
        }
    }


    public void showToast(String toastMessage) {
        sendMessageToHandler(SHOW_TOAST, -1, 1, toastMessage);
    }

    public void loadResourceInImageView(int resource, ImageView imageView) {
        Picasso.get().load(resource).into(imageView);
    }

    @SuppressLint("HandlerLeak")
    private class PauseHandler extends Handler {

        /**
         * Message Queue Buffer
         */
        final Vector<Message> messageQueueBuffer = new Vector<>();
        BaseActivity baseActivity;
        /**
         * Flag indicating the pause state
         */
        private boolean paused;

        /**
         * Resume the handler
         */
        final void resume() {
            paused = false;

            while (messageQueueBuffer.size() > 0) {
                final Message msg = messageQueueBuffer.elementAt(0);
                messageQueueBuffer.removeElementAt(0);
                sendMessage(msg);
            }
        }

        /**
         * Pause the handler
         */
        final void pause() {
            paused = true;
        }

        boolean isPaused() {
            return paused;
        }

        final void setBaseActivity(BaseActivity baseActivity) {
            this.baseActivity = baseActivity;
        }

        boolean storeMessage(Message message) {
            return true;
        }


        @Override
        final public void handleMessage(Message msg) {
            if (paused) {
                if (storeMessage(msg)) {
                    Message msgCopy = new Message();
                    msgCopy.copyFrom(msg);
                    messageQueueBuffer.add(msgCopy);
                }
            } else {
                processMessage(msg);
            }
        }
    }

}

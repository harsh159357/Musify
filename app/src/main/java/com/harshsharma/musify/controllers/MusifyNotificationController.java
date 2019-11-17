package com.harshsharma.musify.controllers;

import android.util.SparseArray;

import com.harshsharma.musify.utilites.Logger;

import java.util.ArrayList;

// Notification Controller Responsible for the notification ations in the app w.r.t music.
public class MusifyNotificationController {

    public static boolean DEBUG_VERSION = false;
    private static int totalEvents = 1;
    public static final int didReceivedNewMessages = totalEvents++;
    public static final int updateInterfaces = totalEvents++;
    public static final int audioProgressDidChanged = totalEvents++;
    public static final int audioDidReset = totalEvents++;
    public static final int audioPlayStateChanged = totalEvents++;

    public static final int screenshotTook = totalEvents++;
    public static final int albumsDidLoaded = totalEvents++;
    public static final int audioDidSent = totalEvents++;
    public static final int audioDidStarted = totalEvents++;
    public static final int audioRouteChanged = totalEvents++;
    public static final int newaudioloaded = totalEvents++;
    public static final int setAnyPendingIntent = totalEvents++;

    private SparseArray<ArrayList<Object>> observers = new SparseArray<ArrayList<Object>>();
    private SparseArray<ArrayList<Object>> removeAfterBroadcast = new SparseArray<ArrayList<Object>>();
    private SparseArray<ArrayList<Object>> addAfterBroadcast = new SparseArray<ArrayList<Object>>();
    private ArrayList<DelayedPost> delayedPosts = new ArrayList<DelayedPost>(10);

    private int broadcasting = 0;
    private boolean animationInProgress;

    public interface NotificationCenterDelegate {
        void didReceivedNotification(int id, Object... args);

        void newSongLoaded(Object... args);
    }

    private class DelayedPost {

        private DelayedPost(int id, Object[] args) {
            this.id = id;
            this.args = args;
        }

        private int id;
        private Object[] args;
    }

    private static volatile MusifyNotificationController Instance = null;

    public static MusifyNotificationController getInstance() {
        MusifyNotificationController localInstance = Instance;
        if (localInstance == null) {
            synchronized (MusifyNotificationController.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new MusifyNotificationController();
                }
            }
        }
        return localInstance;
    }

    public void setAnimationInProgress(boolean value) {
        animationInProgress = value;
        if (!animationInProgress && !delayedPosts.isEmpty()) {
            for (DelayedPost delayedPost : delayedPosts) {
                postNotificationNameInternal(
                        delayedPost.id,
                        true,
                        delayedPost.args);
            }
            delayedPosts.clear();
        }
    }

    public void postNotificationName(int id, Object... args) {
        boolean allowDuringAnimation = false;
        postNotificationNameInternal(id, allowDuringAnimation, args);
    }

    public void postNotificationNameInternal(int id, boolean allowDuringAnimation, Object... args) {
        if (DEBUG_VERSION) {
            if (Thread.currentThread()
                    != MusifyStreamingController.applicationHandler.getLooper().getThread()) {
                throw new RuntimeException("postNotificationName allowed only from MAIN thread");
            }
        }
        if (!allowDuringAnimation && animationInProgress) {
            DelayedPost delayedPost = new DelayedPost(id, args);
            delayedPosts.add(delayedPost);
            if (DEBUG_VERSION) {
                Logger.e("tmessages",
                        "delay post notification " + id + " with args count = " + args.length);
            }
            return;
        }
        broadcasting++;
        ArrayList<Object> objects = observers.get(id);
        if (objects != null && !objects.isEmpty()) {
            for (int a = 0; a < objects.size(); a++) {
                Object obj = objects.get(a);
                ((NotificationCenterDelegate) obj).didReceivedNotification(id, args);
            }
        }
        broadcasting--;
        if (broadcasting == 0) {
            if (removeAfterBroadcast.size() != 0) {
                for (int a = 0; a < removeAfterBroadcast.size(); a++) {
                    int key = removeAfterBroadcast.keyAt(a);
                    ArrayList<Object> arrayList = removeAfterBroadcast.get(key);
                    for (int b = 0; b < arrayList.size(); b++) {
                        removeObserver(arrayList.get(b), key);
                    }
                }
                removeAfterBroadcast.clear();
            }
            if (addAfterBroadcast.size() != 0) {
                for (int a = 0; a < addAfterBroadcast.size(); a++) {
                    int key = addAfterBroadcast.keyAt(a);
                    ArrayList<Object> arrayList = addAfterBroadcast.get(key);
                    for (int b = 0; b < arrayList.size(); b++) {
                        addObserver(arrayList.get(b), key);
                    }
                }
                addAfterBroadcast.clear();
            }
        }
    }

    public void addObserver(Object observer, int id) {
        if (DEBUG_VERSION) {
            if (Thread.currentThread()
                    != MusifyStreamingController.applicationHandler.getLooper().getThread()) {
                throw new RuntimeException("addObserver allowed only from MAIN thread");
            }
        }
        if (broadcasting != 0) {
            ArrayList<Object> arrayList = addAfterBroadcast.get(id);
            if (arrayList == null) {
                arrayList = new ArrayList<Object>();
                addAfterBroadcast.put(id, arrayList);
            }
            arrayList.add(observer);
            return;
        }
        ArrayList<Object> objects = observers.get(id);
        if (objects == null) {
            observers.put(id, (objects = new ArrayList<Object>()));
        }
        if (objects.contains(observer)) {
            return;
        }
        objects.add(observer);
    }

    public void removeObserver(Object observer, int id) {
        if (DEBUG_VERSION) {
            if (Thread.currentThread()
                    != MusifyStreamingController.applicationHandler.getLooper().getThread()) {
                throw new RuntimeException("removeObserver allowed only from MAIN thread");
            }
        }
        if (broadcasting != 0) {
            ArrayList<Object> arrayList = removeAfterBroadcast.get(id);
            if (arrayList == null) {
                arrayList = new ArrayList<Object>();
                removeAfterBroadcast.put(id, arrayList);
            }
            arrayList.add(observer);
            return;
        }
        ArrayList<Object> objects = observers.get(id);
        if (objects != null) {
            objects.remove(observer);
        }
    }

    public void notifyNewSongLoaded(int id, Object... args) {
        ArrayList<Object> objects = observers.get(id);
        if (objects != null && !objects.isEmpty()) {
            for (int a = 0; a < objects.size(); a++) {
                Object obj = objects.get(a);
                ((NotificationCenterDelegate) obj).newSongLoaded(args);
            }
        }
    }
}

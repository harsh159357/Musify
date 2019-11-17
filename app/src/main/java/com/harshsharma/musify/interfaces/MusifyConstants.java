package com.harshsharma.musify.interfaces;

// Common constants used in the App
public interface MusifyConstants {
    int OFFSCREEN_PAGE_LIMIT = 3;

    long SHIMMER_DELAY = 2000;

    String TYPE = "TYPE";

    public interface DashBoardFragments {
        String TRACKS = "TRACKS";
        String PURCHASES = "PURCHASES";
        String PROFILE = "PROFILE";
    }

    interface EventCenter {
        int NO_INTERNET_CONNECTION = 0;

        int GET_ALL_TRACKS_SUCCESSFUL = 100;
        int GET_ALL_TRACKS_UN_SUCCESSFUL = 101;

        int TRACK_PURCHASED_SUCCESSFULLY = 102;
        int TRACK_IS_ALREADY_PURCHASED = 103;
        int UNABLE_TO_PURCHASE_TRACK_OR_SOMETHING_WENT_WRONG = 104;

        int RELOAD_PURCHASES = 105;

        int SETUP_MUSIC = 106;
        int PLAY_A_SPECIFIC_TRACK = 107;

    }

    interface Service {
        interface COMMAND {
            String ACTION_CMD = "com.harshsharma.musify.audiostreaming.ACTION_CMD";
            String CMD_NAME = "CMD_NAME";
            String CMD_PAUSE = "CMD_PAUSE";
            String CMD_STOP_CASTING = "CMD_STOP_CASTING";
        }

        interface NOTIFY {
            String PREVIOUS = "com.harshsharma.musify.audiostreamer.previous";
            String CLOSE = "com.harshsharma.musify.audiostreamer.close";
            String PAUSE = "com.harshsharma.musify.audiostreamer.pause";
            String PLAY = "com.harshsharma.musify.audiostreamer.play";
            String NEXT = "com.harshsharma.musify.audiostreamer.next";
        }
    }

}

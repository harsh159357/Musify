package com.harshsharma.musify.fragments.dashboard;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.harshsharma.musify.R;
import com.harshsharma.musify.activities.DashBoardActivity;
import com.harshsharma.musify.adapters.SlidingImageAdapter;
import com.harshsharma.musify.adapters.ViewPagerAdapterForDashBoard;
import com.harshsharma.musify.controllers.MusifyStreamingController;
import com.harshsharma.musify.customviews.TextViewCustom;
import com.harshsharma.musify.fragments.BaseFragment;
import com.harshsharma.musify.interfaces.MusifyServiceCallback;
import com.harshsharma.musify.models.Track;
import com.harshsharma.musify.models.TrackMetaData;
import com.harshsharma.musify.models.commons.EventObject;
import com.harshsharma.musify.utilites.CustomViewPager;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

// Main Fragment of the App Responsible for loading all other Fragments present in the App.
public class DashBoardFragment extends BaseFragment implements MusifyServiceCallback {
    @BindView(R.id.bottom_navigation_view)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.dashboard_pager)
    CustomViewPager customViewPager;
    @BindView(R.id.dashboard_music_pager)
    CustomViewPager dashBoardMusicPager;

    private MusifyStreamingController musifyStreamingController;
    private TrackMetaData currentTrack;
    private List<TrackMetaData> tracksAvailable = new ArrayList<TrackMetaData>();

    /*
        @BindView(R.id.music_cover_big)
        ImageView musicCoverBig;
    */
    @BindView(R.id.music_cover_small)
    ImageView musicCoverSmall;

    @BindView(R.id.music_title)
    TextViewCustom musicTitle;
    @BindView(R.id.music_artist)
    TextViewCustom musicArtist;

    @BindView(R.id.play_button_tool_bar)
    ImageButton playButtonToolbar;
    @BindView(R.id.pause_button_tool_bar)
    ImageButton pauseButtonToolbar;


    @BindView(R.id.my_toolbar)
    Toolbar toolbar;
    private ActionBar actionBar;

    private LinearLayout bottomSheet;
    private BottomSheetBehavior bottomSheetBehavior;

    private View rootView;
    private Unbinder unbinder;
    private DashBoardActivity dashBoardActivityContext;

    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.tab_tracks:
                    customViewPager.setCurrentItem(0, true);
                    break;
                case R.id.tab_purchases:
                    customViewPager.setCurrentItem(1, true);
                    break;
                case R.id.tab_profile:
                    customViewPager.setCurrentItem(2, true);
                    break;
            }
            return true;
        }
    };
    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            bottomNavigationView.getMenu().getItem(position).setChecked(true);
            switch (position) {
                case 0:
                    actionBar.setTitle(dashBoardActivityContext.getString(R.string.all_tracks));
                    break;
                case 1:
                    actionBar.setTitle(dashBoardActivityContext.getString(R.string.all_purchases));
                    break;
                case 2:
                    actionBar.setTitle(dashBoardActivityContext.getString(R.string.my_profile));
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }

    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configStreaming();
    }

    private void configStreaming() {
        musifyStreamingController = MusifyStreamingController.getInstance(context);
        musifyStreamingController.setPlayMultiple(true);
        musifyStreamingController.setMediaList(tracksAvailable);
        musifyStreamingController.setShowPlayerNotification(true);
        musifyStreamingController.setPendingIntentAct(getNotificationPendingIntent());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_dash_board, container, false);
        EventBus.getDefault().register(this);
        unbinder = ButterKnife.bind(this, rootView);
        initView();
        initToolbar();
        setHasOptionsMenu(false);
        return rootView;
    }

    private void initToolbar() {
        dashBoardActivityContext.setSupportActionBar(toolbar);
        actionBar = dashBoardActivityContext.getSupportActionBar();
        dashBoardActivityContext.getSupportActionBar().setDisplayShowTitleEnabled(true);
        actionBar.setTitle(getString(R.string.all_tracks));
    }

    private void initView() {
        customViewPager.setAdapter(new ViewPagerAdapterForDashBoard(dashBoardActivityContext
                .getSupportFragmentManager(), this));
        customViewPager.setPagingEnabled(true);
        customViewPager.addOnPageChangeListener(onPageChangeListener);
        customViewPager.setOffscreenPageLimit(OFFSCREEN_PAGE_LIMIT);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

        bottomSheet = rootView.findViewById(R.id.bottom_sheet_music);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        bottomNavigationView.setVisibility(View.GONE);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        bottomNavigationView.setVisibility(View.VISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        musicTitle.setSelected(true);
        musicArtist.setSelected(true);
    }

    @Override
    public String getFragmentName() {
        return DashBoardFragment.class.getSimpleName();
    }

    @Subscribe
    public void onEvent(final EventObject eventObject) {
        dashBoardActivityContext.runOnUiThread(() -> {
            switch (eventObject.getId()) {
                case EventCenter.TRACK_PURCHASED_SUCCESSFULLY:
                    dismissProgress();
                    showToast(dashBoardActivityContext.getString(R.string.track_purchase_successful));
                    new Handler()
                            .postDelayed(() ->
                                            EventBus
                                                    .getDefault()
                                                    .post(new EventObject(EventCenter.RELOAD_PURCHASES, null))
                                    , 1000);
                    bottomNavigationView.setSelectedItemId(R.id.tab_purchases);
                    break;
                case EventCenter.TRACK_IS_ALREADY_PURCHASED:
                    dismissProgress();
                    showToast(dashBoardActivityContext.getString(R.string.track_already_purchased));
                    break;
                case EventCenter.UNABLE_TO_PURCHASE_TRACK_OR_SOMETHING_WENT_WRONG:
                    dismissProgress();
                    showToast(dashBoardActivityContext.getString(R.string.unable_to_purchase_track));
                    break;
                case EventCenter.SETUP_MUSIC:
                    loadMusicData((ArrayList<Track>) eventObject.getObject());
                    break;
                case EventCenter.PLAY_A_SPECIFIC_TRACK:
                    playASpecificTrack((Track) eventObject.getObject());
                    break;
            }
        });
    }

    private void loadMusicData(ArrayList<Track> trackArrayList) {
        ArrayList<TrackMetaData> trackMetaData = new ArrayList<>();

        for (int i = 0; i < trackArrayList.size(); i++) {
            TrackMetaData metaData = new TrackMetaData();
            metaData.setTrackId(String.valueOf(trackArrayList.get(i).getTrackId()));
            metaData.setTrackUrl(trackArrayList.get(i).getUrl());
            metaData.setTrackTitle(trackArrayList.get(i).getSong());
            metaData.setTrackArtist(trackArrayList.get(i).getArtists());
            metaData.setTrackArt(trackArrayList.get(i).getCover_image());
            trackMetaData.add(metaData);
        }


        tracksAvailable = trackMetaData;
        configStreaming();
        checkAlreadyPlaying();
        currentTrack = trackMetaData.get(0);
        loadTrackDetails(currentTrack);
        dashBoardMusicPager.setAdapter(new SlidingImageAdapter(dashBoardActivityContext, trackMetaData));

        dashBoardMusicPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                playSong(tracksAvailable.get(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void playASpecificTrack(Track track) {
        TrackMetaData trackMetaData = new TrackMetaData();
        trackMetaData.setTrackId(String.valueOf(track.getTrackId()));
        trackMetaData.setTrackUrl(track.getUrl());
        trackMetaData.setTrackTitle(track.getSong());
        trackMetaData.setTrackArtist(track.getArtists());
        trackMetaData.setTrackArt(track.getCover_image());
        playSong(trackMetaData);
        setPagerIndex(trackMetaData);

    }

    private void setPagerIndex(TrackMetaData checkThisOut) {
        for (int pagerIndex = 0; pagerIndex < tracksAvailable.size(); pagerIndex++) {
            if (tracksAvailable.get(pagerIndex).getTrackId().equals(checkThisOut.getTrackId())) {
                Log.d("Index Found", pagerIndex + "");
                dashBoardMusicPager.setCurrentItem(pagerIndex);
                break;
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dashBoardActivityContext = (DashBoardActivity) context;
    }

    void setTracksAsSelectedTab() {
        bottomNavigationView.setSelectedItemId(R.id.tab_tracks);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            if (musifyStreamingController != null) {
                musifyStreamingController.subscribesCallBack(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        try {
            if (musifyStreamingController != null) {
                musifyStreamingController.unSubscribeCallBack();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        try {
            if (musifyStreamingController != null) {
                musifyStreamingController.unSubscribeCallBack();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @OnClick(R.id.play_button_tool_bar)
    void musicPlayToolBar() {
        playPauseToggle();
    }

    @OnClick(R.id.pause_button_tool_bar)
    void musicPauseToolbar() {
        playPauseToggle();
    }


    private void playPauseToggle() {
        if (currentTrack != null) {
            if (musifyStreamingController.isPlaying()) {
                musifyStreamingController.onPause();

                pauseButtonToolbar.setVisibility(View.GONE);

                playButtonToolbar.setVisibility(View.VISIBLE);

            } else {
                musifyStreamingController.onPlay(currentTrack);

                pauseButtonToolbar.setVisibility(View.VISIBLE);

                playButtonToolbar.setVisibility(View.GONE);
            }
        } else {
            showToast(dashBoardActivityContext.getString(R.string.no_track_loaded_yet));
        }
    }

    private void checkAlreadyPlaying() {
        if (musifyStreamingController.isPlaying()) {
            currentTrack = musifyStreamingController.getCurrentAudio();
            if (currentTrack != null) {
                currentTrack.setPlayState(musifyStreamingController.mLastPlaybackState);
                showTrackInfo(currentTrack);
            }
        }
    }

    private void playSong(TrackMetaData media) {
        if (musifyStreamingController != null && currentTrack != media) {
            togglePlay();
            musifyStreamingController.onPlay(media);
            showTrackInfo(media);
        }
    }

    private void togglePlay() {
        pauseButtonToolbar.setVisibility(View.VISIBLE);
        playButtonToolbar.setVisibility(View.GONE);
    }

    private void showTrackInfo(TrackMetaData trackMetaData) {
        currentTrack = trackMetaData;
        loadTrackDetails(trackMetaData);
    }

    private void loadTrackDetails(TrackMetaData metaData) {
        musicTitle.setText(metaData.getTrackTitle());
        musicArtist.setText(metaData.getTrackArtist());

        Picasso.get()
                .load(metaData.getTrackArt())
                .placeholder(R.drawable.bg_default_cover)
                .error(R.drawable.bg_default_cover)
                .into(musicCoverSmall);

    }

    @Override
    public void updatePlaybackState(int state) {
        switch (state) {
            case PlaybackStateCompat.STATE_PLAYING:
                if (currentTrack != null) {
                    currentTrack.setPlayState(PlaybackStateCompat.STATE_PLAYING);
                }
                break;
            case PlaybackStateCompat.STATE_PAUSED:
                if (currentTrack != null) {
                    currentTrack.setPlayState(PlaybackStateCompat.STATE_PAUSED);
                }
                break;
            case PlaybackStateCompat.STATE_NONE:
                currentTrack.setPlayState(PlaybackStateCompat.STATE_NONE);
                break;
            case PlaybackStateCompat.STATE_STOPPED:
                if (currentTrack != null) {
                    currentTrack.setPlayState(PlaybackStateCompat.STATE_NONE);
                }
                break;
            case PlaybackStateCompat.STATE_BUFFERING:
                showToast(dashBoardActivityContext.getString(R.string.buffering));
                if (currentTrack != null) {
                    currentTrack.setPlayState(PlaybackStateCompat.STATE_NONE);
                }
                break;
        }
    }

    @Override
    public void playSongComplete() {
    }

    @Override
    public void currentSeekBarPosition(int progress) {
    }

    @Override
    public void playCurrent(int trackIndex, TrackMetaData currentAudio) {
        showTrackInfo(currentAudio);
    }

    @Override
    public void playNext(int trackIndex, TrackMetaData currentAudio) {
        togglePlay();
        showTrackInfo(currentAudio);
        setPagerIndex(currentAudio);
    }

    @Override
    public void playPrevious(int trackIndex, TrackMetaData currentAudio) {
        togglePlay();
        showTrackInfo(currentAudio);
        setPagerIndex(currentAudio);
    }

    private PendingIntent getNotificationPendingIntent() {
        Intent intent = new Intent(dashBoardActivityContext, DashBoardActivity.class);
        intent.setAction("openplayer");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent mPendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        return mPendingIntent;
    }

}
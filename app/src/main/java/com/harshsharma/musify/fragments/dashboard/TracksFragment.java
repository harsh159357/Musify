package com.harshsharma.musify.fragments.dashboard;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.harshsharma.musify.R;
import com.harshsharma.musify.activities.DashBoardActivity;
import com.harshsharma.musify.adapters.TracksAdapter;
import com.harshsharma.musify.customviews.TextViewCustom;
import com.harshsharma.musify.fragments.BaseFragment;
import com.harshsharma.musify.interfaces.OnTracksClickListener;
import com.harshsharma.musify.models.Track;
import com.harshsharma.musify.models.commons.EventObject;
import com.harshsharma.musify.requesters.GetTracksRequester;
import com.harshsharma.musify.requesters.PurchaseRequester;
import com.harshsharma.musify.utilites.BackgroundExecutor;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

// Fragment with track coming up from Rest API
public class TracksFragment extends BaseFragment implements OnTracksClickListener {

    @BindView(R.id.recycler_view_tracks)
    RecyclerView recyclerViewTracks;
    @BindView(R.id.shimmer_view_tracks)
    ShimmerFrameLayout shimmerFrameLayout;
    @BindView(R.id.tv_no_tracks)
    TextViewCustom textViewCustomNoTracks;

    private TracksAdapter tracksAdapter;

    private ArrayList<Track> trackArrayList = new ArrayList<>();

    private View rootView;
    private Unbinder unbinder;

    private DashBoardActivity dashBoardActivityContext;


    public TracksFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tracks,
                container, false);
        dashBoardActivityContext = (DashBoardActivity) context;
        EventBus.getDefault().register(this);
        unbinder = ButterKnife.bind(this, rootView);
        initView();
        setHasOptionsMenu(true);
        return rootView;
    }

    private void initView() {
        tracksAdapter =
                new TracksAdapter(trackArrayList, TracksFragment.this);
        recyclerViewTracks.setLayoutManager(
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        recyclerViewTracks.setItemAnimator(new DefaultItemAnimator());
        recyclerViewTracks.setHasFixedSize(true);
        recyclerViewTracks.setAdapter(tracksAdapter);
        new Handler().postDelayed(this::fetchChapters, SHIMMER_DELAY);
    }

    private void fetchChapters() {
        BackgroundExecutor.getInstance().execute(new GetTracksRequester());
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_tracks, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.btn_refresh) {
            trackArrayList.clear();
            tracksAdapter.notifyDataSetChanged();
            textViewCustomNoTracks.callOnClick();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Subscribe
    public void onEvent(final EventObject eventObject) {
        dashBoardActivityContext.runOnUiThread(() -> {
            switch (eventObject.getId()) {
                case EventCenter.GET_ALL_TRACKS_SUCCESSFUL:
                    trackArrayList.clear();
                    trackArrayList.addAll((ArrayList<Track>) eventObject.getObject());
                    tracksAdapter.notifyDataSetChanged();
                    hideShimmer();
                    break;
                case EventCenter.GET_ALL_TRACKS_UN_SUCCESSFUL:
                    hideShimmer();
                    trackArrayList.clear();
                    tracksAdapter.notifyDataSetChanged();
                    textViewCustomNoTracks.setVisibility(View.VISIBLE);
                    showToast(dashBoardActivityContext.getString(R.string.no_track_available));
                    break;
                case EventCenter.TRACK_IS_ALREADY_PURCHASED:
                case EventCenter.TRACK_PURCHASED_SUCCESSFULLY:
                    trackArrayList.remove((Track) eventObject.getObject());
                    tracksAdapter.notifyDataSetChanged();
                    break;
                case EventCenter.NO_INTERNET_CONNECTION:
                    hideShimmer();
                    trackArrayList.clear();
                    tracksAdapter.notifyDataSetChanged();
                    textViewCustomNoTracks.setVisibility(View.VISIBLE);
                    showToast(dashBoardActivityContext.getString(R.string.no_internet_connection));
                    break;
            }
        });
    }

    @OnClick(R.id.tv_no_tracks)
    public void onNoTracksClicks() {
        textViewCustomNoTracks.setVisibility(View.GONE);
        showShimmer();
        new Handler().postDelayed(this::fetchChapters, SHIMMER_DELAY);
    }

    @Override
    public void onResume() {
        super.onResume();
        shimmerFrameLayout.startShimmer();
    }

    @Override
    public void onPause() {
        shimmerFrameLayout.stopShimmer();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }


    @Override
    public String getFragmentName() {
        return TracksFragment.class.getSimpleName();
    }

    @Override
    public void onTrackClick(int position, Track track) {
        EventBus.getDefault().post(new EventObject(EventCenter.PLAY_A_SPECIFIC_TRACK, track));
    }

    @Override
    public void onTrackPurchase(int position, Track track) {
        AlertDialog.Builder alert = new AlertDialog.Builder(dashBoardActivityContext);
        alert.setTitle(dashBoardActivityContext.getString(R.string.purchase));
        alert.setMessage(dashBoardActivityContext.getString(R.string.confirm_purchase) + " " + track.getSong() + " ?");
        alert.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            showProgress(dashBoardActivityContext.getString(R.string.purchasing));
            new Handler()
                    .postDelayed(() -> BackgroundExecutor
                            .getInstance()
                            .execute(new PurchaseRequester(track)), SHIMMER_DELAY);
        });
        alert.setNegativeButton(android.R.string.cancel, null);
        alert.show();
    }

    private void hideShimmer() {
        shimmerFrameLayout.stopShimmer();
        shimmerFrameLayout.setVisibility(View.GONE);
    }

    private void showShimmer() {
        shimmerFrameLayout.startShimmer();
        shimmerFrameLayout.setVisibility(View.VISIBLE);
    }


}

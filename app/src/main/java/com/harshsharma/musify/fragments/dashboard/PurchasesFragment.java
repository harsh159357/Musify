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

import com.harshsharma.musify.R;
import com.harshsharma.musify.activities.DashBoardActivity;
import com.harshsharma.musify.adapters.PurchaseAdapter;
import com.harshsharma.musify.controllers.ObjectBoxController;
import com.harshsharma.musify.customviews.TextViewCustom;
import com.harshsharma.musify.fragments.BaseFragment;
import com.harshsharma.musify.interfaces.OnTracksClickListener;
import com.harshsharma.musify.models.Track;
import com.harshsharma.musify.models.commons.EventObject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.objectbox.query.Query;

// Fragment for All the purchases in the App.
public class PurchasesFragment extends BaseFragment implements OnTracksClickListener {

    @BindView(R.id.recycler_view_purchases)
    RecyclerView recyclerViewPurchases;
    @BindView(R.id.tv_no_purchases)
    TextViewCustom textViewCustomNoPurchases;

    private Query<Track> trackQuery;
    private List<Track> purchasedTracks;

    private PurchaseAdapter purchaseAdapter;

    private View rootView;
    private Unbinder unbinder;

    private DashBoardActivity dashBoardActivityContext;

    public PurchasesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_purchases,
                container, false);
        EventBus.getDefault().register(this);
        initTrackBox();
        dashBoardActivityContext = (DashBoardActivity) context;
        unbinder = ButterKnife.bind(this, rootView);
        initView();
        setHasOptionsMenu(true);
        return rootView;
    }

    private void initTrackBox() {
        trackQuery = ObjectBoxController.getTrackBox().query().build();
        purchasedTracks = trackQuery.find();
    }


    private void initView() {
        if (purchasedTracks.isEmpty()) {
            textViewCustomNoPurchases.setVisibility(View.VISIBLE);
        } else {
            textViewCustomNoPurchases.setVisibility(View.GONE);
            purchaseAdapter =
                    new PurchaseAdapter(purchasedTracks, PurchasesFragment.this);
            recyclerViewPurchases.setLayoutManager(
                    new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            recyclerViewPurchases.setItemAnimator(new DefaultItemAnimator());
            recyclerViewPurchases.setHasFixedSize(true);
            recyclerViewPurchases.setAdapter(purchaseAdapter);
            purchaseAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_purchases, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.btn_delete_all_purchases) {
            if (purchasedTracks.isEmpty()) {
                showToast(dashBoardActivityContext.getString(R.string.no_purchases_found));
            } else {
                AlertDialog.Builder alert = new AlertDialog.Builder(dashBoardActivityContext);
                alert.setTitle(dashBoardActivityContext.getString(R.string.remove_all_purchases));
                alert.setMessage(dashBoardActivityContext.getString(R.string.confirm_remove_purchases));
                alert.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    ObjectBoxController.getTrackBox().removeAll();
                    EventBus.getDefault().post(new EventObject(EventCenter.RELOAD_PURCHASES, null));
                });
                alert.setNegativeButton(android.R.string.cancel, null);
                alert.show();
            }
        }
        return super.onOptionsItemSelected(item);
    }


    @Subscribe
    public void onEvent(final EventObject eventObject) {
        dashBoardActivityContext.runOnUiThread(() -> {
            switch (eventObject.getId()) {
                case EventCenter.RELOAD_PURCHASES:
                    reload();
                    break;
            }
        });
    }

    private void reload() {
        showProgress(dashBoardActivityContext.getString(R.string.progress_text_loading));
        new Handler().postDelayed(() -> {
            initTrackBox();
            initView();
            dismissProgress();
        }, 1000);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }


    @Override
    public String getFragmentName() {
        return PurchasesFragment.class.getSimpleName();
    }

    @Override
    public void onTrackClick(int position, Track track) {
        EventBus.getDefault().post(new EventObject(EventCenter.PLAY_A_SPECIFIC_TRACK, track));
    }

    @Override
    public void onTrackPurchase(int position, Track track) {

    }
}

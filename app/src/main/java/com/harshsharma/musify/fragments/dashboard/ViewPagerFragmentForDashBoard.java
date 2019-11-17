package com.harshsharma.musify.fragments.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.harshsharma.musify.R;
import com.harshsharma.musify.fragments.BaseFragment;

// ViewPager Fragment for Tracks,Purchases and Profile
public class ViewPagerFragmentForDashBoard extends BaseFragment {

    private FragmentManager fragmentManager;
    private DashBoardFragment dashBoardFragment;

    public static ViewPagerFragmentForDashBoard newInstance(String type,
                                                            DashBoardFragment dashBoardFragment) {
        ViewPagerFragmentForDashBoard viewPagerFragmentForDashBoard = new ViewPagerFragmentForDashBoard();
        Bundle extras = new Bundle();
        extras.putString(TYPE, type);
        viewPagerFragmentForDashBoard.setArguments(extras);
        viewPagerFragmentForDashBoard.dashBoardFragment = dashBoardFragment;
        return viewPagerFragmentForDashBoard;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_pager_for_dashboard,
                container, false);
        fragmentManager = getChildFragmentManager();
        Bundle extras = getArguments();
        String type = null;
        type = extras.getString(TYPE, "");
        BaseFragment fragment;
        switch (type) {
            case DashBoardFragments.TRACKS:
                fragment = new TracksFragment();
                switchFragment(fragment);
                break;
            case DashBoardFragments.PURCHASES:
                fragment = new PurchasesFragment();
                switchFragment(fragment);
                break;
            case DashBoardFragments.PROFILE:
                fragment = new ProfileFragment();
                switchFragment(fragment);
                break;
            default:
                fragment = new TracksFragment();
                switchFragment(fragment);
                break;
        }
        return view;
    }

    @Override
    public String getFragmentName() {
        return ViewPagerFragmentForDashBoard.class.getSimpleName();
    }

    public void switchFragment(Fragment fragment) {
        fragmentManager.beginTransaction().replace(R.id.dashboard, fragment).commit();
    }


    public void setHomeAsSelectedTab() {
        dashBoardFragment.setTracksAsSelectedTab();
    }

}
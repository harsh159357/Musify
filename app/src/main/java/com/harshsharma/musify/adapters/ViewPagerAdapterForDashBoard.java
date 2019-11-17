package com.harshsharma.musify.adapters;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.harshsharma.musify.fragments.BaseFragment;
import com.harshsharma.musify.fragments.dashboard.DashBoardFragment;
import com.harshsharma.musify.fragments.dashboard.ViewPagerFragmentForDashBoard;

import java.util.ArrayList;

// ViewPagerAdapter for the Tracks, Purchases and Profile Fragments
public class ViewPagerAdapterForDashBoard extends FragmentStatePagerAdapter {
    private ArrayList<String> dashBoardFragments = new ArrayList<>();
    private DashBoardFragment dashBoardFragment;

    public ViewPagerAdapterForDashBoard(FragmentManager fm, DashBoardFragment dashBoardFragment) {
        super(fm, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        dashBoardFragments.add(ViewPagerFragmentForDashBoard.DashBoardFragments.TRACKS);
        dashBoardFragments.add(ViewPagerFragmentForDashBoard.DashBoardFragments.PURCHASES);
        dashBoardFragments.add(ViewPagerFragmentForDashBoard.DashBoardFragments.PROFILE);
        this.dashBoardFragment = dashBoardFragment;
    }

    @NonNull
    @Override
    public BaseFragment getItem(int position) {
        return ViewPagerFragmentForDashBoard
                .newInstance(dashBoardFragments.get(position), dashBoardFragment);
    }

    @Override
    public int getCount() {
        return dashBoardFragments.size();
    }

    public ArrayList<String> getDashBoardFragments() {
        return dashBoardFragments;
    }

    public void setDashBoardFragments(ArrayList<String> dashBoardFragments) {
        this.dashBoardFragments = dashBoardFragments;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.destroyItem(container, position, object);
    }
}

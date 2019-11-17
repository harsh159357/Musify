package com.harshsharma.musify.activities;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.harshsharma.musify.R;
import com.harshsharma.musify.controllers.ObjectBoxController;
import com.harshsharma.musify.fragments.dashboard.DashBoardFragment;
import com.harshsharma.musify.fragments.dashboard.ViewPagerFragmentForDashBoard;
import com.harshsharma.musify.models.commons.EventObject;

import org.greenrobot.eventbus.Subscribe;

// Main Activity of the App
public class DashBoardActivity extends BaseActivity {

    @Override
    protected int getLayout() {
        return R.layout.activity_dash_board;
    }

    @Subscribe
    @Override
    public void onEvent(EventObject eventObject) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ObjectBoxController.getInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = new DashBoardFragment();
        fragmentTransaction.add(R.id.dash_board_content,
                fragment,
                ViewPagerFragmentForDashBoard.DashBoardFragments.TRACKS);
        fragmentTransaction.commit();
    }
}

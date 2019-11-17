package com.harshsharma.musify.fragments.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.harshsharma.musify.R;
import com.harshsharma.musify.fragments.BaseFragment;
import com.vansuita.materialabout.builder.AboutBuilder;
import com.vansuita.materialabout.views.AboutView;

// Profile Fragment containing my contact Info (Email, LinkedIn, Github)
public class ProfileFragment extends BaseFragment {
    private FrameLayout frameLayout;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile,
                container, false);
        frameLayout = rootView.findViewById(R.id.my_profile);
        myProfile();
        setHasOptionsMenu(false);
        return rootView;
    }

    @Override
    public String getFragmentName() {
        return PurchasesFragment.class.getSimpleName();
    }

    private void myProfile() {
        AboutBuilder builder = AboutBuilder.with(getActivity())
                .setPhoto(R.drawable.bg_profile_picture)
                .setCover(R.drawable.bg_profile_cover)
                .setLinksAnimated(true)
                .setDividerDashGap(13)
                .setName(getString(R.string.name))
                .setSubTitle(getString(R.string.sub_title))
                .setLinksColumnsCount(4)
                .setBrief(getString(R.string.brief))
                .addGitHubLink(getString(R.string.github_link))
                .addFacebookLink(getString(R.string.facebook_link))
                .addLinkedInLink(getString(R.string.linkedin_link))
                .addEmailLink(getString(R.string.email_link))
                .addFeedbackAction(getString(R.string.email_link))
                .setWrapScrollView(true)
                .setShowAsCard(true);

        AboutView view = builder.build();
        frameLayout.addView(view);
    }
}
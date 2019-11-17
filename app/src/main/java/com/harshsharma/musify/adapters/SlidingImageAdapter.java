package com.harshsharma.musify.adapters;

import android.content.Context;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import com.harshsharma.musify.R;
import com.harshsharma.musify.models.TrackMetaData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SlidingImageAdapter extends PagerAdapter {

    private ArrayList<TrackMetaData> trackMetaData;
    private LayoutInflater inflater;
    private Context context;


    public SlidingImageAdapter(Context context, ArrayList<TrackMetaData> trackMetaData) {
        this.context = context;
        this.trackMetaData = trackMetaData;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return trackMetaData.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = inflater.inflate(R.layout.music_cover, view, false);

        assert imageLayout != null;
        final ImageView musicCover = imageLayout
                .findViewById(R.id.music_cover_big);

        Picasso.get()
                .load(trackMetaData.get(position).getTrackArt())
                .placeholder(R.drawable.bg_default_cover)
                .error(R.drawable.bg_default_cover)
                .into(musicCover);

        view.addView(imageLayout, 0);

        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }


}
package deo.com.signaldemo.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;

import deo.com.signaldemo.R;

public class EmoticonsPagerAdapter extends PagerAdapter {

    ArrayList<String> emoticons;
    private static final int NO_OF_EMOTICONS_PER_PAGE = 12;
    Activity mActivity;
    EmoticonsGridAdapter.KeyClickListener mListener;

    public EmoticonsPagerAdapter(Activity activity,
                                 ArrayList<String> emoticons,
                                 EmoticonsGridAdapter.KeyClickListener listener) {
        this.mActivity = activity;
        this.emoticons = emoticons;
        this.mListener = listener;
    }

    @Override
    public int getCount() {

        return (int) Math.ceil((double) emoticons.size()
                / (double) NO_OF_EMOTICONS_PER_PAGE);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = mActivity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.emoticons_grid, null);
//        Log.d("get Count", Integer.toString(getCount()));
        int initialPosition = position * NO_OF_EMOTICONS_PER_PAGE;
//        Log.d("Position", Integer.toString(position));
//        Log.d("NO_OF_EMOTICONS", Integer.toString(NO_OF_EMOTICONS_PER_PAGE));
//        Log.d("Init Position", Integer.toString(initialPosition));
//        Log.d("emoticons size", Integer.toString(emoticons.size()));

        ArrayList<String> emoticonsInAPage = new ArrayList<String>();

        for (int i = initialPosition; i < initialPosition
                + NO_OF_EMOTICONS_PER_PAGE
                && i < emoticons.size(); i++) {
            emoticonsInAPage.add(emoticons.get(i));
        }

        GridView grid = (GridView) layout.findViewById(R.id.emoticons_grid);
        EmoticonsGridAdapter adapter = new EmoticonsGridAdapter(
                mActivity.getApplicationContext(), emoticonsInAPage, position,
                mListener);
        grid.setAdapter(adapter);

        ((ViewPager) container).addView(layout);

        return layout;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.destroyItem(container, position, object);
        ((ViewPager) container).removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}

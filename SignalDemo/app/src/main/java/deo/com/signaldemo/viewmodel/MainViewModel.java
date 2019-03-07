package deo.com.signaldemo.viewmodel;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import deo.com.signaldemo.MainActivity;
import deo.com.signaldemo.R;
import deo.com.signaldemo.SearchActivity;
import deo.com.signaldemo.adapter.MainFragmentAdapter;

public class MainViewModel implements ViewModel {

    private MainActivity mainActivity;
    private Activity activity;
    private FloatingActionsMenu fab;
    private TabLayout tab;
    private ViewPager pager;
    private MainFragmentAdapter adapter;
    private int position;
    private FloatingActionButton[] fabs;

    public MainViewModel(Activity activity, FloatingActionsMenu fab, TabLayout tab, ViewPager pager, MainFragmentAdapter adapter, int position, FloatingActionButton... fabs) {
        this.activity = activity;
        this.fab = fab;
        this.tab = tab;
        this.pager = pager;
        this.adapter = adapter;
        this.position = position;
        this.fabs = fabs;
    }

    @Override
    public void onCreate() {
        bindingViewPager();

        int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;

        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onPause() {

    }

    private void bindingViewPager() {
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(3);
        tab.addTab(tab.newTab().setIcon(R.drawable.ic_people_black_24dp),0,true);
        tab.addTab(tab.newTab().setIcon(R.drawable.ic_chat_bubble_black_24dp),1);
        tab.addTab(tab.newTab().setIcon(R.drawable.ic_more_horiz_black_24dp),2);
        tab.addOnTabSelectedListener(pagerListener);
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tab));
        pager.setCurrentItem(position);
    }

    private TabLayout.OnTabSelectedListener pagerListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            pager.setCurrentItem(tab.getPosition());
            switch (tab.getPosition()) {
                case 0:
                    //fab.setBackgroundResource(R.drawable.ic_person_add_black_24dp);
                    fab.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    //fab.setBackgroundResource(R.drawable.ic_message_black_24dp);
                    fab.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    fab.setVisibility(View.GONE);
                    break;
            }
        }
        @Override public void onTabUnselected(TabLayout.Tab tab) {}
        @Override public void onTabReselected(TabLayout.Tab tab) {}
    };

    public void fabListener(View view) {
        switch (view.getId()) {
            case R.id.searchEmailFab:
                activity.startActivity(new Intent(activity.getApplicationContext(), SearchActivity.class).putExtra("type","email"));
                activity.finish();
                break;
            case R.id.searchDeviceFab:
                ((MainActivity)MainActivity.context).bluetoothConnect();
                break;
//            case R.id.searchNicknameFab:
//                activity.startActivity(new Intent(activity.getApplicationContext(), SearchActivity.class).putExtra("type","nickname"));
//                break;
        }

    }



}

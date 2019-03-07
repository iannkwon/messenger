package deo.com.signaldemo.fragment;


import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import deo.com.signaldemo.R;
import deo.com.signaldemo.SigninActivity;
import deo.com.signaldemo.SignupActivity;
import deo.com.signaldemo.databinding.FragmentSettingBinding;
import deo.com.signaldemo.item.Device;
import deo.com.signaldemo.viewmodel.SettingViewModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment {

    FragmentSettingBinding binding;

    public static SettingFragment newInstance() {
        // Required empty public constructor
        Bundle args = new Bundle();

        SettingFragment fragment = new SettingFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_setting, container, false);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false);

        View view = binding.getRoot();

        binding.btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), SigninActivity.class));
            }
        });

        SettingViewModel settingViewModel = new SettingViewModel(binding, getContext());
        settingViewModel.onCreate();

        return view;
    }

}

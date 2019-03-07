package deo.com.signaldemo.viewmodel;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import deo.com.signaldemo.R;
import deo.com.signaldemo.databinding.FragmentSettingBinding;

public class SettingViewModel implements ViewModel {

    FragmentSettingBinding binding;
    Context context;

    public SettingViewModel(FragmentSettingBinding binding, Context context){
        this.binding = binding;
        this.context = context;
    }

    @Override
    public void onCreate() {
        binding.btnDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder ab = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                View layout = inflater.inflate(R.layout.device_add, (ViewGroup) )
            }
        });
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
}

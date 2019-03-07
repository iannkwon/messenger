package deo.com.signaldemo;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

import deo.com.signaldemo.databinding.ActivitySigninBinding;
import deo.com.signaldemo.viewmodel.SigninViewModel;

public class SigninActivity extends AppCompatActivity {

    ActivitySigninBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_signin);
        // Hide action bar
        getSupportActionBar().hide();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signin);
        binding.rlSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(binding.rlSignin.getWindowToken(), 0);
            }
        });

        SigninViewModel signinViewModel = new SigninViewModel(this, this, binding);
        signinViewModel.onCreate();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
} // end class

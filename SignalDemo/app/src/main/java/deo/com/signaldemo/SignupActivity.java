package deo.com.signaldemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import deo.com.signaldemo.databinding.ActivitySignupBinding;
import deo.com.signaldemo.viewmodel.SignupViewModel;

public class SignupActivity extends AppCompatActivity {

    ActivitySignupBinding binding;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_signup);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup);

        // layer click hide keyboard
        binding.rlSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(binding.rlSignup.getWindowToken(), 0);
            }
        });

        SignupViewModel signupViewModel = new SignupViewModel(binding.etEmail, binding.etPw, binding.etConpw,
                binding.etNick, binding.btnOk, this);
        signupViewModel.onCreate();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, SigninActivity.class));
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
